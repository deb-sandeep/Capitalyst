package com.sandy.capitalyst.core.exception;

@SuppressWarnings( "serial" )
public class AccountOverdraftException extends RuntimeException {
    
    private String accountNumber = null ;
    private String description = null ;
    
    public AccountOverdraftException( String accNo, String description ) {
        this.accountNumber = accNo ;
        this.description = description ;
    }

    @Override
    public String getMessage() {
        return "Account " + accountNumber + " overdraft detected \n" + 
               "while executing transaction - " + description ;
    }
}
