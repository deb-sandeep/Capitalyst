package com.sandy.capitalyst.domain.core;

import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Date ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;

public class Account {
    
    public static class Entry {
        private static final SimpleDateFormat SDF = new SimpleDateFormat( "MM/yyyy" ) ;
        
        private Date date = null ;
        private String description = null ;
        private double amount = 0 ;
        
        Entry( double amt, Date date, String description ) {
            this.amount = amt ;
            this.date = date ;
            this.description = description ;
        }
        
        public double getAmount() { return this.amount ; }
        public Date   getDate()   { return this.date ; }
        public String getDescription() { return this.description ; }
        
        public boolean isCredit() { return this.amount > 0 ; }
        
        public String toString() {
            return "Entry [date = " + SDF.format( date ) + 
                   ", amount = " + amount + "]" +
                   " : " + description ; 
        }
    }
    
    public interface AccountListener {
        public void accountPreUpdate( Account account, Entry entry ) ;
        public void accountPostUpdate( Account account, Entry entry ) ;
    }
    
    private String name = null ;
    private double amount = 0 ;
    
    private Map<Date, List<Entry>> creditEntriesMap = new LinkedHashMap<Date, List<Account.Entry>>() ;
    private Map<Date, List<Entry>> debitEntriesMap  = new LinkedHashMap<Date, List<Account.Entry>>() ;
    
    private TriggerActionManager triggerActionManager = new TriggerActionManager() ;
    
    private List<AccountListener> listeners = new ArrayList<Account.AccountListener>() ;
    
    public Account( String name ) {
        this.name = name ;
        this.addListener( triggerActionManager ) ;
    }
    
    public Account withInitialAmount( double amt ) {
        this.amount = amt ;
        return this ;
    }
    
    public void registerPreCreditTrigger( AccountTrigger trigger, 
                                          AccountAction action ) {
        triggerActionManager.registerPreCreditTrigger( trigger, action ) ;
    }
    
    public void registerPostCreditTrigger( AccountTrigger trigger, 
                                           AccountAction action ) {
        triggerActionManager.registerPostCreditTrigger( trigger, action ) ;
    }

    public void registerPreDebitTrigger( AccountTrigger trigger, 
                                         AccountAction action ) {
        triggerActionManager.registerPreDebitTrigger( trigger, action ) ;
    }
    
    public void registerPostDebitTrigger( AccountTrigger trigger, 
                                          AccountAction action ) {
        triggerActionManager.registerPostDebitTrigger( trigger, action ) ;
    }
    
    public double getAmount() {
        return this.amount ;
    }
    
    public void addListener( AccountListener listener ) {
        if( !listeners.contains( listener ) ) {
            listeners.add( listener ) ;
        }
    }
    
    public void operate( double amt, Date date, String description ) {
        if( amt != 0 ) {
            Entry entry = new Entry( amt, date, description ) ;
            
            for( AccountListener listener : listeners ) {
                listener.accountPreUpdate( this, entry ) ;
            }
            
            if( amt > 0 ) {
                addToEntryMap( creditEntriesMap, date, entry ) ;
            }
            else if( amt < 0 ) {
                addToEntryMap( debitEntriesMap, date, entry ) ;
            }
            this.amount += amt ;
            
            for( AccountListener listener : listeners ) {
                listener.accountPostUpdate( this, entry ) ;
            }
        }
    }
    
    private void addToEntryMap( Map<Date, List<Entry>> map, Date date, Entry entry ) {
        
        List<Entry> entryCollection = map.get( date ) ;
        if( entryCollection == null ) {
            entryCollection = new ArrayList<Account.Entry>() ;
            map.put( date, entryCollection ) ;
        }
        entryCollection.add( entry ) ;
    }
    
    public String getName() {
        return this.name ;
    }
    
    public Collection<Entry> getCreditEntries() {
        List<Entry> entries = new ArrayList<Account.Entry>() ;
        for( List<Entry> ledgerEntries : this.creditEntriesMap.values() ) {
            entries.addAll( ledgerEntries ) ;
        }
        return entries ;
    }
    
    public Collection<Entry> getDebitEntries() {
        List<Entry> entries = new ArrayList<Account.Entry>() ;
        for( List<Entry> ledgerEntries : this.debitEntriesMap.values() ) {
            entries.addAll( ledgerEntries ) ;
        }
        return entries ;
    }
    
    public Map<Date, List<Entry>> getCreditEntriesMap() {
        return this.creditEntriesMap ;
    }
    
    public Map<Date, List<Entry>> getDebitEntriesMap() {
        return this.debitEntriesMap ;
    }
    
    public String toString() {
        return "Account [name = " + name + ", balance = " + amount + "]" ;
    }
}
