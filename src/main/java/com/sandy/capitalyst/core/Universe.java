package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

public class Universe implements TimeObserver {

    private List<TxnGenerator> txnGenerators = null ;
    private Journal journal = null ;
    private AccountManager accMgr = null ;
    
    public Universe() {
        txnGenerators = new ArrayList<TxnGenerator>() ;
        accMgr = new AccountManager( this ) ;
        journal = new Journal( this, accMgr ) ;
    }
    
    public void addAccount( Account account ) {
        account.setUniverse( this ) ; 
        accMgr.addAccount( account ) ;
        if( account instanceof TxnGenerator ) {
            registerTimedTxnGenerator( ( TxnGenerator )account ) ;
        }
    }
    
    public void removeAccount( Account account ) {
        if( account instanceof TxnGenerator ) {
            txnGenerators.remove( account ) ;
        }
        accMgr.removeAccount( account ) ;
    }
    
    public Account getAccount( String accNo ) {
        return accMgr.getAccount( accNo ) ;
    }
    
    public void registerTimedTxnGenerator( TxnGenerator txGen ) {
        if( !txnGenerators.contains( txGen ) ) {
            txnGenerators.add( txGen ) ;
        }
    }
    
    public void postTransaction( Txn txn ) {
        journal.addTransaction( txn ) ;
    }
    
    public void postTransactions( List<Txn> txnList ) {
        journal.addTransactions( txnList ) ;
    }
    
    @Override
    public void handleDateEvent( Date date ) {
        
        List<Txn> tempList = null ;
        for( TxnGenerator txGen : txnGenerators ) {
            tempList = new ArrayList<Txn>() ;
            txGen.getTransactionsForDate( date, tempList, this ) ;
            if( tempList != null && !tempList.isEmpty() ) {
                journal.addTransactions( tempList ) ;
            }
        }
        
        for( Account account : accMgr.getAllAccounts() ) {
            if( account instanceof TimeObserver ) {
                ( ( TimeObserver )account ).handleDateEvent( date ) ;
            }
        }
    }
}
