package com.sandy.capitalyst.junit.domain;

import static org.junit.Assert.assertArrayEquals ;
import static org.junit.Assert.assertEquals ;

import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.LinkedHashMap ;

import org.junit.Before ;
import org.junit.Test ;

import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.util.IncomeItem ;

public class AccountingBookTest {

    private static final int SIM_NUM_MONTHS = 12 ;
    private static final int SIM_START_YEAR = 2015 ;
    private static final SimpleDateFormat SDF = new SimpleDateFormat( "MM/YYYY" ) ;
    
    private AccountingBook book = null ;
    private double[] amounts = null ;
    private LinkedHashMap<String, Double> monthAmounts = new LinkedHashMap<String, Double>() ;
    
    private void runSimulation() {
        
        Date month = null ;
        Calendar cal = Calendar.getInstance() ;
        cal.set( SIM_START_YEAR, Calendar.JANUARY, 1 ) ;

        for( int i=0; i<SIM_NUM_MONTHS; i++ ) {
            month = cal.getTime() ;
            amounts[i] = book.getEntryForMonth( month ) ;
            monthAmounts.put( SDF.format( month ), amounts[i] ) ;
            
            cal.add( Calendar.MONTH, 1 ) ;
        }
    }
    
    @Before
    public void setUp() {
        book = new AccountingBook( "Test" ) ;
        amounts = new double[ SIM_NUM_MONTHS ] ;
        monthAmounts.clear() ;
    }
    
    @Test
    public void simpleIncomeItem() {
        book.addAccountingItem( new IncomeItem( "Test income", 100 ) ) ;
        runSimulation() ;
        for( int i=0; i<SIM_NUM_MONTHS; i++ ) {
            assertEquals( 100, amounts[i], 0.00001 );
        }
    }
    
    @Test
    public void incomeItemWithStartAndEndTime() {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100 )
                .startsOn( "01/2015" )
                .endsOn( "03/2015" ) 
        ) ;
        
        runSimulation() ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 100, 100, 100, 000, 000, 000, 000, 000, 000, 000, 000, 000 }, 
                amounts, 0.0001 ) ;
    }

    @Test
    public void incomeItemWithStartAndNumRepeats() {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100 )
                .startsOn( "06/2015" )
                .numTimes( 3 ) 
                ) ;
        
        runSimulation() ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 000, 000, 000, 000, 000, 100, 100, 100, 000, 000, 000, 000 }, 
                amounts, 0.0001 ) ;
    }
    
    @Test
    public void incomeItemWithActiveMonths() {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100 )
                .startsOn( "01/2015" )
                .activeOnMonths( Calendar.JANUARY, Calendar.APRIL, Calendar.JULY, Calendar.OCTOBER )
                ) ;
        
        runSimulation() ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 100, 000, 000, 100, 000, 000, 100, 000, 000, 100, 000, 000 }, 
                amounts, 0.0001 ) ;
    }
    
    @Test
    public void incomeItemWithActiveMonthsAndNumRepeats() {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100 )
                .startsOn( "01/2015" )
                .activeOnMonths( Calendar.JANUARY, Calendar.APRIL, Calendar.JULY, Calendar.OCTOBER )
                .numTimes( 2 )
                ) ;
        
        runSimulation() ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 100, 000, 000, 100, 000, 000, 000, 000, 000, 000, 000, 000 }, 
                amounts, 0.0001 ) ;
    }
    
    @Test
    public void incomeItemWithPiecewiseDef1() {
        book.addAccountingItem( 
                new IncomeItem( "Test income", 100 )
                .startsOn( "01/2015" )
                .endsOn( "02/2015" )
                .withPiecewiseDefinition( new IncomeItem( 100 ) 
                                         .startsOn( "08/2015" )
                                         .numTimes( 2 ) )
                ) ;
        
        runSimulation() ;
        assertArrayEquals( 
                //              1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12
                new double[]{ 100, 100, 000, 000, 000, 000, 000, 100, 100, 000, 000, 000 }, 
                amounts, 0.0001 ) ;
    }
}
