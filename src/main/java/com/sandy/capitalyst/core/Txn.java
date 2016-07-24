package com.sandy.capitalyst.core;

import java.util.Date ;

public class Txn {
    
    public static enum TxnType { CREDIT, DEBIT } ;

    private String accountNumber ;
    private double amount ;
    private Date   date ;
    private String description ;
    
    public Txn( String accountNumber, double amount, Date date ) {
        this( accountNumber, amount, date, "" ) ;
    }
    
    public Txn( String accountNumber, double amount, Date date,
                String description ) {
        super() ;
        this.accountNumber = accountNumber ;
        this.amount = amount ;
        this.date = date ;
        this.description = description ;
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
    
    public String getDescription() {
        return description ;
    }
    
    @Override
    public int hashCode() {
        
        final int prime = 31 ;
        int result = 1 ;
        result = prime * result
                + ( ( accountNumber == null ) ? 0 : accountNumber.hashCode() ) ;
        long temp ;
        temp = Double.doubleToLongBits( amount ) ;
        result = prime * result + (int) ( temp ^ ( temp >>> 32 ) ) ;
        result = prime * result + ( ( date == null ) ? 0 : date.hashCode() ) ;
        return result ;
    }

    @Override
    public boolean equals( Object obj ) {
        
        if( this == obj ) return true ;
        if( obj == null ) return false ;
        if( getClass() != obj.getClass() ) return false ;
        
        Txn other = (Txn) obj ;
        if( accountNumber == null ) {
            if( other.accountNumber != null ) return false ;
        }
        else if( !accountNumber.equals( other.accountNumber ) ) {
            return false ;
        }
        
        if( Double.doubleToLongBits( amount ) != 
            Double.doubleToLongBits( other.amount ) ) {
            return false ;
        }
        
        if( date == null ) {
            if( other.date != null ) return false ;
        }
        else if( !date.equals( other.date ) ) {
            return false ;
        }
        
        return true ;
    }
}
