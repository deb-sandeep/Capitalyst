package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.List ;

public class Account {

    private Universe universe = null ;
    private String accountNumber ;
    private String name ;
    private double amount ;
    
    private List<Txn> ledger = new ArrayList<Txn>() ;
    
    public Account( String id, String name ) {
        this( id, name, 0 ) ;
    }
    
    public void setUniverse( Universe universe ) {
        this.universe = universe ;
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
    
    public void postTransaction( Txn t ) {
        ledger.add( t ) ;
        this.amount += t.getAmount() ;
    }
}
