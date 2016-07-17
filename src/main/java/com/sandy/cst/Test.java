package com.sandy.cst ;

import java.text.SimpleDateFormat ;

import com.sandy.cst.domain.Account ;
import com.sandy.cst.domain.AccountManager ;
import com.sandy.cst.domain.Journal ;
import com.sandy.cst.domain.Transaction ;

public class Test {
    
    private static final SimpleDateFormat SDF = new SimpleDateFormat( "dd/MM/YYYY" ) ;

    public static void main( String[] args ) throws Exception {
        
        AccountManager accMgr = new AccountManager() ;
        accMgr.addAccount( new Account( "5212", "Sandy SB" ) ) ;
        accMgr.addAccount( new Account( "5686", "Sweety SB" ) ) ;
        
        Journal journal = new Journal( accMgr ) ;
        
        Transaction t = new Transaction( "5212", 10, SDF.parse( "10/10/2015" ) ) ;
        journal.postTransaction( t ) ;
        
        System.out.println( accMgr.getAccount( "5212" ).getAmount() ) ;
    }
}
