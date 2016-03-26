package com.sandy.capitalyst.domain.util.instruction;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.Instruction ;

public class AccountLogInstruction extends Instruction {
    
    private static final Logger logger = Logger.getLogger( AccountLogInstruction.class ) ;
    
    public AccountLogInstruction() {
        super( "Accoung log instruction" ) ;
    }

    @Override
    public void execute( boolean preUpdate, Account account, Entry entry ) {
        String prePostStr = ( preUpdate ) ? "Pre update" : "Post update" ;
        logger.debug( "Accout log : [" + prePostStr + "] : " + 
                      account + ", " + entry ) ;
    }
}
