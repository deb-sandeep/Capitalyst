package com.sandy.capitalyst.app.ui;

import java.awt.event.KeyEvent ;

import javax.swing.JMenu ;
import javax.swing.JMenuBar ;

import com.sandy.capitalyst.app.ui.actions.Actions ;
import com.sandy.capitalyst.app.util.ObjectRepository ;

@SuppressWarnings( "serial" )
public class AppMenu extends JMenuBar {

    private Actions actions = null ;
    
    public AppMenu() {
        actions = ObjectRepository.getUiActions() ;
        setUpMenus() ;
    }
    
    private void setUpMenus() {
        add( buildAppMenu() ) ;
    }
    
    private JMenu buildAppMenu() {
        JMenu menu = new JMenu( "App" ) ;
        menu.setMnemonic( KeyEvent.VK_A ) ;
        
        menu.add( actions.getExitAppAction() ) ;
        return menu ;
    }
}
