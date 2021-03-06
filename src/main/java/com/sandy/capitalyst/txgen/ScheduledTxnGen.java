package com.sandy.capitalyst.txgen;

import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.amount.Amount ;
import com.sandy.capitalyst.core.exception.AccountNotFoundException ;
import com.sandy.capitalyst.util.Utils ;

public class ScheduledTxnGen extends AbstractTxnGen {
    
    private static Logger log = Logger.getLogger( ScheduledTxnGen.class ) ;
    
    @Cfg
    private Amount amount = null ;
    
    @Cfg
    private ExecutionTime executionTime = null ;
    
    @Cfg( mandatory=false )
    private String debitACNo = null ;
    
    @Cfg( mandatory=false )
    private String creditACNo = null ;

    @Cfg( mandatory=false )
    private Date startDate = null ;

    @Cfg( mandatory=false )
    private Date endDate = null ;

    @Cfg( mandatory=false )
    private String description = null ;
    
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
    
    @Override
    public final void getTransactionsForDate( Date date, List<Txn> txnList ) {
        if( this.isValidFor( date ) ) {
            createTxn( date, txnList ) ;
        }
    }

    private boolean isValidFor( Date date ) {
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
            return Utils.isMatch( executionTime, date ) ;
        }
        catch( Exception e ) {
            log.error( "Error in cron matching.", e );
            throw e ;
        }
    }

    private void createTxn( Date date, List<Txn> txnList ) {

        if( creditACNo == null && debitACNo == null ) {
            throw new AccountNotFoundException( "creditAccount and debitAccount "
                                                + "can't both be unspecified." ) ;
        }
        
        double amt = amount.getAmount() ;
        
        if( debitACNo != null ) {
            Txn debitTxn  = new Txn( debitACNo, -amt, date, 
                                     "Transfer for " + getDescription() ) ;
            txnList.add( debitTxn ) ;
        }
        
        if( creditACNo != null ) {
            Txn creditTxn = new Txn( creditACNo, amt, date, 
                                     "Transfer to "  + getDescription() ) ;
            txnList.add( creditTxn ) ;
        }
    }
}
