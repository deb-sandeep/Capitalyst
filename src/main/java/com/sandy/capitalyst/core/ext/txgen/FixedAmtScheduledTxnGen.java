package com.sandy.capitalyst.core.ext.txgen;

import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.core.Txn ;

public class FixedAmtScheduledTxnGen extends ScheduledTxnGen {

    private double amt = 0 ;
    private String acctNo = null ;
    
    public FixedAmtScheduledTxnGen( String scheduleExpression, 
                                    double amt, 
                                    String acctNo ) {
        super( scheduleExpression ) ;
        this.amt = amt ;
        this.acctNo = acctNo ;
    }
    
    @Override
    protected void generateScheduledTxnForDate( Date date, List<Txn> txnList ) {
        txnList.add( new Txn( acctNo, amt, date, getName() ) ) ;
    }
}
