
package com.sandy.capitalyst.util;

import java.text.DecimalFormat ;
import java.util.List ;

import org.apache.commons.lang.StringUtils ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Txn.TxnType ;
import com.sandy.capitalyst.ui.panel.ledger.LedgerTableModel.LedgerEntry ;

public class LedgerUtils {

    public static final DecimalFormat     DF = new DecimalFormat( "00" ) ;
    
    private static final int HDR_LEN    = 80 ;
    private static final int FIELD_LEN  = 10 ;
    
    public static String getFormattedLedger( Account acct ) {
        
        StringBuilder buffer = new StringBuilder() ;
        appendHeader( buffer, acct ) ;
        
        double balance = acct.getOpeningBalance() ;
        printOpeningBalance( buffer, acct ) ;

        for( Txn txn : acct.getLedger() ) {
            balance = printTxnRow( buffer, txn, balance ) ;
        }
        appendFooter( buffer, acct ) ;
        return buffer.toString() ;
    }
    
    public static String getFormattedLedger( Account account, 
                                             List<LedgerEntry> entries ) {
        
        StringBuilder buffer = new StringBuilder() ;
        
        if( entries.isEmpty() ) {
            buffer.append( "No ledger entries found" ) ;
            return buffer.toString() ;
        }
        
        appendHeader( buffer, account ) ;
        for( LedgerEntry entry : entries ) {
            printTxnRow( buffer, entry.getTxn(), entry.getAccountBalance() ) ;
        }
        appendSeparatorLine( buffer ) ;
        
        return buffer.toString() ;
    }
    
    private static void appendSeparatorLine( StringBuilder buffer ) {
        buffer.append( StringUtils.repeat( "-", HDR_LEN ) ).append( "\n" ) ;
    }
    
    private static void appendHeader( StringBuilder buffer, Account acct ) {
        
        appendSeparatorLine( buffer ) ;
        
        String headerText = "Ledger for " + 
                            acct.getAccountNumber() +
                            " (" + acct.getName() + ")" ;
        
        buffer.append( StringUtils.center( headerText, HDR_LEN ) ) 
              .append( "\n" ) ;

        appendSeparatorLine( buffer ) ;
        
        buffer.append( StringUtils.center( "Date", FIELD_LEN ) )
              .append( " | " )
              .append( StringUtils.center( "Credit", FIELD_LEN ) )
              .append( " | " )
              .append( StringUtils.center( "Debit", FIELD_LEN ) )
              .append( " | " )
              .append( StringUtils.center( "Balance", FIELD_LEN ) )
              .append( " | " )
              .append( "Description" )
              .append( "\n" ) ;
        
        appendSeparatorLine( buffer ) ;
    }
    
    private static void appendFooter( StringBuilder buffer, Account acct ) {
        
        appendSeparatorLine( buffer ) ;
        
        buffer.append( "Total liquidable amount = " )
              .append( DF.format( acct.getLiquidableAmount() ) )
              .append( "\n" ) ;

        appendSeparatorLine( buffer ) ;
    }
    
    private static void printOpeningBalance( StringBuilder buffer, Account acct ) {

        buffer.append( StringUtils.repeat( " ", FIELD_LEN ) )
              .append( " | " )
              .append( StringUtils.repeat( " ", FIELD_LEN ) )
              .append( " | " )
              .append( StringUtils.repeat( " ", FIELD_LEN ) )
              .append( " | " )
              .append( StringUtils.leftPad( DF.format( Math.abs( acct.getOpeningBalance() ) ), FIELD_LEN ) )
              .append( " | Opening Balance" )
              .append( "\n" ) ;
    }
    
    private static double printTxnRow( StringBuilder buffer, Txn txn, double balance ) {
        
        String dateString = "" ;
        if( txn.getDate() != null ) {
            dateString = Utils.formatDate( txn.getDate() ) ;
        }

        buffer.append( StringUtils.rightPad( dateString, FIELD_LEN ) )
              .append( " | " ) ;
          
        if( txn.getTxnType() == TxnType.CREDIT ) {
            buffer.append( StringUtils.leftPad( DF.format( txn.getAmount() ), FIELD_LEN ) )
                  .append( " | " )
                  .append( StringUtils.repeat( " ", FIELD_LEN ) ) ;
        }
        else {
            buffer.append( StringUtils.repeat( " ", FIELD_LEN ) )
                  .append( " | " )
                  .append( StringUtils.leftPad( DF.format( Math.abs( txn.getAmount() ) ), FIELD_LEN ) ) ;
        }

        balance += txn.getAmount() ;
          
        buffer.append( " | " )
              .append( StringUtils.leftPad( DF.format( Math.abs( balance ) ), FIELD_LEN ) ) ;

        buffer.append( " | " )
              .append( txn.getDescription() )
              .append( "\n" ) ;
        
        return balance ;
    }
}
