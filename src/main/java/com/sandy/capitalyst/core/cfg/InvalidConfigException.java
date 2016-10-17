package com.sandy.capitalyst.core.cfg;

import com.sandy.capitalyst.util.Config;

@SuppressWarnings("serial")
public class InvalidConfigException extends ConfigException {

	public InvalidConfigException( Config cfg, String cfgKey ) {
		super( cfg, cfgKey ) ;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer() ;
		buffer.append( "Invalid configuration found.\n" )
		      .append( "\tConfig = " )
		      .append( cfg.getUniverseName() ).append( "\n" )
		      .append( "\tKey = " )
		      .append( super.key ).append( "\n" ) ;
		return buffer.toString() ;
	}
}
