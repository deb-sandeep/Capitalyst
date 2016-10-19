package com.sandy.capitalyst.clock;

import java.util.Date ;

public interface EndOfDayObserver extends TimeObserver {
    public void handleEndOfDayEvent( Date date ) ;
}
