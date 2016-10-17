package com.sandy.capitalyst.util;

import java.util.Date ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.Map ;

import org.apache.commons.configuration.ConfigurationException ;
import org.apache.commons.configuration.PropertiesConfiguration ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.cfg.InvalidConfigException;
import com.sandy.capitalyst.util.Utils ;

public class Config extends PropertiesConfiguration {

    private static Logger log = Logger.getLogger( Config.class ) ;
    
    private String universeName = null ;
    private static Config instance = null ;
    private static Map<String, Config> instanceMap = new HashMap<String, Config>() ;
    
    private Config() {
    }
    
    private Config( String universeName ) throws ConfigurationException {
    	super( Config.class.getResource( "/cap-" + universeName.toLowerCase() + ".properties" ) ) ;
    	this.universeName = universeName ;
    }
    
    public static Config instance() {
        return instance( null ) ;
    }
    
    public String getUniverseName() {
    	return this.universeName ;
    }
    
    public static Config instance( String universeName ) {
        
        if( universeName == null ) {
            if( instance == null ) {
                try {
                    instance = new Config( "base" ) ;
                }
                catch( Exception e ) {
                    log.error( "Error loading configuration", e ) ;
                }
            }
            return instance ;
        }
        else {
            Config cfg = instanceMap.get( universeName ) ;
            if( cfg == null ) {
                try {
                    cfg = new Config( universeName ) ;
                    instanceMap.put( universeName, cfg ) ;
                }
                catch( ConfigurationException e ) {
                    log.error( "Configuration for universe " + universeName + 
                               " not found.", e ) ;
                }
            }
            return cfg ;
        }
    }
    
    public Date getDate( String key ) {
    	
        String val = instance.getString( key ) ;
        Date retVal = null ;
        if( val != null ) {
            try {
				retVal = Utils.parseDate( val ) ;
			} 
            catch ( IllegalArgumentException e ) {
            	throw new InvalidConfigException( this, key ) ;
			}
        }
        return retVal ;
    }
    
    public Config getNestedConfig( String prefix ) {
        
        Config config = new Config() ;
        Iterator<String> iter = this.getKeys( prefix ) ;
        
        while( iter.hasNext() ) {
            String key = iter.next() ;
            String newKey = key.substring( prefix.length() ) ;
            
            if( newKey.startsWith( "." ) ) {
                newKey = newKey.substring( 1 ) ;
            }
            config.addProperty( newKey, this.getProperty( key ) ) ;
        }
        
        return config ;
    }
}
