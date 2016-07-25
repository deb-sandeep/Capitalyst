package com.sandy.capitalyst.core;

import java.util.Date ;

public class PDTxn extends Txn {

    public PDTxn( String accountNumber, double amount, Date date, 
                  String description ) {
        super( accountNumber, amount, date, description ) ;
    }
}
