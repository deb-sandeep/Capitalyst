package com.sandy.capitalyst.core;

import java.util.Collection ;
import java.util.Map ;
import java.util.concurrent.ConcurrentHashMap ;

class AccountManager {

    private Universe universe = null ;
    private Map<String, Account> accountMap = null ;
    
    public AccountManager( Universe universe ) {
        this.universe = universe ;
        accountMap = new ConcurrentHashMap<String, Account>() ;
    }
    
    public Universe getUniverse() {
        return this.universe ;
    }
    
    public void addAccount( Account account ) {
        accountMap.put( account.getAccountNumber(), account ) ;
    }
    
    public void removeAccount( Account account ) {
        accountMap.remove( account.getAccountNumber() ) ;
    }
    
    public Account getAccount( String accountNumber ) {
        return accountMap.get( accountNumber ) ;
    }
    
    public Collection<Account> getAllAccounts() {
        return accountMap.values() ;
    }
}
