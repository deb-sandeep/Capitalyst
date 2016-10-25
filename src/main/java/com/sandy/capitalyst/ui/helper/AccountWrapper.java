package com.sandy.capitalyst.ui.helper;

import org.jfree.data.time.Day ;
import org.jfree.data.time.TimeSeries ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.account.AccountListener ;
import com.sandy.capitalyst.core.Txn ;

public class AccountWrapper implements AccountListener {

    private Account    account        = null ;
    private TimeSeries balanceHistory = null ;
    
    public AccountWrapper( Account account ) {
        this.account = account ;
        balanceHistory = new TimeSeries( account.getUniverse().getName() + 
                                         "." + account.getName() ) ;
        account.addListener( this ) ;
    }

    @Override
    public void txnPosted( Txn txn, Account a ) {
        balanceHistory.addOrUpdate( new Day( txn.getDate() ), a.getAmount() ) ;
    }
    
    public Account getAccount() {
        return this.account ;
    }
    
    public TimeSeries getTimeSeries() {
        return this.balanceHistory ;
    }
    
    public String toString() {
        return this.account.getName() ;
    }
}
