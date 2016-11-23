package com.sandy.capitalyst.account;

import java.util.Date ;

import com.sandy.capitalyst.action.TransferFullAmtOnClosure ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.timeobservers.EndOfQuarterObserver ;
import com.sandy.capitalyst.util.Utils ;

public class SavingAccount extends BankAccount 
    implements EndOfDayObserver, EndOfQuarterObserver {
    
    private double accumulatedInterest  = 0 ;
    
    @Cfg( mandatory=false )
    private Date closingDate = null ;
    
    @Cfg( mandatory=false )
    private String closingFundTfrAc = null ;
    
    @Cfg 
    private double roi = 0 ;
    
    public void setClosingDate( Date date ) {
        this.closingDate = date ;
    }
    
    public Date getClosingDate() {
        return this.closingDate ;
    }
    
    public String getClosingFundTfrAc() {
        return closingFundTfrAc ;
    }

    public void setClosingFundTfrAc( String closingFundTfrAc ) {
        this.closingFundTfrAc = closingFundTfrAc ;
        super.addClosureAction( new TransferFullAmtOnClosure( closingFundTfrAc ) ) ;
    }

    public void setRoi( double roi ) {
        this.roi = roi ;
    }
    
    public double getRoi() {
        return this.roi ;
    }
    
    @Override
    public void handleEndOfDayEvent( Date date ) {
        if( isActive() ) {
            double dailyInterest = getAmount() * (roi/(100*365)) ;
            accumulatedInterest += dailyInterest ;
            if( closingDate != null ) {
                if( Utils.isSame( date, closingDate ) ) {
                    closeAccount( date ) ;
                }
            }
        }
    }

    @Override
    public void handleEndOfQuarterEvent( Date date ) {
        postAccumulatedInterest( date ) ;
    }

    @Override
    public void closeAccount( Date date ) {
        postAccumulatedInterest( date ) ;
        super.closeAccount( date ) ;
    }
    
    private void postAccumulatedInterest( Date date ) {
        if( accumulatedInterest > 0 ) {
            Txn txn = new Txn( getAccountNumber(), accumulatedInterest, date,
                               "SB Interest" ) ;
            txn.setTaxable( true ) ;
            txn.setTaxableAmount( accumulatedInterest ) ;
            
            super.getUniverse().postTransaction( txn ) ;
            accumulatedInterest = 0 ;
        }
    }
}
