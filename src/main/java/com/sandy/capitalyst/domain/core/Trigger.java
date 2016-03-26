package com.sandy.capitalyst.domain.core;

import com.sandy.capitalyst.domain.core.Account.Entry ;

public abstract class Trigger {

    public boolean isTriggered( Account account, Entry entry ) {
        return false ;
    }
}
