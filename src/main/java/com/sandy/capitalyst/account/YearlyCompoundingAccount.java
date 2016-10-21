package com.sandy.capitalyst.account;

import java.util.Date ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.clock.EndOfYearObserver ;

public class YearlyCompoundingAccount extends PeriodicallyCompoundingAccount 
    implements EndOfYearObserver {
    
    static Logger log = Logger.getLogger( YearlyCompoundingAccount.class ) ;
    
    @Override
    public void handleEndOfYearEvent( Date date ) {
        if( !isAccountClosed ) {
            super.postAccumulatedInterest( date ) ;
        }
    }
}
