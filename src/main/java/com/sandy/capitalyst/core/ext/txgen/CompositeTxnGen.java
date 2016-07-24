package com.sandy.capitalyst.core.ext.txgen;

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.core.TxnGenerator ;
import com.sandy.capitalyst.core.Txn ;

public class CompositeTxnGen implements TxnGenerator {
    
    private List<TxnGenerator> generators = new ArrayList<TxnGenerator>() ;
    
    public CompositeTxnGen( TxnGenerator ... txGens ) {
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
