
package com.sandy.capitalyst.util;

import java.text.DecimalFormat ;
import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.time.Duration ;
import java.util.Calendar ;
import java.util.Date ;

import org.apache.commons.lang.StringUtils ;
import org.apache.commons.lang.time.DateUtils ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.DayClock ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Txn.TxnType ;

public class Utils {

    public static final SimpleDateFormat SDF = new SimpleDateFormat( "dd/MM/yyyy" ) ;
    public static final DecimalFormat     DF = new DecimalFormat( "00.0" ) ;
    
    private static final int HDR_LEN    = 80 ;
    private static final int FIELD_LEN  = 10 ;
    
    public static Date parseDate( String dateStr ) throws IllegalArgumentException {
        try {
            return SDF.parse( dateStr ) ;
        }
        catch( ParseException e ) {
            throw new IllegalArgumentException( "Invalid date specified. " + dateStr ) ;
        }
    }
    
    public static String formatDate( Date date ) {
        return SDF.format( date ) ;
    }
    
    public static Date addDays( int numDays, Date date ) {
        return DateUtils.addDays( date, numDays ) ;
    }
    
    public static boolean isBefore( Date toCompare, Date milestone ) {
        if( DateUtils.truncatedCompareTo( toCompare, milestone, Calendar.DAY_OF_MONTH ) < 0 ) {
            return true ;
        }
        return false ;
    }
    
    public static boolean isSame( Date toCompare, Date milestone ) {
        if( DateUtils.truncatedCompareTo( toCompare, milestone, Calendar.DAY_OF_MONTH ) == 0 ) {
            return true ;
        }
        return false ;
    }
    
    public static boolean isAfter( Date toCompare, Date milestone ) {
        if( DateUtils.truncatedCompareTo( toCompare, milestone, Calendar.DAY_OF_MONTH ) > 0 ) {
            return true ;
        }
        return false ;
    }
    
    public static boolean isEndOfMonth( Calendar cal ) {
        int maxDays = cal.getActualMaximum( Calendar.DAY_OF_MONTH ) ;
        int dayNum  = cal.get( Calendar.DAY_OF_MONTH ) ;
        
        return maxDays == dayNum ;
    }
    
    public static boolean isEndOfQuarter( Calendar cal ) {
        if( isEndOfMonth( cal ) ) {
            int monthNum  = cal.get( Calendar.MONTH ) ;
            if( monthNum == Calendar.MARCH || 
                    monthNum == Calendar.JUNE || 
                    monthNum == Calendar.SEPTEMBER ||
                    monthNum == Calendar.DECEMBER ) {
                return true ;
            }
        }
        return false ;
    }
    
    public static boolean isEndOfQuarter( Date date ) {
        Calendar cal = Calendar.getInstance() ;
        cal.setTime( date ) ;
        return isEndOfQuarter( cal ) ;
    }
    
    public static boolean isEndOfYear( Calendar cal ) {
        if( isEndOfMonth( cal ) ) {
            int monthNum  = cal.get( Calendar.MONTH ) ;
            if( monthNum == Calendar.DECEMBER ) {
                return true ;
            }
        }
        return false ;
    }
    
    public static int getNumDaysBetween( Date fromDate, Date toDate ) {

        Duration duration = Duration.between( fromDate.toInstant(), 
                                              toDate.toInstant() ) ;
        return (int)duration.toDays() ;
    }
    
    public static void transfer( Account fromAcc, Account toAcc ) {
        
        transfer( fromAcc, toAcc, DayClock.instance().now(), "" ) ;
    }

    public static void transfer( Account fromAcc, Account toAcc, 
                                 Date date, String preamble ) {
        
        transfer( fromAcc.getAmount(), fromAcc, toAcc, date, preamble ) ;
    }
    
    public static void transfer( double amt, 
                                 Account fromAcc, Account toAcc, 
                                 Date date, String preamble ) {
        
        if( !(preamble == null || preamble.trim().equals( "" )) ) {
            preamble = preamble + ". " ;
        }
        
        transfer( amt, fromAcc, toAcc,
                  preamble + "Transfer to A/C " + toAcc.getAccountNumber(),
                  preamble + "Transfer from A/C " + fromAcc.getAccountNumber(),
                  date ) ;
    }
    
    public static void transfer( double amt, Account fromAcc, Account toAcc,
                                 String debitDesc, String creditDesc, Date date ) {
        
        Txn debitTxn  = new Txn( fromAcc.getAccountNumber(), -amt, date, debitDesc ) ;
        Txn creditTxn = new Txn( toAcc.getAccountNumber(),    amt, date, creditDesc ) ;
        
        fromAcc.getUniverse().postTransaction( debitTxn ) ;
        fromAcc.getUniverse().postTransaction( creditTxn ) ;
    }
    
    public static String printLedger( Account acct ) {
        
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( StringUtils.repeat( "-", HDR_LEN ) ).append( "\n" ) ;
        buffer.append( StringUtils.center( "Ledger for " + acct.getAccountNumber(), HDR_LEN ) ) 
              .append( "\n" ) ;
        buffer.append( StringUtils.repeat( "-", HDR_LEN ) ).append( "\n" ) ;
        
        buffer.append( StringUtils.center( "Date", FIELD_LEN ) )
              .append( " | " )
              .append( StringUtils.center( "Credit", FIELD_LEN ) )
              .append( " | " )
              .append( StringUtils.center( "Debit", FIELD_LEN ) )
              .append( " | " )
              .append( "Description" )
              .append( "\n" ) ;
        
        buffer.append( StringUtils.repeat( ".", HDR_LEN ) ).append( "\n" ) ;
        
        for( Txn txn : acct.getLedger() ) {
            buffer.append( StringUtils.rightPad( formatDate( txn.getDate() ), FIELD_LEN ) )
                  .append( " | " ) ;
            
            if( txn.getTxnType() == TxnType.CREDIT ) {
                buffer.append( StringUtils.leftPad( Utils.DF.format( txn.getAmount() ), FIELD_LEN ) )
                      .append( " | " )
                      .append( StringUtils.repeat( " ", FIELD_LEN ) ) ;
            }
            else {
                buffer.append( StringUtils.repeat( " ", FIELD_LEN ) )
                      .append( " | " )
                      .append( StringUtils.leftPad( Utils.DF.format( Math.abs( txn.getAmount() ) ), FIELD_LEN ) ) ;
            }
            buffer.append( " | " )
                  .append( txn.getDescription() )
                  .append( "\n" ) ;
        }
        buffer.append( StringUtils.repeat( ".", HDR_LEN ) ).append( "\n" ) ;
        buffer.append( "Total liquidable amount = " + Utils.DF.format( acct.getLiquidableAmount() ) ).append( "\n" ) ;
        buffer.append( StringUtils.repeat( "-", HDR_LEN ) ).append( "\n" ) ;
        
        return buffer.toString() ;
    }
}
