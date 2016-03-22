package com.sandy.capitalyst.junit.domain;

import static org.junit.Assert.assertArrayEquals ;
import static org.junit.Assert.assertEquals ;

import java.util.Calendar ;
import java.util.LinkedHashMap ;

import org.apache.log4j.Logger ;
import org.junit.Before ;
import org.junit.Test ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.util.IncomeItem ;

public class AccountingBookTest {

    static final Logger logger = Logger.getLogger( AccountingBookTest.class ) ;
    
    private static final int SIM_NUM_MONTHS = 12 ;
    
    private AccountingBook book = null ;
    private Account account = null ;
    private LinkedHashMap<String, Double> monthAmounts = new LinkedHashMap<String, Double>() ;
    
    @Before
    public void setUp() {
        book = new AccountingBook( "Test" ) ;
        account = new Account( "Test A/C" ) ;
        monthAmounts.clear() ;
    }
    
    @Test
    public void simpleIncomeItem() throws Exception {
        book.addAccountingItem( new IncomeItem( "Test income", 100, account ) ) ;
        book.runSimulation( "01/2015", "12/2015" ) ;
        double[] amounts = book.getMonthTotals() ;
        for( int i=0; i<SIM_NUM_MONTHS; i++ ) {
            assertEquals( 100, amounts[i], 0.00001 );
        }
        
        assertEquals( SIM_NUM_MONTHS*100, account.getAmount(), 0.0001 ) ;
    }
    
    @Test
    public void incomeItemWithStartAndEndTime() throws Exception {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100, account )
                .startsOn( "01/2015" )
                .endsOn( "03/2015" ) 
        ) ;
        
        book.runSimulation( "01/2015", "12/2015" ) ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 100, 100, 100, 000, 000, 000, 000, 000, 000, 000, 000, 000 }, 
                book.getMonthTotals(), 0.0001 ) ;
        
        assertEquals( 3*100, account.getAmount(), 0.0001 ) ;
    }

    @Test
    public void incomeItemWithStartAndNumRepeats() throws Exception {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100, account )
                .startsOn( "06/2015" )
                .numTimes( 3 ) 
                ) ;
        
        book.runSimulation( "01/2015", "12/2015" ) ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 000, 000, 000, 000, 000, 100, 100, 100, 000, 000, 000, 000 }, 
                book.getMonthTotals(), 0.0001 ) ;
        
        assertEquals( 3*100, account.getAmount(), 0.0001 ) ;
    }
    
    @Test
    public void incomeItemWithActiveMonths() throws Exception {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100, account )
                .startsOn( "01/2015" )
                .activeOnMonths( Calendar.JANUARY, Calendar.APRIL, Calendar.JULY, Calendar.OCTOBER )
                ) ;
        
        book.runSimulation( "01/2015", "12/2015" ) ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 100, 000, 000, 100, 000, 000, 100, 000, 000, 100, 000, 000 }, 
                book.getMonthTotals(), 0.0001 ) ;
        
        assertEquals( 4*100, account.getAmount(), 0.0001 ) ;
   }
    
    @Test
    public void incomeItemWithActiveMonthsAndNumRepeats() throws Exception {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100, account )
                .startsOn( "01/2015" )
                .activeOnMonths( Calendar.JANUARY, Calendar.APRIL, Calendar.JULY, Calendar.OCTOBER )
                .numTimes( 2 )
                ) ;
        
        book.runSimulation( "01/2015", "12/2015" ) ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 100, 000, 000, 100, 000, 000, 000, 000, 000, 000, 000, 000 }, 
                book.getMonthTotals(), 0.0001 ) ;
        
        assertEquals( 2*100, account.getAmount(), 0.0001 ) ;
    }
    
    @Test
    public void incomeItemWithPiecewiseDef1() throws Exception {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100, account )
                .startsOn( "01/2015" )
                .endsOn( "02/2015" )
                .withPiecewiseDefinition( new IncomeItem( 100 ) 
                                         .startsOn( "08/2015" )
                                         .numTimes( 2 ) )
                ) ;
        
        book.runSimulation( "01/2015", "12/2015" ) ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 100, 100, 000, 000, 000, 000, 000, 100, 100, 000, 000, 000 }, 
                book.getMonthTotals(), 0.0001 ) ;
        
        assertEquals( 4*100, account.getAmount(), 0.0001 ) ;
    }
}
