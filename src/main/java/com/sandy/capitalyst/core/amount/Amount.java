package com.sandy.capitalyst.core.amount;

import com.sandy.capitalyst.cfg.Cfg ;

public abstract class Amount {
    
    private double amount = 0 ;
    @Cfg private double baseAmount = 0 ;
    @Cfg private String creationString = null ;

    public double getBaseAmount() { 
        return this.baseAmount ; 
    }
    
    public void setBaseAmount( double amt ) {
        this.amount = amt ;
        this.baseAmount = amt ;
    }
    
    public double getAmount() { 
        return this.amount ; 
    }
    
    public void setAmount( double amt ) {
        this.amount = amt ;
    }
    
    public void setCreationString( String str ) {
        this.creationString = str ;
    }
    
    public String getCreationString() {
        return this.creationString ;
    }
    
    public String toString() {
        return creationString ;
    }
}
