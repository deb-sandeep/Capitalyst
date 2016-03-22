package com.sandy.capitalyst.domain.core;

import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.LinkedHashMap ;
import java.util.List ;

import org.apache.commons.lang.ArrayUtils ;
import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;

public class AccountingBook {
    
    static Logger logger = Logger.getLogger( AccountingBook.class ) ;
    
    private AccountingItemGroup root = null ;
    private LinkedHashMap<String, Double> monthTotal = new LinkedHashMap<String, Double>() ;
    
    public AccountingBook( String name ) {
        root = new AccountingItemGroup( name ) ;
    }
    
    public void addAccountingItem( AccountingItem item ) {
        
        if( StringUtil.isEmptyOrNull( item.getName() ) ) {
            throw new IllegalArgumentException( "Can't add item with no name" ) ;
        }
        
        if( item.getAccount() == null ) {
            throw new IllegalArgumentException( "Can't add an accoungting " + 
                                  "item which doesn't operate on an account" ) ;
        }
        
        String parentPath = item.getParentPath() ;
        if( parentPath != null && parentPath.trim().length() != 0 ) {
            AccountingItemGroup group = root.getGroup( parentPath, true ) ;
            group.addAccountingItem( item ) ;
        }
        else {
            root.addAccountingItem( item ) ;
        }
        
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
        while( DateUtils.truncatedCompareTo( curMth, endDate, Calendar.DAY_OF_MONTH ) <= 0 ) {
            
            double amt = root.getEntryForMonth( curMth ) ;
            monthTotal.put( sdf.format( curMth ), amt ) ;
            
            curMth = DateUtils.addMonths( curMth, 1 ) ;
        }
    }
    
    public double[] getMonthTotals() {
        return ArrayUtils.toPrimitive( monthTotal.values().toArray( new Double[0] ) ) ;
    }
}
