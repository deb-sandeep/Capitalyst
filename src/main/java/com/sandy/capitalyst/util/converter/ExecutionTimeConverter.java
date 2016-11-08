package com.sandy.capitalyst.util.converter;

import org.apache.commons.beanutils.converters.AbstractConverter ;
import org.apache.log4j.Logger ;

import com.cronutils.model.Cron ;
import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.util.Utils ;
import com.sandy.common.util.StringUtil ;

public class ExecutionTimeConverter extends AbstractConverter {

    static final Logger log = Logger.getLogger( ExecutionTimeConverter.class ) ;
    
    @Override
    protected <T> T convertToType( Class<T> type, Object value )
            throws Throwable {
        if( ExecutionTime.class.equals( type ) ) {
            return type.cast( createExecutionTime( value.toString() ) ) ;
        }
        throw conversionException(type, value);
    }
    
    private ExecutionTime createExecutionTime( String input ) {
        
        log.debug( "\tConverting '" + input + "' to Cron" ) ;
        checkNotEmptyOrNull( "Cron", input ) ;
        
        Cron cron = Utils.CRON_PARSER.parse( input ) ;
        return ExecutionTime.forCron( cron ) ;
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
