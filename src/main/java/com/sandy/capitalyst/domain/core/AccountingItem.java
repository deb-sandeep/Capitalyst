package com.sandy.capitalyst.domain.core;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

public abstract class AccountingItem {

    private String itemName = null ;
    private String parentPath = "" ;
    private AccountingItem parent = null ;
    
    private List<AccountingItem> derivedItems = new ArrayList<AccountingItem>() ;
    
    private Map<Date, Double> computedAmtMap = new HashMap<Date, Double>() ;
    
    private boolean isEntryAddedToGroupSum = true ;
    
    public AccountingItem( String qualifiedName ) {

        this.itemName = qualifiedName ;
        
        int indexOfGt = qualifiedName.lastIndexOf( ">" ) ;
        if( indexOfGt != -1 ) {
            this.itemName   = qualifiedName.substring( indexOfGt+1 ).trim() ;
            this.parentPath = qualifiedName.substring( 0, indexOfGt ).trim() ;
        }
    }
    
    String getParentPath() {
        return this.parentPath ;
    }
    
    void setParent( AccountingItem parent ) {
        this.parent = parent ;
    }
    
    protected abstract double computeEntryForMonth( Date date ) ;
    
    public List<AccountingItem> getDerivedAccountingItems() {
        return derivedItems ;
    }
    
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
