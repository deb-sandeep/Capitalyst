package com.sandy.capitalyst.junit.domain;

import static com.sandy.capitalyst.domain.core.AccountingBook.ACCOUNTING_ITEM_ADDED ;
import static com.sandy.capitalyst.domain.core.AccountingBook.ACCOUNTING_ITEM_GROUP_ADDED ;

import org.apache.log4j.Logger ;
import org.junit.Before ;
import org.junit.Test ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.core.AccountingItem ;
import com.sandy.capitalyst.domain.core.AccountingItemGroup ;
import com.sandy.capitalyst.domain.util.IncomeItem ;
import com.sandy.common.bus.Event ;
import com.sandy.common.bus.EventSubscriber ;

public class CapitalystEventTest {

    static final Logger logger = Logger.getLogger( CapitalystEventTest.class ) ;
    
    private AccountingBook book = null ;
    private Account accountA = null ;
    
    @Before
    public void setUp() {
        book = new AccountingBook( "Test Book" ) ;
        accountA = new Account( "Account A" ) ;
    }
    
    @Test
    public void simpleTrigger() throws Exception {

        book.bus.addSubscriberForEventTypes( new EventSubscriber() {
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
                }
            }
        }, false, ACCOUNTING_ITEM_GROUP_ADDED, ACCOUNTING_ITEM_ADDED ) ;
        
        book.addAccountingItem( new IncomeItem( "Income > Salary > Test Item", 100, accountA ) ) ;
    }
}
