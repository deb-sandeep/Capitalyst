package com.sandy.capitalyst.txgen;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Universe ;


public abstract class AbstractTxnGen implements TxnGenerator {
    
    private Universe universe = null ;
    
    @Cfg( mandatory=false ) private String name = "UNNAMED" ;
    
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
