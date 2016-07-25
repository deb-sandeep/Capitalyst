package com.sandy.capitalyst.util;

import java.text.DecimalFormat ;
import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.Date ;

import org.apache.commons.lang.StringUtils ;
import org.apache.commons.lang.time.DateUtils ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Txn.TxnType ;

public class Utils {

    public static final SimpleDateFormat SDF = new SimpleDateFormat( "dd/MM/yyyy" ) ;
    public static final DecimalFormat     DF = new DecimalFormat( "##.0" ) ;
    
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
                      .append( StringUtils.leftPad( Utils.DF.format( txn.getAmount() ), FIELD_LEN ) ) ;
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
