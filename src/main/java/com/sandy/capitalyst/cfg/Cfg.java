package com.sandy.capitalyst.cfg;

import java.lang.annotation.ElementType ;
import java.lang.annotation.Retention ;
import java.lang.annotation.RetentionPolicy ;
import java.lang.annotation.Target ;

@Retention( RetentionPolicy.RUNTIME )
@Target( {ElementType.FIELD} )

public @interface Cfg {
    public boolean mandatory() default true ;
}
