package com.sandy.capitalyst.ui ;

import java.awt.BorderLayout ;
import java.io.File ;

import javax.swing.JFileChooser ;
import javax.swing.JFrame ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.ui.panel.CapitalystProjectPanel ;

@SuppressWarnings( "serial" )
public class CapitalystMainFrame extends JFrame {
    
    static Logger log = Logger.getLogger( CapitalystMainFrame.class ) ;
    
    private CapitalystMenuBar      menuBar         = null ;
    private JFileChooser           openFileChooser = null ;
    private CapitalystProjectPanel projectPanel    = null ; 
    
    public CapitalystMainFrame() {
        super( "Capitalyst" ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        createOpenFileChooser() ;
        
        menuBar = new CapitalystMenuBar( this ) ;
        super.setJMenuBar( menuBar ) ;
        
        projectPanel = new CapitalystProjectPanel() ;
        super.getContentPane().setLayout( new BorderLayout() ) ;
        super.getContentPane().add( projectPanel, BorderLayout.CENTER ) ;
        
        setExtendedState( getExtendedState()|JFrame.MAXIMIZED_BOTH );
        setVisible( true ) ;
    }
    
    private void createOpenFileChooser() {
        openFileChooser = new JFileChooser() ;
        openFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        openFileChooser.setMultiSelectionEnabled( false ) ;
        openFileChooser.setCurrentDirectory( new File( "/home/sandeep/projects/source/CapitalystHome/src/main/config") );
    }

    public void openProject() throws Exception {
        
        int choice = openFileChooser.showOpenDialog( this ) ;
        if( choice == JFileChooser.APPROVE_OPTION ) {
            File file = openFileChooser.getSelectedFile() ;
            projectPanel.createNewProject( file ) ;
        }
    }
    
    public void runSimulation() {
        projectPanel.runSimulation() ;
    }

    public void exit() {
        super.dispose() ;
        System.exit( 0 ) ;
    }
}
