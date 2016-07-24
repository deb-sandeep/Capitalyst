package com.sandy.capitalyst.core;

import java.util.Date ;
import java.util.List ;

public interface TxnGenerator {
    
    public void getTransactionsForDate( Date date, List<Txn> txnList ) ;
}
