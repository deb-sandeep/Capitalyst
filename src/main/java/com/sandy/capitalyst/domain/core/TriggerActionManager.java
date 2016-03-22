package com.sandy.capitalyst.domain.core;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import com.sandy.capitalyst.domain.core.Account.AccountListener ;
import com.sandy.capitalyst.domain.core.Account.Entry ;

class TriggerActionManager implements AccountListener {
    
    private Map<AccountTrigger, List<AccountAction>> preUpdateTriggerMap = 
                            new HashMap<AccountTrigger, List<AccountAction>>() ;
    
    private Map<AccountTrigger, List<AccountAction>> postUpdateTriggerMap = 
                            new HashMap<AccountTrigger, List<AccountAction>>() ;
    
    public void registerPreUpdateTrigger( AccountTrigger trigger, 
                                          AccountAction action ) {
        registerTrigger( preUpdateTriggerMap, trigger, action ) ;
    }
    
    public void registerPostUpdateTrigger( AccountTrigger trigger, 
                                           AccountAction action ) {
        registerTrigger( postUpdateTriggerMap, trigger, action ) ;
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
        checkTriggerCondition( true, preUpdateTriggerMap, account, entry ) ;
    }

    @Override
    public final void accountPostUpdate( Account account, Entry entry ) {
        checkTriggerCondition( false, postUpdateTriggerMap, account, entry ) ;
    }

    private void checkTriggerCondition( boolean preUpdate,
                                        Map<AccountTrigger, List<AccountAction>> map,
                                        Account account, Entry entry ) {
        for( AccountTrigger trigger : map.keySet() ) {
            if( trigger.isTriggered( account, entry ) ) {
                List<AccountAction> actions = map.get( trigger ) ;
                for( AccountAction action : actions ) {
                    if( action.canExecute() ) {
                        action.execute( preUpdate, account, entry ) ;
                    }
                }
            }
        }
    }
}
