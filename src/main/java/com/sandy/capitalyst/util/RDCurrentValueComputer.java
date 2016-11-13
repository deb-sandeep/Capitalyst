package com.sandy.capitalyst.util;

import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;

import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.cronutils.model.time.ExecutionTime ;

public class RDCurrentValueComputer {

    private static Logger log = Logger.getLogger( RDCurrentValueComputer.class ) ;
    
    private double investmentAmount = 0 ;
    private double roi = 0 ;
    private ExecutionTime investmentSchedule = null ;
    private Date openingDate = null ;
    private Date closingDate = null ;
    
    private double principal = 0 ;
    private double interest= 0 ;
    
    protected List<QuantumOfMoney> quantumFragments = new ArrayList<QuantumOfMoney>() ;
    
    public RDCurrentValueComputer( double amt, double roi,
                                   ExecutionTime schedule,
                                   Date start, Date end ) {
        this.investmentAmount = amt ;
        this.roi = roi ;
        this.investmentSchedule = schedule ;
        this.openingDate = start ;
        this.closingDate = end ;
    }
    
    public void compute() {
        
        log.debug( "Running simulation...\n" ) ;
        
        Date now = this.openingDate ;
        while( DateUtils.truncatedCompareTo( now, closingDate, Calendar.DATE ) < 0 ) {
            
            if( Utils.isMatch( investmentSchedule, now ) ) {
                addInvestment( now ) ;
            }
            
            if( Utils.isEndOfQuarter( now ) ) {
                rebalanceQuantums( now ) ;
            }
            
            computeCurrentValue( now ) ;
            
            now = DateUtils.truncate( DateUtils.addDays( now, 1 ), Calendar.DATE ) ;
        }
    }
    
    public double getValue() {
        return getPrincipal() + getInterest() ;
    }
    
    public double getPrincipal() {
        return this.principal ;
    }
    
    public double getInterest() {
        return this.interest ;
    }
    
    private void addInvestment( Date date ) {
        quantumFragments.add( new QuantumOfMoney( investmentAmount, date, roi ) ) ;
        this.principal += investmentAmount ;
    }
    
    private void rebalanceQuantums( Date date ) {
        double totalAmt = 0 ;
        for( QuantumOfMoney q : quantumFragments ) {
            totalAmt += q.getAmount() ;
        }
        
        quantumFragments.clear() ;
        quantumFragments.add( new QuantumOfMoney( totalAmt, date, roi ) ) ;
    }
    
    private void computeCurrentValue( Date date ) {
        double totalAmt = 0 ;
        for( QuantumOfMoney q : quantumFragments ) {
            q.computeAndCollateInterestTillDate( date ) ;
            totalAmt += q.getAmount() ;
        }
        this.interest = totalAmt - this.principal ;
    }
}
