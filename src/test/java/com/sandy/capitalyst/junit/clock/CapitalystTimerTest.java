package com.sandy.capitalyst.junit.clock;

import static com.sandy.capitalyst.util.Utils.parseDate ;
import static org.hamcrest.Matchers.equalTo ;
import static org.hamcrest.Matchers.is ;
import static org.junit.Assert.assertThat ;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.junit.Test ;

import com.sandy.capitalyst.core.DayClock ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.timeobservers.DayObserver ;

public class CapitalystTimerTest {

    private DayClock timer = null ;
    private class Observer implements DayObserver {
      
        private List<Date> dateList = new ArrayList<Date>() ;
        
        @Override
        public void handleDayEvent( Date date ) {
            dateList.add( date ) ;
        }
        
        public int numDateEventsReceived() {
            return dateList.size() ;
        }

        @Override public void setUniverse( Universe u ) { }
        @Override public Universe getUniverse() { return null ; }
        @Override public void setId( String id ) {}
        @Override public String getId() { return null ; }
        
    } ;
    
    @Test
    public void daysInJan2015() {
        
        Observer to1 = new Observer() ;
        Observer to2 = new Observer() ;
        
        timer = new DayClock( null, parseDate( "01/01/2015" ), parseDate( "31/01/2015" ) ) ;
        timer.registerTimeObserver( to1 ) ;
        timer.registerTimeObserver( to2 ) ;
        timer.run() ;
        
        assertThat( to1.numDateEventsReceived(), is( equalTo( 31 ) ) );
        assertThat( to2.numDateEventsReceived(), is( equalTo( 31 ) ) );
    }
    
    @Test
    public void daysIn2015() {
        
        Observer to1 = new Observer() ;
        
        timer = new DayClock( null, parseDate( "01/01/2015" ), parseDate( "31/12/2015" ) ) ;
        timer.registerTimeObserver( to1 ) ;
        timer.run() ;

        assertThat( to1.numDateEventsReceived(), is( equalTo( 365 ) ) );
    }
}
