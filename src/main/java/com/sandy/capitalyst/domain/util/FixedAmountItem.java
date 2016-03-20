package com.sandy.capitalyst.domain.util;

import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;

import com.sandy.capitalyst.domain.core.AccountingItem ;

@SuppressWarnings( "unchecked" )
public class FixedAmountItem<T> extends AccountingItem {
    
    private static SimpleDateFormat SDF = new SimpleDateFormat( "MM/yyyy" ) ;
    
    private Calendar startDate = null ;
    private Calendar endDate   = null ;
    private int      numTimes  = -1 ;
    
    private int[] activeMonths = null ;
    private double amount = 0 ;
    
    private int timesGenerated = 0 ; 

    public FixedAmountItem( String name, double amt ) {
        super( name ) ;
        this.amount = amt ;
    }
    
    public T startsOn( String date ) {
        try {
            this.startDate = getCalendar( SDF.parse( date ) ) ;
        }
        catch( ParseException e ) {
            throw new RuntimeException( e ) ;
        } ;
        return (T)this ;
    }
    
    public T endsOn( String date ) {
        try {
            this.endDate = getCalendar( SDF.parse( date ) ) ;
        }
        catch( ParseException e ) {
            throw new RuntimeException( e ) ;
        } ;
        return (T)this ;
    }
    
    public T numTimes( int times ) {
        this.numTimes = times ;
        return (T)this ;
    }
    
    public T activeOnMonths( int... activeMonths ) {
        this.activeMonths = activeMonths ;
        return (T)this ;
    }
    
    protected double getAmount() {
        return this.amount ;
    }

    @Override
    protected double computeEntryForMonth( Date date ) {
        
        Calendar cal = getCalendar( date ) ;
        
        if( this.startDate != null && before( cal, startDate ) ) {
            return 0 ;
        }
        if( this.endDate != null && after( cal, endDate ) ) {
            return 0 ;
        }
        
        boolean generateAmount = true ;
        if( this.activeMonths != null && this.activeMonths.length > 0 ) {
            generateAmount = false ;
            for( int mth : activeMonths ) {
                if( cal.get( Calendar.MONTH ) == mth ) {
                    generateAmount = true ;
                }
            }
        }
        
        if( generateAmount ) {
            if( numTimes == -1 ) {
                return amount ;
            }
            else {
                if( timesGenerated < numTimes ) {
                    timesGenerated++ ;
                    return amount ;
                }
            }
        }
        
        return 0 ;
    }
    
    private Calendar getCalendar( Date date ) {
        Calendar cal = Calendar.getInstance() ;
        cal.setTime( date ) ;
        return cal ;
    }
    
    private boolean before( Calendar toCompare, Calendar cal ) {
        
        int tcYear  = toCompare.get( Calendar.YEAR ) ;
        int calYear = cal.get( Calendar.YEAR ) ;
        int tcMonth = toCompare.get( Calendar.MONTH ) ;
        int calMonth= cal.get( Calendar.MONTH ) ; 
        
        if( tcYear < calYear ) { return true ; }
        else if( tcYear == calYear  ) {
            return tcMonth < calMonth ;
        }
        return false ;
    }
    
    private boolean after( Calendar toCompare, Calendar cal ) {
        
        int tcYear  = toCompare.get( Calendar.YEAR ) ;
        int calYear = cal.get( Calendar.YEAR ) ;
        int tcMonth = toCompare.get( Calendar.MONTH ) ;
        int calMonth= cal.get( Calendar.MONTH ) ; 
        
        if( tcYear > calYear ) { return true ; }
        else if( tcYear == calYear  ) {
            return tcMonth > calMonth ;
        }
        return false ;
    }
}
