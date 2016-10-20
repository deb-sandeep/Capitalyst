package com.sandy.capitalyst.poc;

import java.util.concurrent.ThreadLocalRandom ;

public class RangeRand {

    public static void main( String[] args ) {
        int rand = ThreadLocalRandom.current().nextInt( 5, 6 ) ;
        System.out.println( rand ) ;
    }

}
