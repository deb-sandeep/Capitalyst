package com.sandy.capitalyst.domain.core;

import com.sandy.capitalyst.domain.core.Account.Entry ;

public abstract class AccountAction {

    public boolean canExecute( Account account, Entry entry ) {
        return true ;
    }

    public abstract void execute( boolean preUpdate, Account account, Entry entry ) ;
}
