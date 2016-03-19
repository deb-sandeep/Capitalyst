package com.sandy.capitalyst.poc.domain;

import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;

import com.sandy.capitalyst.domain.CapitalystAccountingBook ;
import com.sandy.capitalyst.domain.core.AccountingItem ;
import com.sandy.capitalyst.domain.core.AccountingItemGroup ;
import com.sandy.capitalyst.domain.util.CumulativeAccountingItem ;
import com.sandy.capitalyst.domain.util.FixedAmtAccountingItem ;
import com.sandy.capitalyst.domain.util.IncomeTaxAccountingItem ;

public class AccountingBookCreation {

    private CapitalystAccountingBook book = null ;
    private CumulativeAccountingItem cum  = null ;
    
    public AccountingBookCreation() {
        book = new CapitalystAccountingBook( "TestBook" ) ;
        cum  = new CumulativeAccountingItem( "Cumulative", book ) ;
        createSkeleton() ;
        
        addSalaryComponents() ;
    }
    
    private void createSkeleton() {
        book.createNestedGroups( "Income > Salary" ) ;
        book.createNestedGroups( "Income > Dividends" ) ;
        book.createNestedGroups( "Income > Investment maturities" ) ;
        
        book.createNestedGroups( "Expense > Income tax" ) ;
        book.createNestedGroups( "Expense > Investment" ) ;
    }
    
    private void addSalaryComponents() {
        
        AccountingItemGroup group = ( AccountingItemGroup )book.getAccountingItem( "Income > Salary" ) ;
        
        AccountingItem base       = new FixedAmtAccountingItem( "Base salary",     150000 ) ;
        AccountingItem bonus      = new FixedAmtAccountingItem( "Variable payout", 750000, Calendar.MARCH ) ;
        AccountingItem npsInvest  = new FixedAmtAccountingItem( "NPS",             -7900 ) ;

        group.addAccountingItem( base ) ;
        group.addAccountingItem( bonus ) ;
        
        AccountingItemGroup tax = ( AccountingItemGroup )book.getAccountingItem( "Expense > Income tax" ) ;
        tax.addAccountingItem( new IncomeTaxAccountingItem( base, 0.3 ) ) ;
        tax.addAccountingItem( new IncomeTaxAccountingItem( bonus, 0.3 ) ) ;
        
        AccountingItemGroup inv = ( AccountingItemGroup )book.getAccountingItem( "Expense > Investment" ) ;
        inv.addAccountingItem( npsInvest ) ;
    }
    
    public void drive() {
        Calendar cal = Calendar.getInstance() ;
        cal.set( 2015, Calendar.JANUARY, 1 ) ;
        SimpleDateFormat sdf = new SimpleDateFormat( "M-YYYY" ) ;

        for( int i=0; i<24; i++ ) {
            double amt = book.getEntryForMonth( cal.getTime() ) ;
            System.out.println( sdf.format( cal.getTime() ) + " ... " + cum.getEntryForMonth( cal.getTime() ) ) ;
            cal.add( Calendar.MONTH, 1 ) ;
        }
    }
    
    public static void main( String[] args ) {
        new AccountingBookCreation().drive() ;
    }
}
