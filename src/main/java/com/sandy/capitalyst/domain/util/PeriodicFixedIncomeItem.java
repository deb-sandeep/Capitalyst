package com.sandy.capitalyst.domain.util;

import java.util.List ;

import com.sandy.capitalyst.domain.core.AccountingItem ;

public class PeriodicFixedIncomeItem extends PeriodicFixedAmtAccountingItem {
    
    private double taxPct = 0.0 ;
    
    public PeriodicFixedIncomeItem( String name, double amt, double taxPct,
                                    int... months ) {
        super( name, amt, months ) ;
        this.taxPct = taxPct ;
        
        if( this.taxPct > 0 ) {
            List<AccountingItem> derivedItems = super.getDerivedAccountingItems() ;
            derivedItems.add( new IncomeTaxAccountingItem( this, taxPct ) ) ;
        }
    }
}

///////////////////////////////////////////////////
// Employ builder pattern for accounting items
// Revisit income items
// Revisit investment items