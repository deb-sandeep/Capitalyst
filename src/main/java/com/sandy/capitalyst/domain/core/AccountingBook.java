package com.sandy.capitalyst.domain.core;

import java.util.List ;

public abstract class AccountingBook extends AccountingItemGroup {
    
    public AccountingBook( String name ) {
        super( name ) ;
    }
    
    public void addAccountingItem( AccountingItem item ) {
        
        String parentPath = item.getParentPath() ;
        if( parentPath != null && parentPath.trim().length() != 0 ) {
            AccountingItemGroup group = super.getGroup( parentPath, true ) ;
            group.addAccountingItem( item ) ;
        }
        else {
            super.addAccountingItem( item ) ;
        }
        
        List<AccountingItem> derivedItems = item.getDerivedAccountingItems() ;
        if( derivedItems != null ) {
            for( AccountingItem derivedItem : derivedItems ) {
                addAccountingItem( derivedItem ) ;
            }
        }
    }
}
