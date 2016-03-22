package com.sandy.capitalyst.domain.core;

import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Date ;
import java.util.List ;

public class Account {
    
    public static class Entry {
        private Date date = null ;
        private AccountingItem accountingItem = null ;
        private double amount = 0 ;
        
        Entry( double amt, Date date, AccountingItem acctItem ) {
            this.amount = amt ;
            this.date = date ;
            this.accountingItem = acctItem ;
        }
        
        public double getAmount() { return this.amount ; }
        public Date   getDate()   { return this.date ; }
        public AccountingItem getAccountingItem() { return this.accountingItem ; }
    }
    
    private String name = null ;
    private double amount = 0 ;
    
    private List<Entry> creditEntries = new ArrayList<Account.Entry>() ;
    private List<Entry> debitEntries  = new ArrayList<Account.Entry>() ;
    
    public Account( String name ) {
        this.name = name ;
    }
    
    public Account withInitialAmount( double amt ) {
        this.amount = amt ;
        return this ;
    }
    
    public double getAmount() {
        return this.amount ;
    }
    
    public void operate( double amt, Date date, AccountingItem acctItem ) {
        if( amt > 0 ) {
            creditEntries.add( new Entry( amt, date, acctItem ) ) ;
        }
        else if( amt < 0 ) {
            debitEntries.add( new Entry( amt, date, acctItem ) ) ;
        }
        this.amount += amt ;
    }
    
    public String getName() {
        return this.name ;
    }
    
    public Collection<Entry> getCreditEntries() {
        return this.creditEntries ;
    }
}
