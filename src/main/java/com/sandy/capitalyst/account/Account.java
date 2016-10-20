package com.sandy.capitalyst.account;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.Iterator ;
import java.util.List ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.action.AccountClosureAction ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.clock.DayObserver ;
import com.sandy.capitalyst.core.PDTxn ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.txgen.AbstractTxnGen ;

public class Account extends AbstractTxnGen implements DayObserver {
    
    static final Logger log = Logger.getLogger( Account.class ) ;

    @Cfg private String accountNumber ;
    @Cfg( mandatory=false ) protected double amount = 0 ;
    
    private List<Txn> ledger        = new ArrayList<Txn>() ;
    private List<Txn> postDatedTxns = new ArrayList<Txn>() ;
    private List<AccountClosureAction> closureActions = new ArrayList<>() ;
    
    public Account() {
    }
    
    public void setAccountNumber( String accNo ) {
        this.accountNumber = accNo ;
    }
    
    public String getAccountNumber() {
        return accountNumber ;
    }
    
    public void setAmount( double amt ) {
        this.amount = amt ;
    }

    public double getAmount() {
        return amount ;
    }
    
    public void addClosureAction( AccountClosureAction action ) {
        this.closureActions.add( action ) ;
    }
    
    public double getLiquidableAmount() {
        return amount ;
    }
    
    public void postTransaction( Txn t ) {
        if( t instanceof PDTxn ) {
            postDatedTxns.add( t ) ;
        }
        else {
            ledger.add( t ) ;
            this.amount += t.getAmount() ;
        }
    }
    
    public boolean isActive() {
        return true ;
    }
    
    public List<Txn> getLedger() {
        return ledger ;
    }
    
    public void closeAccount( Date date ) {
        closureActions.forEach( a -> a.execute( this, date ) );
    }
    
    @Override
    public final void getTransactionsForDate( Date date, List<Txn> txnList ) {
        
        for( Iterator<Txn> txnIter = postDatedTxns.iterator(); txnIter.hasNext(); ) {
            Txn tx = txnIter.next() ;
            if( DateUtils.isSameDay( date, tx.getDate() ) ) {
                
                Txn newTx = new Txn( tx.getAccountNumber(), tx.getAmount(), 
                                     tx.getDate(), tx.getDescription() ) ;
                txnList.add( newTx ) ;
                txnIter.remove() ;
            }
        }
        
        getTxnForDate( date, txnList ) ;
    }
    
    public void getTxnForDate( Date date, List<Txn> txnList ) {}

    @Override
    public void handleDayEvent( Date date ) {}
}