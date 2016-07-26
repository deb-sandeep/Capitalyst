package com.sandy.capitalyst.core.timeobserver;

import java.util.Date ;

public interface EndOfDayObserver extends TimeObserver {
    public void handleEndOfDayEvent( Date date ) ;
}
