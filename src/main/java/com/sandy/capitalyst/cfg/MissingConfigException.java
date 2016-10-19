package com.sandy.capitalyst.cfg;

@SuppressWarnings("serial")
public class MissingConfigException extends ConfigException {

	public MissingConfigException( String cfgKey ) {
		super( cfgKey ) ;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer() ;
		buffer.append( "Missing configuration. " )
		      .append( "Key = " )
		      .append( super.key ) ;
		return buffer.toString() ;
	}
}
