package com.sandy.capitalyst.account;

import java.util.Date ;

import org.apache.log4j.Logger ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.amount.Amount ;
import com.sandy.capitalyst.util.RDCurrentValueComputer ;
import com.sandy.capitalyst.util.Utils ;

public class RecurringDepositAccount extends QuarterlyCompoundingAccount {
    
    static Logger log = Logger.getLogger( RecurringDepositAccount.class ) ;
    
    @Cfg
    private Amount investmentAmount = null ;
    
    @Cfg
    private ExecutionTime investmentSchedule = null ;
    
    public RecurringDepositAccount() {
        super.setInterestTaxable( true ) ;
    }

    public Amount getInvestmentAmount() {
        return investmentAmount ;
    }

    public void setInvestmentAmount( Amount amt ) {
        this.investmentAmount = amt ;
    }

    public ExecutionTime getInvestmentSchedule() {
        return investmentSchedule ;
    }

    public void setInvestmentSchedule( ExecutionTime schedule ) {
        this.investmentSchedule = schedule ;
    }
    
    @Override
    public void handleEndOfDayEvent( Date date ) {
        super.handleEndOfDayEvent( date ) ;
        if( isActive() ) {
            if( Utils.isMatch( investmentSchedule, date ) ) {
                Utils.transfer( investmentAmount.getAmount(), 
                                getUniverse().getAccount( getParentAccountNumber() ), 
                                this, date, null ) ;
            }
        }
    }

    @Override
    public void initializePostConfig() {
        super.initializePostConfig() ;
        
        RDCurrentValueComputer computer = new RDCurrentValueComputer( 
                investmentAmount.getAmount(), super.getRoi(), 
                investmentSchedule, super.getOpeningDate(), 
                getUniverse().now() ) ;
        computer.compute() ;
        
        double currentVal = computer.getValue() ;
        double currentPrn = computer.getPrincipal() ;
        double currentInt = computer.getInterest() ;

        if( currentVal > 0 ) {
            postCurrentValue( currentPrn, currentInt ) ;
        }
    }
    
    private void postCurrentValue( double principal, double interest ) {
    
        Txn prnTxn = new Txn( getAccountNumber(), principal, getUniverse().now(),
                              "Accumulated principal" ) ;
        
        Txn intTxn = new Txn( getAccountNumber(), interest, getUniverse().now(),
                              "Accumulated interest" ) ;
        intTxn.setTaxable( true ) ;
        intTxn.setTaxableAmount( interest ) ;
        
        postTransaction( prnTxn ) ;
        postTransaction( intTxn ) ;
    }
    
}
