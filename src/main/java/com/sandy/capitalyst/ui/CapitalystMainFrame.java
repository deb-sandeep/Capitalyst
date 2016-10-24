package com.sandy.capitalyst.ui ;

import java.awt.BorderLayout ;
import java.io.File ;

import javax.swing.JFileChooser ;
import javax.swing.JFrame ;
import javax.swing.filechooser.FileFilter ;

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
        super.getContentPane().setLayout( new BorderLayout() ) ;
        
        setExtendedState( getExtendedState()|JFrame.MAXIMIZED_BOTH );
        setVisible( true ) ;
    }
    
    private void createOpenFileChooser() {
        openFileChooser = new JFileChooser() ;
        openFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        openFileChooser.setMultiSelectionEnabled( false ) ;
        openFileChooser.setCurrentDirectory( new File( "/home/sandeep/projects/source/CapitalystHome/src/main/config") );
        openFileChooser.setFileFilter( new FileFilter() {
            
            @Override public String getDescription() {
                return "Capitalyst definition file" ;
            }
            
            @Override public boolean accept( File f ) {
                boolean isCfgFile = false ;
                isCfgFile = f.isFile() && f.getName().endsWith( ".properties" ) ;
                return f.isDirectory() || isCfgFile ;
            }
        } ) ;
    }

    public void newProject() throws Exception {
        if( projectPanel != null ) {
            super.remove( projectPanel ) ;
        }
        
        projectPanel = new CapitalystProjectPanel() ;
        super.getContentPane().add( projectPanel, BorderLayout.CENTER ) ;
        super.validate() ;
        loadAndAddUniverse() ;
    }
    
    public void loadAndAddUniverse() throws Exception {
        
        int choice = openFileChooser.showOpenDialog( this ) ;
        if( choice == JFileChooser.APPROVE_OPTION ) {
            File file = openFileChooser.getSelectedFile() ;
            projectPanel.loadUniverse( file ) ;
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
