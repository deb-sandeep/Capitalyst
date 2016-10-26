package com.sandy.capitalyst.txgen;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.core.Universe ;


public abstract class AbstractTxnGen implements TxnGenerator {
    
    private Universe universe = null ;
    private UniverseConfig config = null ;
    
    @Cfg( mandatory=false )
    private String classifiers = null ;
    
    @Cfg( mandatory=false ) 
    private String name = "<TxGen-UNNAMED>" ;
    
    public void setUniverse( Universe universe ) {
        this.universe = universe ;
    }
    
    public Universe getUniverse() {
        return this.universe ;
    }
    
    public void setConfiguration( UniverseConfig config ) {
        this.config = config ;
    }
    
    public UniverseConfig getConfiguration() {
        return this.config ;
    }
    
    public String getName() {
        return this.name ;
    }
    
    public void setName( String name ) {
        this.name = name ;
    }

    public void setClassifiers( String classifiers ) {
        this.classifiers = classifiers ;
    }
    
    public String getClassifiers() {
        return this.classifiers ;
    }
    
    public String toString() {
        return this.name ;
    }
}
