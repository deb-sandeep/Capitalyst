package com.sandy.capitalyst.account;

import java.time.ZoneId ;
import java.time.ZonedDateTime ;
import java.util.Date ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.util.Utils ;

public abstract class AbstractFixedInvestmentAccount extends BankAccount 
    implements EndOfDayObserver {
    
    @Cfg 
    private double investmentAmount = 0 ;
    
    @Cfg 
    private Date investmentEndDate = null ;
    
    @Cfg 
    private ExecutionTime investmentSchedule = null ;
    
    @Cfg
    private String parentAccountNumber = null ;
    
    public double getInvestmentAmount() {
        return investmentAmount ;
    }

    public void setInvestmentAmount( double investmentAmount ) {
        this.investmentAmount = investmentAmount ;
    }

    public Date getInvestmentEndDate() {
        return investmentEndDate ;
    }

    public void setInvestmentEndDate( Date investmentEndDate ) {
        this.investmentEndDate = investmentEndDate ;
    }

    public ExecutionTime getInvestmentSchedule() {
        return investmentSchedule ;
    }

    public void setInvestmentSchedule( ExecutionTime investmentSchedule ) {
        this.investmentSchedule = investmentSchedule ;
    }
    
    public String getParentAccountNumber() {
        return parentAccountNumber ;
    }

    public void setParentAccountNumber( String parentAccountNumber ) {
        this.parentAccountNumber = parentAccountNumber ;
    }
    
    protected void checkAndMakeInvestmentTxn( Date date ) {
        if( Utils.isSameOrBefore( date, investmentEndDate ) ) {
            ZonedDateTime dt = null ;
            dt = ZonedDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() ) ;
            if( investmentSchedule.isMatch( dt ) ) {
                makeInvestmentTxn( date ) ;
            }
        }
    }
    
    protected void makeInvestmentTxn( Date date ) {
        
        Utils.transfer( investmentAmount, 
                        getUniverse().getAccount( parentAccountNumber ), 
                        getUniverse().getAccount( getAccountNumber() ), 
                        date, 
                        super.getName() );
    }
}
