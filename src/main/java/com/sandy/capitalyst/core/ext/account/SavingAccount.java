package com.sandy.capitalyst.core.ext.account;

import java.util.Date ;

import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.timeobserver.EndOfDayObserver ;
import com.sandy.capitalyst.core.timeobserver.EndOfQuarterObserver ;

public class SavingAccount extends BankAccount 
    implements EndOfDayObserver, EndOfQuarterObserver {
    
    private double accumulatedInterest  = 0 ;
    private double rateOfInterest       = 0 ;

    public SavingAccount( String accountNumber, double initialAmt, double roi ) {
        
        super( accountNumber, initialAmt ) ;
        this.rateOfInterest = roi ;
    }

    @Override
    public void handleEndOfDayEvent( Date date ) {

        double dailyInterest = getAmount() * (rateOfInterest/(100*365)) ;
        accumulatedInterest += dailyInterest ;
    }

    @Override
    public void handleEndOfQuarterEvent( Date date ) {
        
        if( accumulatedInterest > 0 ) {
            Txn txn = new Txn( getAccountNumber(), accumulatedInterest, date ) ;
            txn.setDescription( "SB Interest for quarter" ) ;
            
            super.getUniverse().postTransaction( txn ) ;
            accumulatedInterest = 0 ;
        }
    }
    
    public static SavingAccount create( SavingAccountConfig cfg ) {
        SavingAccount account = null ;
        
        account = new SavingAccount( cfg.getAccountNumber(), 
        		                     cfg.getInitialAmount(), 
        		                     cfg.getRoi() ) ;
        account.setName( cfg.getAccountName() ) ;
        account.setBankName( cfg.getBankName() ) ;
        return account ;
    }
}
