package com.sandy.capitalyst.txgen;

import java.util.Date ;
import java.util.List ;

import org.joda.time.DateTime ;

import com.cronutils.descriptor.CronDescriptor ;
import com.cronutils.model.Cron ;
import com.cronutils.model.definition.CronDefinition ;
import com.cronutils.model.definition.CronDefinitionBuilder ;
import com.cronutils.model.time.ExecutionTime ;
import com.cronutils.parser.CronParser ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.util.Utils ;

public abstract class ScheduledTxnGen 
    extends AbstractTxnGen 
    implements PostConfigInitializable {

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
    
    @Cfg 
    private String scheduleExpr = null ;
    
    @Cfg( mandatory=false ) 
    private Date startDate = null ;
    
    @Cfg( mandatory=false ) 
    private Date endDate = null ;
    
    public void setScheduleExpr( String expr ) {
        this.scheduleExpr = expr ;
    }
    
    public void setStartDate( Date date ) {
        this.startDate = date ;
    }
    
    public void setEndDate( Date date ) {
        this.endDate = date ;
    }
    
    @Override
    public void initializePostConfig() {

        cron = cronParser.parse( scheduleExpr ) ;
        execTime = ExecutionTime.forCron( cron ) ;
        
        scheduleDesc = CronDescriptor.instance().describe( cron ) ;
        scheduleDesc = scheduleDesc.replace( "every minute ", "" ) ;
    }

    public String getScheduleDescription() {
        return this.scheduleDesc ;
    }
    
    @Override
    public final void getTransactionsForDate( Date date, List<Txn> txnList ) {
        
        if( startDate != null && Utils.isBefore( date, startDate ) ) {
            return ;
        }
        
        if( endDate != null && Utils.isAfter( date, endDate ) ) {
            return ;
        }
        
        DateTime dateTime = new DateTime( date.getTime() ) ;
        if( execTime.isMatch( dateTime ) ) {
            generateScheduledTxnForDate( date, txnList ) ;
        }
    }
    
    protected abstract void generateScheduledTxnForDate( Date date, 
                                                         List<Txn> txnList ) ;
}
