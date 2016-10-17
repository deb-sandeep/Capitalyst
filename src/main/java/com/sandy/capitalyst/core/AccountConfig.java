package com.sandy.capitalyst.core;

import com.sandy.capitalyst.core.cfg.AbstractCfgBean;
import com.sandy.capitalyst.core.cfg.ConfigException;
import com.sandy.capitalyst.util.Config;

public class AccountConfig extends AbstractCfgBean {

    private String accountNumber ;
    private double initialAmount ;
    
    @Override
	public void initialize( Config cfg ) throws ConfigException {
	}
    
    public String getAccountNumber() {
    	return this.accountNumber ;
    }
    
    public double getInitialAmount() {
    	return this.initialAmount ;
    }
}
