package com.sandy.capitalyst.core;

import com.sandy.capitalyst.cfg.UniverseConfig ;

public interface UniverseConstituent {

    public void setUniverse( Universe u ) ;
    public Universe getUniverse() ;
    
    public void setConfiguration( UniverseConfig config ) ;
    public UniverseConfig getConfiguration() ;
}
