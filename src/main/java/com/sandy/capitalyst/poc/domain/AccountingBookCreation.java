package com.sandy.capitalyst.poc.domain;

import java.text.SimpleDateFormat ;
import java.util.Calendar ;

import com.sandy.capitalyst.domain.CapitalystAccountingBook ;
import com.sandy.capitalyst.domain.util.CumulativeAccountingItem ;
import com.sandy.capitalyst.domain.util.PeriodicFixedAmtAccountingItem ;
import com.sandy.capitalyst.domain.util.PeriodicFixedIncomeItem ;

public class AccountingBookCreation {

    private static final String SALARY     = "Income > Salary >" ;
    private static final String INVESTMENT = "Expense > Investment>" ;
    
    private CapitalystAccountingBook book = null ;
    private CumulativeAccountingItem cum  = null ;
    
    public AccountingBookCreation() {
        book = new CapitalystAccountingBook( "TestBook" ) ;
        cum  = new CumulativeAccountingItem( "Cumulative", book ) ;
        
        addSalaryComponents() ;
    }
    
    private void addSalaryComponents() {
        
        book.addAccountingItem( new PeriodicFixedIncomeItem( SALARY + "Base salary",     210000, 0.3 ) ) ;
        book.addAccountingItem( new PeriodicFixedIncomeItem( SALARY + "Variable payout", 850000, 0.3, Calendar.MARCH ) ) ;
        book.addAccountingItem( new PeriodicFixedIncomeItem( SALARY + "Food card",         3000, 0.0 ) ) ;
        book.addAccountingItem( new PeriodicFixedAmtAccountingItem( INVESTMENT + "NPS", -7900 ) ) ;
    }
    
    public void drive() {
        Calendar cal = Calendar.getInstance() ;
        cal.set( 2015, Calendar.JANUARY, 1 ) ;
        SimpleDateFormat sdf = new SimpleDateFormat( "M-YYYY" ) ;

        for( int i=0; i<12*10; i++ ) {
            double amt = book.getEntryForMonth( cal.getTime() ) ;
            System.out.println( sdf.format( cal.getTime() ) + " ... " + amt + ", cumulative = " + cum.getEntryForMonth( cal.getTime() )/100000 ) ;
            cal.add( Calendar.MONTH, 1 ) ;
        }
    }
    
    public static void main( String[] args ) {
        new AccountingBookCreation().drive() ;
    }
}
