package com.sandy.capitalyst.poc ;

import java.time.ZoneId ;
import java.time.ZonedDateTime ;
import java.util.Date ;

import com.cronutils.descriptor.CronDescriptor ;
import com.cronutils.model.Cron ;
import com.cronutils.model.definition.CronDefinition ;
import com.cronutils.model.definition.CronDefinitionBuilder ;
import com.cronutils.model.time.ExecutionTime ;
import com.cronutils.parser.CronParser ;
import com.sandy.capitalyst.util.Utils ;

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
                .instance();
        
        CronParser parser = new CronParser( cronDefinition ) ;
        Cron cron = parser.parse( "1 5 * 2019" ) ;
        ExecutionTime execTime = ExecutionTime.forCron( cron ) ;
        
        Date date = Utils.parseDate( "1/6/2019" ) ;
        ZonedDateTime dt = ZonedDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() ) ;
        System.out.println( execTime.isMatch( dt ) ) ;

        CronDescriptor descriptor = CronDescriptor.instance() ;
        System.out.println( descriptor.describe( cron ) ) ;
    }
    
    public static void main( String[] args ) {
        CronExprPOC poc = new CronExprPOC() ;
        poc.basicPOC() ;
    }
}
