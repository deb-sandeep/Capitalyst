package com.sandy.capitalyst.ui;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import javax.swing.JMenu ;
import javax.swing.JMenuBar ;
import javax.swing.JMenuItem ;

import org.apache.log4j.Logger ;

@SuppressWarnings( "serial" )
public class CapitalystMenuBar extends JMenuBar implements ActionListener {

    static Logger log = Logger.getLogger( CapitalystMenuBar.class ) ;
    
    private CapitalystMainFrame mainFrame = null ;
    
    private JMenuItem openProjectMI = null ;
    private JMenuItem exitMI = null ;
    
    private JMenuItem runSimulation = null ;
    
    public CapitalystMenuBar( CapitalystMainFrame frame ) {
        this.mainFrame = frame ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        super.add( createFileMenu() ) ;
        super.add( createSimulationMenu() ) ;
    }
    
    private JMenu createFileMenu() {
        
        openProjectMI = new JMenuItem( "Open project" ) ;
        openProjectMI.addActionListener( this ) ;

        exitMI = new JMenuItem( "Exit" ) ;
        exitMI.addActionListener( this ) ;
        
        JMenu menu = new JMenu( "File" ) ;
        menu.add( openProjectMI ) ;
        menu.addSeparator() ;
        menu.add( exitMI ) ;
        
        return menu ;
    }
    
    private JMenu createSimulationMenu() {
        
        runSimulation = new JMenuItem( "Run" ) ;
        runSimulation.addActionListener( this ) ;
        
        JMenu menu = new JMenu( "Simulation" ) ;
        menu.add( runSimulation ) ;
        
        return menu ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        JMenuItem mi = ( JMenuItem )e.getSource() ;
        if( mi == openProjectMI ) {
            try {
                mainFrame.openProject() ;
            }
            catch( Exception e1 ) {
                log.error( "Could not open project", e1 ) ;
            }
        }
        else if( mi == exitMI ) {
            mainFrame.exit() ;
        }
        else if( mi == runSimulation ) {
            mainFrame.runSimulation() ;
        }
    }
}
