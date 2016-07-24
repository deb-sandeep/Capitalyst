package com.sandy.capitalyst ;

import static com.sandy.capitalyst.Utils.parseDate ;

import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import org.apache.commons.lang.time.DateUtils ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.CapitalystTimer ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.TimedTxnGenerator ;
import com.sandy.capitalyst.core.Universe ;

public class Test {
    
    private CapitalystTimer timer = null ;
    
    public void testUniverse() throws Exception {
        Universe universe = new Universe() ;
        CapitalystTimer timer = getTimer() ;
        
        universe.addAccount( new Account( "5212", "Sandy SB" ) ) ;
        universe.registerTimedTxnGenerator( new TimedTxnGenerator() {
            public void getTransactionsForDate( Date date, List<Txn> txnList ) {
                if( DateUtils.getFragmentInDays( date, Calendar.MONTH ) == 1 ) {
                    txnList.add( new Txn( "5212", 100, date ) ) ;
                }
            }
        } );
        
        timer.registerTimeObserver( universe ) ;
        timer.run() ;
        
        Account acc = universe.getAccount( "5212" ) ;
        System.out.println( acc.getAmount() ) ;
    }
    
    private CapitalystTimer getTimer() {
        if( timer == null ) {
            timer = new CapitalystTimer( parseDate( "01/01/2015" ), 
                                         parseDate( "31/12/2015" ) ) ;
        }
        return timer ;
    }
    
    public static void main( String[] args ) throws Exception {
        new Test().testUniverse() ;
    }
}
