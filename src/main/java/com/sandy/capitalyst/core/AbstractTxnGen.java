package com.sandy.capitalyst.core;

import java.util.Date ;

public abstract class AbstractTxnGen implements TxnGenerator {

    @Override
    public void handleDayEvent( Date date, Universe universe ) {
    }

    @Override
    public void handleEndOfDayEvent( Date date, Universe universe ) {
    }
}
