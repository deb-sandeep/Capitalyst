package com.sandy.capitalyst.domain.core;

public class AccountingHead extends AccountingItemGroup {

    private AccountingBook book = null ;
    
    public AccountingHead( AccountingBook book ) {
        super( null ) ;
        this.book = book ;
    }
    
    public AccountingBook getAccountingBook() {
        return this.book ;
    }
}
