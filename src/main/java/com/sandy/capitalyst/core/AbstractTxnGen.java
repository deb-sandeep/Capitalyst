package com.sandy.capitalyst.core;


public abstract class AbstractTxnGen implements TxnGenerator {
    
    private Universe universe = null ;
    private String name = "UNNAMED" ;
    
    public void setUniverse( Universe universe ) {
        this.universe = universe ;
    }
    
    public Universe getUniverse() {
        return this.universe ;
    }
    
    public String getName() {
        return this.name ;
    }
    
    public void setName( String name ) {
        this.name = name ;
    }
}
