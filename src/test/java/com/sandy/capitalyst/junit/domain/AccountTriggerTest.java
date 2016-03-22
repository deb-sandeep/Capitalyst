package com.sandy.capitalyst.junit.domain;

import org.apache.log4j.Logger ;
import org.junit.Before ;
import org.junit.Test ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.util.IncomeItem ;
import com.sandy.capitalyst.domain.util.action.AccountLogAction ;
import com.sandy.capitalyst.domain.util.action.AccountLogMsgAction ;
import com.sandy.capitalyst.domain.util.trigger.BalanceGreaterThanTrigger ;
import com.sandy.capitalyst.domain.util.trigger.NoConditionTrigger ;

public class AccountTriggerTest {

    static final Logger logger = Logger.getLogger( AccountTriggerTest.class ) ;
    
    private AccountingBook book = null ;
    private Account account = null ;
    
    @Before
    public void setUp() {
        book = new AccountingBook( "Test" ) ;
        account = new Account( "Test A/C" ) ;
    }
    
    @Test
    public void simpleTrigger() throws Exception {
        account.registerPostUpdateTrigger( new NoConditionTrigger(), 
                                          new AccountLogAction() ) ;
        account.registerPostUpdateTrigger( new BalanceGreaterThanTrigger( 900 ), 
                                           new AccountLogMsgAction( "Balance exceeded 900" ) );
        
        book.addAccountingItem( new IncomeItem( "Test", 100, account ) ) ;
        book.runSimulation( "01/2015", "12/2015" ) ;
    }
}
