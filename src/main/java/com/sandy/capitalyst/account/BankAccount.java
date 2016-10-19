package com.sandy.capitalyst.account;

import com.sandy.capitalyst.cfg.Cfg ;

public class BankAccount extends Account {

    @Cfg( mandatory=false ) 
    private String bankName = "<Bank name not specified>" ;
    
    public void setBankName( String bankName ) {
        this.bankName = bankName ;
    }

    public String getBankName() {
        return this.bankName ;
    }
}
