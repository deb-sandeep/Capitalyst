package com.sandy.capitalyst.cfg;

@SuppressWarnings("serial")
public abstract class ConfigException extends RuntimeException {

	protected String key = null ;
	
	public ConfigException( String cfgKey ) {
		this.key = cfgKey ;
	}
}
