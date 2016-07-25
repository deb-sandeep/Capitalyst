package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

public class Universe implements TimeObserver {

    private String name = null ;
    private Journal journal = null ;
    private AccountManager accMgr = null ;
    
    private List<TimeObserver> timeObservers = null ;
    
    public Universe( String name ) {
        this.name = name ;
        timeObservers = new ArrayList<TimeObserver>() ;
        accMgr = new AccountManager( this ) ;
        journal = new Journal( this, accMgr ) ;
    }
    
    public String getName() {
        return this.name ;
    }
    
    public void addAccount( Account account ) {
        account.setUniverse( this ) ; 
        accMgr.addAccount( account ) ;
        if( account instanceof TimeObserver ) {
            registerTimeObserver( ( TimeObserver )account ) ;
        }
    }
    
    public void removeAccount( Account account ) {
        if( account instanceof TimeObserver ) {
            timeObservers.remove( account ) ;
        }
        accMgr.removeAccount( account ) ;
    }
    
    public Account getAccount( String accNo ) {
        return accMgr.getAccount( accNo ) ;
    }
    
    public void registerTxnGenerator( TxnGenerator txGen ) {
        registerTimeObserver( txGen ) ;
    }
    
    private void registerTimeObserver( TimeObserver txGen ) {
        if( !timeObservers.contains( txGen ) ) {
            timeObservers.add( txGen ) ;
        }
    }
    
    public void postTransaction( Txn txn ) {
        journal.addTransaction( txn ) ;
    }
    
    public void postTransactions( List<Txn> txnList ) {
        journal.addTransactions( txnList ) ;
    }
    
    @Override
    public void handleDayEvent( Date date, Universe universe ) {
        
        List<Txn> tempList = null ;
        for( TimeObserver observer : timeObservers ) {
            observer.handleDayEvent( date, this ) ;
        }

        for( TimeObserver observer : timeObservers ) {

            if( observer instanceof TxnGenerator ) {
                tempList = new ArrayList<Txn>() ;
                TxnGenerator txGen = ( TxnGenerator )observer ;
                txGen.getTransactionsForDate( date, tempList, this ) ;

                if( tempList != null && !tempList.isEmpty() ) {
                    journal.addTransactions( tempList ) ;
                }
            }
        }
    }

    @Override
    public void handleEndOfDayEvent( Date date, Universe universe ) {
        
        for( TimeObserver observer : timeObservers ) {
            observer.handleEndOfDayEvent( date, this ) ;
        }
    }
}
