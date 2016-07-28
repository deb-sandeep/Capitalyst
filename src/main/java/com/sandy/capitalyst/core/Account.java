package com.sandy.capitalyst.core;

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Date ;
import java.util.Iterator ;
import java.util.List ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.action.AccountClosureAction ;
import com.sandy.capitalyst.core.timeobserver.DayObserver ;

public class Account extends AbstractTxnGen implements DayObserver {
    
    static final Logger log = Logger.getLogger( Account.class ) ;

    private Universe universe = null ;
    private String accountNumber ;
    private String name ;
    
    protected double amount ;
    
    private List<Txn> ledger        = new ArrayList<Txn>() ;
    private List<Txn> postDatedTxns = new ArrayList<Txn>() ;
    private List<AccountClosureAction> closureActions = new ArrayList<>() ;
    
    public Account( String id, String name, 
                    AccountClosureAction... closeActions ) {
        this( id, name, 0, closeActions ) ;
    }
    
    public Account( String accNo, String name, double amount, 
                    AccountClosureAction... closeActions ) {
        super( name ) ;
        this.accountNumber = accNo ;
        this.name = name ;
        this.amount = amount ;
        
        if( closeActions != null ) {
            closureActions.addAll( Arrays.asList( closeActions ) ) ;
        }
    }

    public void setUniverse( Universe universe ) {
        this.universe = universe ;
    }
    
    public Universe getUniverse() {
        return this.universe ;
    }
    
    public void addClosureAction( AccountClosureAction action ) {
        this.closureActions.add( action ) ;
    }
    
    public String getAccountNumber() {
        return accountNumber ;
    }

    public String getName() {
        return name ;
    }

    public double getAmount() {
        return amount ;
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
