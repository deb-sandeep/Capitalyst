package com.sandy.capitalyst.core.timeobserver;

import java.util.Date ;

public interface EndOfYearObserver extends TimeObserver {
    public void handleEndOfYearEvent( Date date ) ;
}
