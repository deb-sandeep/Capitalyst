package com.sandy.capitalyst.account;

import com.sandy.capitalyst.core.Txn ;

public interface AccountListener {
    public void txnPosted( Txn txn, Account account ) ;
}
