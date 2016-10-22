package com.sandy.capitalyst.timeobservers;

import java.util.Date ;

public interface DayObserver extends TimeObserver {
    public void handleDayEvent( Date date ) ;
}
