package com.sandy.capitalyst.domain.util;

import java.util.List ;

import com.sandy.capitalyst.domain.core.AccountingItem ;

public class IncomeItem extends FixedAmountItem<IncomeItem> {
    
    private double taxPct = 0.0 ;
    
    public IncomeItem( double amt ) {
        super( null, amt ) ;
    }
    
    public IncomeItem( String name, double amt ) {
        super( name, amt ) ;
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

///////////////////////////////////////////////////
// Employ builder pattern for accounting items
// Revisit income items
// Revisit investment items