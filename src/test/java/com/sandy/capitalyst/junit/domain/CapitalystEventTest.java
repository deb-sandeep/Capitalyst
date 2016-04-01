package com.sandy.capitalyst.junit.domain;

import static com.sandy.capitalyst.domain.core.AccountingBook.* ;

import java.util.Date ;

import org.apache.log4j.Logger ;
import org.junit.Before ;
import org.junit.Test ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.core.AccountingItem ;
import com.sandy.capitalyst.domain.core.AccountingItemGroup ;
import com.sandy.capitalyst.domain.util.IncomeItem ;
import com.sandy.capitalyst.domain.util.DomainUtils ;
import com.sandy.capitalyst.domain.util.instruction.AccountLogInstruction ;
import com.sandy.capitalyst.domain.util.trigger.BalanceGreaterThanEqualToTrigger ;
import com.sandy.common.bus.Event ;
import com.sandy.common.bus.EventSubscriber ;

public class CapitalystEventTest {

    static final Logger logger = Logger.getLogger( CapitalystEventTest.class ) ;
    
    private AccountingBook book = null ;
    private Account accountA = null ;
    
    @Before
    public void setUp() {
        book = new AccountingBook( "Test Book" ) ;
        accountA = new Account( "Account A", book ) ;
    }
    
    @Test
    public void simpleTrigger() throws Exception {

        book.bus.addSubscriberForEventRange( new EventSubscriber() {
            public void handleEvent( Event event ) {
                switch( event.getEventType() ) {
                    case ACCOUNTING_ITEM_GROUP_ADDED:
                        AccountingItemGroup group = ( AccountingItemGroup )event.getValue() ;
                        logger.debug( "Accounting Group added." ) ;
                        logger.debug( "\tBook = " + group.getAccountingBook().getName() ) ;
                        logger.debug( "\tFQN  = " + group.getQualifiedName() ) ;
                        break ;
                        
                    case ACCOUNTING_ITEM_ADDED:
                        AccountingItem item = ( AccountingItem )event.getValue() ;
                        logger.debug( "Accounting Item added." ) ;
                        logger.debug( "\tBook = " + item.getAccountingBook().getName() ) ;
                        logger.debug( "\tFQN  = " + item.getQualifiedName() ) ;
                        break ;
                        
                    case SIMULATION_STARTED:
                        logger.debug( "Simulation started @ " + new Date().toString() ) ;
                        break ;
                        
                    case SIMULATION_ENDED:
                        logger.debug( "Simulation ended @ " + new Date().toString() ) ;
                        break ;
                        
                    case MONTH_SIMULATION_STARTED:
                        logger.debug( "Simulation started for month " + DomainUtils.SDF.format( (Date)event.getValue() ) ) ;
                        break ;
                        
                    case MONTH_SIMULATION_ENDED:
                        logger.debug( "Simulation ended for month " + DomainUtils.SDF.format( (Date)event.getValue() ) ) ;
                        break ;
                        
                    case ACCOUNTING_ITEM_PROCESSED:
                        AccountingItemProcessedEventValue e = null ;
                        e = ( AccountingItemProcessedEventValue )event.getValue() ;
                        logger.debug( "Accounting item processed" ) ;
                        logger.debug( "\tItem name = " + e.accItem.getQualifiedName() ) ;
                        logger.debug( "\tDate      = " + DomainUtils.SDF.format( e.date ) ) ;
                        logger.debug( "\tAmount    = " + e.amt ) ;
                        break ;
                        
                    case ACCOUNT_INSTRUCTION_EXECUTED:
                        AccountInstructionExecutionEventValue e1 = null ;
                        e1 = ( AccountInstructionExecutionEventValue )event.getValue() ;
                        logger.debug( "Instruction executed" ) ;
                        logger.debug( "\tInstruction = " + e1.instruction.getName() ) ;
                        logger.debug( "\tDate        = " + DomainUtils.SDF.format( e1.date ) ) ;
                        break ;
                }
            }
        }, false, 1, 10 ) ;
        
        accountA.registerPostCreditTrigger( new BalanceGreaterThanEqualToTrigger( 200 ), 
                                            new AccountLogInstruction() );
        
        book.addAccountingItem( new IncomeItem( "Income > Salary > Test Item", 100, accountA ) ) ;
        book.runSimulation( "01/2015", "03/2015" );
    }
}
