package com.sandy.capitalyst.domain.core;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import com.sandy.capitalyst.domain.core.Account.AccountListener ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.AccountingBook.AccountInstructionExecutionEventValue ;

class AccountInstructionManager implements AccountListener {
    
    private Map<Trigger, List<Instruction>> preCreditTriggerMap = 
                            new HashMap<Trigger, List<Instruction>>() ;
    
    private Map<Trigger, List<Instruction>> postCreditTriggerMap = 
                            new HashMap<Trigger, List<Instruction>>() ;
    
    private Map<Trigger, List<Instruction>> preDebitTriggerMap = 
                            new HashMap<Trigger, List<Instruction>>() ;
    
    private Map<Trigger, List<Instruction>> postDebitTriggerMap = 
                            new HashMap<Trigger, List<Instruction>>() ;
    
    private AccountingBook book = null ;
    
    public AccountInstructionManager( AccountingBook book ) {
        this.book = book ;
    }
    
    public void registerPreCreditTrigger( Trigger trigger, 
                                          Instruction instruction ) {
        registerInstruction( preCreditTriggerMap, trigger, instruction ) ;
    }
    
    public void registerPostCreditTrigger( Trigger trigger, 
                                           Instruction instruction ) {
        registerInstruction( postCreditTriggerMap, trigger, instruction ) ;
    }
    
    public void registerPreDebitTrigger( Trigger trigger, 
                                         Instruction instruction ) {
        registerInstruction( preDebitTriggerMap, trigger, instruction ) ;
    }
    
    public void registerPostDebitTrigger( Trigger trigger, 
                                          Instruction instruction ) {
        registerInstruction( postDebitTriggerMap, trigger, instruction ) ;
    }
    
    private void registerInstruction( Map<Trigger, List<Instruction>> map,
                                      Trigger trigger, Instruction instruction ) {
        
        List<Instruction> actionList = map.get( trigger ) ;
        if( actionList == null ) {
            actionList = new ArrayList<Instruction>() ;
            map.put( trigger, actionList ) ;
        }
        actionList.add( instruction ) ;
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
            checkTriggerAndFireAction( false, postCreditTriggerMap, account, entry ) ;
        }
        else {
            checkTriggerAndFireAction( false, postDebitTriggerMap, account, entry ) ;
        }
    }

    private void checkTriggerAndFireAction( boolean preUpdate,
                                            Map<Trigger, List<Instruction>> map,
                                            Account account, Entry entry ) {
        
        for( Trigger trigger : map.keySet() ) {
            if( trigger.isTriggered( account, entry ) ) {
                List<Instruction> instructions = map.get( trigger ) ;
                for( Instruction instruction : instructions ) {
                    if( instruction.canExecute( account, entry ) ) {
                        instruction.execute( preUpdate, account, entry ) ;
                        
                        book.bus.publishEvent( 
                                AccountingBook.ACCOUNT_INSTRUCTION_EXECUTED, 
                                new AccountInstructionExecutionEventValue( 
                                       entry.getDate(), account, instruction ) );
                    }
                }
            }
        }
    }
}
