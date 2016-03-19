package com.sandy.capitalyst.domain;

import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.core.AccountingItemGroup ;

public class CapitalystAccountingBook extends AccountingBook {

    private AccountingItemGroup incomeHead = null ;
    private AccountingItemGroup expenseHead = null ;
    
    public CapitalystAccountingBook( String bookName ) {
        super( bookName ) ;
        
        this.incomeHead  = new AccountingItemGroup( "Income" ) ;
        this.expenseHead = new AccountingItemGroup( "Expense" ) ;
        
        super.addAccountingItem( this.incomeHead ) ;
        super.addAccountingItem( this.expenseHead ) ;
    }
    
    public AccountingItemGroup getIncomeHead() {
        return this.incomeHead ;
    }
    
    public AccountingItemGroup getExpenseHead() {
        return this.expenseHead ;
    }
}
