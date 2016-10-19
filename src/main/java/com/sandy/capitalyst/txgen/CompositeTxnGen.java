package com.sandy.capitalyst.txgen;

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.core.Txn ;

public class CompositeTxnGen extends AbstractTxnGen {
    
    private List<TxnGenerator> generators = new ArrayList<TxnGenerator>() ;
    
    public CompositeTxnGen( TxnGenerator ... txGens ) {
        super() ;
        if( txGens != null ) {
            generators = Arrays.asList( txGens ) ;
        }
    }
    
    public void addTxGenerator( TxnGenerator txGen ) {
        generators.add( txGen ) ;
    }

    @Override
    public void getTransactionsForDate( Date date, List<Txn> txnList ) {
        for( TxnGenerator txGen : generators ) {
            txGen.getTransactionsForDate( date, txnList ) ;
        }
    }
}
