package com.sandy.capitalyst.app.ui;

import static com.sandy.capitalyst.app.ui.util.UIUtil.getIcon ;

import java.awt.Component ;

import javax.swing.JMenuBar ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.app.ui.widgets.SimulationPanel ;
import com.sandy.common.ui.AbstractMainFrame ;
import com.sandy.common.ui.CloseableTabbedPane ;

public class MainFrame extends AbstractMainFrame {

    private static final long serialVersionUID = -793491630867632079L ;
    
    private static Logger logger = Logger.getLogger( MainFrame.class ) ;
    
    private CloseableTabbedPane tabbedPane         = null ;
    
    public MainFrame() throws Exception {
        super( "Capitalyst", getIcon( "app_icon" ) ) ;
    }

    @Override
    protected Component getCenterComponent() {
        if( tabbedPane == null ) {
            tabbedPane = new CloseableTabbedPane() ;
        }
        return tabbedPane ;
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
    
    public void addNewSimulation() {
        logger.debug( "Loading new simulation" ) ;
        SimulationPanel simPanel = new SimulationPanel() ;
        tabbedPane.add( "Sim Panel", simPanel ) ;
    }
}
