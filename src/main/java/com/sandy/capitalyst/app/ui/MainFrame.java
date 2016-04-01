package com.sandy.capitalyst.app.ui;

import static com.sandy.capitalyst.app.ui.util.UIUtil.getIcon ;

import java.awt.Component ;

import javax.swing.BorderFactory ;
import javax.swing.JMenuBar ;
import javax.swing.border.BevelBorder ;

import com.sandy.common.ui.AbstractMainFrame ;
import com.sandy.common.ui.statusbar.MessageSBComponent ;
import com.sandy.common.ui.statusbar.StatusBar ;

public class MainFrame extends AbstractMainFrame {

    private static final long serialVersionUID = -793491630867632079L ;
    
    private StatusBar          statusBar          = null ;
    private MessageSBComponent statusMsgComponent = null ;
    
    public MainFrame() throws Exception {
        super( "Capitalyst", getIcon( "app_icon" ) ) ;
    }

    @Override
    protected Component getCenterComponent() {
        return null ;
    }

    @Override
    public void handleWindowClosing() {
        super.handleWindowClosing() ;
    }
    
    @Override
    protected void setUpListeners() {
        super.setUpListeners() ;
    }
    
    protected JMenuBar getFrameMenu() {
        return new AppMenu() ;
    }
    
    protected StatusBar getStatusBar() {
        if( statusBar == null ) {
            statusBar = new StatusBar() ;
            statusMsgComponent = new MessageSBComponent() ;
            statusBar.addStatusBarComponent( statusMsgComponent,
                                             StatusBar.Direction.WEST ) ;
            statusBar.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) ) ;
        }
        return statusBar ;
    }
    
    public void logStatus( String status ) {
        statusMsgComponent.logMessage( status ) ;
    }
}
