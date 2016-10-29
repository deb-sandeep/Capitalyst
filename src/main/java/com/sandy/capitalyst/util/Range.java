package com.sandy.capitalyst.util;

import java.util.concurrent.ThreadLocalRandom ;

public class Range {
    
    private double minValue = 0 ;
    private double maxValue = 0 ;
    private double stepValue = 0 ;
    
    public Range( double min, double max, double step ) {
        this.minValue = min ;
        this.maxValue = max ;
        this.stepValue = step ;
    }
    
    public double getMin() {
        return this.minValue ;
    }
    
    public double getMax() {
        return this.maxValue ;
    }
    
    public double getRandom() {
        
        int numSteps  = (int)(( getMax() - getMin() )/stepValue) ;
        int randSteps = ThreadLocalRandom.current().nextInt( 0, numSteps+1 ) ;
        
        double value = getMin() + randSteps*stepValue ;
        return value ;
    }
    
    public String toString() {
        StringBuilder buffer = new StringBuilder() ;
        buffer.append( "Range [" )
              .append( "min=" + minValue )
              .append( ",max=" + maxValue )
              .append( "@stepVal=" + stepValue )
              .append( "]" ) ;
        return buffer.toString() ;
    }
}
