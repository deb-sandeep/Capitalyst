package com.sandy.capitalyst.core.ext.account;

import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.TxnGenerator ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.ext.txgen.ScheduledTxnGen ;
import com.sandy.capitalyst.util.Utils ;

public class SavingAccount extends Account {
    
    private static final Logger log = Logger.getLogger( SavingAccount.class ) ;
    
    private double accumulatedInterest  = 0 ;
    private double rateOfInterest       = 0 ;
    private TxnGenerator interestTxnGen = null ;

    public SavingAccount( String id, String name, double amt, double roi ) {
        super( id, name, amt ) ;
        this.rateOfInterest = roi ;
        
        interestTxnGen = new ScheduledTxnGen( "L 3,6,9,12 * *" ) {
            @Override
            protected void generateScheduledTxnForDate( Date date, 
                                                        List<Txn> txnList,
                                                        Universe u ) {
                log.debug( Utils.formatDate( date ) ) ;
                
                Txn txn = new Txn( SavingAccount.this.getAccountNumber(), 
                                   accumulatedInterest, date ) ;
                txn.setDescription( "SB Interest for quarter" ) ;
                txnList.add( txn ) ;
                accumulatedInterest = 0 ;
            }
        } ;
    }

    @Override
    public void getTransactionsForDate( Date date, List<Txn> txnList,
                                        Universe universe ) {
        interestTxnGen.getTransactionsForDate( date, txnList, universe ) ;
    }

    @Override
    public void handleDayEvent( Date date, Universe universe ) {}

    @Override
    public void handleEndOfDayEvent( Date date, Universe universe ) {

        double dailyInterest = getAmount() * (rateOfInterest/(100*365)) ;
        accumulatedInterest += dailyInterest ;
    }
}
