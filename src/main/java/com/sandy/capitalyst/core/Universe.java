package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import com.sandy.capitalyst.EventType ;
import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.account.AccountManager ;
import com.sandy.capitalyst.account.BankAccount ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.core.exception.AccountNotFoundException ;
import com.sandy.capitalyst.timeobservers.DayObserver ;
import com.sandy.capitalyst.timeobservers.TimeObserver ;
import com.sandy.capitalyst.txgen.TxnGenerator ;
import com.sandy.common.bus.EventBus ;

public class Universe implements DayObserver, PostConfigInitializable {

    private UniverseConfig config = null ;
    
    private String name = null ;
    private Journal journal = null ;
    private AccountManager accMgr = null ;
    
    private List<TxnGenerator> txnGenerators = new ArrayList<TxnGenerator>() ;
    private Map<String, UniverseConstituent> context = new HashMap<String, UniverseConstituent>() ;
    private DayClock clock = null ;
    
    private EventBus bus = new EventBus() ;
    private boolean  virgin = true ;
    
    @Cfg private Date startDate = null ;
    @Cfg private Date endDate = null ;
    
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
    
    public void setStartDate( Date date ) {
        this.startDate = date ;
    }
    
    public void setEndDate( Date date ) {
        this.endDate = date ;
    }
    
    public Date now() {
        return clock.now() ;
    }
    
    @Override
    public void initializePostConfig() {
        clock = new DayClock( this, this.startDate, this.endDate ) ;
        clock.registerTimeObserver( this ) ;
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
    
    public void addToContext( String alias, UniverseConstituent obj ) {
        obj.setUniverse( this ) ;
        context.put( alias, obj ) ;
        if( obj instanceof TimeObserver ) {
            clock.registerTimeObserver( (TimeObserver)obj ) ;
        }
    }
    
    public UniverseConstituent getFromContext( String alias ) {
        return context.get( alias ) ;
    }
     
    public void addAccount( Account account ) {
        account.setUniverse( this ) ; 
        accMgr.addAccount( account ) ;
        registerTxnGenerator( account ) ;
        clock.registerTimeObserver( account ) ;
        
        bus.publishEvent( EventType.ACCOUNT_CREATED, account ) ;
    }
    
    public void removeAccount( Account account ) {
        clock.removeTimeObserver( account ) ;
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
    
    public Collection<TxnGenerator> getAllTxGens() {
        return this.txnGenerators ;
    }
    
    public void registerTxnGenerator( TxnGenerator txGen ) {
        if( !txnGenerators.contains( txGen ) ) {
            txGen.setUniverse( this ) ;
            txnGenerators.add( txGen ) ;
        }
        
        if( txGen instanceof TimeObserver ) {
            clock.registerTimeObserver( (TimeObserver)txGen ) ;
        }
    }
    
    public void postTransaction( Txn txn ) {
        if( txn.getAmount() != 0 ) {
            journal.addTransaction( txn ) ;
        }
    }
    
    public void postTransactions( List<Txn> txnList ) {
        journal.addTransactions( txnList ) ;
    }
    
    @Override
    public void handleDayEvent( Date date ) {
        
        List<Txn> tempList = null ;
        for( TxnGenerator txGen : txnGenerators ) {

            tempList = new ArrayList<Txn>() ;
            txGen.getTransactionsForDate( date, tempList ) ;

            if( tempList != null && !tempList.isEmpty() ) {
                journal.addTransactions( tempList ) ;
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
}
