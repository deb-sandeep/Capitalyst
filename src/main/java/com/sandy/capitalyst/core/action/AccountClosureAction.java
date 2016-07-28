package com.sandy.capitalyst.core.action;

import java.util.Date ;

import com.sandy.capitalyst.core.Account ;

public interface AccountClosureAction {

    public void execute( Account account, Date date ) ;
}
