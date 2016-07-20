package com.sandy.capitalyst.core;

import java.util.Map ;
import java.util.concurrent.ConcurrentHashMap ;

class AccountManager {

    private Universe universe = null ;
    private Map<String, Account> accountMap = null ;
    
    public AccountManager( Universe universe ) {
        this.universe = universe ;
        accountMap = new ConcurrentHashMap<String, Account>() ;
    }
    
    public void addAccount( Account account ) {
        accountMap.put( account.getAccountNumber(), account ) ;
    }
    
    public Account getAccount( String accountNumber ) {
        return accountMap.get( accountNumber ) ;
    }
}
