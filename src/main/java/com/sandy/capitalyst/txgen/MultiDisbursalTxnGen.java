package com.sandy.capitalyst.txgen;

import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.exception.AccountNotFoundException ;

public class MultiDisbursalTxnGen extends AbstractTxnGen {

    static final Logger log = Logger.getLogger( MultiDisbursalTxnGen.class ) ;
    
    @Cfg private ScheduledTxnDef[] txnDefs = null ;
    
    @Cfg( mandatory=false )
    private String defaultCreditAccount = null ;
    
    @Cfg( mandatory=false )
    private String defaultDebitAccount = null ;
    
    public ScheduledTxnDef[] getTxnDefs() {
        return txnDefs ;
    }

    public void setTxnDefs( ScheduledTxnDef[] txnDefs ) {
        this.txnDefs = txnDefs ;
    }
    
    public String getDefaultCreditAccount() {
        return defaultCreditAccount ;
    }

    public void setDefaultCreditAccount( String defaultCreditAccount ) {
        this.defaultCreditAccount = defaultCreditAccount ;
    }

    public String getDefaultDebitAccount() {
        return defaultDebitAccount ;
    }

    public void setDefaultDebitAccount( String defaultDebitAccount ) {
        this.defaultDebitAccount = defaultDebitAccount ;
    }

    @Override
    public void getTransactionsForDate( Date date, List<Txn> txnList ) {
        for( ScheduledTxnDef def : txnDefs ) {
            if( def.isValidFor( date ) ) {
                createTxn( def, date, txnList ) ;
            }
        }
    }
    
    private void createTxn( ScheduledTxnDef def, Date date, 
                            List<Txn> txnList ) {
        
        String creditAC = def.getCreditACNo() ;
        String debitAC  = def.getDebitACNo() ;
        
        if( creditAC == null ) {
            creditAC = defaultCreditAccount ;
        }
        
        if( debitAC == null ) {
            debitAC = defaultDebitAccount ;
        }
        
        if( creditAC == null ) {
            throw new AccountNotFoundException( "creditAccount" ) ;
        }
        
        if( debitAC == null ) {
            throw new AccountNotFoundException( "debitAccount" ) ;
        }
        
        double amt = def.getAmount().getAmount() ;
        
        Txn debitTxn  = new Txn( debitAC, -amt, date, "Transfer for " + def.getDescription() ) ;
        Txn creditTxn = new Txn( creditAC, amt, date, "Transfer to "  + def.getDescription() ) ;
        
        txnList.add( debitTxn ) ;
        txnList.add( creditTxn ) ;
    }
}
