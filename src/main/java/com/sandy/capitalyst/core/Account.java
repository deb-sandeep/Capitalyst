package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

public class Account extends AbstractTxnGen {

    private Universe universe = null ;
    private String accountNumber ;
    private String name ;
    private double amount ;
    
    private List<Txn> ledger = new ArrayList<Txn>() ;
    
    public Account( String id, String name ) {
        this( id, name, 0 ) ;
    }
    
    public Account( String accNo, String name, double amount ) {
        this.accountNumber = accNo ;
        this.name = name ;
        this.amount = amount ;
    }

    public void setUniverse( Universe universe ) {
        this.universe = universe ;
    }
    
    public Universe getUniverse() {
        return this.universe ;
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
    
    public double getLiquidableAmount() {
        return amount ;
    }
    
    public void postTransaction( Txn t ) {
        ledger.add( t ) ;
        this.amount += t.getAmount() ;
    }
    
    public boolean isActive() {
        return true ;
    }
    
    public List<Txn> getLedger() {
        return ledger ;
    }

    @Override
    public void getTransactionsForDate( Date date, List<Txn> txnList,
                                        Universe universe ) {
        // No operation by default.
    }
}
