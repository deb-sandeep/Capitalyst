package com.sandy.capitalyst.clock;

import java.util.Date ;

public interface DayObserver extends TimeObserver {
    public void handleDayEvent( Date date ) ;
}
