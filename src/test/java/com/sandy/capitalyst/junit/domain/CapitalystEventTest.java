package com.sandy.capitalyst.junit.domain;

import org.apache.log4j.Logger ;
import org.junit.Before ;
import org.junit.Test ;

import com.sandy.capitalyst.Capitalyst ;
import com.sandy.capitalyst.domain.EventType ;
import com.sandy.capitalyst.domain.EventType.AccountingItemAddEvent ;
import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingBook ;
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

        Capitalyst.BUS.addSubscriberForEventTypes( new EventSubscriber() {
            public void handleEvent( Event event ) {
                AccountingItemAddEvent e = null ;
                e = ( AccountingItemAddEvent )event.getValue() ;
                
                logger.debug( "AG added: " + event.getEventType() + ": " + 
                              e.book.getName() + " - " + e.item.getQualifiedName() ) ;
            }
        }, false, EventType.ACCOUNTING_ITEM_GROUP_ADDED, EventType.ACCOUNTING_ITEM_ADDED ) ;
        
        book.addAccountingItem( new IncomeItem( "Income > Salary > Test Item", 100, accountA ) ) ;
    }
}
