package com.sandy.capitalyst.core.amount;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.timeobservers.EndOfYearObserver ;
import com.sandy.capitalyst.util.Range ;

public abstract class AbstractEOYObservingAmount extends Amount
    implements EndOfYearObserver {

    private Universe universe = null ;
    
    @Cfg protected Range incrementRange = null ;
    
    public void setIncrementRange( Range r ) { this.incrementRange = r ; }
    public Range getIncrementRange() { return this.incrementRange ; }
    
    @Override public void setUniverse( Universe u ) { this.universe = u ; }
    @Override public Universe getUniverse() { return this.universe ; }

    @Override public void setId( String id ) {} // NOP
    @Override public String getId() { return null; } // NOP
}
