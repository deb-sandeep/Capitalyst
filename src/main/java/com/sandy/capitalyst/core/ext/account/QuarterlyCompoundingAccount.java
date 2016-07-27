package com.sandy.capitalyst.core.ext.account;

import java.time.Duration ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.DayClock ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.timeobserver.EndOfDayObserver ;
import com.sandy.capitalyst.core.timeobserver.EndOfQuarterObserver ;

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
            
            Duration duration = Duration.between( receiptDate.toInstant(), 
                                                  date.toInstant() ) ;
            
            long numDays = duration.toDays() ;
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
    private List<QuantumOfMoney> quantumFragments = 
                   new ArrayList<QuarterlyCompoundingAccount.QuantumOfMoney>() ;

    public QuarterlyCompoundingAccount( String id, String name, 
                                        double initialAmt, double roi, 
                                        String bankName ) {
        super( id, name, initialAmt, bankName ) ;
        this.rateOfInterest = roi ;
        
        if( initialAmt != 0 ) {
            QuantumOfMoney quantum = null ;
            quantum = new QuantumOfMoney( initialAmt, 
                                          DayClock.instance().getStartDate() ) ;
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
        
        double totalAmount = 0 ;
        
        for( QuantumOfMoney q : quantumFragments ) {
            q.computeAndCollateInterestTillDate( date ) ;
            totalAmount += q.getAmount() ;
        }
        
        super.amount = totalAmount ;
    }

    @Override
    public void handleEndOfQuarterEvent( Date date ) {
        
        QuantumOfMoney nettedQuantum = new QuantumOfMoney( 0, date ) ;
        
        quantumFragments.forEach( q -> nettedQuantum.addAmount( q.getAmount() ) );
        quantumFragments.clear() ;
        quantumFragments.add( nettedQuantum ) ;
    }
}
