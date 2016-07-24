package com.sandy.capitalyst.junit.core;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import static org.junit.Assert.* ;
import static com.sandy.capitalyst.util.Utils.parseDate ;
import static org.hamcrest.Matchers.* ;
import org.junit.Test ;

import com.sandy.capitalyst.core.CapitalystTimer ;
import com.sandy.capitalyst.core.TimeObserver ;

public class CapitalystTimerTest {

    private CapitalystTimer timer = null ;
    private class Observer implements TimeObserver {
      
        private List<Date> dateList = new ArrayList<Date>() ;
        
        @Override
        public void handleDateEvent( Date date ) {
            dateList.add( date ) ;
        }
        
        public int numDateEventsReceived() {
            return dateList.size() ;
        }
    } ;
    
    @Test
    public void daysInJan2015() {
        
        Observer to1 = new Observer() ;
        Observer to2 = new Observer() ;
        
        timer = new CapitalystTimer( parseDate( "01/01/2015" ), 
                                     parseDate( "31/01/2015" ) ) ;
        timer.registerTimeObserver( to1 ) ;
        timer.registerTimeObserver( to2 ) ;
        timer.run() ;
        
        assertThat( to1.numDateEventsReceived(), is( equalTo( 31 ) ) );
        assertThat( to2.numDateEventsReceived(), is( equalTo( 31 ) ) );
    }
    
    @Test
    public void daysIn2015() {
        
        Observer to1 = new Observer() ;
        
        timer = new CapitalystTimer( parseDate( "01/01/2015" ), 
                                     parseDate( "31/12/2015" ) ) ;
        timer.registerTimeObserver( to1 ) ;
        timer.run() ;

        assertThat( to1.numDateEventsReceived(), is( equalTo( 365 ) ) );
    }
}
