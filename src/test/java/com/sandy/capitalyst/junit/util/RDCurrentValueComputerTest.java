package com.sandy.capitalyst.junit.util;

import static junit.framework.Assert.assertEquals ;
import static junit.framework.Assert.assertTrue ;

import java.util.Date ;

import org.apache.log4j.Logger ;
import org.junit.Test ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.util.RDCurrentValueComputer ;
import com.sandy.capitalyst.util.Utils ;
import com.sandy.capitalyst.util.converter.ExecutionTimeConverter ;

public class RDCurrentValueComputerTest {

    static Logger log = Logger.getLogger( RDCurrentValueComputer.class ) ;
    
    private RDCurrentValueComputer computer = null ;

    private void createComputer( double amt, double roi, String startDateStr, 
                                 String endDateStr, String schedStr ) {
        
        ExecutionTimeConverter schedConv = new ExecutionTimeConverter() ;
        Date start = Utils.parseDate( startDateStr ) ;
        Date end = Utils.parseDate( endDateStr ) ;
        ExecutionTime schedule = schedConv.convert( ExecutionTime.class, schedStr ) ;
        
        computer = new RDCurrentValueComputer( amt, roi, schedule, start, end ) ;
    }
    
    @Test
    public void test() {
        createComputer( 1000, 7.25, "13/11/2016", "13/11/2017", "13 * * *" ) ;
        computer.compute() ;
        assertTrue( computer.getValue()-12479 < 2 );
        assertTrue( computer.getInterest()-479 < 2 );
        assertEquals( computer.getPrincipal(), 12000, 0.001 ) ;
    }
}
