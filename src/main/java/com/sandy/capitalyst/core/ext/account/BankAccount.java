package com.sandy.capitalyst.core.ext.account;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.action.AccountClosureAction ;

public class BankAccount extends Account {

    private String bankName = "<Bank name not specified>" ;
    
    public BankAccount( String accountNumber, 
                        AccountClosureAction... closeActions ) {
        
        this( accountNumber, 0, closeActions ) ;
    }

    public BankAccount( String acccountNumber, 
                        double amount, 
                        AccountClosureAction... closeActions ) {
        
        super( acccountNumber, amount, closeActions ) ;
    }
    
    public String getBankName() {
        return this.bankName ;
    }
    
    public void setBankName( String bankName ) {
        this.bankName = bankName ;
    }
}
