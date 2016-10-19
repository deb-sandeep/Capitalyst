package com.sandy.capitalyst.sample ;

import static com.sandy.capitalyst.util.Utils.parseDate ;

import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.account.SavingAccount ;
import com.sandy.capitalyst.clock.DayClock ;
import com.sandy.capitalyst.core.PDTxn ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.txgen.AbstractTxnGen ;
import com.sandy.capitalyst.txgen.FixedAmtScheduledTxnGen ;
import com.sandy.capitalyst.txgen.InterAccountTransferScheduledTxnGen ;
import com.sandy.capitalyst.txgen.TxnGenerator ;
import com.sandy.capitalyst.util.Utils ;

public class BasicWorkingSample {
    
    public void testUniverse() throws Exception {
//        
//        DayClock timer = DayClock.instance() ;
//        Universe universe = new Universe( "Test" ) ;
//
//        Account sbAccount = new SavingAccount( "5212", 1000, 4 ) ;
//        Account npsAccount= new Account( "NPS" ) ;
//        
//        AbstractTxnGen salary = new FixedAmtScheduledTxnGen( "L * * *", 1000, "5212" ) ;
//        salary.setName( "Salary" ) ;
//        
//        TxnGenerator interAccTrfr = new InterAccountTransferScheduledTxnGen( 
//                                           "7 * * *", 500, "5212", "NPS" ) ; 
//        
//        TxnGenerator pdTxnGen = new AbstractTxnGen() {
//            @Override
//            public void getTransactionsForDate( Date date, List<Txn> txnList ) {
//                if( Utils.isSame( date, Utils.parseDate( "30/06/2015" ) ) ) {
//                    txnList.add( new PDTxn( "5212", 5000, Utils.addDays( 5, date ), "PD Txn" ) ) ;
//                }
//            }
//        } ;
//        
//        universe.addAccount( sbAccount ) ;
//        universe.addAccount( npsAccount ) ;
//        
//        universe.registerTxnGenerator( salary ) ;
//        universe.registerTxnGenerator( interAccTrfr ) ;
//        universe.registerTxnGenerator( pdTxnGen ) ;
//        
//        timer.setDateRange( parseDate( "01/01/2015" ), parseDate( "01/3/2016" ) ) ;
//        timer.run() ;
//        
//        System.out.println( Utils.printLedger( universe.getAccount( "5212" ) ) ) ;
//        System.out.println( Utils.printLedger( universe.getAccount( "NPS" ) ) ) ;
    }
    
    public static void main( String[] args ) throws Exception {
        new BasicWorkingSample().testUniverse() ;
    }
}
