package com.sandy.capitalyst.txgen;

import java.util.Date ;

import com.cronutils.model.time.ExecutionTime ;
import com.sandy.capitalyst.core.amount.Amount ;

public class ScheduledTxnDef {
    
    private Amount        amount         = null ;
    private ExecutionTime executionTime  = null ;
    private String        debitACNo      = null ;
    private String        creditACNo     = null ;
    private Date          startDate      = null ;
    private Date          endDate        = null ;
    private String        description    = null ;
    private String        classifiers    = null ;
    private String        name           = null ;
    
    public Amount getAmount() {
        return amount ;
    }
    
    public void setAmount( Amount amount ) {
        this.amount = amount ;
    }

    public ExecutionTime getExecutionTime() {
        return executionTime ;
    }

    public void setExecutionTime( ExecutionTime executionTime ) {
        this.executionTime = executionTime ;
    }

    public String getDebitACNo() {
        return debitACNo ;
    }
    
    public void setDebitACNo( String debitACNo ) {
        this.debitACNo = debitACNo ;
    }
    
    public String getCreditACNo() {
        return creditACNo ;
    }
    
    public void setCreditACNo( String creditACNo ) {
        this.creditACNo = creditACNo ;
    }
    
    public Date getStartDate() {
        return startDate ;
    }
    
    public void setStartDate( Date startDate ) {
        this.startDate = startDate ;
    }
    
    public Date getEndDate() {
        return endDate ;
    }
    
    public void setEndDate( Date endDate ) {
        this.endDate = endDate ;
    }
    
    public String getDescription() {
        return description ;
    }
    
    public void setDescription( String description ) {
        this.description = description ;
    }
    
    public String getClassifiers() {
        return classifiers ;
    }

    public void setClassifiers( String classifiers ) {
        this.classifiers = classifiers ;
    }

    public String getName() {
        return name ;
    }

    public void setName( String name ) {
        this.name = name ;
    }
}
