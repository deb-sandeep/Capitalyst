package com.sandy.capitalyst.core.cfg;

import com.sandy.capitalyst.util.Config;

public abstract class AbstractCfgBean {

	public abstract void initialize( Config cfg ) 
	    throws ConfigException ;
}
