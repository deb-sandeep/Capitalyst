package com.sandy.capitalyst.core.cfg;

import com.sandy.capitalyst.util.Config;

@SuppressWarnings("serial")
public abstract class ConfigException extends RuntimeException {

	protected Config cfg = null ;
	protected String key = null ;
	
	public ConfigException( Config cfg, String cfgKey ) {
		this.cfg = cfg ;
		this.key = cfgKey ;
	}
}
