package com.sandy.capitalyst.action;

import java.util.Date ;

import com.sandy.capitalyst.account.Account ;

public interface AccountClosureAction {

    public void execute( Account account, Date date ) ;
}
