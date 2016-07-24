package com.sandy.capitalyst.poc ;

import org.joda.time.DateTime ;

import com.cronutils.descriptor.CronDescriptor ;
import com.cronutils.model.Cron ;
import com.cronutils.model.definition.CronDefinition ;
import com.cronutils.model.definition.CronDefinitionBuilder ;
import com.cronutils.model.time.ExecutionTime ;
import com.cronutils.parser.CronParser ;

public class CronExprPOC {

    public void basicPOC() {
        CronDefinition cronDefinition =
            CronDefinitionBuilder.defineCron()
                .withDayOfMonth()
                .supportsHash().supportsL().supportsW().and()
                .withMonth().and()
                .withDayOfWeek()
                .withIntMapping(7, 0) //we support non-standard non-zero-based numbers!
                .supportsHash().supportsL().supportsW().and()
                .withYear().and()
                .lastFieldOptional()
                .instance() ;
        
        CronParser parser = new CronParser( cronDefinition ) ;
        Cron cron = parser.parse( "L JAN,MAR,JUN *" ) ;
        ExecutionTime execTime = ExecutionTime.forCron( cron ) ;
        
        DateTime dateTime = new DateTime( 2015, 9, 9, 0, 0 ) ;
        
        System.out.println( execTime.isMatch( dateTime ) ) ;

        CronDescriptor descriptor = CronDescriptor.instance() ;
        System.out.println( descriptor.describe( cron ) ) ;
    }
    
    public static void main( String[] args ) {
        CronExprPOC poc = new CronExprPOC() ;
        poc.basicPOC() ;
    }
}
