package com.sandy.capitalyst.domain.core;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import com.sandy.capitalyst.domain.core.Account.AccountListener ;
import com.sandy.capitalyst.domain.core.Account.Entry ;

class TriggerActionManager implements AccountListener {
    
    private Map<AccountTrigger, List<AccountAction>> preCreditTriggerMap = 
                            new HashMap<AccountTrigger, List<AccountAction>>() ;
    
    private Map<AccountTrigger, List<AccountAction>> postCreditTriggerMap = 
                            new HashMap<AccountTrigger, List<AccountAction>>() ;
    
    private Map<AccountTrigger, List<AccountAction>> preDebitTriggerMap = 
            new HashMap<AccountTrigger, List<AccountAction>>() ;
    
    private Map<AccountTrigger, List<AccountAction>> postDebitTriggerMap = 
            new HashMap<AccountTrigger, List<AccountAction>>() ;
    
    public void registerPreCreditTrigger( AccountTrigger trigger, 
                                          AccountAction action ) {
        registerTrigger( preCreditTriggerMap, trigger, action ) ;
    }
    
    public void registerPostCreditTrigger( AccountTrigger trigger, 
                                           AccountAction action ) {
        registerTrigger( postCreditTriggerMap, trigger, action ) ;
    }
    
    public void registerPreDebitTrigger( AccountTrigger trigger, 
                                         AccountAction action ) {
        registerTrigger( preDebitTriggerMap, trigger, action ) ;
    }
    
    public void registerPostDebitTrigger( AccountTrigger trigger, 
                                          AccountAction action ) {
        registerTrigger( postDebitTriggerMap, trigger, action ) ;
    }
    
    private void registerTrigger( Map<AccountTrigger, List<AccountAction>> map,
                                  AccountTrigger trigger, AccountAction action ) {
        
        List<AccountAction> actionList = map.get( trigger ) ;
        if( actionList == null ) {
            actionList = new ArrayList<AccountAction>() ;
            map.put( trigger, actionList ) ;
        }
        actionList.add( action ) ;
    }

    @Override
    public final void accountPreUpdate( Account account, Entry entry ) {
        if( entry.isCredit() ) {
            checkTriggerAndFireAction( true, preCreditTriggerMap, account, entry ) ;
        }
        else {
            checkTriggerAndFireAction( true, preDebitTriggerMap, account, entry ) ;
        }
    }

    @Override
    public final void accountPostUpdate( Account account, Entry entry ) {
        if( entry.isCredit() ) {
            checkTriggerAndFireAction( true, postCreditTriggerMap, account, entry ) ;
        }
        else {
            checkTriggerAndFireAction( true, postDebitTriggerMap, account, entry ) ;
        }
    }

    private void checkTriggerAndFireAction( boolean preUpdate,
                                            Map<AccountTrigger, List<AccountAction>> map,
                                            Account account, Entry entry ) {
        
        for( AccountTrigger trigger : map.keySet() ) {
            if( trigger.isTriggered( account, entry ) ) {
                List<AccountAction> actions = map.get( trigger ) ;
                for( AccountAction action : actions ) {
                    if( action.canExecute( account, entry ) ) {
                        action.execute( preUpdate, account, entry ) ;
                    }
                }
            }
        }
    }
}
