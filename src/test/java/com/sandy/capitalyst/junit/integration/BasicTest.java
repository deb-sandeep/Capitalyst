package com.sandy.capitalyst.junit.integration;

import org.apache.log4j.Logger ;
import org.junit.Test ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.UniverseLoader ;
import com.sandy.capitalyst.util.LedgerUtils ;

public class BasicTest {

    static Logger log = Logger.getLogger( BasicTest.class ) ;
    
    private Universe universe = null ;
    
    public void setUp( String universeName ) throws Exception {
        UniverseLoader loader = new UniverseLoader( universeName ) ;
        universe = loader.loadUniverse() ;
    }

    @Test
    public void basic() throws Exception {
        setUp( "test-1" ) ;
        universe.runSimulation() ;
        log.debug( LedgerUtils.getFormattedLedger( universe.getAccount( "1234" ) ) ) ;
        log.debug( LedgerUtils.getFormattedLedger( universe.getAccount( "5678" ) ) ) ;
    }
}
