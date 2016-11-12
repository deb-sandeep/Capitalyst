package com.sandy.capitalyst.util;

import java.lang.reflect.Field ;

public class ConfigurableField {
    
    private Field field = null ;
    private boolean mandatory = false ;
    
    public ConfigurableField( Field f, boolean mandatory ) {
        this.field = f ;
        this.mandatory = mandatory ;
    }
    
    public String getName() {
        return this.field.getName() ;
    }
    
    public Field getField() {
        return this.field ;
    }
    
    public boolean isMandatory() {
        return this.mandatory ;
    }
}