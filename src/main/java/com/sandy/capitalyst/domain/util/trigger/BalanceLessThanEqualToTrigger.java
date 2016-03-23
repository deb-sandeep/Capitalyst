package com.sandy.capitalyst.domain.util.trigger;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.AccountTrigger ;

public class BalanceLessThanEqualToTrigger extends AccountTrigger {
    
    double minBalance = 0 ;
    
    public BalanceLessThanEqualToTrigger( double minBalance ) {
        this.minBalance = minBalance ;
    }
    
    @Override
    public boolean isTriggered( Account account, Entry entry ) {
        return account.getAmount() <= minBalance ;
    }
}
