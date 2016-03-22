package com.sandy.capitalyst.domain.util.action;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.AccountAction ;

public class AccountLogMsgAction extends AccountAction {
    
    private static final Logger logger = Logger.getLogger( AccountLogMsgAction.class ) ;
    
    private String msg = null ;
    
    public AccountLogMsgAction( String msg ) {
        this.msg = msg ;
    }

    @Override
    public void execute( boolean preUpdate, Account account, Entry entry ) {
        String prePostStr = ( preUpdate ) ? "Pre update" : "Post update" ;
        logger.debug( "Accout log : [" + prePostStr + "] : " + account + ", message " + msg ) ; 
    }
}
