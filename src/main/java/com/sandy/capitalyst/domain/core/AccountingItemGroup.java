package com.sandy.capitalyst.domain.core;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

class AccountingItemGroup extends AccountingItem {

    private List<AccountingItem> children = new ArrayList<AccountingItem>() ;
    private Map<String, AccountingItem> childrenMap = new HashMap<String, AccountingItem>() ;
    
    public AccountingItemGroup( String name ) {
        super( name ) ;
    }
    
    public void addAccountingItem( AccountingItem child ) {
        
        String childName = child.getName() ;
        if( childrenMap.containsKey( childName ) ) {
            throw new IllegalArgumentException( 
                    "An accounting item with the same name is already " + 
                    "registered with group " + super.getName() ) ;
        }
        else {
            childrenMap.put( childName, child ) ;
        }
        this.children.add( child ) ;
        child.setParent( this ) ;
    }
    
    public AccountingItemGroup getGroup( String path ) {
        return this.getGroup( path, false ) ;
    }
    
    public AccountingItemGroup getGroup( String path, boolean create ) {
        
        String stepName = path.trim() ;
        String nextLevelSteps = null ;
        
        int indexOfGt = path.indexOf( ">" ) ;
        if( indexOfGt != -1 ) {
            stepName       = path.substring( 0, indexOfGt ).trim() ;
            nextLevelSteps = path.substring( indexOfGt + 1 ).trim() ;
        }
        
        AccountingItem child = childrenMap.get( stepName ) ;
        if( child == null ) {
            if( create ) {
                child = new AccountingItemGroup( stepName ) ;
                this.addAccountingItem( child ) ;
            }
            else {
                throw new IllegalArgumentException( "No child with the name '" + 
                        stepName + "' is registered with group '" + 
                        super.getName() + "'" ) ;
            }
        }
        
        if( nextLevelSteps == null ) {
            if( child instanceof AccountingItemGroup ) {
                return ( AccountingItemGroup )child ;
            }
            else {
                throw new IllegalArgumentException( 
                                 "Path does not end in an accounting group." ) ;
            }
        }
        else if( !(child instanceof AccountingItemGroup) ){
            throw new IllegalArgumentException( 
                    "A child with the name '" + stepName + "' is already " + 
                    "registered, but is not a item group." ) ;
        }
        else {
            return (( AccountingItemGroup )child).getGroup( nextLevelSteps, create ) ;
        }
    }
    
    public AccountingItem getAccountingItem( String path ) {
        
        String stepName = path.trim() ;
        String nextLevelSteps = null ;
        
        int indexOfGt = path.indexOf( ">" ) ;
        if( indexOfGt != -1 ) {
            stepName       = path.substring( 0, indexOfGt ).trim() ;
            nextLevelSteps = path.substring( indexOfGt + 1 ).trim() ;
        }
        
        AccountingItem child = childrenMap.get( stepName ) ;
        if( child == null ) {
            throw new IllegalArgumentException( "No child with the name '" + 
                                     stepName + "' is registered with group '" + 
                                     super.getName() + "'" ) ;
        }
        
        if( nextLevelSteps == null ) {
            return child ;
        }
        else if( child instanceof AccountingItemGroup ){
            return (( AccountingItemGroup )child).getAccountingItem( nextLevelSteps ) ;
        }
        else {
            throw new IllegalArgumentException( 
                            "The path element doesn't point to a item group" ) ;
        }
    }

    @Override
    protected double computeEntryForMonth( Date date ) {
        double entry = 0 ;
        for( AccountingItem child : children ) {
            double childEntry = child.getEntryForMonth( date ) ;
            if( child.shouldEntryBeAddedToGroupSum() ) {
                entry += childEntry ;
            }
        }
        return entry ;
    }
}
