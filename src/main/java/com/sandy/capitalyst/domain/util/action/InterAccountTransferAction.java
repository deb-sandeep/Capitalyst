package com.sandy.capitalyst.domain.util.action;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.AccountAction ;

public class InterAccountTransferAction extends AccountAction {
    
    static final Logger logger = Logger.getLogger( InterAccountTransferAction.class ) ;
    
    private Account src    = null ;
    private Account dest   = null ;
    private double  amount = 0 ;
    
    public InterAccountTransferAction( Account src, Account dest, double amt ) {
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
