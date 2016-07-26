package com.sandy.capitalyst.sample ;

import static com.sandy.capitalyst.util.Utils.parseDate ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.DayClock ;
import com.sandy.capitalyst.core.ext.account.QuarterlyCompoundingAccount ;
import com.sandy.capitalyst.util.Utils ;

public class CompoundAccountSample {
    
    public void testCompoundAccount() throws Exception {
        
        DayClock timer = DayClock.instance() ;
        timer.setDateRange( parseDate( "01/01/2015" ), parseDate( "01/01/2018" ) ) ;

        Account account = new QuarterlyCompoundingAccount( 
                                        "5212", "Sandy SB", 1000, 4, "ICICI" ) ;
        
        timer.registerTimeObserver( account ) ;
        timer.run() ;
        
        System.out.println( Utils.printLedger( account ) ) ;
    }
    
    public static void main( String[] args ) throws Exception {
        new CompoundAccountSample().testCompoundAccount() ;
    }
}
