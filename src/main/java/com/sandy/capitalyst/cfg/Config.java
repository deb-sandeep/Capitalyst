package com.sandy.capitalyst.cfg;

import java.net.URL ;
import java.util.Date ;
import java.util.Iterator ;

import org.apache.commons.configuration.CompositeConfiguration ;
import org.apache.commons.configuration.PropertiesConfiguration ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.util.Utils ;

public class Config extends CompositeConfiguration {

    static Logger log = Logger.getLogger( Config.class ) ;
    
    private String universeName = null ;
    private static Config instance = new Config() ;
    
    public static Config instance() {
        return instance ;
    }
    
    public void initialize( String universeName ) 
        throws Exception {
        
        instance.clear() ;
        this.universeName = universeName ;

        URL baseCfgURL = Config.class.getResource( "/cap-base.properties" ) ;
        if( baseCfgURL == null ) {
            throw new IllegalStateException( "Base configuration not found." ) ;
        }
        
        String univCfgName = "/cap-" + universeName + ".properties" ;
        URL univCfgURL = Config.class.getResource( univCfgName ) ;
        if( univCfgURL == null ) {
            throw new IllegalStateException( "Config for universe " + 
                                             universeName + " not found." ) ;
        }
        
        CompositeConfiguration compConfig = new CompositeConfiguration() ;
        
        compConfig.addConfiguration( new PropertiesConfiguration( baseCfgURL ) ) ;
        compConfig.addConfiguration( new PropertiesConfiguration( univCfgURL ) ) ;
        
        super.addConfiguration( compConfig.interpolatedConfiguration() ) ;
    }
    
    public String getUniverseName() {
    	return this.universeName ;
    }
    
    public Date getDate( String key ) {
    	
        String val = instance.getString( key ) ;
        Date retVal = null ;
        if( val != null ) {
            try {
				retVal = Utils.parseDate( val ) ;
			} 
            catch ( IllegalArgumentException e ) {
            	throw new InvalidConfigException( key ) ;
			}
        }
        return retVal ;
    }
    
    @SuppressWarnings("unchecked")
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
