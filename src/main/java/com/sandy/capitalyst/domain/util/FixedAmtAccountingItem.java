package com.sandy.capitalyst.domain.util;

import java.util.Calendar ;
import java.util.Date ;

import com.sandy.capitalyst.domain.core.AccountingItem ;

public class FixedAmtAccountingItem extends AccountingItem {
    
    private Calendar startDate = null ;
    private Calendar endDate   = null ;
    
    private int[] activeMonths = null ;
    private double amount = 0 ;

    public FixedAmtAccountingItem( String name, double amt, 
                                   int... months ) {
        super( name ) ;
        this.amount = amt ;
        this.activeMonths = months ;
    }
    
    public void setStartDate( Date date ) { 
        if( date != null ) {
            this.startDate = getCalendar( date ) ;
        }
    }
    
    public void setEndDate( Date date ) { 
        if( date != null ) {
            this.endDate = getCalendar( date ) ;
        }
    }
    
    public void setActiveMonths( int... activeMonths ) {
        this.activeMonths = activeMonths ;
    }

    @Override
    protected double computeEntryForMonth( Date date ) {
        Calendar cal = getCalendar( date ) ;
        
        if( this.startDate != null && cal.before( startDate ) ) {
            return 0 ;
        }
        if( this.endDate != null && cal.after( this.endDate ) ) {
            return 0 ;
        }
        
        if( this.activeMonths != null && this.activeMonths.length > 0 ) {
            for( int mth : activeMonths ) {
                if( cal.get( Calendar.MONTH ) == mth ) {
                    return amount ;
                }
            }
        }
        else {
            return amount ;
        }
        
        return 0 ;
    }
    
    private Calendar getCalendar( Date date ) {
        Calendar cal = Calendar.getInstance() ;
        cal.setTime( date ) ;
        return cal ;
    }
}
