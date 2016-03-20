package com.sandy.capitalyst.domain.util;

import java.util.Date ;

import com.sandy.capitalyst.domain.core.AccountingItem ;

public class IncomeTaxAccountingItem extends AccountingItem {
    
    private AccountingItem incomeItem = null ;
    private double percentage = 0.3 ;

    public IncomeTaxAccountingItem( AccountingItem incomeItem, double percentage ) {
        super( "Expense > Income tax > Tax on " + incomeItem.getName() ) ;
        this.incomeItem = incomeItem ;
        this.percentage = percentage ;
    }
    
    public IncomeTaxAccountingItem( AccountingItem incomeItem ) {
        this( incomeItem, 0.3 ) ;
    }

    @Override
    protected double computeEntryForMonth( Date date ) {
        double amt = incomeItem.getEntryForMonth( date ) ;
        if( amt > 0 ) {
            amt = -1 * amt * percentage ;
        }
        return amt ;
    }
}
