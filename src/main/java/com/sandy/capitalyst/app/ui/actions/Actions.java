package com.sandy.capitalyst.app.ui.actions;

import static com.sandy.capitalyst.app.util.ObjectRepository.getMainFrame ;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK ;
import static java.awt.event.KeyEvent.VK_N ;
import static java.awt.event.KeyEvent.VK_X ;

import java.awt.event.ActionEvent ;
import java.lang.reflect.Method ;

import javax.swing.JOptionPane ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.ReflectionUtil ;

public class Actions {

    private static final Logger logger = Logger.getLogger( Actions.class ) ;
    
    private AbstractBaseAction exitAppAction       = null ;
    private AbstractBaseAction newSimAction        = null ;
    
    private Object[][] menuConfig = {
        { "exitApp",       "Exit",             null,          VK_X,     -1  , -1 },
        
        { "newSimulation", "New simulation",   null,          VK_N,     VK_N, CTRL_DOWN_MASK}
    } ;
    
    public Actions() {
        exitAppAction       = constructAction( "exitApp"       ) ;
        newSimAction        = constructAction( "newSimulation" ) ;
    }
    
    public AbstractBaseAction getExitAppAction() {
        return exitAppAction ;
    }
    
    public AbstractBaseAction getNewSimAppAction() {
        return newSimAction ;
    }
    
    @SuppressWarnings( "serial" )
    private AbstractBaseAction constructAction( String actionId ) {
        
        AbstractBaseAction action = null ;
        int i = 0 ;
        
        for( i=0; i<menuConfig.length; i++ ) {
            if( menuConfig[i][0].toString().equals( actionId ) ) {
                break ;
            }
        }
        
        if( i == menuConfig.length ) {
            throw new IllegalArgumentException( "Menu with id " + actionId +
                                                " is not configured." ) ;
        }
        else {
            String fnName      = (String)menuConfig[i][0] ;
            String displayName = (String)menuConfig[i][1] ;
            String iconName    = (String)menuConfig[i][2] ;
            int    mnemonic    = (int)menuConfig[i][3] ;
            int    accelerator = (int)menuConfig[i][4] ;
            int    accMods     = (int)menuConfig[i][5] ;
            
            final Method m = ReflectionUtil.findMethod( Actions.class, fnName, null ) ;
            if( m == null ) {
                throw new IllegalArgumentException( "Method with name " + 
                                           actionId + " is not implemented." ) ;
            }
            else {
                action = new AbstractBaseAction( displayName, iconName, 
                                                 mnemonic, accelerator, 
                                                 accMods ) {
                    
                    @Override public void actionPerformed( ActionEvent e ) {
                        try {
                            m.setAccessible( true ) ;
                            m.invoke( Actions.this, (Object[])null ) ;
                        }
                        catch( Exception e1 ) {
                            JOptionPane.showMessageDialog( getMainFrame(), 
                                    "Error in invoking action - " + e1.getMessage() ) ;
                            logger.error( "Error invoking action", e1 ) ;
                        }
                    }
                } ;
            }
        }
        return action ;
    }

    @SuppressWarnings( "unused" )
    private void exitApp() {
        getMainFrame().handleWindowClosing() ;
    }
    
    @SuppressWarnings( "unused" )
    private void newSimulation() {
        getMainFrame().loadNewSimulation() ;
    }
}
