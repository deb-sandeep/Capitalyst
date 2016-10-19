package com.sandy.capitalyst.clock;

import java.util.Date ;

public interface EndOfQuarterObserver extends TimeObserver {
    public void handleEndOfQuarterEvent( Date date ) ;
}
