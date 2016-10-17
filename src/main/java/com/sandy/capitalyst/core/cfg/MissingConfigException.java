package com.sandy.capitalyst.core.cfg;

import com.sandy.capitalyst.util.Config;

@SuppressWarnings("serial")
public class MissingConfigException extends ConfigException {

	public MissingConfigException( Config cfg, String cfgKey ) {
		super( cfg, cfgKey ) ;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer() ;
		buffer.append( "Missing configuration.\n" )
		      .append( "\tConfig = " )
		      .append( cfg.getUniverseName() ).append( "\n" )
		      .append( "\tKey = " )
		      .append( super.key ).append( "\n" ) ;
		return buffer.toString() ;
	}
}
