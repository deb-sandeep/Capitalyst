package com.sandy.capitalyst.account;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.clock.DayClock ;
import com.sandy.capitalyst.clock.EndOfDayObserver ;
import com.sandy.capitalyst.clock.EndOfQuarterObserver ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.util.Utils ;

public class QuarterlyCompoundingAccount extends BankAccount 
    implements EndOfDayObserver, EndOfQuarterObserver, PostConfigInitializable {
    
    static Logger log = Logger.getLogger( QuarterlyCompoundingAccount.class ) ;
    
    private class QuantumOfMoney {
        
        private double principal ;
        private Date   receiptDate ;
        private double interestTillDate ;
        
        public QuantumOfMoney( double principal, Date receiptDate ) {
            
            this.principal = principal ;
            this.receiptDate = receiptDate ;
            this.interestTillDate = 0 ;
        }
        
        public void computeAndCollateInterestTillDate( Date date ) {
            
            long numDays = Utils.getNumDaysBetween( receiptDate, date ) ;
            interestTillDate = principal*(roi/(100*365)) * numDays ;
        }
        
        public double getAmount() {
            return principal + interestTillDate ;
        }
        
        public void addAmount( double amt ) {
            this.principal += amt ;
        }
    }
    
    @Cfg 
    private double roi = 0 ;
    
    @Cfg( mandatory=false ) 
    private Date openingDate = null ;
    
    @Cfg( mandatory=false ) 
    private Date closingDate = null ;
    
    private boolean isAccountClosed = false ;
    
    private List<QuantumOfMoney> quantumFragments = 
                   new ArrayList<QuarterlyCompoundingAccount.QuantumOfMoney>() ;

    public QuarterlyCompoundingAccount() {
        this.openingDate = DayClock.instance().now() ;
    }
    
    @Override
    public void initializePostConfig() {
        
        double initialAmt = super.getAmount() ;
        if( initialAmt != 0 ) {
            
            Date date = DayClock.instance().now() ;
            if( Utils.isAfter( DayClock.instance().now(), closingDate ) ) {
                date = closingDate ;
            }
            
            int numDays = Utils.getNumDaysBetween( openingDate, date ) ;
            
            if( numDays > 0 ) {
                float numYears = ((float)numDays)/365 ;
                initialAmt = initialAmt * Math.pow( ( 1 + (roi/400) ), 
                                                    4*numYears ) ;
            }
            
            super.amount = initialAmt ;
        }

        if( Utils.isAfter( DayClock.instance().now(), closingDate ) ) {
            isAccountClosed = true ;
            super.closeAccount( closingDate ) ;
        }
        else if( super.amount > 0 ){
            QuantumOfMoney quantum = null ;
            quantum = new QuantumOfMoney( super.amount, DayClock.instance().now() ) ;
            quantumFragments.add( quantum ) ;
        }
    }
    
    public void setOpeningDate( Date date ) {
        this.openingDate = date ;
    }

    public void setRoi( double roi ) {
        this.roi = roi ;
    }
    
    public void setClosingDate( Date date ) {
        
        if( this.closingDate != null && 
            Utils.isAfter( this.openingDate, this.closingDate ) ) {
            
            throw new IllegalArgumentException( 
                  "Opening date is later than closing date for account " + 
                  super.getAccountNumber() + " [" + getName() + "]" 
            ) ;
        }
    }
    
    public void postTransaction( Txn t ) {

        super.postTransaction( t ) ;
        QuantumOfMoney quantum = new QuantumOfMoney( t.getAmount(), t.getDate() ) ;
        quantumFragments.add( quantum ) ;
    }
    
    @Override
    public void handleEndOfDayEvent( Date date ) {

        if( !isAccountClosed ) {
            double totalAmount = 0 ;
            
            for( QuantumOfMoney q : quantumFragments ) {
                q.computeAndCollateInterestTillDate( date ) ;
                totalAmount += q.getAmount() ;
            }
            
            super.amount = totalAmount ;
            
            if( closingDate != null && Utils.isSame( closingDate, date ) ) {
                isAccountClosed = true ;
                quantumFragments.clear() ;
                super.closeAccount( date ) ;
            }
        }
    }

    @Override
    public void handleEndOfQuarterEvent( Date date ) {
        
        if( !isAccountClosed ) {
            QuantumOfMoney nettedQuantum = new QuantumOfMoney( 0, date ) ;
            
            quantumFragments.forEach( q -> nettedQuantum.addAmount( q.getAmount() ) );
            quantumFragments.clear() ;
            quantumFragments.add( nettedQuantum ) ;
        }
    }
}
