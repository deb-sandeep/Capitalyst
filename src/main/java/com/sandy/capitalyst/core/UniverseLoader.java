package com.sandy.capitalyst.core;

import java.lang.annotation.Annotation ;
import java.lang.reflect.Field ;
import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Comparator ;
import java.util.Date ;
import java.util.Iterator ;
import java.util.LinkedHashSet ;
import java.util.List ;
import java.util.Locale ;
import java.util.Set ;

import org.apache.commons.beanutils.BeanUtils ;
import org.apache.commons.beanutils.ConvertUtils ;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.cfg.Config ;
import com.sandy.capitalyst.cfg.InvalidConfigException ;
import com.sandy.capitalyst.cfg.MissingConfigException ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.txgen.TxnGenerator ;

public class UniverseLoader {

    private static final Logger log = Logger.getLogger( UniverseLoader.class ) ;
    
    static {
        DateLocaleConverter converter = null ;
        converter = new DateLocaleConverter( Locale.getDefault(), "dd/MM/yyyy" ) ;
        ConvertUtils.register( converter, Date.class );
    }
    
    private String univName = null ;
    private Config univCfg  = null ;
    private Universe universe = null ;
    
    public UniverseLoader( String name ) {
        this.univName = name ;
    }
    
    public UniverseLoader( Config config ) {
        this.univCfg = config ;
    }
    
    public Universe loadUniverse() throws Exception {
        
        try {
            if( this.univCfg == null ) {
                this.univCfg  = new Config( this.univName ) ;
            }
            
            log.debug( "Loading universe " + univCfg.getUniverseName() ) ;
            log.debug( "---------------------------------------------" );
            
            universe = new Universe( univCfg.getUniverseName() ) ;
            
            configureUniverse( universe ) ;
            loadContextObjects( universe ) ;
            loadAccounts( universe ) ;
            loadTxGenerators( universe ) ;
            
            universe.setConfig( univCfg ) ;
        }
        catch( Exception e ) {
            log.error( "Loading universe " + univName + " failed.", e ) ;
            throw e ;
        }
        
        return universe ;
    }
    
    private void configureUniverse( Universe universe ) throws Exception {
        
        log.debug( "Configuring universe" ) ;
        Config attrCfg  = univCfg.getNestedConfig( "DayClock.attr" ) ;
        injectFieldValues( universe, attrCfg ) ;
    }
    
    private void loadContextObjects( Universe universe ) 
        throws Exception {
        
        for( String beanAlias : getUniqueEntityAliases( univCfg, "Ctx" ) ) {
            
            log.debug( "Loading context bean :: " + beanAlias ) ;
            Config beanCfg = univCfg.getNestedConfig( "Ctx." + beanAlias ) ;
            UniverseConstituent uc = ( UniverseConstituent )loadObject( beanCfg ) ;

            universe.addToContext( beanAlias, uc ) ;        }
    }

    private void loadAccounts( Universe universe ) 
            throws Exception {
        
        for( String accAlias : getUniqueEntityAliases( univCfg, "Account") ) {
            
            log.debug( "Loading account :: " + accAlias ) ;
            Config  accCfg = univCfg.getNestedConfig( "Account." + accAlias ) ;
            Account acc    = (Account)loadObject( accCfg ) ;
            
            universe.addAccount( acc ) ;
        }
    }
    
    private void loadTxGenerators( Universe universe ) 
        throws Exception {
        
        for( String txgenAlias : getUniqueEntityAliases( univCfg, "TxGen") ) {
            
            log.debug( "Loading tx generator :: " + txgenAlias ) ;
            Config       tgCfg = univCfg.getNestedConfig( "TxGen." + txgenAlias ) ;
            TxnGenerator txgen = ( TxnGenerator )loadObject( tgCfg ) ;
            
            universe.registerTxnGenerator( txgen ) ;
        }
    }
    
    private Object loadObject( Config objCfg ) 
        throws Exception {
        
        String type = objCfg.getString( "type" ) ;
        if( type == null ) {
            throw new MissingConfigException( "type" ) ;
        }
        
        String clsName = univCfg.getString( "_typeClassMap." + type ) ;
        if( clsName == null ) {
            clsName = type ;
        }
        
        log.debug( "\t[ " + clsName + " ]" ) ;
        
        Class<?> objCls  = Class.forName( clsName ) ;
        Object   obj     = objCls.newInstance() ;
        Config   attrCfg = objCfg.getNestedConfig( "attr" ) ;
        
        injectFieldValues( obj, attrCfg ) ;
        
        return obj ;
    }
    
    private void injectFieldValues( Object obj, Config attrCfg ) {
        
        List<Field> fields = getAllConfigurableFields( obj.getClass() ) ;
        fields.sort( new Comparator<Field>() {
            @Override public int compare( Field f1, Field f2 ) {
                return f1.getName().compareTo( f2.getName() ) ;
            }
        } ) ;
        
        for( Field field :  fields ) {
            populateField( obj, field, attrCfg );
        }
        
        if( obj instanceof UniverseConstituent ) {
            ((UniverseConstituent)obj).setUniverse( universe ) ; 
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
                log.debug( "\t" + fieldName + " = " + fieldRawVal ) ;
                BeanUtils.setProperty( obj, fieldName, fieldRawVal ) ;
            }
            catch( Exception e ) {
                log.error( "Unable to set property - " + fieldName + 
                           " = " + fieldRawVal, e ) ;
                throw new InvalidConfigException( fieldName ) ;
            }
        }
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
