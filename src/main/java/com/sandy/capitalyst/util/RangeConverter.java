package com.sandy.capitalyst.util;

import org.apache.commons.beanutils.converters.AbstractConverter ;
import org.apache.commons.configuration.ConversionException ;
import org.apache.log4j.Logger ;

public class RangeConverter extends AbstractConverter {

    static final Logger log = Logger.getLogger( RangeConverter.class ) ;
    
    @Override
    protected <T> T convertToType( Class<T> type, Object value )
            throws Throwable {
        if( Range.class.equals( type ) ) {
            return type.cast( createRange( value.toString() ) ) ;
        }
        throw conversionException(type, value);
    }
    
    private Range createRange( String input ) {
       double min=0, max=0, stepVal=0.1;
       
       String[] parts = input.split( ":" ) ;
       if( parts.length != 2 ) {
           throw new ConversionException( "Range syntax invalid for " + input ) ;
       }
       
       min = toDouble( parts[0], "min" ) ;
       
       String[] parts2 = parts[1].trim().split( "@" ) ;
       if( parts2.length != 2 ) {
           throw new ConversionException( "Range syntax invalid for " + input ) ;
       }
       
       max = toDouble( parts2[0], "max" ) ;
       stepVal = toDouble( parts2[1], "stepVal" ) ;
       
       return new Range( min, max, stepVal ) ; 
    }
    
    private double toDouble( String val, String msg ) {

        double number = 0 ;
        try {
            number = Double.parseDouble( val.trim() ) ;
        }
        catch( Exception e ) {
            throw new ConversionException( 
                              "'" + msg + "' value is not a number : " + val ) ;
        }
        return number ;
    }

    @Override
    protected Class<?> getDefaultType() { return null ; }
}
