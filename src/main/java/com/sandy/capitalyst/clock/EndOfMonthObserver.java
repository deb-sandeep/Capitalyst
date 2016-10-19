package com.sandy.capitalyst.clock;

import java.util.Date ;

public interface EndOfMonthObserver extends TimeObserver {
    public void handleEndOfMonthEvent( Date date ) ;
}
