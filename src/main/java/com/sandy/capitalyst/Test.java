package com.sandy.capitalyst ;

import static com.sandy.capitalyst.CapitalystUtils.parseDate ;

import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import org.apache.commons.lang.time.DateUtils ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.CapitalystTimer ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.TxnGenerator ;
import com.sandy.capitalyst.core.Universe ;

public class Test {
    
    private CapitalystTimer timer = null ;
    
    public void testUniverse() {
        Universe universe = new Universe() ;
        CapitalystTimer timer = getTimer() ;
        
        universe.addAccount( new Account( "5212", "Sandy SB" ) ) ;
        universe.registerTxnGenerator( new TxnGenerator() {
            public List<Txn> getTransactionsForDate( Date date ) {
                
                if( DateUtils.getFragmentInDays( date, Calendar.MONTH ) == 1 ) {
                    List<Txn> txnList = new ArrayList<Txn>() ;
                    Txn txn = new Txn( "5212", 100, date ) ;
                    txnList.add( txn ) ;
                    return txnList ;
                }
                return null ;
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
