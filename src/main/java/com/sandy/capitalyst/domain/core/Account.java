package com.sandy.capitalyst.domain.core;

import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Date ;
import java.util.List ;

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
    
    private List<Entry> creditEntries = new ArrayList<Account.Entry>() ;
    private List<Entry> debitEntries  = new ArrayList<Account.Entry>() ;
    
    private TriggerActionManager triggerActionManager = new TriggerActionManager() ;
    
    private List<AccountListener> listeners = new ArrayList<Account.AccountListener>() ;
    
    private boolean listenersSupressed = false ;
    
    public Account( String name ) {
        this.name = name ;
        this.addListener( triggerActionManager ) ;
    }
    
    public Account withInitialAmount( double amt ) {
        this.amount = amt ;
        return this ;
    }
    
    public void registerPreUpdateTrigger( AccountTrigger trigger, 
                                          AccountAction action ) {
        triggerActionManager.registerPreUpdateTrigger( trigger, action ) ;
    }
    
    public void registerPostUpdateTrigger( AccountTrigger trigger, 
                                           AccountAction action ) {
        triggerActionManager.registerPostUpdateTrigger( trigger, action ) ;
    }

    public double getAmount() {
        return this.amount ;
    }
    
    public void addListener( AccountListener listener ) {
        if( !listeners.contains( listener ) ) {
            listeners.add( listener ) ;
        }
    }
    
    public void supressListeners() { listenersSupressed = true ; }
    public void enableListeners()  { listenersSupressed = false ; }
    
    public void operate( double amt, Date date, String description ) {
        if( amt != 0 ) {
            Entry entry = new Entry( amt, date, description ) ;
            
            if( !listenersSupressed ) {
                for( AccountListener listener : listeners ) {
                    listener.accountPreUpdate( this, entry ) ;
                }
            }
            
            if( amt > 0 ) {
                creditEntries.add( entry ) ;
            }
            else if( amt < 0 ) {
                debitEntries.add( entry ) ;
            }
            this.amount += amt ;
            
            if( !listenersSupressed ) {
                for( AccountListener listener : listeners ) {
                    listener.accountPostUpdate( this, entry ) ;
                }
            }
        }
    }
    
    public String getName() {
        return this.name ;
    }
    
    public Collection<Entry> getCreditEntries() {
        return this.creditEntries ;
    }
    
    public String toString() {
        return "Account [name = " + name + ", balance = " + amount + "]" ;
    }
}
