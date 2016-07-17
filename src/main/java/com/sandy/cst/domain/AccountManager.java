package com.sandy.cst.domain;

import java.util.Map ;
import java.util.concurrent.ConcurrentHashMap ;

public class AccountManager {

    private Map<String, Account> accountMap = null ;
    
    public AccountManager() {
        accountMap = new ConcurrentHashMap<String, Account>() ;
    }
    
    public void addAccount( Account account ) {
        accountMap.put( account.getAccountNumber(), account ) ;
    }
    
    public Account getAccount( String accountNumber ) {
        return accountMap.get( accountNumber ) ;
    }
}
