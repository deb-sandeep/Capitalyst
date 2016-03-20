package com.sandy.capitalyst.domain.util;

import java.util.List ;

import com.sandy.capitalyst.domain.core.AccountingItem ;


public class InvestmentItem extends FixedAmountItem<InvestmentItem> {
    
    private double taxPct = 0.0 ;
    
    public InvestmentItem( String name, double amt ) {
        super( name, amt ) ;
    }
    
    public InvestmentItem withMaturityDetails( IncomeItem incomeItem ) {
        List<AccountingItem> derivedItems = super.getDerivedAccountingItems() ;
        derivedItems.add( incomeItem ) ;
        return this ;
    }
}

///////////////////////////////////////////////////
// Employ builder pattern for accounting items
// Revisit income items
// Revisit investment items