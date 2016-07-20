package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.List ;

class Journal {

    private Universe universe = null ;
    private AccountManager accMgr = null ;
    private List<Txn> transactions = new ArrayList<Txn>() ;
    
    public Journal( Universe universe, AccountManager accMgr ) {
        this.universe = universe ;
        this.accMgr = accMgr ;
    }
    
    public void addTransaction( Txn t ) 
            throws AccountNotFoundException {
        Account account = accMgr.getAccount( t.getAccountNumber() ) ;
        if( account == null ) {
            throw new AccountNotFoundException( t.getAccountNumber() ) ;
        }
        
        transactions.add( t ) ;
        account.postTransaction( t ) ;
    }
    
    public void addTransactions( List<Txn> txnList ) 
        throws AccountNotFoundException {
        for( Txn txn : txnList ) {
            addTransaction( txn ) ;
        }
    }
}
