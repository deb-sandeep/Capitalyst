package com.sandy.capitalyst.core;

import java.util.Date ;

public interface TimeObserver {

    public void handleDayEvent( Date date, Universe universe ) ;
    public void handleEndOfDayEvent( Date date, Universe universe ) ;
}
