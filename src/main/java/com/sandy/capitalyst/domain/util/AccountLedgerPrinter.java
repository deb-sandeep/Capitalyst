package com.sandy.capitalyst.domain.util;

import java.io.OutputStream ;
import java.io.PrintStream ;
import java.text.DecimalFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;
import java.util.Map ;

import org.apache.commons.lang.StringUtils ;
import org.apache.commons.lang.time.DateUtils ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;

public class AccountLedgerPrinter {
    
    private static final DecimalFormat DF = new DecimalFormat( "0.0" ) ;
    
    private Account account   = null ;
    private Date    startDate = null ;
    private Date    endDate   = null ;
    
    private Entry[] creditEntries = null ;
    private Entry[] debitEntries  = null ;
    
    private Map<Date, List<Entry>> creditEntriesMap = null ;
    private Map<Date, List<Entry>> debitEntriesMap  = null ;
    
    public AccountLedgerPrinter( Account account ) {
        this.account = account ;
        creditEntries = account.getCreditEntries().toArray( new Entry[0] ) ;
        debitEntries  = account.getDebitEntries().toArray( new Entry[0] ) ;
        
        creditEntriesMap = account.getCreditEntriesMap() ;
        debitEntriesMap  = account.getDebitEntriesMap() ;
    }
    
    public void print( OutputStream os ) 
        throws Exception {
        print( os, null, null ) ;
    }
    
    public void print( OutputStream os, String start, String end ) 
        throws Exception {
        
        calculateDateRange( start, end ) ;
        Date curDate = startDate ;
        
        PrintStream ps = new PrintStream( os ) ;
        
        ps.println( "------------------------------------------------------------" ) ;
        ps.println( "Account Ledger : " + account.getName() ) ;
        ps.println( "----------+----------+----------+---------------------------" ) ;
        ps.print( StringUtils.rightPad( "Month", 10 ) ) ;
        ps.print( "|" ) ;
        ps.print( StringUtils.rightPad( "Credit", 10 ) ) ;
        ps.print( "|" ) ;
        ps.print( StringUtils.rightPad( "Debit", 10 ) ) ;
        ps.print( "|" ) ;
        ps.println( "Description" ) ;
        ps.println( "----------+----------+----------+---------------------------" ) ;
        
        while( DomainUtils.compare( curDate, endDate ) <= 0 ) {
            
            if( creditEntriesMap.containsKey( curDate ) ) {
                List<Entry> entries = creditEntriesMap.get( curDate ) ;
                for( Entry entry : entries ) {
                    printEntry( ps, entry ) ;
                }
            }
            
            if( debitEntriesMap.containsKey( curDate ) ) {
                List<Entry> entries = debitEntriesMap.get( curDate ) ;
                for( Entry entry : entries ) {
                    printEntry( ps, entry ) ;
                }
            }
            
            curDate = DateUtils.addMonths( curDate, 1 ) ;
        }
    }
    
    private void printEntry( PrintStream ps, Entry entry ) {
        
        Date   date = entry.getDate() ;
        double amt  = entry.getAmount() ;
        String descr= entry.getDescription() ;
        
        ps.print( StringUtils.rightPad( DomainUtils.SDF.format( date ), 10 ) ) ;
        ps.print( "|" ) ;
        if( amt > 0 ) {
            ps.print( StringUtils.leftPad( DF.format( amt ) + " ", 10 ) ) ;
            ps.print( "|" ) ;
            ps.print( StringUtils.leftPad( "", 10 ) ) ;
            ps.print( "|" ) ;
            ps.println( " [C] " + descr ) ;
        }
        else {
            ps.print( StringUtils.leftPad( "", 10 ) ) ;
            ps.print( "|" ) ;
            ps.print( StringUtils.leftPad( DF.format( amt ) + " ", 10 ) ) ;
            ps.print( "|" ) ;
            ps.println( " [D] " + descr ) ;
        }
    }
    
    private void calculateDateRange( String start, String end ) throws Exception {
        
        Date creditEntryStartDate = DomainUtils.parse( start ) ;
        Date debitEntryStartDate  = DomainUtils.parse( start ) ;
        Date creditEntryEndDate   = DomainUtils.parse( end ) ;
        Date debitEntryEndDate    = DomainUtils.parse( end ) ;
        
        if( creditEntries.length > 0 ) {
            creditEntryStartDate = creditEntries[0].getDate() ;
            creditEntryEndDate   = creditEntries[creditEntries.length-1].getDate() ;
        }
        
        if( debitEntries.length > 0 ) {
            debitEntryStartDate = debitEntries[0].getDate() ;
            debitEntryEndDate   = debitEntries[debitEntries.length-1].getDate() ;
        }
        
        if( DomainUtils.compare( creditEntryStartDate, debitEntryStartDate ) <= 0 ) {
            startDate = creditEntryStartDate ;
        }
        else {
            startDate = debitEntryStartDate ;
        }
        
        if( DomainUtils.compare( creditEntryEndDate, debitEntryEndDate ) > 0 ) {
            endDate = creditEntryEndDate ;
        }
        else {
            endDate = debitEntryEndDate ;
        }
        
        startDate = DateUtils.truncate( startDate, Calendar.MONTH ) ;
        endDate   = DateUtils.truncate( endDate, Calendar.MONTH ) ;
    }
}
