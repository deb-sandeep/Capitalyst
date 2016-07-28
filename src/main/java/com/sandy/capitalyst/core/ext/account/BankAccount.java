package com.sandy.capitalyst.core.ext.account;

import com.sandy.capitalyst.core.Account ;
import com.sandy.capitalyst.core.action.AccountClosureAction ;

public class BankAccount extends Account {

    private String bankName = null ;
    
    public BankAccount( String accountNumber, 
                        String name, 
                        String bankName, 
                        AccountClosureAction... closeActions ) {
        
        this( accountNumber, name, 0, bankName, closeActions ) ;
    }

    public BankAccount( String acccountNumber, 
                        String name, 
                        double amount, 
                        String bankName, 
                        AccountClosureAction... closeActions ) {
        
        super( acccountNumber, name, amount, closeActions ) ;
        this.bankName = bankName ;
    }
    
    public String getBankName() {
        return this.bankName ;
    }
}
