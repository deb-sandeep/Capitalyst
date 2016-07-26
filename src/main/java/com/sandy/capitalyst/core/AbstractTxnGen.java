package com.sandy.capitalyst.core;


public abstract class AbstractTxnGen implements TxnGenerator {
    
    private Universe universe = null ;
    private String name = null ;
    
    public AbstractTxnGen( String name ) {
        this.name = name ;
    }
    
    public void setUniverse( Universe universe ) {
        this.universe = universe ;
    }
    
    public Universe getUniverse() {
        return this.universe ;
    }
    
    public String getName() {
        return this.name ;
    }
}
