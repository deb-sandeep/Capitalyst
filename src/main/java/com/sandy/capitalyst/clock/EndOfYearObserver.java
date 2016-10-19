package com.sandy.capitalyst.clock;

import java.util.Date ;

public interface EndOfYearObserver extends TimeObserver {
    public void handleEndOfYearEvent( Date date ) ;
}
