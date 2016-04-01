package com.sandy.capitalyst.domain.core;

import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.LinkedHashMap ;
import java.util.List ;

import org.apache.commons.lang.ArrayUtils ;
import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.domain.util.DomainUtils ;
import com.sandy.common.bus.Event ;
import com.sandy.common.bus.EventBus ;
import com.sandy.common.bus.EventSubscriber ;
import com.sandy.common.util.StringUtil ;

public class AccountingBook implements EventSubscriber {
    
    static Logger logger = Logger.getLogger( AccountingBook.class ) ;
    
    private static final int START_EVENT_ID = 1 ;
    
    public static final int ACCOUNTING_ITEM_GROUP_ADDED  = START_EVENT_ID ;
    public static final int ACCOUNTING_ITEM_ADDED        = START_EVENT_ID + 1 ;
    public static final int SIMULATION_STARTED           = START_EVENT_ID + 2 ;
    public static final int SIMULATION_ENDED             = START_EVENT_ID + 3 ;
    public static final int MONTH_SIMULATION_STARTED     = START_EVENT_ID + 4 ;
    public static final int MONTH_SIMULATION_ENDED       = START_EVENT_ID + 5 ;
    public static final int ACCOUNTING_ITEM_PROCESSED    = START_EVENT_ID + 6 ;
    public static final int ACCOUNT_INSTRUCTION_EXECUTED = START_EVENT_ID + 7 ;
    
    private static final int END_EVENT_ID = START_EVENT_ID + 10 ;
    
    public static class AccountingItemProcessedEventValue {
        public Date date = null ;
        public AccountingItem accItem = null ;
        public double amt = 0 ;
        
        public AccountingItemProcessedEventValue( Date date, AccountingItem item, 
                                                  double amt ) {
            this.date = date ;
            this.accItem = item ;
            this.amt = amt ;
        }
    }
    
    public static class AccountInstructionExecutionEventValue {
        public Date date = null ;
        public Account account = null ;
        public Instruction instruction = null ;
        
        public AccountInstructionExecutionEventValue( Date date, Account account,
                                                      Instruction instruction ) {
            this.date = date ;
            this.account = account ;
            this.instruction = instruction ;
        }
    }
    
    public interface AccountingBookListener {
        public void accountingItemGroupAdded( AccountingItemGroup itemGroup ) ;
        public void accountingItemAdded( AccountingItem item ) ;
    }
    
    public static class AccountingBookListenerAdapter implements AccountingBookListener {
        public void accountingItemGroupAdded( AccountingItemGroup itemGroup ){}
        public void accountingItemAdded( AccountingItem item ){}
    }
    
    public EventBus bus = new EventBus() ;
    
    private AccountingItemGroup root = null ;
    private LinkedHashMap<String, Double> monthTotal = new LinkedHashMap<String, Double>() ;
    private List<AccountingBookListener> listeners = new ArrayList<AccountingBook.AccountingBookListener>() ;
    
    public AccountingBook( String name ) {
        root = new AccountingItemGroup( name, this ) ;
        bus.addSubscriberForEventRange( this, false, START_EVENT_ID, END_EVENT_ID ) ;
    }
    
    public String getName() {
        return root.getName() ;
    }
    
    public AccountingItemGroup getRoot() {
        return this.root ;
    }
    
    public void addAccountingBookListener( AccountingBookListener listener ) {
        listeners.add( listener ) ;
    }
    
    public void addAccountingItem( AccountingItem item ) {
        
        if( StringUtil.isEmptyOrNull( item.getName() ) ) {
            throw new IllegalArgumentException( "Can't add item with no name" ) ;
        }
        
        if( item.getAccount() == null ) {
            throw new IllegalArgumentException( "Can't add an accounting " + 
                                  "item which doesn't operate on an account" ) ;
        }
        
        item.setAccountingBook( this ) ;
        
        String parentPath = item.getParentPath() ;
        if( parentPath != null && parentPath.trim().length() != 0 ) {
            AccountingItemGroup group = root.getGroup( parentPath, true ) ;
            group.addAccountingItem( item ) ;
        }
        else {
            root.addAccountingItem( item ) ;
        }

        bus.publishEvent( ACCOUNTING_ITEM_ADDED, item ) ;
        
        List<AccountingItem> derivedItems = item.getDerivedAccountingItems() ;
        if( derivedItems != null ) {
            for( AccountingItem derivedItem : derivedItems ) {
                addAccountingItem( derivedItem ) ;
            }
        }
    }
    
    public void runSimulation( String fromDate, String toDate ) 
            throws Exception {
        
        SimpleDateFormat sdf = new SimpleDateFormat( "MM/yyyy" ) ;
        Date startDate = sdf.parse( fromDate ) ;
        Date endDate   = sdf.parse( toDate ) ;
        
        if( startDate.after( endDate ) ) {
            throw new IllegalArgumentException( "Start date can't be after to date" ) ;
        }
        
        Date curMth = startDate ;
        bus.publishEvent( SIMULATION_STARTED, null ) ;
        while( DateUtils.truncatedCompareTo( curMth, endDate, Calendar.DAY_OF_MONTH ) <= 0 ) {
            
            bus.publishEvent( MONTH_SIMULATION_STARTED, curMth ) ;
            double amt = root.getEntryForMonth( curMth ) ;
            monthTotal.put( sdf.format( curMth ), amt ) ;
            bus.publishEvent( MONTH_SIMULATION_ENDED, curMth ) ;
            
            curMth = DateUtils.addMonths( curMth, 1 ) ;
        }
        bus.publishEvent( SIMULATION_ENDED, null ) ;
    }
    
    public double[] getMonthTotals() {
        return ArrayUtils.toPrimitive( monthTotal.values().toArray( new Double[0] ) ) ;
    }

    public void handleEvent( Event event ) {
        switch( event.getEventType() ) {
            case ACCOUNTING_ITEM_GROUP_ADDED:
                AccountingItemGroup group = ( AccountingItemGroup )event.getValue() ;
                for( AccountingBookListener l : listeners ) {
                    l.accountingItemGroupAdded( group ) ;
                }
                break ;
                
            case ACCOUNTING_ITEM_ADDED:
                AccountingItem item = ( AccountingItem )event.getValue() ;
                for( AccountingBookListener l : listeners ) {
                    l.accountingItemAdded( item ) ;
                }
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
}
