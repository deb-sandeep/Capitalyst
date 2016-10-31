
package com.sandy.capitalyst.util;

import java.text.DecimalFormat ;
import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.time.Duration ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.Locale ;

import org.apache.commons.lang.time.DateUtils ;

import com.cronutils.model.definition.CronDefinition ;
import com.cronutils.model.definition.CronDefinitionBuilder ;
import com.cronutils.parser.CronParser ;
import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Txn ;

public class Utils {

    public static final SimpleDateFormat SDF = new SimpleDateFormat( "dd/MM/yyyy" ) ;
    public static final DecimalFormat     DF = new DecimalFormat( "0.0" ) ;
    private static final CronDefinition CRON_DEF =
                            CronDefinitionBuilder.defineCron()
                                .withDayOfMonth()
                                .supportsHash().supportsL().supportsW().and()
                                .withMonth().and()
                                .withDayOfWeek()
                                .withIntMapping(7, 0) 
                                .supportsHash().supportsL().supportsW().and()
                                .withYear().and()
                                .lastFieldOptional()
                                .instance() ;
    public static final CronParser  CRON_PARSER = new CronParser( CRON_DEF ) ;
    
    public static Date parseDate( String dateStr ) throws IllegalArgumentException {
        try {
            return SDF.parse( dateStr ) ;
        }
        catch( ParseException e ) {
            throw new IllegalArgumentException( "Invalid date specified. " + dateStr ) ;
        }
    }
    
    public static String formatLakh( double d ) {
        
        String s = String.format( Locale.UK, "%1.2f", Math.abs(d) ) ;
        s = s.replaceAll( "(.+)(...\\...)", "$1,$2" ) ;
        while( s.matches("\\d{3,},.+") ) {
            s = s.replaceAll( "(\\d+)(\\d{2},.+)", "$1,$2" ) ;
        }
        s = s.substring( 0, s.indexOf( '.' ) ) ;
        return d < 0 ? ("-" + s) : s;
    }    
    
    public static int getMonth( Date date ) {
        return DateUtils.toCalendar( date ).get( Calendar.MONTH ) ;
    }
    
    public static int getYear( Date date ) {
        return DateUtils.toCalendar( date ).get( Calendar.YEAR ) ;
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
    
    public static boolean isEndOfMonth( Date date ) {
        return isEndOfMonth( DateUtils.toCalendar( date ) ) ;
    }
    
    public static boolean isEndOfYear( Date date ) {
        return isEndOfYear( DateUtils.toCalendar( date ) ) ;
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
        
        transfer( fromAcc, toAcc, fromAcc.getUniverse().now(), "" ) ;
    }

    public static void transfer( Account fromAcc, Account toAcc, 
                                 Date date, String preamble ) {
        
        transfer( fromAcc.getAmount(), fromAcc, toAcc, date, preamble ) ;
    }
    
    public static void transfer( double amt, 
                                 Account fromAcc, Account toAcc, 
                                 Date date, String preamble ) {
        
        if( preamble == null ) {
            preamble = "" ;
        }
        else if( !(preamble == null || preamble.trim().equals( "" )) ) {
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
}
