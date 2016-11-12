package com.sandy.capitalyst.factory;

import org.apache.commons.beanutils.ConvertUtils ;
import org.apache.log4j.Logger ;

import com.cronutils.model.Cron ;
import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.core.amount.Amount ;
import com.sandy.capitalyst.txgen.ScheduledTxnGen ;
import com.sandy.capitalyst.util.Utils ;
import com.sandy.capitalyst.util.converter.AmountConverter ;
import com.sandy.common.util.StringUtil ;

public class ScheduleTxnGenConverter {

    static final Logger log = Logger.getLogger( ScheduleTxnGenConverter.class ) ;
    
    public static int NUM_COLS = 8 ;
    
    private static int CLASSIFIER_ID  = 0 ;
    private static int AMT_ID         = 1 ;
    private static int DESCRIPTION_ID = 2 ;
    private static int CRON_ID        = 3 ;
    private static int DEBIT_AC_ID    = 4 ;
    private static int CREDIT_AC_ID   = 5 ;
    private static int START_DATE_ID  = 6 ;
    private static int END_DATE_ID    = 7 ;
    
    // 600@2% :1 * * *:${Sandy.iciciSBAccount}:${Sandy.expenseAC}:::Malhari salary
    // <Amount>:<Cron>:<Debit A/C>:<Credit A/C>:[Start Date]:[End Date]:<Description>
    public ScheduledTxnGen createTxnGen( String input ) {
        log.debug( "Creating ScheduledTxnGen from " + input ) ;
        
        input = input.replaceAll( ";", "," ) ;
        
        ScheduledTxnGen def = new ScheduledTxnGen() ;
        String[] parts = input.split( ":" ) ;
        
        setClassifier ( def, parts[CLASSIFIER_ID  ].trim() ) ;
        setAmount     ( def, parts[AMT_ID         ].trim() ) ;
        setDescription( def, parts[DESCRIPTION_ID ].trim() ) ;
        setCron       ( def, parts[CRON_ID        ].trim() ) ;
        setDebitACNo  ( def, parts[DEBIT_AC_ID    ].trim() ) ;
        setCreditACNo ( def, parts[CREDIT_AC_ID   ].trim() ) ;
        setStartDate  ( def, parts[START_DATE_ID  ].trim() ) ;
        setEndDate    ( def, parts[END_DATE_ID    ].trim() ) ;
        
        return def ;
    }
    
    private void setClassifier( ScheduledTxnGen def, String input ) {
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setClassifiers( input ) ;
        }
    }
    
    private void setAmount( ScheduledTxnGen def, String input ) {
        log.debug( "\tConverting '" + input + "' to Amount" ) ;
        checkNotEmptyOrNull( "Amount", input ) ;
        
        AmountConverter conv = ( AmountConverter )ConvertUtils.lookup( Amount.class ) ;
        Amount amt = conv.convert( Amount.class, input ) ;
        def.setAmount( amt ) ;
    }
    
    private void setCron( ScheduledTxnGen def, String input ) {
        log.debug( "\tConverting '" + input + "' to Cron" ) ;
        checkNotEmptyOrNull( "Cron", input ) ;
        
        Cron cron = Utils.CRON_PARSER.parse( input ) ;
        ExecutionTime execTime = ExecutionTime.forCron( cron ) ;
        
        def.setExecutionTime( execTime ) ;
    }

    private void setDebitACNo( ScheduledTxnGen def, String input ) {
        log.debug( "\tConverting '" + input + "' to Debit Account" ) ;
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setDebitACNo( input ) ;
        }
    }

    private void setCreditACNo( ScheduledTxnGen def, String input ) {
        log.debug( "\tConverting '" + input + "' to Credit Account" ) ;
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setCreditACNo( input ) ;
        }
    }

    private void setStartDate( ScheduledTxnGen def, String input ) {
        log.debug( "\tConverting '" + input + "' to Start Date" ) ;
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setStartDate( Utils.parseDate( input ) );
        }
    }

    private void setEndDate( ScheduledTxnGen def, String input ) {
        log.debug( "\tConverting '" + input + "' to End Date" ) ;
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setEndDate( Utils.parseDate( input ) );
        }
    }

    private void setDescription( ScheduledTxnGen def, String input ) {
        log.debug( "\tConverting '" + input + "' to Description" ) ;
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setDescription( input ) ;
        }
    }
    
    private void checkNotEmptyOrNull( String field, String input ) {
        if( StringUtil.isEmptyOrNull( input ) ) {
            throw new IllegalArgumentException( "Value of " + field +  
                                                " field can't be empty" ) ;
        }
    }
}
