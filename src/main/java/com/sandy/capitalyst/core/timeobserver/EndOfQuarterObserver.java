package com.sandy.capitalyst.core.timeobserver;

import java.util.Date ;

public interface EndOfQuarterObserver extends TimeObserver {
    public void handleEndOfQuarterEvent( Date date ) ;
}
