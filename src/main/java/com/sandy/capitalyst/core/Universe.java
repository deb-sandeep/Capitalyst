package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.LinkedHashSet ;
import java.util.List ;
import java.util.Map ;
import java.util.Set ;

import com.sandy.capitalyst.EventType ;
import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.account.AccountManager ;
import com.sandy.capitalyst.account.BankAccount ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.cfg.PostUniverseCreationInitializable ;
import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.core.exception.AccountNotFoundException ;
import com.sandy.capitalyst.timeobservers.DayObserver ;
import com.sandy.capitalyst.timeobservers.TimeObserver ;
import com.sandy.capitalyst.txgen.TxnGenerator ;
import com.sandy.capitalyst.util.Range ;
import com.sandy.capitalyst.util.Utils ;
import com.sandy.common.bus.EventBus ;

public class Universe implements DayObserver, PostConfigInitializable {

    private UniverseConfig config = null ;
    
    private Journal journal = null ;
    private AccountManager accMgr = null ;
    
    private List<TxnGenerator> txnGenerators = new ArrayList<TxnGenerator>() ;
    private Map<String, UniverseConstituent> context = new HashMap<String, UniverseConstituent>() ;
    private Map<Integer, Double> inflationRates = new HashMap<>() ;
    private Set<UniverseConstituent> allEntities = new LinkedHashSet<UniverseConstituent>() ;
    private DayClock clock = null ;
    
    private EventBus bus = new EventBus() ;
    private boolean  virgin = true ;
    
    @Cfg private String name = null ;
    @Cfg private Date   startDate = null ;
    @Cfg private Date   endDate = null ;
    @Cfg private Range  inflationRate = null ;
    
    public Universe( String name ) {
        
        this.name = name ;
        
        accMgr  = new AccountManager( this ) ;
        journal = new Journal( this, accMgr ) ;
    }
    
    public String getName() { return this.name ; }
    public void setName( String name ) { this.name = name ; }
    
    public void setStartDate( Date date ) { this.startDate = date ; }
    public Date getStartDate() { return this.startDate ; }
    
    public void setEndDate( Date date ) { this.endDate = date ; }
    public Date getEndDate() { return this.endDate ; }
    
    public void setInflationRate( Range rate ) { this.inflationRate = rate ; }
    public Range getInflationRate() { return inflationRate ; }

    public void setConfiguration( UniverseConfig c ) { this.config = c ; }
    
    @Override public void setUniverse( Universe u ) {}
    @Override public Universe getUniverse() { return this ; }
    
    @Override public void setId( String id ) { setName( id ) ; }
    @Override public String getId() { return getName() ; }
    
    @Override
    public void initializePostConfig() {
        clock = new DayClock( this, this.startDate, this.endDate ) ;
        registerTimeObserver( this ) ;
    }
    
    public void initializePostCreation() {
        PostUniverseCreationInitializable puc = null ;
        for( UniverseConstituent uc : allEntities  ) {
            if( uc instanceof PostUniverseCreationInitializable ) {
                puc = ( PostUniverseCreationInitializable )uc ;
                puc.initializePostUniverseCreation() ; 
            }
        }
    }
    
    public EventBus getBus() {
        return this.bus ;
    }
    
    public void runSimulation() {
        virgin = false ;
        clock.run() ;
    }
    
    public String getInterpolatedValue( String input ) {
        return this.config.interpolate( input ) ;
    }
    
    public Date now() {
        return clock.now() ;
    }
    
    public boolean isVirgin() {
        return virgin ;
    }

    public void addToContext( String alias, UniverseConstituent obj ) {
        addEntity( obj ) ;
        obj.setUniverse( this ) ;
        context.put( alias, obj ) ;
        if( obj instanceof TimeObserver ) {
            registerTimeObserver( (TimeObserver)obj ) ;
        }
    }
    
    public void registerAccount( Account account ) {
        account.setUniverse( this ) ; 
        accMgr.addAccount( account ) ;
        registerTxnGenerator( account ) ;
        registerTimeObserver( account ) ;
        
        bus.publishEvent( EventType.ACCOUNT_CREATED, account ) ;
    }
    
    public void registerTimeObserver( TimeObserver observer ) {
        addEntity( observer ) ;
        if( observer.getUniverse() == null ) {
            observer.setUniverse( this ) ;
        }
        else if( observer.getUniverse() != this ) {
            throw new IllegalStateException( "Entity from a different " + 
                    "universe is being added" ) ;
        }
        clock.registerTimeObserver( observer ) ;
    }
    
    public void registerTxnGenerator( TxnGenerator txGen ) {
        if( !txnGenerators.contains( txGen ) ) {
            addEntity( txGen ) ;
            
            txGen.setUniverse( this ) ;
            txnGenerators.add( txGen ) ;
            
            if( txGen instanceof TimeObserver ) {
                registerTimeObserver( (TimeObserver)txGen ) ;
            }
        }
    }
    
    public UniverseConstituent getFromContext( String alias ) {
        return context.get( alias ) ;
    }
    
    public Account getAccount( String accNo ) {
        Account acc = accMgr.getAccount( accNo ) ;
        if( acc == null ) {
            throw new AccountNotFoundException( accNo ) ;
        }
        return acc ;
    }
    
    public Collection<Account> getAllAccounts() {
        return accMgr.getAllAccounts() ;
    }
    
    public Collection<TxnGenerator> getAllTxGens() {
        return this.txnGenerators ;
    }
    
    public void postTransactions( List<Txn> txnList ) {
        for( Txn t : txnList ) {
            postTransaction( t ) ;
        }
    }
    
    public void postTransaction( Txn txn ) {
        if( txn.getAmount() != 0 ) {
            if( !txn.isTaxable() || txn.isPostDated() ) {
                journal.addTransaction( txn ) ;
            }
            else {
                double taxAmt = txn.getTaxableAmount() * 0.3 ;
                if( txn.isTDSEnabled() ) {
                    taxAmt /= 3 ;
                }
                
                String taxAC = getTaxAccount( txn.getAccountNumber() ) ;
                Txn taxTx = new Txn( taxAC, taxAmt, txn.getDate(),
                                     "TDS on " + txn.getDescription() + 
                                     ".A/C" + txn.getAccountNumber() ) ;
                
                txn.setTaxTxn( taxTx ) ;
                journal.addTransaction( txn ) ;
                journal.addTransaction( taxTx ) ;
            }
        }
    }
    
    @Override
    public void handleDayEvent( Date date ) {
        
        List<Txn> tempList = null ;
        for( TxnGenerator txGen : txnGenerators ) {

            tempList = new ArrayList<Txn>() ;
            txGen.getTransactionsForDate( date, tempList ) ;

            if( tempList != null && !tempList.isEmpty() ) {
                for( Txn t : tempList ) {
                    if( Utils.isAfter( t.getDate(), date ) ) {
                        t.setPostDated( true ) ;
                    }
                    else if( Utils.isBefore( t.getDate(), date ) ) {
                        throw new IllegalStateException( "Predated txn not supported" ) ;
                    }
                    postTransaction( t ) ;
                }
            }
        }
    }
    
    public String getTaxAccount( String targetAccountNo ) {
        
        Account tgtAccount = accMgr.getAccount( targetAccountNo ) ;
        if( ! (tgtAccount instanceof BankAccount) ) {
            throw new IllegalStateException( "No tax account associated with " + 
                                             "account " + targetAccountNo ) ;
        }
        
        String pan = (( BankAccount )tgtAccount).getPanNumber() ;
        String taxACNo = "TAX_AC_" + pan ;
        if( accMgr.getAccount( taxACNo ) != null ) {
            return taxACNo ;
        }
        throw new AccountNotFoundException( "Tax account not found for " + 
                                            targetAccountNo ) ;
    }
    
    public String toString() {
        return getName() ;
    }

    public double getCurrentInflationRate() {
        Double rate = inflationRates.get( clock.getYear() ) ;
        if( rate == null ) {
            rate = inflationRate.getRandom() ;
            inflationRates.put( clock.getYear(), rate ) ;
        }
        return rate ;
    }
    
    protected double getInflatedAmount( double base ) {
        return base*(1+getCurrentInflationRate()/100) ;
    }

    private void addEntity( UniverseConstituent entity ) {
        allEntities.add( entity ) ;
    }

    public Universe clone( String newName ) {
        
        Universe u = new Universe( newName ) ;
        u.setStartDate( getStartDate() ) ;
        u.setEndDate( getEndDate() ) ;
        u.setInflationRate( getInflationRate() ) ;
        u.setId( getId() ) ;
        
        u.initializePostConfig() ;
        
        cloneContextObjects( u ) ;
        cloneAccounts( u ) ;
        cloneTxGens( u ) ;
        cloneTimeObservers( u ) ;
        
        u.initializePostCreation() ;
        
        return u ;
    }
    
    private void cloneContextObjects( Universe newUniverse ) {
        
        UniverseConstituent uc = null ;
        UniverseConstituent ucClone = null ;
        
        for( String ctxKey : this.context.keySet() ) {
            uc = this.context.get( ctxKey ) ;
            ucClone = ( UniverseConstituent )Utils.clone( uc, newUniverse ) ;
            
            newUniverse.addToContext( ctxKey, ucClone ) ;
        }
    }
    
    private void cloneAccounts( Universe newUniverse ) {
        
        Account acClone = null ;
        for( Account ac : getAllAccounts() ) {
            acClone = ( Account )Utils.clone( ac, newUniverse ) ;
            newUniverse.registerAccount( acClone ) ;
        }
    }
    
    private void cloneTxGens( Universe newUniverse ) {
        
        TxnGenerator txGenClone = null ;
        for( TxnGenerator txGen : getAllTxGens() ) {
            txGenClone = ( TxnGenerator )Utils.clone( txGen, newUniverse ) ;
            newUniverse.registerTxnGenerator( txGenClone ) ;
        }
    }
    
    private void cloneTimeObservers( Universe newUniverse ) {
        
        TimeObserver oClone = null ;
        for( TimeObserver o : clock.getObservers() ) {
            if( !( o instanceof Account || 
                   o instanceof TxnGenerator || 
                   this.context.containsValue( o ) ) ) {
                
                oClone = ( TimeObserver )Utils.clone( o, newUniverse ) ;
                newUniverse.registerTimeObserver( oClone ) ;
            }
        }
    }
}
