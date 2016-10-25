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
    
    private JMenuItem newProjectMI   = null ;
    private JMenuItem loadUniverseMI = null ;
    private JMenuItem exitMI         = null ;
    
    private JMenuItem newChartMI     = null ;
    private JMenuItem increaseNumColMI = null ;
    private JMenuItem decreaseNumColMI = null ;

    private JMenuItem runSimulation = null ;
    
    public CapitalystMenuBar( CapitalystMainFrame frame ) {
        this.mainFrame = frame ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        super.add( createFileMenu() ) ;
        super.add( createSimulationMenu() ) ;
        super.add( createChartMenu() ) ;
    }
    
    private JMenu createFileMenu() {
        
        newProjectMI = new JMenuItem( "New project" ) ;
        newProjectMI.addActionListener( this ) ;
        
        loadUniverseMI = new JMenuItem( "Load universe" ) ;
        loadUniverseMI.addActionListener( this ) ;

        exitMI = new JMenuItem( "Exit" ) ;
        exitMI.addActionListener( this ) ;
        
        JMenu menu = new JMenu( "File" ) ;
        menu.add( newProjectMI ) ;
        menu.add( loadUniverseMI ) ;
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
    
    private JMenu createChartMenu() {
        
        newChartMI = new JMenuItem( "New chart" ) ;
        newChartMI.addActionListener( this ) ;
        
        increaseNumColMI = new JMenuItem( "Columns +" ) ;
        increaseNumColMI.addActionListener( this ) ;
        
        decreaseNumColMI = new JMenuItem( "Columns -" ) ;
        decreaseNumColMI.addActionListener( this ) ;
        
        JMenu menu = new JMenu( "Chart" ) ;
        menu.add( newChartMI ) ;
        menu.add( increaseNumColMI ) ;
        menu.add( decreaseNumColMI ) ;
        
        return menu ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        JMenuItem mi = ( JMenuItem )e.getSource() ;
        if( mi == newProjectMI ) {
            mainFrame.newProject() ;
        }
        else if( mi == loadUniverseMI ) {
            mainFrame.loadAndAddUniverse() ;
        }
        else if( mi == newChartMI ) {
            mainFrame.newChart() ;
        }
        else if( mi == increaseNumColMI ) {
            mainFrame.changeNumChartCols(1) ;
        }
        else if( mi == decreaseNumColMI ) {
            mainFrame.changeNumChartCols(-1) ;
        }
        else if( mi == exitMI ) {
            mainFrame.exit() ;
        }
        else if( mi == runSimulation ) {
            mainFrame.runSimulation() ;
        }
    }
}
