package com.sandy.capitalyst.core.ext.txgen;

import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.exception.AccountOverdraftException ;

public class InterAccountTransferScheduledTxnGen extends ScheduledTxnGen {

    private double amt          = 0 ;
    private String creditAcctNo = null ;
    private String debitAcctNo  = null ;
    private String description  = null ;
    private boolean allowOverdraft = false ;
    
    public InterAccountTransferScheduledTxnGen( String name,
                                                String scheduleExpr, 
                                                double amt,
                                                String creditAcctNo, 
                                                String debitAcctNo ) {
        this( name, scheduleExpr, amt, creditAcctNo, debitAcctNo, "" ) ;
    }

    public InterAccountTransferScheduledTxnGen( String name,
                                                String scheduleExpression, 
                                                double amt, 
                                                String creditAcctNo,
                                                String debitAcctNo,
                                                String description ) {
        super( name, scheduleExpression ) ;
        this.amt = amt ;
        this.creditAcctNo = creditAcctNo ;
        this.debitAcctNo  = debitAcctNo ;
        this.description  = description ;
    }
    
    public boolean isOverdraftAllowed() {
        return this.allowOverdraft ;
    }
    
    public void allowOverdraft( boolean allow ) {
        this.allowOverdraft = allow ;
    }
    
    @Override
    protected void generateScheduledTxnForDate( Date date, List<Txn> txnList ) {
        
        Account creditAcct = getUniverse().getAccount( creditAcctNo ) ;
        if( !allowOverdraft && ( creditAcct.getLiquidableAmount() < amt ) ) {
            throw new AccountOverdraftException( creditAcctNo ) ;
        }
        
        txnList.add( new Txn( creditAcctNo, -amt, date, 
                              "Transfer to A/C " + debitAcctNo + " " + description) ) ;
        txnList.add( new Txn( debitAcctNo, amt, date, 
                              "Transfer from A/C " + creditAcctNo + " " + description ) ) ;
    }
}
