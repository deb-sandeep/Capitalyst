package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.core.timeobserver.DayObserver ;
import com.sandy.capitalyst.core.timeobserver.TimeObserver ;

public class Universe implements DayObserver {

    private String name = null ;
    private Journal journal = null ;
    private AccountManager accMgr = null ;
    
    private List<TxnGenerator> txnGenerators = new ArrayList<TxnGenerator>() ;
    
    public Universe( String name ) {
        this.name = name ;
        accMgr = new AccountManager( this ) ;
        journal = new Journal( this, accMgr ) ;
        DayClock.instance().registerTimeObserver( this ) ;
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
    
    public void addAccount( Account account ) {
        account.setUniverse( this ) ; 
        accMgr.addAccount( account ) ;
        registerTxnGenerator( account ) ;
        DayClock.instance().registerTimeObserver( account ) ;
    }
    
    public void removeAccount( Account account ) {
        DayClock.instance().removeTimeObserver( account ) ;
        accMgr.removeAccount( account ) ;
    }
    
    public Account getAccount( String accNo ) {
        return accMgr.getAccount( accNo ) ;
    }
    
    public void registerTxnGenerator( TxnGenerator txGen ) {
        if( !txnGenerators.contains( txGen ) ) {
            txGen.setUniverse( this ) ;
            txnGenerators.add( txGen ) ;
        }
        
        if( txGen instanceof TimeObserver ) {
            DayClock.instance().registerTimeObserver( (TimeObserver)txGen ) ;
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
}
