package com.sandy.capitalyst.core;

import java.lang.annotation.Annotation ;
import java.lang.reflect.Field ;
import java.net.URL ;
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
import com.sandy.capitalyst.cfg.InvalidConfigException ;
import com.sandy.capitalyst.cfg.MissingConfigException ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.txgen.TxnGenerator ;

public class UniverseLoader {

    private static final Logger log = Logger.getLogger( UniverseLoader.class ) ;
    
    static {
        DateLocaleConverter converter = null ;
        converter = new DateLocaleConverter( Locale.getDefault(), "dd/MM/yyyy" ) ;
        ConvertUtils.register( converter, Date.class );
    }
    
    private static final String UNNAMED_UNIVERSE_PREFIX = "Unnamed Universe" ;
    private static int nextUnnamedUniverseID = 0 ;
    
    private String univName   = null ;
    private UniverseConfig univCfg    = null ;
    private URL    univCfgURL = null ;
    
    private Universe universe = null ;
    
    public UniverseLoader( String name ) {
        if( name == null ) {
            throw new IllegalArgumentException( "Universe name is null" ) ;
        }
        this.univName = name ;
    }
    
    public UniverseLoader( UniverseConfig config ) {
        this.univName = UNNAMED_UNIVERSE_PREFIX + nextUnnamedUniverseID++ ;
        this.univCfg = config ;
    }
    
    public UniverseLoader( URL configURL, String name ) {
        if( configURL == null ) {
            throw new IllegalArgumentException( "Universe config URL is null" ) ;
        }
        this.univName = name ;
        this.univCfgURL = configURL ;
    }
    
    public UniverseLoader( URL cfgURL ) {
        this( cfgURL, UNNAMED_UNIVERSE_PREFIX + nextUnnamedUniverseID++ ) ;
    }
    
    public Universe loadUniverse() throws Exception {
        
        try {
            if( this.univCfg == null ) {
                this.univCfg  = new UniverseConfig( getConfigURL() ) ;
            }
            
            if( univName.startsWith( UNNAMED_UNIVERSE_PREFIX ) ) {
                if( this.univCfg.getString( "Universe.name" ) != null ) {
                    this.univName = this.univCfg.getString( "Universe.name" ) ;
                }
            }
            
            log.debug( "Loading universe " + this.univName ) ;
            log.debug( "---------------------------------------------" );
            
            universe = new Universe( univName ) ;
            
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
    
    private URL getConfigURL() {
        
        if( univName == null && univCfgURL == null ) {
            throw new IllegalStateException( 
                    "Both universe name and configural URL are null" ) ;
        }
        else if( univCfgURL == null ) {
            
            String univCfgName = "/cap-" + this.univName + ".properties" ;
            univCfgURL = UniverseConfig.class.getResource( univCfgName ) ;
            if( univCfgURL == null ) {
                throw new IllegalStateException( "Config for universe " + 
                                                 univName + " not found." ) ;
            }
        }
        
        return univCfgURL ;
    }
    
    private void configureUniverse( Universe universe ) throws Exception {
        
        log.debug( "Configuring universe" ) ;
        UniverseConfig attrCfg  = univCfg.getNestedConfig( "DayClock.attr" ) ;
        injectFieldValues( universe, attrCfg ) ;
    }
    
    private void loadContextObjects( Universe universe ) 
        throws Exception {
        
        for( String beanAlias : getUniqueEntityAliases( univCfg, "Ctx" ) ) {
            
            log.debug( "Loading context bean :: " + beanAlias ) ;
            UniverseConfig beanCfg = univCfg.getNestedConfig( "Ctx." + beanAlias ) ;
            UniverseConstituent uc = ( UniverseConstituent )loadObject( beanCfg ) ;

            universe.addToContext( beanAlias, uc ) ;        }
    }

    private void loadAccounts( Universe universe ) 
            throws Exception {
        
        for( String accAlias : getUniqueEntityAliases( univCfg, "Account") ) {
            
            log.debug( "Loading account :: " + accAlias ) ;
            UniverseConfig  accCfg = univCfg.getNestedConfig( "Account." + accAlias ) ;
            Account acc    = (Account)loadObject( accCfg ) ;
            
            universe.addAccount( acc ) ;
        }
    }
    
    private void loadTxGenerators( Universe universe ) 
        throws Exception {
        
        for( String txgenAlias : getUniqueEntityAliases( univCfg, "TxGen") ) {
            
            log.debug( "Loading tx generator :: " + txgenAlias ) ;
            UniverseConfig       tgCfg = univCfg.getNestedConfig( "TxGen." + txgenAlias ) ;
            TxnGenerator txgen = ( TxnGenerator )loadObject( tgCfg ) ;
            
            universe.registerTxnGenerator( txgen ) ;
        }
    }
    
    private Object loadObject( UniverseConfig objCfg ) 
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
        UniverseConfig   attrCfg = objCfg.getNestedConfig( "attr" ) ;
        
        injectFieldValues( obj, attrCfg ) ;
        
        return obj ;
    }
    
    private void injectFieldValues( Object obj, UniverseConfig attrCfg ) {
        
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
    
    private void populateField( Object obj, Field f, UniverseConfig attrValues ) {
        
        
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
    private Collection<String> getUniqueEntityAliases( UniverseConfig config, 
                                                       String type ) {
        
        Set<String>      uniqueAliases = new LinkedHashSet<String>() ;
        UniverseConfig           allCfgs       = config.getNestedConfig( type ) ;
        Iterator<String> keys          = allCfgs.getKeys() ;
        
        while(  keys.hasNext() ) {
            uniqueAliases.add( keys.next().split( "\\." )[0] ) ;
        }
        return uniqueAliases ;
    }

}
