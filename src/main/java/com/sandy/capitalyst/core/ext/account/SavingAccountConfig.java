package com.sandy.capitalyst.core.ext.account;

import com.sandy.capitalyst.core.cfg.ConfigException;
import com.sandy.capitalyst.util.Config;

public class SavingAccountConfig extends BankAccountConfig {
	
	private static final String CK_ROI = "roi" ;
	
	private double roi = 0 ;
	
	@Override
	public void initialize( Config cfg ) throws ConfigException {
		super.initialize( cfg ) ;
        roi = cfg.getDouble( CK_ROI ) ;
	}
	
	public double getRoi() {
		return this.roi ;
	}
}
