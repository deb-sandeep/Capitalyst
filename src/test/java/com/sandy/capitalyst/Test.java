package com.sandy.capitalyst ;

import static com.sandy.capitalyst.util.Utils.parseDate ;

import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.core.AbstractTxnGen ;
import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.DayClock ;
import com.sandy.capitalyst.core.PDTxn ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.ext.account.SavingAccount ;
import com.sandy.capitalyst.core.ext.txgen.FixedAmtScheduledTxnGen ;
import com.sandy.capitalyst.core.ext.txgen.InterAccountTransferScheduledTxnGen ;
import com.sandy.capitalyst.util.Utils ;

public class Test {
    
    public void testUniverse() throws Exception {
        
        Universe universe = new Universe( "Test" ) ;
        DayClock timer = DayClock.instance() ;
        
        universe.addAccount( new SavingAccount( "5212", "Sandy SB", 1000, 4, "ICICI" ) ) ;
        universe.addAccount( new Account( "NPS",  "Sandy NPS" ) ) ;
        universe.registerTxnGenerator( 
                new FixedAmtScheduledTxnGen( "Salary gen", "L * * *", 1000, "5212", "Salary" ) ) ;
        universe.registerTxnGenerator( 
                new InterAccountTransferScheduledTxnGen( "NPS withdraw", "7 * * *", 500, "5212", "NPS" ) ) ;
        universe.registerTxnGenerator( new AbstractTxnGen( "PD Txn GEn") {
            
            @Override
            public void getTransactionsForDate( Date date, List<Txn> txnList ) {
                if( Utils.isSame( date, Utils.parseDate( "30/06/2015" ) ) ) {
                    txnList.add( new PDTxn( "5212", 5000, Utils.addDays( 5, date ), "PD Txn" ) ) ;
                }
            }
        } ) ;
        
        timer.setDateRange( parseDate( "01/01/2015" ), parseDate( "01/3/2016" ) ) ;
        timer.run() ;
        
        System.out.println( Utils.printLedger( universe.getAccount( "5212" ) ) ) ;
        System.out.println( Utils.printLedger( universe.getAccount( "NPS" ) ) ) ;
    }
    
    public static void main( String[] args ) throws Exception {
        new Test().testUniverse() ;
    }
}
