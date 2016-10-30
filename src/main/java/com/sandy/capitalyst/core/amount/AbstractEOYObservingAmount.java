package com.sandy.capitalyst.core.amount;

import org.apache.commons.lang.NotImplementedException ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.timeobservers.EndOfYearObserver ;
import com.sandy.capitalyst.util.Range ;

public abstract class AbstractEOYObservingAmount implements Amount, EndOfYearObserver {

    private Universe universe = null ;
    private double amount = 0 ;
    
    protected Range incrementRange = null ;
    
    public AbstractEOYObservingAmount( double amt ) {
        this.amount = amt ;
    }
    
    public void setIncrementRange( Range r ) {
        this.incrementRange = r ;
    }
    
    public void setAmount( double amt ) {
        this.amount = amt ;
    }
    
    @Override
    public double getAmount() {
        return amount ;
    }

    @Override
    public void setUniverse( Universe u ) {
        this.universe = u ;
    }

    @Override
    public Universe getUniverse() {
        return this.universe ;
    }

    @Override
    public void setId( String id ) {
        throw new NotImplementedException( "This method should not be called." ) ;
    }

    @Override
    public String getId() {
        throw new NotImplementedException( "This method should not be called." ) ;
    }
}
