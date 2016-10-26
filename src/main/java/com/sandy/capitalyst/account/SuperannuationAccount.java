package com.sandy.capitalyst.account;

import java.util.Date ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.action.AccountClosureAction ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.timeobservers.EndOfMonthObserver ;
import com.sandy.capitalyst.util.Utils ;

public class SuperannuationAccount extends YearlyCompoundingAccount 
    implements EndOfMonthObserver {

    static Logger log = Logger.getLogger( SuperannuationAccount.class ) ;
    
    @Cfg private double maxTaxFreeWithdrawalPct = 0 ;
    @Cfg private double annuityPer1000PerYear = 0 ;
    
    private double annuityPayoutPerMonth = 0 ;
    
    public double getAnnuityPer1000PerYear() {
        return annuityPer1000PerYear ;
    }
    
    public void setMaxTaxFreeWithdrawalPct( double pct ) {
        this.maxTaxFreeWithdrawalPct = pct/100 ;
    }
    
    public void setAnnuityPer1000PerYear( double amt ){
        this.annuityPer1000PerYear = amt ;
    }
    
    public double getMaxTaxFreeWithdrawalPct() {
        return maxTaxFreeWithdrawalPct ;
    }
    
    private class SAClosureAction implements AccountClosureAction {
        public void execute( Account account, Date date ) {
            
            double accumulatedCorpus = SuperannuationAccount.this.getAmount() ;  
            double withdrawalAmt = maxTaxFreeWithdrawalPct * accumulatedCorpus ;
            double annuityCorpus = accumulatedCorpus - withdrawalAmt ;
            
            annuityPayoutPerMonth = (annuityCorpus/1000)*annuityPer1000PerYear/12 ;
            
            Utils.transfer( withdrawalAmt, 
                            SuperannuationAccount.this, 
                            getUniverse().getAccount( getParentAccountNumber() ),
                            date, "" ) ;
        }
    }
    
    @Override
    public void initializePostConfig() {
        super.initializePostConfig() ;
        super.removeAllClosureActions() ;
        super.addClosureAction( new SAClosureAction() ) ;
    }
    
    public void handleEndOfMonthEvent( Date date ) {
        if( isAccountClosed ) {
            Txn txn = new Txn( getParentAccountNumber(), 
                               annuityPayoutPerMonth, date,
                               "Annuity payout from " + getName() ) ;
            getUniverse().postTransaction( txn ) ;
        }
    }
}
