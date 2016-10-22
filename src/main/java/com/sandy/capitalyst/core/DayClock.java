package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.timeobservers.DayObserver ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.timeobservers.EndOfMonthObserver ;
import com.sandy.capitalyst.timeobservers.EndOfQuarterObserver ;
import com.sandy.capitalyst.timeobservers.EndOfYearObserver ;
import com.sandy.capitalyst.timeobservers.TimeObserver ;
import com.sandy.capitalyst.util.Utils ;

public class DayClock {

    static final Logger log = Logger.getLogger( DayClock.class ) ;
    
    private Date startDate = null ;
    private Date endDate = null ;
    
    private Date now = null ;
    private List<TimeObserver> observers = new ArrayList<TimeObserver>() ;
    
    public DayClock( Date start, Date end ) {
        this.startDate = start ;
        this.endDate   = end ;
        this.now       = start ;
    }
    
    public void registerTimeObserver( TimeObserver to ) {
        if( !observers.contains( to ) ) {
            observers.add( to ) ;
        }
    }
    
    public Date now() {
        return this.now ;
    }
    
    public void reset() {
        now = null ;
        startDate = null ;
        endDate = null ;
        observers = new ArrayList<TimeObserver>() ;
    }
    
    public void removeTimeObserver( TimeObserver to ) {
        observers.remove( to ) ;
    }
    
    public void run() {
        
        log.debug( "Running simulation...\n" ) ;
        
        now = startDate ;
        while( DateUtils.truncatedCompareTo( now, endDate, Calendar.DATE ) <= 0 ) {

            observers.stream()
                     .filter( p -> p instanceof DayObserver ) 
                     .forEach( p -> {
                         DayObserver o = ( DayObserver )p ;
                         o.handleDayEvent( now ) ;                         
                     }) ;

            Calendar cal = Calendar.getInstance() ;
            cal.setTime( now ) ;
            
            if( Utils.isEndOfMonth( cal ) ) {
                observers.stream()
                         .filter( p -> p instanceof EndOfMonthObserver ) 
                         .forEach( p -> { 
                             EndOfMonthObserver o = ( EndOfMonthObserver )p ;
                             o.handleEndOfMonthEvent( now ) ; 
                          }) ;
            }
            
            if( Utils.isEndOfQuarter( cal ) ) {
                observers.stream()
                         .filter( p -> p instanceof EndOfQuarterObserver ) 
                         .forEach( p -> { 
                             EndOfQuarterObserver o = ( EndOfQuarterObserver )p ;
                             o.handleEndOfQuarterEvent( now ) ; 
                         }) ;
            }
            
            if( Utils.isEndOfYear( cal ) ) {
                observers.stream()
                         .filter( p -> p instanceof EndOfYearObserver ) 
                         .forEach( p -> {
                             EndOfYearObserver o = ( EndOfYearObserver )p ;
                             o.handleEndOfYearEvent( now ) ;                             
                         }) ;
            }
            
            observers.stream()
                     .filter( p -> p instanceof EndOfDayObserver ) 
                     .forEach( p -> {
                         EndOfDayObserver o = ( EndOfDayObserver )p ;
                         o.handleEndOfDayEvent( now ) ;                         
                     }) ;

            now = DateUtils.truncate( DateUtils.addDays( now, 1 ), Calendar.DATE ) ;
            
            if( Utils.isEndOfMonth( now ) ) {
                System.out.print( "." ) ;
            }
            
            if( Utils.isEndOfYear( now ) ) {
                System.out.println( " " + Utils.getYear( now ) ) ;
            }
        }
    }
}
