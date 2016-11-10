package com.sandy.capitalyst.txgen;

import java.time.ZoneId ;
import java.time.ZonedDateTime ;
import java.util.Date ;

import org.apache.log4j.Logger ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.core.amount.Amount ;
import com.sandy.capitalyst.util.Utils ;

public class ScheduledTxnDef {
    
    private static Logger log = Logger.getLogger( ScheduledTxnDef.class ) ;

    private Amount        amount         = null ;
    private ExecutionTime executionTime  = null ;
    private String        debitACNo      = null ;
    private String        creditACNo     = null ;
    private Date          startDate      = null ;
    private Date          endDate        = null ;
    private String        description    = null ;
    
    public Amount getAmount() {
        return amount ;
    }
    
    public void setAmount( Amount amount ) {
        this.amount = amount ;
    }

    public ExecutionTime getExecutionTime() {
        return executionTime ;
    }

    public void setExecutionTime( ExecutionTime executionTime ) {
        this.executionTime = executionTime ;
    }

    public String getDebitACNo() {
        return debitACNo ;
    }
    
    public void setDebitACNo( String debitACNo ) {
        this.debitACNo = debitACNo ;
    }
    
    public String getCreditACNo() {
        return creditACNo ;
    }
    
    public void setCreditACNo( String creditACNo ) {
        this.creditACNo = creditACNo ;
    }
    
    public Date getStartDate() {
        return startDate ;
    }
    
    public void setStartDate( Date startDate ) {
        this.startDate = startDate ;
    }
    
    public Date getEndDate() {
        return endDate ;
    }
    
    public void setEndDate( Date endDate ) {
        this.endDate = endDate ;
    }
    
    public String getDescription() {
        return description ;
    }
    
    public void setDescription( String description ) {
        this.description = description ;
    }
    
    public boolean isValidFor( Date date ) {
        if( startDate != null ) {
            if( Utils.isBefore( date, startDate ) ) {
                return false ;
            }
        }
        
        if( endDate != null ) {
            if( Utils.isAfter( date, endDate ) ) {
                return false ;
            }
        }
        
        try {
            ZonedDateTime dt = null ;
            dt = ZonedDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() ) ;
            return executionTime.isMatch( dt ) ; 
        }
        catch( Exception e ) {
            log.error( "Error in cron matching.", e );
            throw e ;
        }
    }
}
