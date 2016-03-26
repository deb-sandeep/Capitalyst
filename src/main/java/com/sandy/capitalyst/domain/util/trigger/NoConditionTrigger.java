package com.sandy.capitalyst.domain.util.trigger;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.Trigger ;

public class NoConditionTrigger extends Trigger {
    @Override
    public boolean isTriggered( Account account, Entry entry ) {
        return true ;
    }
}
