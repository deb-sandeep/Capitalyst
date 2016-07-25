package com.sandy.capitalyst ;

import static com.sandy.capitalyst.util.Utils.parseDate ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.CapitalystTimer ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.ext.account.SavingAccount ;
import com.sandy.capitalyst.core.ext.txgen.FixedAmtScheduledTxnGen ;
import com.sandy.capitalyst.core.ext.txgen.InterAccountTransferScheduledTxnGen ;
import com.sandy.capitalyst.util.Utils ;

public class Test {
    
    private CapitalystTimer timer = null ;
    
    public void testUniverse() throws Exception {
        Universe universe = new Universe() ;
        CapitalystTimer timer = getTimer() ;
        
        universe.addAccount( new SavingAccount( "5212", "Sandy SB", 1000, 4 ) ) ;
        universe.addAccount( new Account( "NPS",  "Sandy NPS" ) ) ;
        universe.registerTxnGenerator( 
                new FixedAmtScheduledTxnGen( "L * * *", 1000, "5212", "Salary" ) ) ;
        universe.registerTxnGenerator( 
                new InterAccountTransferScheduledTxnGen( "7 * * *", 500, "5212", "NPS" ) ) ;
        
        timer.registerTimeObserver( universe ) ;
        timer.run() ;
        
        System.out.println( Utils.printLedger( universe.getAccount( "5212" ) ) ) ;
        System.out.println( Utils.printLedger( universe.getAccount( "NPS" ) ) ) ;
    }
    
    private CapitalystTimer getTimer() {
        if( timer == null ) {
            timer = new CapitalystTimer( parseDate( "01/01/2015" ), 
                                         parseDate( "01/01/2016" ) ) ;
        }
        return timer ;
    }
    
    public static void main( String[] args ) throws Exception {
        new Test().testUniverse() ;
    }
}
