package com.sandy.capitalyst.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.ext.account.SavingAccount;
import com.sandy.capitalyst.core.ext.account.SavingAccountConfig;
import com.sandy.capitalyst.util.Config;

public class UniverseInitializer {

    private static final Logger log = Logger.getLogger( UniverseInitializer.class ) ;
    
    public Universe createUniverse( String universeName ) throws Exception {
        
        log.debug( "Creating universe " + universeName ) ;
        initializeTimer() ;
        
        Config config = Config.instance( universeName ) ;
        Universe universe = new Universe( universeName ) ;
        
        loadContextObjects( universe, config ) ;
        loadAccounts( universe, config ) ;
        
        //loadTxGenerators( universe, config ) ;
        
        return universe ;
    }
    
    private void initializeTimer() throws Exception {
        
    	DayClock instance = DayClock.instance() ;
        DayClockConfig rangeCfg = new DayClockConfig() ;
        
        rangeCfg.initialize( Config.instance() ) ;
        instance.setDateRange( rangeCfg ) ;
    }
    
    private void loadContextObjects( Universe universe, Config config ) 
        throws Exception {
    	
    	for( String beanAlias : getUniqueEntityAliases( config, "Bean" ) ) {
    		log.debug( "Loading context bean :: " + beanAlias ) ;
    		addCtxObject( universe, config, beanAlias ) ;
    	}
    }

    private void loadAccounts( Universe universe, Config config ) {
    	
    	for( String acctAlias : getUniqueEntityAliases( config, "Account") ) {
    		log.debug( "Loading account :: " + acctAlias ) ;
            universe.addAccount( loadAccount( config, acctAlias ) ) ;
    	}
    }
    
    public Account loadAccount( Config config, String alias ) {
        
        Account account = null ;
        Config accCfg = config.getNestedConfig( "Account." + alias ) ;
        String accountType = accCfg.getString( "accountType" ) ;
        
        if( accountType.equals( "SB" ) ) {
        	SavingAccountConfig saCfg = new SavingAccountConfig() ;
        	saCfg.initialize( accCfg ) ;
            account = SavingAccount.create( saCfg ) ;
        }
        
        return account ;
    }
    
    private void addCtxObject( Universe universe, Config config, String beanName ) 
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
