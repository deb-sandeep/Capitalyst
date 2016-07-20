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
    }
    
    public Account getAccount( String accNo ) {
        return accMgr.getAccount( accNo ) ;
    }
    
    public void registerTxnGenerator( TxnGenerator txGen ) {
        if( !txnGenerators.contains( txGen ) ) {
            txnGenerators.add( txGen ) ;
        }
    }
    
    @Override
    public void handleDateEvent( Date date ) {
        List<Txn> tempList = null ;
        for( TxnGenerator txGen : txnGenerators ) {
            tempList = txGen.getTransactionsForDate( date ) ;
            if( tempList != null && !tempList.isEmpty() ) {
                journal.addTransactions( tempList ) ;
            }
        }
    }
}
