package com.sandy.capitalyst;

import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.Date ;

public class CapitalystUtils {

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
