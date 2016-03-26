package com.sandy.capitalyst.domain.util.instruction;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.Instruction ;

public class AccountLogMsgInstruction extends Instruction {
    
    private static final Logger logger = Logger.getLogger( AccountLogMsgInstruction.class ) ;
    
    private String msg = null ;
    
    public AccountLogMsgInstruction( String msg ) {
        super( "Accoung log message instruction." ) ;
        this.msg = msg ;
    }

    @Override
    public void execute( boolean preUpdate, Account account, Entry entry ) {
        String prePostStr = ( preUpdate ) ? "Pre update" : "Post update" ;
        logger.debug( "Accout log : [" + prePostStr + "] : " + account + ", message " + msg ) ; 
    }
}
