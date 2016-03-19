package com.sandy.capitalyst.domain.core;

import java.util.ArrayList ;
import java.util.List ;

public class AccountingItemGroup extends AccountingItem {

    private List<AccountingItem> children = new ArrayList<AccountingItem>() ;
    
    public AccountingItemGroup( AccountingItem parent ) {
        super( parent ) ;
    }
}
