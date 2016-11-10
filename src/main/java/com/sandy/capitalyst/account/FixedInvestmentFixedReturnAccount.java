package com.sandy.capitalyst.account;

import java.util.Date ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.util.Utils ;

public class FixedInvestmentFixedReturnAccount 
    extends AbstractFixedInvestmentAccount 
    implements EndOfDayObserver {
    
    @Cfg 
    private double maturityAmount = 0 ;
    
    @Cfg( mandatory=false )
    private Date maturityDate = null ;
    
    public double getMaturityAmount() {
        return maturityAmount ;
    }

    public void setMaturityAmount( double amt ) {
        this.maturityAmount = amt ;
    }

    public Date getMaturityDate() {
        if( maturityDate != null ) {
            return maturityDate ;
        }
        return super.getInvestmentEndDate() ;
    }

    public void setMaturityDate( Date maturityDate ) {
        this.maturityDate = maturityDate ;
    }
    
    @Override
    public void handleEndOfDayEvent( Date date ) {
        
        if( isActive() ) {
            if( Utils.isSame( date, getMaturityDate() ) ) {
                makeMaturityPaymentTxn( date ) ;
                super.closeAccount( date ) ;
            }
            else {
                checkAndMakeInvestmentTxn( date ) ;
            }
        }
    }

    private void makeMaturityPaymentTxn( Date date ) {
        
        Txn txn = new Txn( getParentAccountNumber(), 
                           maturityAmount, 
                           date, 
                           "Maturity amount for " + getName() ) ;
        getUniverse().postTransaction( txn ) ;
    }
}
