package com.sandy.capitalyst.core.ext.txgen;

import java.util.Date ;
import java.util.List ;

import org.joda.time.DateTime ;

import com.cronutils.descriptor.CronDescriptor ;
import com.cronutils.model.Cron ;
import com.cronutils.model.definition.CronDefinition ;
import com.cronutils.model.definition.CronDefinitionBuilder ;
import com.cronutils.model.time.ExecutionTime ;
import com.cronutils.parser.CronParser ;
import com.sandy.capitalyst.core.AbstractTxnGen ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.util.Utils ;

public abstract class ScheduledTxnGen extends AbstractTxnGen {

    private final CronDefinition cronDefinition =
                            CronDefinitionBuilder.defineCron()
                                .withDayOfMonth()
                                .supportsHash().supportsL().supportsW().and()
                                .withMonth().and()
                                .withDayOfWeek()
                                .withIntMapping(7, 0) 
                                .supportsHash().supportsL().supportsW().and()
                                .withYear().and()
                                .lastFieldOptional()
                                .instance() ;
    
    private CronParser    cronParser   = new CronParser( cronDefinition ) ;
    private Cron          cron         = null ;
    private ExecutionTime execTime     = null ;
    private String        scheduleDesc = null ;
    private Date          startDate    = null ;
    private Date          endDate      = null ;
    
    public ScheduledTxnGen( String scheduleExpr ) {
        this( scheduleExpr, null, null ) ;
    }
    
    public ScheduledTxnGen( String scheduleExpr, Date start, Date end ) {
        
        startDate = start ;
        endDate   = end ;
        
        cron = cronParser.parse( scheduleExpr ) ;
        execTime = ExecutionTime.forCron( cron ) ;
        
        scheduleDesc = CronDescriptor.instance().describe( cron ) ;
        scheduleDesc = scheduleDesc.replace( "every minute ", "" ) ;
    }
    
    public ScheduledTxnGen startBy( Date date ) {
        startDate = date ;
        return this ;
    }
    
    public ScheduledTxnGen endBy( Date date ) {
        endDate = date ;
        return this ;
    }
    
    public String getScheduleDescription() {
        return this.scheduleDesc ;
    }
    
    @Override
    public final void getTransactionsForDate( Date date, List<Txn> txnList, Universe u ) {
        
        if( startDate != null && Utils.isBefore( date, startDate ) ) {
            return ;
        }
        
        if( endDate != null && Utils.isAfter( date, endDate ) ) {
            return ;
        }
        
        DateTime dateTime = new DateTime( date.getTime() ) ;
        if( execTime.isMatch( dateTime ) ) {
            generateScheduledTxnForDate( date, txnList, u ) ;
        }
    }
    
    protected abstract void generateScheduledTxnForDate( Date date, 
                                                         List<Txn> txnList,
                                                         Universe u ) ;
}
