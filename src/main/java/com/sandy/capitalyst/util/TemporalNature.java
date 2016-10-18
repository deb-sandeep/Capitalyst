package com.sandy.capitalyst.util;

import java.util.Calendar ;
import java.util.Date ;

import org.apache.log4j.Logger ;

public class TemporalNature {

    private static final Logger log = Logger.getLogger( TemporalNature.class ) ;
    
    public interface Callback {
        public void handleCallback( TemporalNature nature ) ;
    }
    
    private Callback callback = null ;
    
    private String natureType = null ;
    private int periodicity   = 1 ;
    private float minIncrement = -1 ;
    private float maxIncrement = -1 ;
    private boolean incrementAsPct = true ;
    private int yearEndMonth = Calendar.MARCH ;
    
    public TemporalNature( String config ) {
        log.debug( config ) ;
    }
    
    public void setCallback( Callback callback ) {
        this.callback = callback ;
    }
    
    public void handleDateEvent( Date date ) {
        callback.handleCallback( this ) ;
    }
}
