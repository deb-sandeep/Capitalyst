package com.sandy.capitalyst.account;

import java.util.Date ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.clock.EndOfQuarterObserver ;

public class QuarterlyCompoundingAccount extends PeriodicallyCompoundingAccount 
    implements EndOfQuarterObserver {
    
    static Logger log = Logger.getLogger( QuarterlyCompoundingAccount.class ) ;
    
    @Override
    public void handleEndOfQuarterEvent( Date date ) {
        if( !isAccountClosed ) {
            super.postAccumulatedInterest( date ) ;
        }
    }
}
