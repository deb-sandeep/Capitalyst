package com.sandy.capitalyst.core.amount;

import java.util.Date ;

public class InflatingAmount extends AbstractEOYObservingAmount {

    public InflatingAmount( double amt ) {
        super( amt ) ;
    }
    
    @Override
    public void handleEndOfYearEvent( Date date ) {
        double rate = 0 ;
        if( incrementRange == getUniverse().getInflationRate() ) {
            rate = getUniverse().getCurrentInflationRate() ;
        }
        else {
            rate = incrementRange.getRandom() ;
        }
        super.setAmount( super.getAmount()*(1+rate/100) );
    }
}
