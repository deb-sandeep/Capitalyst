package com.sandy.capitalyst.timeobservers;

import java.util.Date ;

public interface EndOfMonthObserver extends TimeObserver {
    public void handleEndOfMonthEvent( Date date ) ;
}
