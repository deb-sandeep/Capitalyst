package com.sandy.capitalyst.core.ext.account;

import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.TxnGenerator ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.ext.txgen.ScheduledTxnGen ;

public class SavingAccount extends BankAccount {
    
    private double accumulatedInterest  = 0 ;
    private double rateOfInterest       = 0 ;
    private TxnGenerator interestTxnGen = null ;

    public SavingAccount( String id, String name, double amt, double roi, 
                          String bankName ) {
        
        super( id, name, amt, bankName ) ;
        this.rateOfInterest = roi ;
        
        interestTxnGen = new ScheduledTxnGen( "SB interest generator",
                                              "L 3,6,9,12 * *" ) {
            @Override
            protected void generateScheduledTxnForDate( Date date, 
                                                        List<Txn> txnList,
                                                        Universe u ) {
                Txn txn = new Txn( SavingAccount.this.getAccountNumber(), 
                                   accumulatedInterest, date ) ;
                txn.setDescription( "SB Interest for quarter" ) ;
                txnList.add( txn ) ;
                accumulatedInterest = 0 ;
            }
        } ;
    }

    @Override
    public void getTxnForDate( Date date, List<Txn> txnList,
                               Universe universe ) {
        interestTxnGen.getTransactionsForDate( date, txnList, universe ) ;
    }

    @Override
    public void handleEndOfDayEvent( Date date, Universe universe ) {

        double dailyInterest = getAmount() * (rateOfInterest/(100*365)) ;
        accumulatedInterest += dailyInterest ;
    }
}
