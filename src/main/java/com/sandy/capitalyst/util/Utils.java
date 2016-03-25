package com.sandy.capitalyst.util;

import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;

public class Utils {
    
    public static final SimpleDateFormat SDF = new SimpleDateFormat( "MM/yyyy" ) ;
    
    public static Date parse( String dateStr ) throws ParseException {
        if( dateStr == null ) return new Date() ;
        return SDF.parse( dateStr ) ;
    }
    
    public static int compare( Date d1, Date d2 ) {
        return org.apache.commons.lang.time.DateUtils.truncatedCompareTo( d1, d2, Calendar.MONTH ) ;
    }
}
