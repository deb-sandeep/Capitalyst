package com.sandy.capitalyst.account;

import java.util.Date ;

import org.joda.time.DateTime ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.util.Utils ;

public class FixedInvestmentFixedReturnAccount extends BankAccount 
    implements EndOfDayObserver {
    
    @Cfg 
    private double investmentAmount = 0 ;
    
    @Cfg 
    private double maturityAmount = 0 ;
    
    @Cfg 
    private Date investmentEndDate = null ;
    
    @Cfg( mandatory=false )
    private Date maturityDate = null ;
    
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

    public double getMaturityAmount() {
        return maturityAmount ;
    }

    public void setMaturityAmount( double amt ) {
        this.maturityAmount = amt ;
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
    
    public Date getMaturityDate() {
        if( maturityDate != null ) {
            return maturityDate ;
        }
        return this.investmentEndDate ;
    }

    public void setMaturityDate( Date maturityDate ) {
        this.maturityDate = maturityDate ;
    }
    
    @Override
    public void handleEndOfDayEvent( Date date ) {
        
        if( isActive() ) {
            if( Utils.isSame( date, getMaturityDate() ) ) {
                makeMaturityPaymentTxn( date ) ;
                super.closeAccount( date ) ;
            }
            else {
                if( Utils.isBefore( date, investmentEndDate ) ) {
                    if( investmentSchedule.isMatch( new DateTime( date.getTime() ) ) ) {
                        makeInvestmentTxn( date ) ;
                    }
                }
            }
        }
    }

    private void makeInvestmentTxn( Date date ) {
        
        Utils.transfer( investmentAmount, 
                        getUniverse().getAccount( parentAccountNumber ), 
                        getUniverse().getAccount( getAccountNumber() ), 
                        date, 
                        super.getName() );
    }

    private void makeMaturityPaymentTxn( Date date ) {
        
        Txn txn = new Txn( parentAccountNumber, maturityAmount, date, 
                           "Maturity amount for " + getName() ) ;
        getUniverse().postTransaction( txn ) ;
    }
}
