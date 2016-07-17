package com.sandy.cst;

import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

public class CapitalystTimer {

    private static final SimpleDateFormat SDF = new SimpleDateFormat( "dd/MM/YYYY" ) ;
    
    private Date startDate = null ;
    
    private List<TimeObserver> observers = new ArrayList<TimeObserver>() ;
    
    public CapitalystTimer( String initDateStr ) throws ParseException {
        startDate = SDF.parse( initDateStr ) ;
    }
    
    public void run( String tillDateStr ) {
        
    }
}
