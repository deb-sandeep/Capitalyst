package com.sandy.capitalyst.domain.util;

import java.util.List ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingItem ;


public class InvestmentItem extends FixedAmountItem<InvestmentItem> {
    
    public InvestmentItem( String name, double amt, Account account ) {
        super( name, amt, account ) ;
    }
    
    public InvestmentItem withMaturityDetails( IncomeItem incomeItem ) {
        List<AccountingItem> derivedItems = super.getDerivedAccountingItems() ;
        derivedItems.add( incomeItem ) ;
        return this ;
    }
}
