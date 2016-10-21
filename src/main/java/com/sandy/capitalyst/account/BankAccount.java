package com.sandy.capitalyst.account;

import com.sandy.capitalyst.cfg.Cfg ;

public class BankAccount extends Account {

    @Cfg
    private String panNumber = null ;
    
    @Cfg( mandatory=false ) 
    private String bankName = "<Bank name not specified>" ;
    
    public void setBankName( String bankName ) {
        this.bankName = bankName ;
    }
    
    public void setPanNumber( String pan ) {
        this.panNumber = pan ;
    }
    
    public String getPanNumber() {
        return this.panNumber ;
    }

    public String getBankName() {
        return this.bankName ;
    }
}
