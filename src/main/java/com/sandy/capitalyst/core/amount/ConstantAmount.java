package com.sandy.capitalyst.core.amount;

public class ConstantAmount implements Amount {

    private double amount = 0 ;
    
    public ConstantAmount( double amt ) {
        this.amount = amt ;
    }
    
    @Override
    public double getAmount() {
        return amount ;
    }
}
