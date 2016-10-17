package com.sandy.capitalyst.core.ext.account;

import com.sandy.capitalyst.core.AccountConfig;
import com.sandy.capitalyst.core.cfg.ConfigException;
import com.sandy.capitalyst.util.Config;

public class BankAccountConfig extends AccountConfig {
	
	private String bankName = null ;
	
	@Override
	public void initialize( Config cfg ) throws ConfigException {
	}
	
	public String getBankName() {
		return this.bankName ;
	}
}
