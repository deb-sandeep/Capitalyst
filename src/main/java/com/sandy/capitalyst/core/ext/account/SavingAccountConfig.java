package com.sandy.capitalyst.core.ext.account;

import com.sandy.capitalyst.core.cfg.ConfigException;
import com.sandy.capitalyst.util.Config;

public class SavingAccountConfig extends BankAccountConfig {
	
	private double roi = 0 ;
	
	@Override
	public void initialize( Config cfg ) throws ConfigException {
	}
	
	public double getRoi() {
		return this.roi ;
	}
}
