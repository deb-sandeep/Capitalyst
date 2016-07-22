package com.sandy.capitalyst.core.exception;

@SuppressWarnings( "serial" )
public class AccountExpiredException extends RuntimeException {
    
    private String accountNumber = null ;
    
    public AccountExpiredException( String accNo ) {
        this.accountNumber = accNo ;
    }

    @Override
    public String getMessage() {
        return "Account " + accountNumber + " has expired." ;
    }
}
