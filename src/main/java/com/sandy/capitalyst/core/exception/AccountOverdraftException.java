package com.sandy.capitalyst.core.exception;

@SuppressWarnings( "serial" )
public class AccountOverdraftException extends RuntimeException {
    
    private String accountNumber = null ;
    
    public AccountOverdraftException( String accNo ) {
        this.accountNumber = accNo ;
    }

    @Override
    public String getMessage() {
        return "Account " + accountNumber + " overdraft detected." ;
    }
}
