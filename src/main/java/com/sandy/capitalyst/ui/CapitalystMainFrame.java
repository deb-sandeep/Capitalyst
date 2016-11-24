package com.sandy.capitalyst.ui ;

import java.awt.BorderLayout ;
import java.awt.event.WindowAdapter ;
import java.awt.event.WindowEvent ;
import java.io.File ;

import javax.swing.JFileChooser ;
import javax.swing.JFrame ;
import javax.swing.UIManager ;
import javax.swing.filechooser.FileFilter ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.ui.panel.CapitalystProjectPanel ;
import com.sandy.common.ui.SwingUtils ;

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
        super.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                exit() ;
            }
        } );

        SwingUtils.centerOnScreen( this, 600, 500 ) ;
        setExtendedState( getExtendedState()|JFrame.MAXIMIZED_BOTH );
        
        try {
            UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
        } 
        catch ( Exception e ) {
            e.printStackTrace();
        }
        setVisible( true ) ;
        newProject() ;
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
                isCfgFile = f.isFile() && f.getName().endsWith( ".cap.properties" ) ;
                return f.isDirectory() || isCfgFile ;
            }
        } ) ;
    }

    public void newProject() {
        if( projectPanel != null ) {
            super.remove( projectPanel ) ;
        }
        
        projectPanel = new CapitalystProjectPanel() ;
        super.getContentPane().add( projectPanel, BorderLayout.CENTER ) ;
        super.validate() ;
        loadAndAddUniverse() ;
    }
    
    public void loadAndAddUniverse()  {
        
        try {
//            int choice = openFileChooser.showOpenDialog( this ) ;
//            if( choice == JFileChooser.APPROVE_OPTION ) {
//                File file = openFileChooser.getSelectedFile() ;
//                projectPanel.loadUniverse( file ) ;
//            }
            File file = new File( "/home/sandeep/projects/source/CapitalystHome/src/main/config/my.cap.properties" ) ;
            projectPanel.loadUniverse( file ) ;
        }
        catch( Exception e ) {
            log.error( "Could not load universe", e ) ;
        }
    }

    public void newChart() {
        projectPanel.newChart() ;
    }
    
    public void removeChart() {
        projectPanel.removeChart() ;
    }

    public void exit() {
        super.dispose() ;
        System.exit( 0 ) ;
    }

    public void changeNumChartCols( int i ) {
        projectPanel.getChartPanel().changeNumChartCols( i ) ;
    }
}
