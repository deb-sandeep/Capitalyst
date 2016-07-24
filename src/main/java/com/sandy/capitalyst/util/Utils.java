package com.sandy.capitalyst.util;

import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.Date ;

public class Utils {

    public static final SimpleDateFormat SDF = new SimpleDateFormat( "dd/MM/yyyy" ) ;
    
    public static Date parseDate( String dateStr ) throws IllegalArgumentException {
        try {
            return SDF.parse( dateStr ) ;
        }
        catch( ParseException e ) {
            throw new IllegalArgumentException( "Invalid date specified. " + dateStr ) ;
        }
    }
}
