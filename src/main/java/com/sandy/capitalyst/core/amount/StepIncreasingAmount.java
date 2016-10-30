package com.sandy.capitalyst.core.amount;

import java.util.Date ;

public class StepIncreasingAmount extends AbstractEOYObservingAmount {

    public StepIncreasingAmount( double amt ) {
        super( amt ) ;
    }
    
    @Override
    public void handleEndOfYearEvent( Date date ) {
        double step = incrementRange.getRandom() ;
        super.setAmount( super.getAmount()+step );
    }
}
