package com.sandy.capitalyst.core.ext.account;

import com.sandy.capitalyst.core.AccountConfig;
import com.sandy.capitalyst.core.cfg.ConfigException;
import com.sandy.capitalyst.util.Config;

public class BankAccountConfig extends AccountConfig {
	
	private static final String CK_BANK_NAME = "bankName" ;
	
	private String bankName = null ;
	
	@Override
	public void initialize( Config cfg ) throws ConfigException {
		super.initialize( cfg ) ;
        bankName = cfg.getString( CK_BANK_NAME ) ;        
	}
	
	public String getBankName() {
		return this.bankName ;
	}
}
