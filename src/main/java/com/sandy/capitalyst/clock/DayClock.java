package com.sandy.capitalyst.clock;

import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.util.Utils ;

public class DayClock implements PostConfigInitializable {

	static final Logger log = Logger.getLogger( DayClock.class ) ;
	
    private static DayClock instance = new DayClock() ;
    
    @Cfg
    private Date startDate = null ;
    
    @Cfg
    private Date endDate = null ;
    
    private Date now = null ;
    private List<TimeObserver> observers = new ArrayList<TimeObserver>() ;
    
    private DayClock() {}
    
    public static DayClock instance() {
        return instance ;
    }
    
    public void setStartDate( Date date ) {
        this.startDate = date ;
    }
    
    public void setEndDate( Date date ) {
        this.endDate = date ;
    }
    
    @Override
    public void initializePostConfig() {
        this.now = this.startDate ;
    }

    public void registerTimeObserver( TimeObserver to ) {
        if( !observers.contains( to ) ) {
            observers.add( to ) ;
        }
    }
    
    public Date getStartDate() {
        return this.startDate ;
    }
    
    public Date getEndDate() {
        return this.endDate ;
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
