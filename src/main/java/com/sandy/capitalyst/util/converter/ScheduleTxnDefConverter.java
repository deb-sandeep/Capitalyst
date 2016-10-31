package com.sandy.capitalyst.util.converter;

import org.apache.commons.beanutils.ConvertUtils ;
import org.apache.commons.beanutils.converters.AbstractConverter ;
import org.apache.log4j.Logger ;

import com.cronutils.model.Cron ;
import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.core.amount.Amount ;
import com.sandy.capitalyst.txgen.ScheduledTxnDef ;
import com.sandy.capitalyst.util.Utils ;
import com.sandy.common.util.StringUtil ;

public class ScheduleTxnDefConverter extends AbstractConverter {

    static final Logger log = Logger.getLogger( ScheduleTxnDefConverter.class ) ;
    
    private static int AMT_ID         = 0 ;
    private static int CRON_ID        = 1 ;
    private static int DEBIT_AC_ID    = 2 ;
    private static int CREDIT_AC_ID   = 3 ;
    private static int START_DATE_ID  = 4 ;
    private static int END_DATE_ID    = 5 ;
    private static int DESCRIPTION_ID = 6 ;
    
    @Override
    protected <T> T convertToType( Class<T> type, Object value )
            throws Throwable {
        if( ScheduledTxnDef.class.equals( type ) ) {
            return type.cast( createTxnDef( value.toString() ) ) ;
        }
        throw conversionException(type, value);
    }
    
    // 600@2% :1 * * *:${Sandy.iciciSBAccount}:${Sandy.expenseAC}:::Malhari salary
    // <Amount>:<Cron>:<Debit A/C>:<Credit A/C>:[Start Date]:[End Date]:<Description>
    private ScheduledTxnDef createTxnDef( String input ) {
        log.debug( "Creating ScheduledTxnDef from " + input ) ;
        
        ScheduledTxnDef def = new ScheduledTxnDef() ;
        String[] parts = input.split( ":" ) ;
        
        setAmount     ( def, parts[AMT_ID         ].trim() ) ;
        setCron       ( def, parts[CRON_ID        ].trim() ) ;
        setDebitACNo  ( def, parts[DEBIT_AC_ID    ].trim() ) ;
        setCreditACNo ( def, parts[CREDIT_AC_ID   ].trim() ) ;
        setStartDate  ( def, parts[START_DATE_ID  ].trim() ) ;
        setEndDate    ( def, parts[END_DATE_ID    ].trim() ) ;
        setDescription( def, parts[DESCRIPTION_ID ].trim() ) ;
        
        return def ;
    }
    
    private void setAmount( ScheduledTxnDef def, String input ) {
        log.debug( "\tConverting '" + input + "' to Amount" ) ;
        checkNotEmptyOrNull( "Amount", input ) ;
        
        AmountConverter conv = ( AmountConverter )ConvertUtils.lookup( Amount.class ) ;
        Amount amt = conv.convert( Amount.class, input ) ;
        def.setAmount( amt ) ;
    }
    
    private void setCron( ScheduledTxnDef def, String input ) {
        log.debug( "\tConverting '" + input + "' to Cron" ) ;
        checkNotEmptyOrNull( "Cron", input ) ;
        
        Cron cron = Utils.CRON_PARSER.parse( input ) ;
        ExecutionTime execTime = ExecutionTime.forCron( cron ) ;
        
        def.setExecutionTime( execTime ) ;
    }

    private void setDebitACNo( ScheduledTxnDef def, String input ) {
        log.debug( "\tConverting '" + input + "' to Debit Account" ) ;
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setDebitACNo( input ) ;
        }
    }

    private void setCreditACNo( ScheduledTxnDef def, String input ) {
        log.debug( "\tConverting '" + input + "' to Credit Account" ) ;
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setCreditACNo( input ) ;
        }
    }

    private void setStartDate( ScheduledTxnDef def, String input ) {
        log.debug( "\tConverting '" + input + "' to Start Date" ) ;
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setStartDate( Utils.parseDate( input ) );
        }
    }

    private void setEndDate( ScheduledTxnDef def, String input ) {
        log.debug( "\tConverting '" + input + "' to End Date" ) ;
        if( StringUtil.isNotEmptyOrNull( input ) ) {
            def.setEndDate( Utils.parseDate( input ) );
        }
    }

    private void setDescription( ScheduledTxnDef def, String input ) {
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

    @Override
    protected Class<?> getDefaultType() { return null ; }
}
