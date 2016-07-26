package com.sandy.capitalyst.core.ext.txgen;

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.core.AbstractTxnGen ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.TxnGenerator ;

public class CompositeTxnGen extends AbstractTxnGen {
    
    private List<TxnGenerator> generators = new ArrayList<TxnGenerator>() ;
    
    public CompositeTxnGen( String name, TxnGenerator ... txGens ) {
        super( name ) ;
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
