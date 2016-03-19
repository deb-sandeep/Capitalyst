package com.sandy.capitalyst.domain.util;

import java.util.Date ;

import com.sandy.capitalyst.domain.core.AccountingItem ;

public class CumulativeAccountingItem extends AccountingItem {
    
    private AccountingItem[] delegates = null ;
    private double lastAmt = 0 ;

    public CumulativeAccountingItem( String name, AccountingItem... items ) {
        super( name ) ;
        this.delegates = items ;
        super.setShouldEntryBeAddedToGroupSum( false ) ;
    }

    @Override
    protected double computeEntryForMonth( Date date ) {
        double amt = lastAmt ;
        for( AccountingItem d : this.delegates ) {
            double childEntry = d.getEntryForMonth( date ) ;
            if( d.shouldEntryBeAddedToGroupSum() ) {
                amt += childEntry ;
            }
        }
        this.lastAmt = amt ;
        return amt ;
    }
}
