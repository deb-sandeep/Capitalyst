package com.sandy.capitalyst.domain.core;

import com.sandy.capitalyst.domain.core.Account.Entry ;

public abstract class Instruction {
    
    private String name = null ;
    
    public Instruction( String name ) {
        this.name = name ;
    }
    
    public String getName() {
        return this.name ;
    }

    public boolean canExecute( Account account, Entry entry ) {
        return true ;
    }

    public abstract void execute( boolean preUpdate, Account account, Entry entry ) ;
}
