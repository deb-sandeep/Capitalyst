package com.sandy.capitalyst.sample ;

import static com.sandy.capitalyst.util.Utils.parseDate ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.DayClock ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.action.TransferFullAmtOnClosure ;
import com.sandy.capitalyst.core.ext.account.QuarterlyCompoundingAccount ;
import com.sandy.capitalyst.core.ext.account.SavingAccount ;
import com.sandy.capitalyst.util.Utils ;

public class CompoundAccountSample {
    
    public void testCompoundAccount() throws Exception {
        
        DayClock timer = DayClock.instance() ;
        timer.setDateRange( parseDate( "01/01/2015" ), parseDate( "01/01/2017" ) ) ;
        
        
        Account saving = new SavingAccount( "0000", 0, 4 ) ;

        Account account = new QuarterlyCompoundingAccount( 
                                        "5212",
                                        100000, 
                                        parseDate( "28/02/2014" ), 
                                        parseDate( "14/11/2016" ),
                                        8.75, 
                                        new TransferFullAmtOnClosure( "0000" ) ) ;

        Universe universe = new Universe( "Test Universe" ) ;
        universe.addAccount( saving ) ;
        universe.addAccount( account ) ;
        
        timer.run() ;
        
        System.out.println( Utils.printLedger( account ) ) ;
        System.out.println( Utils.printLedger( saving ) ) ;
    }
    
    public static void main( String[] args ) throws Exception {
        new CompoundAccountSample().testCompoundAccount() ;
    }
}
