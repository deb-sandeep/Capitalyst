package com.sandy.capitalyst.account;

import java.util.Date ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.action.TransferFullAmtOnClosure ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.timeobservers.EndOfYearObserver ;
import com.sandy.capitalyst.util.Range ;
import com.sandy.capitalyst.util.Utils ;

public class DividendAccount extends BankAccount 
    implements EndOfDayObserver, EndOfYearObserver, PostConfigInitializable {
    
    @Cfg 
    private double roi = 0 ;
    
    @Cfg
    private Range divRoiRange = null ;
    
    @Cfg 
    private ExecutionTime dividendSchedule = null ;
    
    @Cfg
    private String parentAccountNumber = null ;
    
    @Cfg
    private Date maturityDate = null ;
    
    private Date lastPayoutDate = null ;
    
    public String getParentAccountNumber() {
        return parentAccountNumber ;
    }

    public void setParentAccountNumber( String acNo ) {
        this.parentAccountNumber = acNo ;
    }

    public Range getDivRoiRange() {
        return divRoiRange ;
    }

    public void setDivRoiRange( Range range ) {
        this.divRoiRange = range ;
    }

    public ExecutionTime getDividendSchedule() {
        return dividendSchedule ;
    }

    public void setDividendSchedule( ExecutionTime schedule ) {
        this.dividendSchedule = schedule ;
    }

    public Date getMaturityDate() {
        return maturityDate ;
    }

    public void setMaturityDate( Date maturityDate ) {
        this.maturityDate = maturityDate ;
    }
    
    public void setRoi( double roi ) {
        this.roi = roi ;
    }
    
    public double getRoi() {
        return this.roi ;
    }

    @Override
    public void initializePostConfig() {
        super.initializePostConfig() ;
        super.addClosureAction( new TransferFullAmtOnClosure( parentAccountNumber ) ) ;
        lastPayoutDate = getUniverse().now() ;
    }

    @Override
    public void handleEndOfDayEvent( Date date ) {
        if( super.isActive() ) {
            if( Utils.isMatch( dividendSchedule, date ) ) {
                payoutDividend( date ) ;
            }
            
            if( Utils.isSame( date, maturityDate ) ) {
                super.closeAccount( date ) ;
            }
        }
    }
    
    private void payoutDividend( Date date ) {
        
        double roi = divRoiRange.getRandom() ;
        int numDays = Utils.getNumDaysBetween( lastPayoutDate, date ) ;
        double interest = super.getAmount()*(roi/(100*360)) * numDays ;

        Txn txn = new Txn( parentAccountNumber, interest, date,
                           "Dividend payout for " + super.getName() ) ;
        getUniverse().postTransaction( txn ) ;
        
        lastPayoutDate = date ;
    }

    @Override
    public void handleEndOfYearEvent( Date date ) {
        if( super.isActive() ) {
            double growth = super.getAmount()*(roi/100) ;
            super.postAmount( date, growth, "Dividend fund growth" ) ;
        }
    }
}
