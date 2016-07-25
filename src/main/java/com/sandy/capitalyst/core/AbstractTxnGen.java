package com.sandy.capitalyst.core;

import java.util.Date ;

public abstract class AbstractTxnGen implements TxnGenerator {
    
    private String name = null ;
    
    public AbstractTxnGen( String name ) {
        this.name = name ;
    }
    
    public String getName() {
        return this.name ;
    }

    @Override
    public void handleDayEvent( Date date, Universe universe ) {
    }

    @Override
    public void handleEndOfDayEvent( Date date, Universe universe ) {
    }
}
