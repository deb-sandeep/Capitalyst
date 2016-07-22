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
    
    public void run() throws Exception {
        Date now = startDate ;
        while( DateUtils.truncatedCompareTo( now, endDate, Calendar.DATE ) <= 0 ) {
            for( TimeObserver observer : observers ) {
                observer.handleDateEvent( now ) ;
            }
            now = DateUtils.truncate( DateUtils.addDays( now, 1 ), Calendar.DATE ) ; 
        }
    }
}
