package com.sandy.capitalyst.core.timeobserver;

import java.util.Date ;

public interface EndOfMonthObserver extends TimeObserver {
    public void handleEndOfMonthEvent( Date date ) ;
}
