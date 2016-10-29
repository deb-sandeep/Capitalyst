package com.sandy.capitalyst.core;

import java.util.Date ;

public class Txn {
    
    public static enum TxnType { CREDIT, DEBIT } ;

    private String accountNumber ;
    private double amount ;
    private Date   date ;
    private String description ;
    
    private boolean postDated    = false ;
    
    private boolean taxable      = false ;
    private double  taxableAmout = 0 ;
    private boolean tdsEnabled   = false ;
    
    private Txn taxTxn = null ;
    
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
        this.postDated = false ;
    }

    public TxnType getTxnType() {
        return amount > 0 ? TxnType.CREDIT : TxnType.DEBIT ;
    }

    public String getAccountNumber() {
        return accountNumber ;
    }

    public double getAmount() {
        if( taxTxn != null ) {
            return amount - taxTxn.getAmount() ;
        }
        return amount ;
    }

    public Date getDate() {
        return date ;
    }
    
    public void setDescription( String description ) {
        this.description = description ;
    }
    
    public String getDescription() {
        return description ;
    }
    
    public void setPostDated( boolean b ) {
        this.postDated = b ;
    }
    
    public boolean isPostDated() {
        return this.postDated ;
    }
    
    public void setTaxable( boolean t ) {
        this.taxable = t ;
    }
    
    public boolean isTaxable() {
        return this.taxable ;
    }
    
    public void setTaxableAmount( double amt ) {
        if( amt > this.amount ) {
            throw new IllegalArgumentException( 
             "Taxable amount can't be greater than total transaction amount" ) ;
        }
        this.taxableAmout = amt ;
    }
    
    public double getTaxableAmount() {
        return this.taxableAmout ;
    }
    
    public boolean isTDSEnabled() {
        return this.tdsEnabled ;
    }
    
    public void setTDSEnabled( boolean b ) {
        this.tdsEnabled = b ;
    }
    
    public void setTaxTxn( Txn t ) {
        this.taxTxn = t ;
    }
    
    public Txn getTaxTxn() {
        return this.taxTxn ;
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
