package com.sandy.capitalyst.domain.util;

import java.util.List ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingItem ;

public class IncomeItem extends FixedAmountItem<IncomeItem> {
    
    private double taxPct = 0.0 ;
    
    public IncomeItem( double amt ) {
        super( null, amt, null ) ;
    }
    
    public IncomeItem( double amt, Account account ) {
        super( null, amt, account ) ;
    }
    
    public IncomeItem( String name, double amt, Account account ) {
        super( name, amt, account ) ;
    }
    
    public IncomeItem withTax( double pct ) {
        this.taxPct = pct ;
        if( this.taxPct > 0 ) {
            List<AccountingItem> derivedItems = super.getDerivedAccountingItems() ;
            derivedItems.add( new IncomeTaxItem( this, taxPct ) ) ;
        }
        return this ;
    }
}
