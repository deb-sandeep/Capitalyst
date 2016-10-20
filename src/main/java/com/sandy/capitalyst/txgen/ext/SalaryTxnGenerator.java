package com.sandy.capitalyst.txgen.ext;

import java.util.Calendar ;
import java.util.Date ;
import java.util.List ;
import java.util.concurrent.ThreadLocalRandom ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.txgen.ScheduledTxnGen ;
import com.sandy.capitalyst.util.Utils ;

public class SalaryTxnGenerator extends ScheduledTxnGen {
    
    static Logger log = Logger.getLogger( SalaryTxnGenerator.class ) ;
    
    // Salary components
    @Cfg private double basic = 0 ;
    @Cfg private double specialAllowance = 0 ;
    @Cfg private double medicalReimbusement = 0 ;
    @Cfg private double incentive = 0 ;
    @Cfg private double insurancePay = 0 ;
    
    @Cfg( mandatory=false ) 
    private double minIncrementPct = 5 ;
    
    @Cfg( mandatory=false ) 
    private double maxIncrementPct = 5 ;
    
    @Cfg( mandatory=false ) 
    private int minIncentiveMultiple = 3 ;
    
    @Cfg( mandatory=false ) 
    private int maxIncentiveMultiple = 3 ;
    
    @Cfg( mandatory=false ) 
    private int minIncentivePayoutPct = 80 ;
    
    @Cfg( mandatory=false ) 
    private int maxIncentivePayoutPct = 80 ;

    // Salary account number
    @Cfg private String salaryAccountNumber = null ;
    
    @Cfg private int nextIncentiveIncrementYear = 0 ;
    
    public void setBasic( double basic ) {
        this.basic = basic ;
    }

    public void setSpecialAllowance( double specialAllowance ) {
        this.specialAllowance = specialAllowance ;
    }

    public void setMedicalReimbusement( double medicalReimbusement ) {
        this.medicalReimbusement = medicalReimbusement ;
    }

    public void setIncentive( double incentive ) {
        this.incentive = incentive ;
    }

    public void setInsurancePay( double insurancePay ) {
        this.insurancePay = insurancePay ;
    }
    
    public void setSalaryAccountNumber( String acctNo ) {
        this.salaryAccountNumber = acctNo ;
    }
    
    public void setNextIncentiveIncrementYear( int year ) {
        this.nextIncentiveIncrementYear = year ;
    }

    public void setMinIncrementPct( double pct ) {
        this.minIncrementPct = pct ;
    }

    public void setMaxIncrementPct( double pct ) {
        this.maxIncrementPct = pct ;
    }

    public void setMinIncentiveMultiple( int mult ) {
        this.minIncentiveMultiple = mult ;
    }

    public void setMaxIncentiveMultiple( int mult ) {
        this.maxIncentiveMultiple = mult ;
    }
    
    public void setMinIncentivePayoutPct( int minPct ) {
        this.minIncentivePayoutPct = minPct ;
    }
    
    public void setMaxIncentivePayoutPct( int maxPct ) {
        this.maxIncentivePayoutPct = maxPct ;
    }
    
    public double getHra() {
        return 0.6 * this.basic ;
    }
    
    public double getEmployerPF() {
        return 0.12 * this.basic ;
    }

    @Override
    protected void generateScheduledTxnForDate( Date date, List<Txn> txnList ) {
        
        // Revisions are applicable from July every year
        if( Utils.getMonth( date ) == Calendar.JULY ) {
            computeAndApplyIncentives( date ) ;
        }

        // Payout of incentive and remaining bulk amounts
        if( Utils.getMonth( date ) == Calendar.MARCH ) {
            txnList.add( getIncentivePayoutTxn( date ) ) ;
        }
    }
    
    private void computeAndApplyIncentives( Date date ) {
        
        int year = Utils.getYear( date ) ;
        if( year == nextIncentiveIncrementYear ) {
            
            int randMult = ThreadLocalRandom.current().nextInt( 
                                minIncentiveMultiple, maxIncentiveMultiple+1 ) ;
            this.incentive += 50000*randMult ;
            nextIncentiveIncrementYear += 3 ;
        }
        
        int maxIncrementSteps = (int)(( maxIncrementPct - minIncrementPct )/10) ;
        int randIncrementStep = ThreadLocalRandom.current().nextInt( 0, maxIncrementSteps+1 ) ;
        double incrPct = minIncrementPct + 0.1*randIncrementStep ;
        
        this.basic            *= ( 1 + incrPct ) ;
        this.specialAllowance *= ( 1 + incrPct ) ;
    }
    
    private Txn getIncentivePayoutTxn( Date date ) {
        
        int maxIncrementSteps = (int)(( maxIncentivePayoutPct - minIncentivePayoutPct )/10) ;
        int randIncrementStep = ThreadLocalRandom.current().nextInt( 0, maxIncrementSteps+1 ) ;
        double incrPct = (minIncentivePayoutPct + 0.1*randIncrementStep)/100 ;
        
        Txn tx = new Txn( salaryAccountNumber, incentive*incrPct, date ) ;
        tx.setDescription( "Incentive payout" );
        
        return tx ;
    }
}
