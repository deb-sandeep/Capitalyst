package com.sandy.capitalyst.core.ext.account;

import com.sandy.capitalyst.core.Account ;

public class BankAccount extends Account {

    private String bankName = null ;
    
    public BankAccount( String id, String name, String bankName ) {
        this( id, name, 0, bankName ) ;
    }

    public BankAccount( String accNo, String name, double amount, String bankName ) {
        super( accNo, name, amount ) ;
        this.bankName = bankName ;
    }
    
    public String getBankName() {
        return this.bankName ;
    }
}
