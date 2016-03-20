package com.sandy.capitalyst.poc.domain;

import java.text.SimpleDateFormat ;
import java.util.Calendar ;

import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.util.CumulativeAccountingItem ;
import com.sandy.capitalyst.domain.util.IncomeItem ;
import com.sandy.capitalyst.domain.util.InvestmentItem ;

public class AccountingBookCreation {

    private static final String SALARY     = "Income > Salary >" ;
    private static final String MATURITY   = "Income > Maturity >" ;
    private static final String INVESTMENT = "Expense > Investment>" ;
    
    private AccountingBook book = null ;
    private CumulativeAccountingItem cum  = null ;
    
    public AccountingBookCreation() {
        book = new AccountingBook( "TestBook" ) ;
        cum  = new CumulativeAccountingItem( "Cumulative", book ) ;
        
        addSalaryComponents() ;
    }
    
    private void addSalaryComponents() {
        
//        book.addAccountingItem( new IncomeItem( SALARY + "Base salary",     210000 ).withTax( 0.3 ).startsOn( "02/2015" ).endsOn( "02/2015" ) ) ;
//        book.addAccountingItem( new IncomeItem( SALARY + "Variable payout", 850000 ).withTax( 0.3 ).activeOnMonths( Calendar.MARCH ) ) ;
//        book.addAccountingItem( 
//                new IncomeItem( SALARY + "Test", 1000 )
//                    .startsOn( "03/2015" )
//                    .activeOnMonths( Calendar.JULY, Calendar.DECEMBER )
//                    .numTimes( 1 )
//        ) ;
        
         book.addAccountingItem(  
                 new InvestmentItem( INVESTMENT + "Test", 1000 )
                 .startsOn( "03/2015" )
                 .numTimes( 6 )
                 .withMaturityDetails( new IncomeItem( MATURITY + "Test maturity", 5000 )
                                       .startsOn( "01/2016" )
                                       .activeOnMonths( Calendar.JANUARY, Calendar.MARCH, Calendar.MAY )
                                       .endsOn( "12/2016" ) ) 
         ) ;
        
//        book.addAccountingItem( new IncomeItem( SALARY + "Food card",         3000 ) ) ;
//        book.addAccountingItem( new FixedAmountItem( INVESTMENT + "NPS",     -7900 ) ) ;
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
