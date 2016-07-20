package com.sandy.cst.domain;

import java.util.Date ;
import java.util.List ;

public interface TxnGenerator {

    public List<Txn> getTransactionsForDate( Date date ) ;
}
