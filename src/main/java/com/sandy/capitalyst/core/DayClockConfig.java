package com.sandy.capitalyst.core;

import java.util.Date;

import com.sandy.capitalyst.core.cfg.AbstractCfgBean;
import com.sandy.capitalyst.core.cfg.ConfigException;
import com.sandy.capitalyst.core.cfg.MissingConfigException;
import com.sandy.capitalyst.util.Config;

public class DayClockConfig extends AbstractCfgBean {

    private static String CK_START_DATE = "startDate" ;
    private static String CK_END_DATE   = "endDate" ;
    
    private Date startDate = null ;
    private Date endDate = null ;

	@Override
	public void initialize( Config cfg ) 
        throws ConfigException {
		
        this.startDate = cfg.getDate( CK_START_DATE ) ;
        this.endDate   = cfg.getDate( CK_END_DATE ) ;
        
        if( startDate == null ) {
            throw new MissingConfigException( cfg, CK_START_DATE ) ;
        }
        
        if( endDate == null ) {
            throw new MissingConfigException( cfg, CK_END_DATE ) ;
        }
	}
	
	public Date getStartDate() {
		return this.startDate ;
	}
	
	public Date getEndDate() {
		return this.endDate ;
	}
}
