package com.sandy.cst.domain;

import java.util.Date ;

public class Transaction {

    private String accountNumber ;
    private double amount ;
    private Date   date ;
    
    public Transaction( String accountNumber, double amount, Date date ) {
        super() ;
        this.accountNumber = accountNumber ;
        this.amount = amount ;
        this.date = date ;
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
