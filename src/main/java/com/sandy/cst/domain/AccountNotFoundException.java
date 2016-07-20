package com.sandy.cst.domain;

@SuppressWarnings( "serial" )
public class AccountNotFoundException extends RuntimeException {
    
    private String accountNumber = null ;
    
    public AccountNotFoundException( String accNo ) {
        this.accountNumber = accNo ;
    }

    @Override
    public String getMessage() {
        return "Account " + accountNumber + " not found." ;
    }
}
