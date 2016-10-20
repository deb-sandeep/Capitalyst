package com.sandy.capitalyst.junit.integration;

import org.junit.Test ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.UniverseLoader ;

public class BasicTest {

    private Universe universe = null ;
    
    public void setUp( String universeName ) throws Exception {
        universe = UniverseLoader.loadUniverse( universeName ) ;
    }

    @Test
    public void basic() throws Exception {
        setUp( "test-1" ) ;
    }
}
