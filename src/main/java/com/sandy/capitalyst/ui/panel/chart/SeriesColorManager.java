package com.sandy.capitalyst.ui.panel.chart;

import java.awt.Color ;
import java.util.HashMap ;
import java.util.Map ;

public class SeriesColorManager {

    private static SeriesColorManager instance = new SeriesColorManager() ;
    
    private Map<String, Color> seriesColorMap = new HashMap<String, Color>() ;
    
    private Color[] colors = {
        c( "E43117" ),
        c( "1531EC" ),
        c( "F67DFA" ),
        c( "980517" ),
        c( "C34A2C" ),
        c( "5FFB17" ),
        c( "E55451" ),
        c( "7F5217" ),
        c( "B3B3B1" ),
        c( "CCFB5D" ),
        c( "CCFB5D" ),
    } ;
    
    public static Color c( String colorStr ) {
        return new Color(
                Integer.valueOf( colorStr.substring( 0, 2 ), 16 ),
                Integer.valueOf( colorStr.substring( 2, 4 ), 16 ),
                Integer.valueOf( colorStr.substring( 4, 6 ), 16 ) );
    }    
    
    private int nextColorIndex = 0 ;
    
    public static SeriesColorManager instance() {
        return instance ;
    }
    
    public Color getColor( String key ) {
        Color color = seriesColorMap.get( key ) ;
        if( color == null ) {
            color = getNextColor() ;
            seriesColorMap.put( key, color ) ;
        }
        return color ;
    }
    
    private Color getNextColor() {
        if( this.nextColorIndex >= colors.length ) {
            this.nextColorIndex = 0 ;
        }
        return colors[nextColorIndex++] ;
    }
}
