package com.sandy.capitalyst.account;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;

public class AggregateAccount extends Account 
    implements AccountListener {

    @Cfg private String[] accountNumbers = null ;
    
    public AggregateAccount() {
        super.setOverdraftAllowed( true ) ;
    }

    public String[] getAccountNumbers() {
        return accountNumbers ;
    }

    public void setAccountNumbers( String[] accountNumbers ) {
        this.accountNumbers = accountNumbers ;
    }
    
    public void addAccount( Account account ) {
        account.addListener( this ) ;
        super.amount += account.getAmount() ;
    }

    @Override
    public void initializePostConfig() {
        super.initializePostConfig() ;
        for( String accNo : accountNumbers ) {
            Account account = getUniverse().getAccount( accNo ) ;
            addAccount( account ) ;
        }
    }

    @Override
    public void txnPosted( Txn txn, Account account ) {
        super.postTransaction( txn ) ;
    }
}
