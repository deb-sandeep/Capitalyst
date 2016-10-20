package com.sandy.capitalyst.txgen;

import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;

public class FixedAmtScheduledTxnGen extends ScheduledTxnGen {
    
    static Logger log = Logger.getLogger( FixedAmtScheduledTxnGen.class ) ;

    @Cfg private double amount = 0 ;
    @Cfg private String accountNumber = null ;
    
    public void setAmount( double amt ) {
        this.amount = amt ;
    }
    
    public void setAccountNumber( String acctNo ) {
        this.accountNumber = acctNo ;
    }
    
    @Override
    protected void generateScheduledTxnForDate( Date date, List<Txn> txnList ) {
        txnList.add( new Txn( accountNumber, amount, date, getName() ) ) ;
    }
}
