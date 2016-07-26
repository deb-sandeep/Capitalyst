package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import org.apache.commons.lang.time.DateUtils ;

import com.sandy.capitalyst.core.timeobserver.DayObserver ;
import com.sandy.capitalyst.core.timeobserver.EndOfDayObserver ;
import com.sandy.capitalyst.core.timeobserver.EndOfMonthObserver ;
import com.sandy.capitalyst.core.timeobserver.EndOfQuarterObserver ;
import com.sandy.capitalyst.core.timeobserver.EndOfYearObserver ;
import com.sandy.capitalyst.core.timeobserver.TimeObserver ;
import com.sandy.capitalyst.util.Utils ;

public class DayClock {

    private static DayClock instance = new DayClock() ;
    
    private Date startDate = null ;
    private Date endDate = null ;
    private Date now = null ;
    private List<TimeObserver> observers = new ArrayList<TimeObserver>() ;
    
    private DayClock() {}
    
    public static DayClock instance() {
        return instance ;
    }
    
    public void registerTimeObserver( TimeObserver to ) {
        if( !observers.contains( to ) ) {
            observers.add( to ) ;
        }
    }
    
    public void setDateRange( Date start, Date end ) {
        this.startDate = start ;
        this.endDate = end ;
    }
    
    public Date getStartDate() {
        return this.startDate ;
    }
    
    public Date getEndDate() {
        return this.endDate ;
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
        }
    }
}
