package com.sandy.capitalyst.account;

import java.util.Date ;

import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;

public class FixedInvestmentFixedAnnuityAccount extends BankAccount 
    implements EndOfDayObserver {
    
    @Override
    public void handleEndOfDayEvent( Date date ) {
    }
}
