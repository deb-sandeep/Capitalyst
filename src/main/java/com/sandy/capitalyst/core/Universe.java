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
    
    public void setConfiguration( UniverseConfig config ) {
        this.config = config ;
    }

    public UniverseConfig getConfiguration() {
        return this.config ;
    }
    
    public EventBus getBus() {
        return this.bus ;
    }
    
    public Date now() {
        return clock.now() ;
    }
    
    @Override
    public void initializePostConfig() {
        clock = new DayClock( this, this.startDate, this.endDate ) ;
        addTimeObserver( this ) ;
    }
    
    public void runSimulation() {
        virgin = false ;
        clock.run() ;
    }
    
    public boolean isVirgin() {
        return virgin ;
    }

    @Override
    public void setUniverse( Universe u ) {}

    @Override
    public Universe getUniverse() {
        return this ;
    }
    
    public String getName() {
        return this.name ;
    }
    
    public void setName( String name ) {
        this.name = name ;
    }
    
    public void setStartDate( Date date ) {
        this.startDate = date ;
    }
    
    public void setEndDate( Date date ) {
        this.endDate = date ;
    }
    
    public Date getStartDate() {
        return this.startDate ;
    }
    
    public Date getEndDate() {
        return this.endDate ;
    }
    
    public Range getInflationRate() {
        return inflationRate ;
    }

    public void setInflationRate( Range inflationRate ) {
        this.inflationRate = inflationRate ;
    }

    public void addToContext( String alias, UniverseConstituent obj ) {
        addEntity( obj ) ;
        obj.setUniverse( this ) ;
        context.put( alias, obj ) ;
        if( obj instanceof TimeObserver ) {
            addTimeObserver( (TimeObserver)obj ) ;
        }
    }
    
    public UniverseConstituent getFromContext( String alias ) {
        return context.get( alias ) ;
    }
     
    public void addAccount( Account account ) {
        account.setUniverse( this ) ; 
        accMgr.addAccount( account ) ;
        registerTxnGenerator( account ) ;
        addTimeObserver( account ) ;
        
        bus.publishEvent( EventType.ACCOUNT_CREATED, account ) ;
    }
    
    public void removeAccount( Account account ) {
        removeTimeObserver( account ) ;
        accMgr.removeAccount( account ) ;
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
    
    public void addTimeObserver( TimeObserver observer ) {
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
    
    public void removeTimeObserver( TimeObserver observer ) {
        clock.removeTimeObserver( observer ) ;
    }
    
    public Collection<TxnGenerator> getAllTxGens() {
        return this.txnGenerators ;
    }
    
    public void registerTxnGenerator( TxnGenerator txGen ) {
        addEntity( txGen ) ;
        if( !txnGenerators.contains( txGen ) ) {
            txGen.setUniverse( this ) ;
            txnGenerators.add( txGen ) ;
        }
        
        if( txGen instanceof TimeObserver ) {
            addTimeObserver( (TimeObserver)txGen ) ;
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
    
    public void postTransactions( List<Txn> txnList ) {
        for( Txn t : txnList ) {
            postTransaction( t ) ;
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

    @Override
    public void setId( String id ) {
        setName( id ) ;
    }

    @Override
    public String getId() {
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

    public void initializePostCreation() {
        PostUniverseCreationInitializable puc = null ;
        for( UniverseConstituent uc : allEntities  ) {
            if( uc instanceof PostUniverseCreationInitializable ) {
                puc = ( PostUniverseCreationInitializable )uc ;
                puc.initializePostUniverseCreation() ; 
            }
        }
    }
    
    private void addEntity( UniverseConstituent entity ) {
        allEntities.add( entity ) ;
    }
}
