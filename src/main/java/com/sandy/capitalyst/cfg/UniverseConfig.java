package com.sandy.capitalyst.cfg;

import java.net.URL ;
import java.util.Date ;
import java.util.Iterator ;

import org.apache.commons.configuration.CompositeConfiguration ;
import org.apache.commons.configuration.ConfigurationException ;
import org.apache.commons.configuration.PropertiesConfiguration ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.util.Utils ;

public class UniverseConfig extends CompositeConfiguration {

    static Logger log = Logger.getLogger( UniverseConfig.class ) ;
    
    private UniverseConfig() {
    }
    
    public UniverseConfig( URL configURL ) throws ConfigurationException {
        initialize( configURL ) ;
    }
    
    private void initialize( URL univCfgURL ) 
        throws ConfigurationException {
        
        URL baseCfgURL = UniverseConfig.class.getResource( "/cap-base.properties" ) ;
        if( baseCfgURL == null ) {
            throw new IllegalStateException( "Base configuration not found." ) ;
        }
        
        CompositeConfiguration compConfig = new CompositeConfiguration() ;
        
        compConfig.addConfiguration( new PropertiesConfiguration( baseCfgURL ) ) ;
        compConfig.addConfiguration( new PropertiesConfiguration( univCfgURL ) ) ;
        
        super.addConfiguration( compConfig.interpolatedConfiguration() ) ;
    }
    
    public Date getDate( String key ) {
        
        String val = getString( key ) ;
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
    public UniverseConfig getNestedConfig( String prefix ) {
        
        UniverseConfig config = new UniverseConfig() ;
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
