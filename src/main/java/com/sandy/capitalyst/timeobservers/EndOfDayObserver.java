package com.sandy.capitalyst.timeobservers;

import java.util.Date ;

public interface EndOfDayObserver extends TimeObserver {
    public void handleEndOfDayEvent( Date date ) ;
}
