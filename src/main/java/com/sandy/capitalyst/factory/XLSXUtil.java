package com.sandy.capitalyst.factory;

import org.apache.poi.ss.usermodel.Cell ;
import org.apache.poi.ss.usermodel.Row ;

import com.sandy.common.util.StringUtil ;

public class XLSXUtil {

    public static boolean isIgnorableRow( Row row ) {
        
        if( row != null ) {
            Cell cell1 = row.getCell( 0 ) ;
            Cell cell2 = row.getCell( 1 ) ;
            
            if( cell1 != null ) {
                String content = cell1.toString().trim() ;
                if( content.startsWith( "#" ) || 
                    content.startsWith( "[H]" ) ) {
                    return true ;
                }
            }
            
            if( cell2 != null ) {
                String content = cell2.toString() ;
                if( StringUtil.isNotEmptyOrNull( content ) ) {
                    return false ;
                }
            }
        }
        return true ;
    }
}
