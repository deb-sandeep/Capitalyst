package com.sandy.capitalyst.junit.domain;

import org.apache.log4j.Logger ;
import org.junit.Before ;
import org.junit.Test ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.util.IncomeItem ;
import com.sandy.capitalyst.domain.util.action.InterAccountTransferAction ;
import com.sandy.capitalyst.domain.util.trigger.BalanceGreaterThanEqualToTrigger ;

public class AccountTriggerTest {

    static final Logger logger = Logger.getLogger( AccountTriggerTest.class ) ;
    
    private AccountingBook book = null ;
    private Account accountA = null ;
    private Account accountB = null ;
    
    @Before
    public void setUp() {
        book = new AccountingBook( "Test" ) ;
        accountA = new Account( "Account A" ) ;
        accountB = new Account( "Account B" ) ;
    }
    
    @Test
    public void simpleTrigger() throws Exception {
        accountA.registerPostUpdateTrigger( new BalanceGreaterThanEqualToTrigger( 900 ), 
                                            new InterAccountTransferAction( accountA, accountB, 50 ) ) ;
        
        book.addAccountingItem( new IncomeItem( "Test", 100, accountA ) ) ;
        book.runSimulation( "01/2015", "12/2015" ) ;
        
        logger.debug( "Account A = " + accountA ) ;
        logger.debug( "Account B = " + accountB ) ;
    }
}
