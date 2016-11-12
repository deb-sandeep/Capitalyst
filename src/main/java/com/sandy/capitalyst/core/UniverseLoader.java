package com.sandy.capitalyst.core;

import java.net.URL ;
import java.util.Collection ;
import java.util.Date ;
import java.util.Iterator ;
import java.util.LinkedHashSet ;
import java.util.Locale ;
import java.util.Set ;

import org.apache.commons.beanutils.BeanUtilsBean ;
import org.apache.commons.beanutils.ConvertUtils ;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter ;
import org.apache.commons.cli.MissingArgumentException ;
import org.apache.log4j.Logger ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.cfg.MissingConfigException ;
import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.core.amount.Amount ;
import com.sandy.capitalyst.txgen.TxnGenerator ;
import com.sandy.capitalyst.util.Range ;
import com.sandy.capitalyst.util.Utils ;
import com.sandy.capitalyst.util.converter.AmountConverter ;
import com.sandy.capitalyst.util.converter.ExecutionTimeConverter ;
import com.sandy.capitalyst.util.converter.RangeConverter ;

public class UniverseLoader {

    private static final Logger log = Logger.getLogger( UniverseLoader.class ) ;
    
    static {
        BeanUtilsBean.getInstance().getConvertUtils().register( true, false, 0 ) ;
        
        DateLocaleConverter converter = null ;
        converter = new DateLocaleConverter( Locale.getDefault(), "dd/MM/yyyy" ) ;
        ConvertUtils.register( converter, Date.class );
        
        ConvertUtils.register( new RangeConverter(), Range.class ) ;
        ConvertUtils.register( new ExecutionTimeConverter(), ExecutionTime.class ) ;
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
                if( this.univCfg.getString( "Universe.attr.name" ) != null ) {
                    this.univName = this.univCfg.getString( "Universe.attr.name" ) ;
                }
            }
            
            log.debug( "Loading universe " + this.univName ) ;
            log.debug( "---------------------------------------------" );
            
            universe = new Universe( univName ) ;
            configureUniverse( universe ) ;
            
            ConvertUtils.register( new AmountConverter( universe ), Amount.class );
            
            loadContextObjects( universe ) ;
            loadAccounts( universe ) ;
            loadTxGenerators( universe ) ;
            loadFactories( universe ) ;
            
            universe.setId( univName ) ;
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
        UniverseConfig attrCfg  = univCfg.getNestedConfig( "Universe.attr" ) ;
        Utils.injectFieldValues( universe, attrCfg ) ;
        universe.setConfiguration( univCfg ) ;
        universe.initializePostConfig() ;
    }
    
    private void loadContextObjects( Universe universe ) 
        throws Exception {
        
        for( String beanAlias : getUniqueEntityAliases( univCfg, "Ctx" ) ) {
            
            try {
                log.debug( "Loading context bean :: " + beanAlias ) ;
                UniverseConfig beanCfg = univCfg.getNestedConfig( "Ctx." + beanAlias ) ;
                UniverseConstituent uc = ( UniverseConstituent )loadObject( beanAlias, beanCfg ) ;

                universe.addToContext( beanAlias, uc ) ;
            }
            catch( Exception e ) {
                log.error( "Error loading " + beanAlias, e ) ;
                throw new IllegalArgumentException( "Error loading " + beanAlias, e ) ;
            }
        }
    }

    private void loadAccounts( Universe universe ) 
            throws Exception {
        
        for( String accAlias : getUniqueEntityAliases( univCfg, "Account") ) {
            
            try {
                log.debug( "Loading account :: " + accAlias ) ;
                UniverseConfig  accCfg = univCfg.getNestedConfig( "Account." + accAlias ) ;
                Account acc    = (Account)loadObject( accAlias, accCfg ) ;
                
                universe.addAccount( acc ) ;
            }
            catch( Exception e ) {
                log.error( "Error loading " + accAlias, e ) ;
                throw new IllegalArgumentException( "Error loading " + accAlias, e ) ;
            }
        }
    }
    
    private void loadTxGenerators( Universe universe ) 
        throws Exception {
        
        for( String txgenAlias : getUniqueEntityAliases( univCfg, "TxGen") ) {
            
            try {
                log.debug( "Loading tx generator :: " + txgenAlias ) ;
                UniverseConfig       tgCfg = univCfg.getNestedConfig( "TxGen." + txgenAlias ) ;
                TxnGenerator txgen = ( TxnGenerator )loadObject( txgenAlias, tgCfg ) ;
                
                universe.registerTxnGenerator( txgen ) ;
            }
            catch( Exception e ) {
                log.error( "Error loading " + txgenAlias, e ) ;
                throw new IllegalArgumentException( "Error loading " + txgenAlias, e ) ;
            }
        }
    }
    
    private void loadFactories( Universe universe ) 
        throws Exception {
        
        for( String factoryAlias : getUniqueEntityAliases( univCfg, "Factory") ) {
            
            try {
                log.debug( "Loading factory :: " + factoryAlias ) ;
                UniverseConfig factoryCfg = univCfg.getNestedConfig( "Factory." + factoryAlias ) ;
                loadObject( factoryAlias, factoryCfg ) ;
            }
            catch( Exception e ) {
                log.error( "Error loading " + factoryAlias, e ) ;
                throw new IllegalArgumentException( "Error loading " + factoryAlias, e ) ;
            }
        }
    }
    
    private Object loadObject( String objId, UniverseConfig objCfg ) 
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
        
        Class<?>       objCls  = Class.forName( clsName ) ;
        UniverseConfig attrCfg = extractAttributes( objId, objCfg ) ;

        return Utils.createEntity( objCls, attrCfg, objId, universe ) ;
    }
    
    private UniverseConfig extractAttributes( String objId, UniverseConfig objCfg ) 
        throws MissingArgumentException {
        
        UniverseConfig cfg = objCfg.getNestedConfig( "attr" ) ;
        
        String[] attributes = objCfg.getStringArray( "attributes" ) ;
        if( attributes != null ) {
            for( String attribute : attributes ) {
                String[] nvp = attribute.split( "=" ) ;
                cfg.addProperty( nvp[0].trim(), nvp[1].trim() );
            }
        }
        return cfg ;
    }
    
    private Collection<String> getUniqueEntityAliases( UniverseConfig config, 
                                                       String type ) {
        
        Set<String>      uniqueAliases = new LinkedHashSet<String>() ;
        UniverseConfig   allCfgs       = config.getNestedConfig( type ) ;
        Iterator<String> keys          = allCfgs.getKeys() ;
        
        while(  keys.hasNext() ) {
            uniqueAliases.add( keys.next().split( "\\." )[0] ) ;
        }
        return uniqueAliases ;
    }

}
