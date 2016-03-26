package com.sandy.capitalyst.domain.util.instruction;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.Instruction ;

public class InterAccountTransferInstruction extends Instruction {
    
    static final Logger logger = Logger.getLogger( InterAccountTransferInstruction.class ) ;
    
    private Account src    = null ;
    private Account dest   = null ;
    private double  amount = 0 ;
    
    public InterAccountTransferInstruction( Account src, Account dest, double amt ) {
        super( "Inter account transfer instruction." ) ;
        this.src    = src ;
        this.dest   = dest ;
        this.amount = amt ;
    }
    
    @Override
    public boolean canExecute( Account account, Entry entry ) {
        return account.getAmount() >= amount ;
    }

    @Override
    public void execute( boolean preUpdate, Account account, Entry entry ) {
        src.operate( -amount, entry.getDate(), "Internal debit to account " + dest.getName() ) ;
        dest.operate( amount, entry.getDate(), "Internal credit from account " + src.getName() ) ;
    }
}
