package com.sandy.capitalyst.core.action;

import java.util.Date ;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.util.Utils ;

public class TransferFullAmtOnClosure implements AccountClosureAction {

    private String tgtAccNo = null ;
    
    public TransferFullAmtOnClosure( String tgtAccNo ) {
        this.tgtAccNo = tgtAccNo ;
    }
    
    @Override
    public void execute( Account account, Date date ) {
        
        Utils.transfer( account, account.getUniverse().getAccount( tgtAccNo ), 
                        date, "Closure proceeds" ) ;
    }
}
