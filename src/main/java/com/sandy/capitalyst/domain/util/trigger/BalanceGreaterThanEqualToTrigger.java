package com.sandy.capitalyst.domain.util.trigger;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.Trigger ;

public class BalanceGreaterThanEqualToTrigger extends Trigger {
    
    double maxBalance = 0 ;
    
    public BalanceGreaterThanEqualToTrigger( double maxBalance ) {
        this.maxBalance = maxBalance ;
    }
    
    @Override
    public boolean isTriggered( Account account, Entry entry ) {
        return account.getAmount() >= maxBalance ;
    }
}
