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
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.TxnGenerator ;
import com.sandy.capitalyst.core.Universe ;

public abstract class ScheduledTxnGen implements TxnGenerator {

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
    
    public ScheduledTxnGen( String scheduleExpr ) {
        
        cron = cronParser.parse( scheduleExpr ) ;
        execTime = ExecutionTime.forCron( cron ) ;
        
        scheduleDesc = CronDescriptor.instance().describe( cron ) ;
        scheduleDesc = scheduleDesc.replace( "every minute ", "" ) ;
    }
    
    public String getScheduleDescription() {
        return this.scheduleDesc ;
    }
    
    @Override
    public final void getTransactionsForDate( Date date, List<Txn> txnList, Universe u ) {
        
        DateTime dateTime = new DateTime( date.getTime() ) ;
        if( execTime.isMatch( dateTime ) ) {
            generateScheduledTxnForDate( date, txnList, u ) ;
        }
    }
    
    protected abstract void generateScheduledTxnForDate( Date date, 
                                                         List<Txn> txnList,
                                                         Universe u ) ;
}
