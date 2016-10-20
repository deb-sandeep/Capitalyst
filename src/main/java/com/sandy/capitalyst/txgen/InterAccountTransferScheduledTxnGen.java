package com.sandy.capitalyst.txgen;

import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.exception.AccountOverdraftException ;

public class InterAccountTransferScheduledTxnGen extends ScheduledTxnGen {

    @Cfg
    private double ammount = 0 ;
    
    @Cfg
    private String creditAccountNumber = null ;
    
    @Cfg
    private String debitAccountNumber = null ;
    
    @Cfg( mandatory=false )
    private String description = "" ;
    
    @Cfg( mandatory=false )
    private boolean allowOverdraft = false ;
    
    public void setAmmount( double amt ) {
        this.ammount = amt ;
    }
    
    public void setCreditAccountNumber( String acctNo ) {
        this.creditAccountNumber = acctNo ;
    }
    
    public void setDebitAccountNumber( String acctNo ) {
        this.debitAccountNumber = acctNo ;
    }
    
    public void setAllowOverdraft( boolean allowOverdraft ) {
        this.allowOverdraft = allowOverdraft ;
    }
    
    public void setDesciption( String descr ) {
        this.description = descr ;
    }
    
    @Override
    protected void generateScheduledTxnForDate( Date date, List<Txn> txnList ) {
        
        Account creditAcct = getUniverse().getAccount( creditAccountNumber ) ;
        if( !allowOverdraft && ( creditAcct.getLiquidableAmount() < ammount ) ) {
            throw new AccountOverdraftException( creditAccountNumber ) ;
        }
        
        txnList.add( new Txn( creditAccountNumber, -ammount, date, 
                              "Transfer to A/C " + debitAccountNumber + " " + description) ) ;
        txnList.add( new Txn( debitAccountNumber, ammount, date, 
                              "Transfer from A/C " + creditAccountNumber + " " + description ) ) ;
    }
}