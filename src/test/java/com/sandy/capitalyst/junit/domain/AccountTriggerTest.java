package com.sandy.capitalyst.junit.domain;

import static org.hamcrest.Matchers.hasKey ;
import static org.hamcrest.Matchers.hasSize ;
import static org.hamcrest.Matchers.not ;
import static org.junit.Assert.assertEquals ;
import static org.junit.Assert.assertThat ;

import org.apache.log4j.Logger ;
import org.junit.Before ;
import org.junit.Test ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.util.IncomeItem ;
import com.sandy.capitalyst.domain.util.instruction.InterAccountTransferInstruction ;
import com.sandy.capitalyst.domain.util.trigger.BalanceGreaterThanEqualToTrigger ;
import com.sandy.capitalyst.util.Utils ;

public class AccountTriggerTest {

    static final Logger logger = Logger.getLogger( AccountTriggerTest.class ) ;
    
    private AccountingBook book = null ;
    private Account accountA = null ;
    private Account accountB = null ;
    
    @Before
    public void setUp() {
        book = new AccountingBook( "Test" ) ;
        accountA = new Account( "Account A", book ) ;
        accountB = new Account( "Account B", book ) ;
    }
    
    @Test
    public void simpleTrigger() throws Exception {
        
        accountA.registerPostCreditTrigger( new BalanceGreaterThanEqualToTrigger( 900 ), 
                                            new InterAccountTransferInstruction( accountA, accountB, 50 ) ) ;
        
        book.addAccountingItem( new IncomeItem( "Test", 100, accountA ) ) ;
        book.runSimulation( "01/2015", "12/2015" ) ;
        
        //AccountLedgerPrinter printer = new AccountLedgerPrinter( accountA ) ;
        //printer.print( System.out, "01/2015", "12/2015" ) ;
        
        assertEquals( 1000.0, accountA.getAmount(), 0.001 ) ;
        assertEquals(  200.0, accountB.getAmount(), 0.001 ) ;
        
        assertThat( accountA.getDebitEntries(), hasSize( 4 ) ) ;
        assertThat( accountB.getCreditEntries(), hasSize( 4 ) ) ;
        assertThat( accountA.getCreditEntries(), hasSize( 12 ) ) ;
        
        assertThat( accountA.getDebitEntriesMap(), hasKey( Utils.parse( "09/2015" ) )) ;
        assertThat( accountA.getDebitEntriesMap(), not(hasKey( Utils.parse( "08/2015" ) ))) ;
    }
}
