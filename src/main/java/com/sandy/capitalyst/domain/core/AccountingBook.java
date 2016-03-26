package com.sandy.capitalyst.domain.core;

import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.LinkedHashMap ;
import java.util.List ;

import org.apache.commons.lang.ArrayUtils ;
import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.bus.EventBus ;
import com.sandy.common.util.StringUtil ;

public class AccountingBook {
    
    static Logger logger = Logger.getLogger( AccountingBook.class ) ;
    
    public static final int ACCOUNTING_ITEM_GROUP_ADDED  = 1 ;
    public static final int ACCOUNTING_ITEM_ADDED        = 2 ;
    public static final int SIMULATION_STARTED           = 3 ;
    public static final int SIMULATION_ENDED             = 4 ;
    public static final int MONTH_SIMULATION_STARTED     = 5 ;
    public static final int MONTH_SIMULATION_ENDED       = 6 ;
    public static final int ACCOUNTING_ITEM_PROCESSED    = 7 ;
    public static final int ACCOUNT_INSTRUCTION_EXECUTED = 8 ;
    
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
    
    public EventBus bus = new EventBus() ;
    
    private AccountingItemGroup root = null ;
    private LinkedHashMap<String, Double> monthTotal = new LinkedHashMap<String, Double>() ;
    
    public AccountingBook( String name ) {
        root = new AccountingItemGroup( name, this ) ;
    }
    
    public String getName() {
        return root.getName() ;
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
}
