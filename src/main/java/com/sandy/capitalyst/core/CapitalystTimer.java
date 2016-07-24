package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import org.apache.commons.lang.time.DateUtils ;

public class CapitalystTimer {

    private Date startDate = null ;
    private Date endDate   = null ;
    
    private List<TimeObserver> observers = new ArrayList<TimeObserver>() ;
    
    public CapitalystTimer( Date start, Date end ) {
        startDate = start ;
        endDate   = end ;
    }
    
    public void registerTimeObserver( TimeObserver to ) {
        if( !observers.contains( to ) ) {
            observers.add( to ) ;
        }
    }
    
    public void run() {
        Date now = startDate ;
        while( DateUtils.truncatedCompareTo( now, endDate, Calendar.DATE ) <= 0 ) {
            for( TimeObserver observer : observers ) {
                // Make a defensive copy of now to ensure that observers are 
                // not able to influence each other by changing the instance of
                // time they receive.
                observer.handleDateEvent( new Date( now.getTime() ) ) ;
            }
            now = DateUtils.truncate( DateUtils.addDays( now, 1 ), Calendar.DATE ) ; 
        }
    }
}
