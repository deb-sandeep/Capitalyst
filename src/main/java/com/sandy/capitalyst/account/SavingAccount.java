package com.sandy.capitalyst.account;

import java.util.Date ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.timeobservers.EndOfQuarterObserver ;

public class SavingAccount extends BankAccount 
    implements EndOfDayObserver, EndOfQuarterObserver {
    
    private double accumulatedInterest  = 0 ;
    
    @Cfg 
    private double roi = 0 ;
    
    public void setRoi( double roi ) {
        this.roi = roi ;
    }
    
    public double getRoi() {
        return this.roi ;
    }
    
    @Override
    public void handleEndOfDayEvent( Date date ) {

        double dailyInterest = getAmount() * (roi/(100*365)) ;
        accumulatedInterest += dailyInterest ;
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
            
            super.getUniverse().postTransaction( txn ) ;
            accumulatedInterest = 0 ;
        }
    }
}
