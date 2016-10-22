package com.sandy.capitalyst.timeobservers;

import java.util.Date ;

public interface EndOfQuarterObserver extends TimeObserver {
    public void handleEndOfQuarterEvent( Date date ) ;
}
