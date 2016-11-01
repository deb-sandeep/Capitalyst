package com.sandy.capitalyst.poc;

import java.io.InputStream ;

import org.apache.log4j.Logger ;
import org.apache.poi.ss.usermodel.Row ;
import org.apache.poi.ss.usermodel.Sheet ;
import org.apache.poi.ss.usermodel.Workbook ;
import org.apache.poi.xssf.usermodel.XSSFWorkbook ;

public class POIPOC {

    static Logger log = Logger.getLogger( POIPOC.class ) ;
    
    private static String resName = "/sample.xlsx" ;
    
    public void execute() throws Exception {
    
        InputStream is = POIPOC.class.getResourceAsStream( resName ) ;
        Workbook workbook = new XSSFWorkbook( is ) ;
        
        Sheet sheet = workbook.getSheetAt( 0 ) ;
        log.debug( "First row = " + sheet.getFirstRowNum() );
        log.debug( "Last row  = " + sheet.getLastRowNum() );
        
        for( int i=0; i<=sheet.getLastRowNum(); i++ ) {
            Row row = sheet.getRow( i ) ;
            for( int c=0; c<7; c++ ) {
                log.debug( row.getCell( c ) ) ;
            }
            log.debug( "" );
        }
    }
    
    public static void main( String[] args ) 
        throws Exception {
        
        new POIPOC().execute() ;
    }
}
