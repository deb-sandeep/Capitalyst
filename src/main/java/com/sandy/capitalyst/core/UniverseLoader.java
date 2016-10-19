package com.sandy.capitalyst.core;

import java.lang.annotation.Annotation ;
import java.lang.reflect.Field ;
import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Date ;
import java.util.Iterator ;
import java.util.LinkedHashSet ;
import java.util.List ;
import java.util.Locale ;
import java.util.Set ;

import org.apache.commons.beanutils.BeanUtils ;
import org.apache.commons.beanutils.BeanUtilsBean ;
import org.apache.commons.beanutils.ConvertUtils ;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.cfg.Config ;
import com.sandy.capitalyst.cfg.InvalidConfigException ;
import com.sandy.capitalyst.cfg.MissingConfigException ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.clock.DayClock ;

public class UniverseLoader {

    private static final Logger log = Logger.getLogger( UniverseLoader.class ) ;
    
    static {
        DateLocaleConverter converter = null ;
        converter = new DateLocaleConverter( Locale.getDefault(), "dd/MM/yyyy" ) ;
        ConvertUtils.register( converter, Date.class );
    }
    
    public Universe loadUniverse() throws Exception {
        
        Config config = Config.instance() ;
        log.debug( "Loading universe " + config.getUniverseName() ) ;
        initializeTimer() ;
        
        Universe universe = new Universe( config.getUniverseName() ) ;
        
        loadContextObjects( universe, config ) ;
        loadAccounts( universe, config ) ;
        
        //loadTxGenerators( universe, config ) ;
        
        return universe ;
    }
    
    private void initializeTimer() throws Exception {
        
    	DayClock instance = DayClock.instance() ;
        Config   attrCfg  = Config.instance().getNestedConfig( "DayClock.attr" ) ;
    	injectFieldValues( instance, attrCfg ) ;
    }
    
    private void loadContextObjects( Universe universe, Config config ) 
        throws Exception {
    	
    	for( String beanAlias : getUniqueEntityAliases( config, "Bean" ) ) {
    		log.debug( "Loading context bean :: " + beanAlias ) ;
    		loadCtxObject( universe, config, beanAlias ) ;
    	}
    }

    private void loadAccounts( Universe universe, Config config ) 
        throws Exception {
    	
    	for( String accAlias : getUniqueEntityAliases( config, "Account") ) {
    	    
    		log.debug( "Loading account :: " + accAlias ) ;
            Config  accCfg = config.getNestedConfig( "Account." + accAlias ) ;
            Account acc    = (Account)loadObject( accCfg ) ;
            
            universe.addAccount( acc ) ;
    	}
    }
    
    private Object loadObject( Config objCfg ) 
        throws Exception {
        
        String type = objCfg.getString( "type" ) ;
        if( type == null ) {
            throw new MissingConfigException( "type" ) ;
        }
        
        String clsName = Config.instance().getString( "_typeClassMap." + type ) ;
        if( clsName == null ) {
            clsName = type ;
        }
        
        log.debug( "Loading object of class type " + clsName ) ;
        
        Class<?> objCls  = Class.forName( clsName ) ;
        Object   obj     = objCls.newInstance() ;
        Config   attrCfg = objCfg.getNestedConfig( "attr" ) ;
        
        injectFieldValues( obj, attrCfg ) ;
        
        return obj ;
    }
    
    private void injectFieldValues( Object obj, Config attrCfg ) {
        
        List<Field> fields  = getAllConfigurableFields( obj.getClass() ) ;
        for( Field field :  fields ) {
            populateField( obj, field, attrCfg );
        }
        
        if( obj instanceof PostConfigInitializable ) {
            ( (PostConfigInitializable)obj ).initializePostConfig() ;
        }
    }
    
    private List<Field> getAllConfigurableFields( Class<?> cls ) {
        
        List<Field> allFields = new ArrayList<Field>() ;
        
        do {
            Field[] fields = cls.getDeclaredFields() ;
            for( Field f : fields ) {
                Annotation[] annotations = f.getAnnotations() ;
                if( annotations.length > 0 ) {
                    for( Annotation a : annotations ) {
                        if( a instanceof com.sandy.capitalyst.cfg.Cfg ) {
                            allFields.add( f ) ;
                        }
                    }
                }
            }
            cls = cls.getSuperclass() ;
        }
        while( cls != null ) ;
        
        return allFields ;
    }
    
    private void populateField( Object obj, Field f, Config attrValues ) {
        
        
        boolean mandatory   = true ;
        String  fieldName   = f.getName() ;
        String  fieldRawVal = attrValues.getString( fieldName ) ;
        
        Annotation[] annotations = f.getAnnotations() ;
        for( Annotation a : annotations ) {
            if( a instanceof com.sandy.capitalyst.cfg.Cfg ) {
                mandatory = ((com.sandy.capitalyst.cfg.Cfg)a).mandatory() ;
                break ;
            }
        }
        
        if( mandatory && fieldRawVal == null ) {
            throw new MissingConfigException( fieldName ) ;
        }
        else if( fieldRawVal != null ) {
            try {
                log.debug( "Setting field " + fieldName + 
                           " with value " + fieldRawVal ) ;
                BeanUtils.setProperty( obj, fieldName, fieldRawVal ) ;
            }
            catch( Exception e ) {
                throw new InvalidConfigException( fieldName ) ;
            }
        }
    }
    
    private void loadCtxObject( Universe universe, Config config, String beanName ) 
        throws Exception {
    
    	UniverseConstituent uc = null ;
    	uc = ( UniverseConstituent )loadBean( config, beanName ) ;
    	universe.addToContext( beanName, uc ) ;
    }
    
    @SuppressWarnings("unchecked")
	private Object loadBean( Config config, String alias ) 
        throws Exception {

        String clsKey  = "Bean." + alias + ".class" ;
        String clsName = config.getString( clsKey ) ;
        Object obj     = Class.forName( clsName ).newInstance() ;
        Config cfg     = config.getNestedConfig( "Bean." + alias + ".attribute" ) ;
        
        for( Iterator<String> keys = cfg.getKeys(); keys.hasNext(); ) {
            
            String key = keys.next() ;
            String value = cfg.getString( key ) ;
            
            BeanUtilsBean.getInstance().setProperty( obj, key, value ) ;
        }
        return obj ;
    }

    @SuppressWarnings("unchecked")
	private Collection<String> getUniqueEntityAliases( Config config, 
		                                               String type ) {
    	
		Set<String>      uniqueAliases = new LinkedHashSet<String>() ;
		Config           allCfgs       = config.getNestedConfig( type ) ;
		Iterator<String> keys          = allCfgs.getKeys() ;
		
		while(  keys.hasNext() ) {
			uniqueAliases.add( keys.next().split( "\\." )[0] ) ;
		}
		return uniqueAliases ;
	}

}
