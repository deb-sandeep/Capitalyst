package com.sandy.capitalyst.txgen;

import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;

public class FixedAmtScheduledTxnGen extends ScheduledTxnGen {

    @Cfg private double ammount = 0 ;
    @Cfg private String accountNumber = null ;
    
    public void setAmount( double amt ) {
        this.ammount = amt ;
    }
    
    public void setAccountNumber( String acctNo ) {
        this.accountNumber = acctNo ;
    }
    
    @Override
    protected void generateScheduledTxnForDate( Date date, List<Txn> txnList ) {
        txnList.add( new Txn( accountNumber, ammount, date, getName() ) ) ;
    }
}
