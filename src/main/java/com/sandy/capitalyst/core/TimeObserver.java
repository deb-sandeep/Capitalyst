package com.sandy.capitalyst.core;

import java.util.Date ;

public interface TimeObserver {

    public void handleDateEvent( Date date ) throws Exception ;
}
