package com.sandy.capitalyst.domain.core;


public abstract class AccountingItem extends CapitalystEntity {

    private AccountingItem parent = null ;
    
    public AccountingItem( AccountingItem parent ) {
        this.parent = parent ;
    }
}
