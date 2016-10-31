package com.sandy.capitalyst.core;

import java.lang.annotation.Annotation ;
import java.lang.reflect.Field ;
import java.net.URL ;
import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Comparator ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.LinkedHashSet ;
import java.util.List ;
import java.util.Locale ;
import java.util.Map ;
import java.util.Set ;

import org.apache.commons.beanutils.BeanUtils ;
import org.apache.commons.beanutils.BeanUtilsBean ;
import org.apache.commons.beanutils.ConvertUtils ;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter ;
import org.apache.commons.cli.MissingArgumentException ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.cfg.InvalidConfigException ;
import com.sandy.capitalyst.cfg.MissingConfigException ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.core.amount.Amount ;
import com.sandy.capitalyst.txgen.ScheduledTxnDef ;
import com.sandy.capitalyst.txgen.TxnGenerator ;
import com.sandy.capitalyst.util.Range ;
import com.sandy.capitalyst.util.converter.AmountConverter ;
import com.sandy.capitalyst.util.converter.RangeConverter ;
import com.sandy.capitalyst.util.converter.ScheduleTxnDefConverter ;

public class UniverseLoader {

    private static final Logger log = Logger.getLogger( UniverseLoader.class ) ;
    
    static {
        BeanUtilsBean.getInstance().getConvertUtils().register( true, false, 0 ) ;
        
        DateLocaleConverter converter = null ;
        converter = new DateLocaleConverter( Locale.getDefault(), "dd/MM/yyyy" ) ;
        ConvertUtils.register( converter, Date.class );
        
        ConvertUtils.register( new RangeConverter(), Range.class ) ;
        ConvertUtils.register( new ScheduleTxnDefConverter(), ScheduledTxnDef.class );
    }
    
    public static class ConfigurableField {
        private Field field = null ;
        private boolean mandatory = false ;
        
        public ConfigurableField( Field f, boolean mandatory ) {
            this.field = f ;
            this.mandatory = mandatory ;
        }
        
        public Field getField() {
            return this.field ;
        }
        
        public boolean isMandatory() {
            return this.mandatory ;
        }
    }
    
    public static Map<Class<?>, List<ConfigurableField>> configurableFieldMap =
                                                               new HashMap<>() ;
    
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
            
            universe.setConfiguration( univCfg ) ;
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
        injectFieldValues( universe, attrCfg ) ;
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
        Object         obj     = objCls.newInstance() ;
        UniverseConfig attrCfg = extractAttributes( objId, objCfg ) ;
        
        injectFieldValues( obj, attrCfg ) ;
        
        if( obj instanceof UniverseConstituent ) {
            UniverseConstituent uc = ( UniverseConstituent )obj ;
            uc.setId( objId ) ;
        }
        
        return obj ;
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
    
    private void injectFieldValues( Object obj, UniverseConfig attrCfg ) {
        
        List<ConfigurableField> fields = getAllConfigurableFields( obj.getClass() ) ;
        for( ConfigurableField field :  fields ) {
            populateField( obj, field, attrCfg );
        }
        
        if( obj instanceof UniverseConstituent ) {
            UniverseConstituent uc = ( UniverseConstituent )obj ;
            uc.setUniverse( universe ) ;
        }
        
        if( obj instanceof PostConfigInitializable ) {
            ( (PostConfigInitializable)obj ).initializePostConfig() ;
        }
    }
    
    public static List<ConfigurableField> getAllConfigurableFields( Class<?> cls ) {
        
        List<ConfigurableField> allFields = null ;
        
        allFields = configurableFieldMap.get( cls ) ;
        if( allFields == null ) {
            allFields = new ArrayList<>() ;
            do {
                Field[] fields = cls.getDeclaredFields() ;
                for( Field f : fields ) {
                    Annotation[] annotations = f.getAnnotations() ;
                    if( annotations.length > 0 ) {
                        for( Annotation a : annotations ) {
                            if( a instanceof com.sandy.capitalyst.cfg.Cfg ) {
                                allFields.add( new ConfigurableField( f,
                              ((com.sandy.capitalyst.cfg.Cfg)a).mandatory() ) ) ;
                            }
                        }
                    }
                }
                cls = cls.getSuperclass() ;
            }
            while( cls != null ) ;

            allFields.sort( new Comparator<ConfigurableField>() {
                @Override public int compare( ConfigurableField f1, ConfigurableField f2 ) {
                    return f1.getField().getName().compareTo( f2.getField().getName() ) ;
                }
            } ) ;
            
            configurableFieldMap.put( cls, allFields ) ;
        }
        
        return allFields ;
    }
    
    private void populateField( Object obj, ConfigurableField f, 
                                UniverseConfig attrValues ) {
        
        boolean mandatory      = f.isMandatory() ;
        String  fieldName      = f.getField().getName() ;
        String  fieldRawVals[] = attrValues.getStringArray( fieldName ) ;
        
        if( mandatory && fieldRawVals == null ) {
            throw new MissingConfigException( fieldName ) ;
        }
        else if( fieldRawVals != null ) {
            try {
                if( fieldRawVals.length == 1 ) {
                    String rawVal = fieldRawVals[0] ;
                    log.debug( "\t" + fieldName + " = " + rawVal ) ;
                    BeanUtils.setProperty( obj, fieldName, rawVal ) ;
                }
                else if( fieldRawVals.length > 1 ){
                    for( String rawVal : fieldRawVals ) {
                        log.debug( "\t" + fieldName + " = " + rawVal ) ;
                    }
                    BeanUtils.setProperty( obj, fieldName, fieldRawVals ) ;
                }
            }
            catch( Exception e ) {
                log.error( "Unable to set property - " + fieldName + 
                           " = " + fieldRawVals, e ) ;
                throw new InvalidConfigException( fieldName ) ;
            }
        }
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
