package com.sandy.capitalyst.cfg;

@SuppressWarnings("serial")
public class InvalidConfigException extends ConfigException {

	public InvalidConfigException( String cfgKey ) {
		super( cfgKey ) ;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer() ;
		buffer.append( "Invalid configuration found. " )
		      .append( "Key = " )
		      .append( super.key ) ;
		return buffer.toString() ;
	}
}
