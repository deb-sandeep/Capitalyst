package com.sandy.capitalyst.core.amount;

import java.util.Date ;

public class StepIncreasingAmount extends AbstractEOYObservingAmount {

    @Override
    public void handleEndOfYearEvent( Date date ) {
        double step = incrementRange.getRandom() ;
        super.setAmount( super.getAmount()+step );
    }
}
