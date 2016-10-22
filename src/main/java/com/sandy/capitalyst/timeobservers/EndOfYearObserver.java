package com.sandy.capitalyst.timeobservers;

import java.util.Date ;

public interface EndOfYearObserver extends TimeObserver {
    public void handleEndOfYearEvent( Date date ) ;
}
