package com.sandy.capitalyst.core.ext.account;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.DayClock ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.action.AccountClosureAction ;
import com.sandy.capitalyst.core.timeobserver.EndOfDayObserver ;
import com.sandy.capitalyst.core.timeobserver.EndOfQuarterObserver ;
import com.sandy.capitalyst.util.Utils ;

public class QuarterlyCompoundingAccount extends BankAccount 
    implements EndOfDayObserver, EndOfQuarterObserver {
    
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
            interestTillDate = principal*(rateOfInterest/(100*365)) * numDays ;
        }
        
        public double getAmount() {
            return principal + interestTillDate ;
        }
        
        public void addAmount( double amt ) {
            this.principal += amt ;
        }
    }
    
    private double rateOfInterest = 0 ;
    private Date openingDate = null ;
    private Date closeDate = null ;
    private boolean isAccountClosed = false ;
    
    private List<QuantumOfMoney> quantumFragments = 
                   new ArrayList<QuarterlyCompoundingAccount.QuantumOfMoney>() ;

    public QuarterlyCompoundingAccount( String accountNumber, 
                                        double initialAmt, 
                                        Date openDate,
                                        Date closeDate,
                                        double roi, 
                                        AccountClosureAction... closeActions) {
        
        super( accountNumber, initialAmt, closeActions ) ;
        
        this.rateOfInterest = roi ;
        this.openingDate = openDate ;
        this.closeDate = closeDate ;
        
        if( this.openingDate == null ) {
            this.openingDate = DayClock.instance().now() ;
        }
        
        if( this.closeDate != null && 
            Utils.isAfter( this.openingDate, this.closeDate ) ) {
            throw new IllegalArgumentException( "Opening date is later than " +  
              "closing date for account " + accountNumber + " [" + getName() + "]" ) ;
        }
        
        if( initialAmt > 0 ) {
            computeStartAmount( initialAmt ) ;
        }
    }
    
    private void computeStartAmount( double initialAmt ) {
        
        if( initialAmt != 0 ) {
            
            Date date = DayClock.instance().now() ;
            if( Utils.isAfter( DayClock.instance().now(), closeDate ) ) {
                date = closeDate ;
            }
            
            int numDays = Utils.getNumDaysBetween( openingDate, date ) ;
            
            if( numDays > 0 ) {
                float numYears = ((float)numDays)/365 ;
                initialAmt = initialAmt * Math.pow( ( 1 + (rateOfInterest/400) ), 
                                                    4*numYears ) ;
            }
            
            super.amount = initialAmt ;
        }

        if( Utils.isAfter( DayClock.instance().now(), closeDate ) ) {
            isAccountClosed = true ;
            super.closeAccount( closeDate ) ;
        }
        else if( super.amount > 0 ){
            QuantumOfMoney quantum = null ;
            quantum = new QuantumOfMoney( super.amount, DayClock.instance().now() ) ;
            quantumFragments.add( quantum ) ;
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
            
            if( closeDate != null && Utils.isSame( closeDate, date ) ) {
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
