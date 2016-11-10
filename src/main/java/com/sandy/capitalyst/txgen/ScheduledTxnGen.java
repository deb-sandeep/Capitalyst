package com.sandy.capitalyst.txgen;

import java.time.ZoneId ;
import java.time.ZonedDateTime ;
import java.util.Date ;
import java.util.List ;

import com.cronutils.descriptor.CronDescriptor ;
import com.cronutils.model.Cron ;
import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.util.Utils ;

public abstract class ScheduledTxnGen 
    extends AbstractTxnGen 
    implements PostConfigInitializable {

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
    
    public String getScheduleExpr() {
        return this.scheduleExpr ;
    }
    
    public void setStartDate( Date date ) {
        this.startDate = date ;
    }
    
    public Date getStartDate() {
        return this.startDate ;
    }
    
    public void setEndDate( Date date ) {
        this.endDate = date ;
    }
    
    public Date getEndDate() {
        return this.endDate ;
    }
    
    @Override
    public void initializePostConfig() {

        cron = Utils.CRON_PARSER.parse( scheduleExpr ) ;
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
        
        ZonedDateTime dt = null ;
        dt = ZonedDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() ) ;
        if( execTime.isMatch( dt ) ) {
            generateScheduledTxnForDate( date, txnList ) ;
        }
    }
    
    protected abstract void generateScheduledTxnForDate( Date date, 
                                                         List<Txn> txnList ) ;
}
