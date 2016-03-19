package com.sandy.capitalyst.domain.core;

import java.util.Date ;
import java.util.HashMap ;
import java.util.Map ;

public abstract class AccountingItem {

    private String itemName = null ;
    private AccountingItem parent = null ;
    
    private Map<Date, Double> computedAmtMap = new HashMap<Date, Double>() ;
    
    private boolean isEntryAddedToGroupSum = true ;
    
    public AccountingItem( String name ) {
        this.itemName = name ;
    }
    
    public void setParent( AccountingItem parent ) {
        this.parent = parent ;
    }
    
    protected abstract double computeEntryForMonth( Date date ) ;
    
    public final double getEntryForMonth( Date date ) {
        if( computedAmtMap.containsKey( date ) ) {
            return computedAmtMap.get( date ) ;
        }
        
        double amt = computeEntryForMonth( date ) ;
        computedAmtMap.put( date, amt ) ;
        return amt ;
    }
    
    public boolean shouldEntryBeAddedToGroupSum() {
        return isEntryAddedToGroupSum ;
    }
    
    public void setShouldEntryBeAddedToGroupSum( boolean flag ) {
        this.isEntryAddedToGroupSum = flag ;
    }
    
    public AccountingItem getParent() {
        return this.parent ;
    }
    
    public String getName() {
        return this.itemName ;
    }
}
