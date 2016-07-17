package com.sandy.cst.domain;

import java.util.ArrayList ;
import java.util.List ;

public class Journal {

    private AccountManager accMgr = null ;
    private List<Transaction> transactions = new ArrayList<Transaction>() ;
    
    public Journal( AccountManager accMgr ) {
        this.accMgr = accMgr ;
    }
    
    public void postTransaction( Transaction t ) 
            throws AccountNotFoundException {
        Account account = accMgr.getAccount( t.getAccountNumber() ) ;
        if( account == null ) {
            throw new AccountNotFoundException( t.getAccountNumber() ) ;
        }
        
        transactions.add( t ) ;
        account.postTransaction( t ) ;
    }
}
