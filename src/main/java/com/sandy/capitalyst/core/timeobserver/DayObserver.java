package com.sandy.capitalyst.core.timeobserver;

import java.util.Date ;

public interface DayObserver extends TimeObserver {
    public void handleDayEvent( Date date ) ;
}
