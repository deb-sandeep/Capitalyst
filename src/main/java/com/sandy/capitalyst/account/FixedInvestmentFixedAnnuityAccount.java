package com.sandy.capitalyst.account;

import java.time.ZoneId ;
import java.time.ZonedDateTime ;
import java.util.Date ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.util.Utils ;

public class FixedInvestmentFixedAnnuityAccount 
    extends AbstractFixedInvestmentAccount 
    implements EndOfDayObserver {
    
    @Cfg 
    private double annuityAmount = 0 ;
    
    @Cfg( mandatory=false )
    private Date annuityStartDate = null ;
    
    @Cfg( mandatory=false )
    private Date annuityEndDate = null ;
    
    @Cfg( mandatory=false )
    private ExecutionTime annuityPaymentSchedule = null ;
    
    public double getAnnuityAmount() {
        return annuityAmount ;
    }

    public void setAnnuityAmount( double amt ) {
        this.annuityAmount = amt ;
    }

    public Date getAnnuityStartDate() {
        return annuityStartDate == null ?
               super.getInvestmentEndDate() :
               annuityStartDate ;
    }

    public void setAnnuityStartDate( Date date ) {
        this.annuityStartDate = date ;
    }

    public Date getAnnuityEndDate() {
        return annuityEndDate ;
    }

    public void setAnnuityEndDate( Date date ) {
        this.annuityEndDate = date ;
    }
    
    public ExecutionTime getAnnuityPaymentSchedule() {
        return annuityPaymentSchedule ;
    }

    public void setAnnuityPaymentSchedule( ExecutionTime annuityPaymentSchedule ) {
        this.annuityPaymentSchedule = annuityPaymentSchedule ;
    }

    @Override
    public void handleEndOfDayEvent( Date date ) {
        
        checkAndMakeInvestmentTxn( date ) ;
        
        if( Utils.isSameOrAfter( date, getAnnuityStartDate() ) ) {
            
            if( annuityEndDate != null ) {
                if( Utils.isSameOrAfter( date, annuityEndDate ) ) {
                    return ;
                }
            }
            
            if( annuityPaymentSchedule == null ) {
                if( Utils.isEndOfMonth( date ) ) {
                    makeAnnuityPaymentTxn( date ) ;
                }
            }
            else {
                ZonedDateTime dt = null ;
                dt = ZonedDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() ) ;
                if( annuityPaymentSchedule.isMatch( dt ) ) {
                    makeAnnuityPaymentTxn( date ) ;
                }
            }
        }
    }

    private void makeAnnuityPaymentTxn( Date date ) {
        
        Txn txn = new Txn( getParentAccountNumber(), 
                           annuityAmount, 
                           date, 
                           "Annuity amount for " + getName() ) ;
        getUniverse().postTransaction( txn ) ;
    }
}
