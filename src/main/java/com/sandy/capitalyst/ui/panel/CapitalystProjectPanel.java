package com.sandy.capitalyst.ui.panel;

import java.awt.BorderLayout ;
import java.io.File ;
import java.net.URL ;

import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JSplitPane ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.UniverseLoader ;
import com.sandy.capitalyst.ui.panel.chart.CapitalystChart ;
import com.sandy.capitalyst.ui.panel.chart.CapitalystChartPanel ;
import com.sandy.capitalyst.ui.panel.tree.CapitalystTreePanel ;

@SuppressWarnings( "serial" )
public class CapitalystProjectPanel extends JPanel {
    
    private CapitalystTreePanel  treePanel  = null ;
    private CapitalystChartPanel chartPanel = null ;
    
    public CapitalystProjectPanel() {
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        chartPanel = new CapitalystChartPanel() ;
        treePanel  = new CapitalystTreePanel() ;
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;
        splitPane.add( treePanel ) ;
        splitPane.add( chartPanel ) ;
        splitPane.setDividerLocation( 250 ) ;
        
        super.setLayout( new BorderLayout() ) ;
        super.add( splitPane ) ;
    }
    
    public void loadUniverse( File file ) throws Exception {
        
        URL url = file.toURI().toURL() ;
        UniverseLoader loader = new UniverseLoader( url ) ;
        Universe universe = loader.loadUniverse() ;
        
        treePanel.addUniverse( universe ) ;
    }
    
    public void newChart() {
        chartPanel.addChart( new CapitalystChart() ) ;
    }
    
    public void runSimulation() {
        JOptionPane.showMessageDialog( this, "To be implemented" ) ;
    }

    public CapitalystChartPanel getChartPanel() {
        return chartPanel ;
    }
}