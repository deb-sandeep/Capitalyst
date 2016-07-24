package com.sandy.capitalyst.core;

import java.util.Date ;

public class Txn {
    
    public static enum TxnType { CREDIT, DEBIT } ;

    private String accountNumber ;
    private double amount ;
    private Date   date ;
    
    public Txn( String accountNumber, double amount, Date date ) {
        this.accountNumber = accountNumber ;
        this.amount = amount ;
        this.date = date ;
    }
    
    public TxnType getTxnType() {
        return amount > 0 ? TxnType.CREDIT : TxnType.DEBIT ;
    }

    public String getAccountNumber() {
        return accountNumber ;
    }

    public double getAmount() {
        return amount ;
    }

    public Date getDate() {
        return date ;
    }
}
