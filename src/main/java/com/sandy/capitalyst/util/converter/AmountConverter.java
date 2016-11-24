package com.sandy.capitalyst.util.converter;

import org.apache.commons.beanutils.converters.AbstractConverter ;
import org.apache.commons.configuration.ConversionException ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.amount.Amount ;
import com.sandy.capitalyst.core.amount.ConstantAmount ;
import com.sandy.capitalyst.core.amount.InflatingAmount ;
import com.sandy.capitalyst.core.amount.StepIncreasingAmount ;
import com.sandy.capitalyst.util.Range ;

public class AmountConverter extends AbstractConverter {

    static final Logger log = Logger.getLogger( AmountConverter.class ) ;
    
    private Universe universe = null ;
    
    public AmountConverter( Universe u ) {
        this.universe = u ;
    }
    
    @Override
    protected <T> T convertToType( Class<T> type, Object value )
            throws Throwable {
        if( Amount.class.equals( type ) ) {
            return type.cast( createAmount( value.toString() ) ) ;
        }
        throw conversionException(type, value);
    }
    
    private Amount createAmount( String input ) {
       
       Amount amt = null ;
       double baseAmount = 0 ;
       
       String[] parts = input.split( "@" ) ;
       if( parts.length ==0 || parts.length > 2 ) {
           throw new ConversionException( "Range syntax invalid for " + input ) ;
       }
       
       baseAmount = toDouble( parts[0], "baseAmount" ) ;
       
       if( parts.length == 1 ) {
           if( !input.endsWith( "@" ) ) {
               amt = new ConstantAmount() ;
               amt.setBaseAmount( baseAmount ) ;
           }
           else {
               amt = createAmtWithUniverseRateOfInflation( baseAmount ) ;
           }
       }
       else {
           if( input.endsWith( "%" ) ) {
               amt = createAmtWithInflationRates( baseAmount, parts[1].trim() ) ;
           }
           else {
               amt = createAmtWithStepBounds( baseAmount, parts[1].trim() ) ;
           }
       }
       
       amt.setCreationString( input ) ;
       return amt ;
    }
    
    private Amount createAmtWithUniverseRateOfInflation( double baseAmount ) {
        InflatingAmount amt = new InflatingAmount() ;
        amt.setBaseAmount( baseAmount ) ;
        amt.setIncrementRange( universe.getInflationRate() ) ;
        universe.registerTimeObserver( amt ) ;
        return amt ;
    }
    
    private Amount createAmtWithInflationRates( double baseAmount, String minMaxStr ) {
        String input = minMaxStr.substring( 0, minMaxStr.length()-1 ) ;
        InflatingAmount amt = new InflatingAmount() ;
        amt.setBaseAmount( baseAmount ) ;
        amt.setIncrementRange( createRange( input ) ) ;
        universe.registerTimeObserver( amt ) ;
        return amt ;
    }
    
    private Amount createAmtWithStepBounds( double baseAmount, String rangeInput ) {
        StepIncreasingAmount amt = new StepIncreasingAmount() ;
        amt.setBaseAmount( baseAmount ) ;
        amt.setIncrementRange( createRange( rangeInput ) ) ;
        universe.registerTimeObserver( amt ) ;
        return amt ;
    }
    
    private Range createRange( String input ) {

        double min=0, max=0 ;
        String parts[] = input.split( "-" ) ;
        
        if( parts.length == 1 ) {
            min = toDouble( parts[0], "minRate" ) ;
            max = min ;
        }
        else {
            min = toDouble( parts[0], "minRate" ) ;
            max = toDouble( parts[1], "maxRate" ) ;
        }
        return new Range( min, max, 0.1 ) ;
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
