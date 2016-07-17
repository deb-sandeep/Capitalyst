package com.sandy.cst.domain;

import java.util.ArrayList ;
import java.util.List ;

public class Account {

    private String accountNumber ;
    private String name ;
    private double amount ;
    
    private List<Transaction> ledger = new ArrayList<Transaction>() ;
    
    public Account( String id, String name ) {
        this( id, name, 0 ) ;
    }

    public Account( String accNo, String name, double amount ) {
        super() ;
        this.accountNumber = accNo ;
        this.name = name ;
        this.amount = amount ;
    }

    public String getAccountNumber() {
        return accountNumber ;
    }

    public String getName() {
        return name ;
    }

    public double getAmount() {
        return amount ;
    }
    
    public void postTransaction( Transaction t ) {
        ledger.add( t ) ;
        this.amount += t.getAmount() ;
    }
}
