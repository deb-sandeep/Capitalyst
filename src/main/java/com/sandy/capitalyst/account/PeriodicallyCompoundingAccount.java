package com.sandy.capitalyst.account;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.action.TransferFullAmtOnClosure ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.timeobservers.EndOfDayObserver ;
import com.sandy.capitalyst.util.QuantumOfMoney ;
import com.sandy.capitalyst.util.Utils ;

public class PeriodicallyCompoundingAccount extends BankAccount 
    implements EndOfDayObserver {
    
    static Logger log = Logger.getLogger( PeriodicallyCompoundingAccount.class ) ;
    
    @Cfg 
    private double roi = 0 ;
    
    @Cfg( mandatory=false ) 
    private Date openingDate = null ;
    
    @Cfg
    private Date closingDate = null ;
    
    @Cfg
    private String parentAccountNumber = null ;
    
    @Cfg( mandatory=false )
    private boolean interestTaxable = false ;
    
    protected List<QuantumOfMoney> quantumFragments = new ArrayList<QuantumOfMoney>() ;

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
            closeAccount( closingDate ) ;
        }
        else if( super.amount > 0 ){
            addNewQuantumOfMoney( super.amount, getUniverse().now() ) ;
        }
    }
    
    public void setOpeningDate( Date date ) {
        this.openingDate = date ;
    }
    
    public Date getOpeningDate() {
        return this.openingDate ;
    }

    public void setClosingDate( Date date ) {
        this.closingDate = date ;
    }
    
    public Date getClosingDate() {
        return this.closingDate ;
    }
    
    public void setRoi( double roi ) {
        this.roi = roi ;
    }
    
    public double getRoi() {
        return this.roi ;
    }
    
    public boolean isInterestTaxable() {
        return interestTaxable ;
    }

    public void setInterestTaxable( boolean interestTaxable ) {
        this.interestTaxable = interestTaxable ;
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
        addNewQuantumOfMoney( t.getAmount(), t.getDate() ) ;
    }
    
    protected void addNewQuantumOfMoney( double amt, Date date ) {
        quantumFragments.add( new QuantumOfMoney( amt, date, roi ) ) ;
    }
    
    @Override
    public void handleEndOfDayEvent( Date date ) {

        if( isActive() ) {
            for( QuantumOfMoney q : quantumFragments ) {
                q.computeAndCollateInterestTillDate( date ) ;
            }
            
            if( closingDate != null && Utils.isSame( closingDate, date ) ) {
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
        addNewQuantumOfMoney( super.getAmount(), date ) ;
        
        Txn txn = new Txn( getAccountNumber(), accumulatedInterest, date,
                           "Accumulated Interest" ) ;
        if( isInterestTaxable() ) {
            txn.setTaxable( true ) ;
            txn.setTaxableAmount( accumulatedInterest ) ;
        }
        
        super.getUniverse().postTransaction( txn ) ;
    }
}
