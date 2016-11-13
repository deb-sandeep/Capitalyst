package com.sandy.capitalyst.util;

import java.util.Date ;

public class QuantumOfMoney {
    
    private double principal ;
    private Date   receiptDate ;
    private double interestTillDate ;
    private double roi ;
    
    public QuantumOfMoney( double principal, Date receiptDate, double roi ) {
        
        this.principal        = principal ;
        this.receiptDate      = receiptDate ;
        this.interestTillDate = 0 ;
        this.roi              = roi ;
    }
    
    public void computeAndCollateInterestTillDate( Date date ) {
        
        long numDays = Utils.getNumDaysBetween( receiptDate, date ) ;
        interestTillDate = principal*(roi/(100*360)) * numDays ;
    }
    
    public double getAmount() {
        return principal + interestTillDate ;
    }
    
    public double getPrincipal() {
        return this.principal ;
    }
    
    public double getInterest() {
        return this.interestTillDate ;
    }
}

