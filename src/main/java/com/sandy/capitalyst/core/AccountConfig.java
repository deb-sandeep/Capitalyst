package com.sandy.capitalyst.core;

import com.sandy.capitalyst.core.cfg.AbstractCfgBean;
import com.sandy.capitalyst.core.cfg.ConfigException;
import com.sandy.capitalyst.util.Config;

public class AccountConfig extends AbstractCfgBean {

	private static final String CK_AC_NO    = "roi" ;
	private static final String CK_AC_NAME  = "name" ;
	private static final String CK_INIT_AMT = "initialAmount" ;
	
    private String accountNumber ;
    private String accountName ;
    private double initialAmount ;
    
    @Override
	public void initialize( Config cfg ) throws ConfigException {
        accountNumber = cfg.getString( CK_AC_NO ) ;
        accountName   = cfg.getString( CK_AC_NAME ) ;
        initialAmount = cfg.getDouble( CK_INIT_AMT ) ;
	}
    
    public String getAccountName() {
    	return this.accountName ;
    }
    
    public String getAccountNumber() {
    	return this.accountNumber ;
    }
    
    public double getInitialAmount() {
    	return this.initialAmount ;
    }
}
