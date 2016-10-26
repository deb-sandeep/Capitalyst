package com.sandy.capitalyst.txgen;

import java.util.Date ;
import java.util.List ;

import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.UniverseConstituent ;

public interface TxnGenerator extends UniverseConstituent {
    
    public String getName() ;
    public String getClassifiers() ;
    public void getTransactionsForDate( Date date, List<Txn> txnList ) ;
}
