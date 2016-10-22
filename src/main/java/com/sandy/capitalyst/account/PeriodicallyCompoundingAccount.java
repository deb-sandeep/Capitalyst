package com.sandy.capitalyst.account;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.action.TransferFullAmtOnClosure ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.util.Utils ;

public class PeriodicallyCompoundingAccount extends BankAccount 
    implements EndOfDayObserver {
    
    static Logger log = Logger.getLogger( PeriodicallyCompoundingAccount.class ) ;
    
    class QuantumOfMoney {
        
        private double principal ;
        private Date   receiptDate ;
        private double interestTillDate ;
        
        public QuantumOfMoney( double principal, Date receiptDate ) {
            
            this.principal        = principal ;
            this.receiptDate      = receiptDate ;
            this.interestTillDate = 0 ;
        }
        
        public void computeAndCollateInterestTillDate( Date date ) {
            
            long numDays = Utils.getNumDaysBetween( receiptDate, date ) ;
            interestTillDate = principal*(roi/(100*365)) * numDays ;
        }
        
        public double getAmount() {
            return principal + interestTillDate ;
        }
        
        public double getPrincipal() {
            return this.principal ;
        }
        
        public double getInterest() {
            return this.interestTillDate ;
        }
        
        public void addAmount( double amt ) {
            this.principal += amt ;
        }
    }
    
    @Cfg 
    private double roi = 0 ;
    
    @Cfg( mandatory=false ) 
    private Date openingDate = null ;
    
    @Cfg
    private Date closingDate = null ;
    
    @Cfg
    private String parentAccountNumber = null ;
    
    protected boolean isAccountClosed = false ;
    
    protected List<QuantumOfMoney> quantumFragments = 
                   new ArrayList<PeriodicallyCompoundingAccount.QuantumOfMoney>() ;

    @Override
    public void initializePostConfig() {
        
        super.initializePostConfig() ;
        
        if( this.openingDate == null ) {
            this.openingDate = getUniverse().now() ;
        }
        
        if( this.closingDate != null && 
            Utils.isAfter( this.openingDate, this.closingDate ) ) {
            
            throw new IllegalArgumentException( 
                  "Opening date is later than closing date for account " + 
                  super.getAccountNumber() + " [" + getName() + "]" 
            ) ;
        }
        
        double initialAmt = super.getAmount() ;
        if( initialAmt != 0 ) {
            
            Date date = getUniverse().now() ;
            if( Utils.isAfter( getUniverse().now(), closingDate ) ) {
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

        if( Utils.isAfter( getUniverse().now(), closingDate ) ) {
            isAccountClosed = true ;
            closeAccount( closingDate ) ;
        }
        else if( super.amount > 0 ){
            QuantumOfMoney quantum = null ;
            quantum = new QuantumOfMoney( super.amount, getUniverse().now() ) ;
            quantumFragments.add( quantum ) ;
        }
    }
    
    public void setOpeningDate( Date date ) {
        this.openingDate = date ;
    }

    public void setClosingDate( Date date ) {
        this.closingDate = date ;
    }
    
    public void setRoi( double roi ) {
        this.roi = roi ;
    }
    
    public void setParentAccountNumber( String acctNo ) {
        this.parentAccountNumber = acctNo ;
        super.addClosureAction( new TransferFullAmtOnClosure( acctNo ) );
    }
    
    public String getParentAccountNumber() {
        return this.parentAccountNumber ;
    }
    
    public void postTransaction( Txn t ) {

        super.postTransaction( t ) ;
        QuantumOfMoney quantum = new QuantumOfMoney( t.getAmount(), t.getDate() ) ;
        quantumFragments.add( quantum ) ;
    }
    
    @Override
    public void handleEndOfDayEvent( Date date ) {

        if( !isAccountClosed ) {
            for( QuantumOfMoney q : quantumFragments ) {
                q.computeAndCollateInterestTillDate( date ) ;
            }
            
            if( closingDate != null && Utils.isSame( closingDate, date ) ) {
                isAccountClosed = true ;
                postAccumulatedInterest( date ) ;
                closeAccount( date ) ;
            }
        }
    }
    
    protected void postAccumulatedInterest( Date date ) {
        
        double accumulatedInterest  = 0 ;
        for( QuantumOfMoney q : quantumFragments ) {
            accumulatedInterest  += q.getInterest() ;
        }
        
        quantumFragments.clear() ;
        quantumFragments.add( new QuantumOfMoney( super.getAmount(), date ) ) ;
        
        Txn txn = new Txn( getAccountNumber(), accumulatedInterest, date,
                           "Accumulated Interest" ) ;
        super.getUniverse().postTransaction( txn ) ;
    }
}
