package com.sandy.capitalyst.ui.panel;

import java.awt.BorderLayout ;
import java.io.File ;
import java.net.URL ;

import javax.swing.JPanel ;
import javax.swing.JSplitPane ;
import javax.swing.TransferHandler ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.UniverseLoader ;
import com.sandy.capitalyst.ui.helper.AccountTransferHandler ;
import com.sandy.capitalyst.ui.panel.chart.CapitalystChart ;
import com.sandy.capitalyst.ui.panel.chart.CapitalystChartPanel ;
import com.sandy.capitalyst.ui.panel.tree.CapitalystTreePanel ;

@SuppressWarnings( "serial" )
public class CapitalystProjectPanel extends JPanel {
    
    private CapitalystTreePanel  treePanel  = null ;
    private CapitalystChartPanel chartPanel = null ;
    
    private TransferHandler accountTransferHandler = null ;
    
    public CapitalystProjectPanel() {
        accountTransferHandler = new AccountTransferHandler() ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        chartPanel = new CapitalystChartPanel() ;
        treePanel  = new CapitalystTreePanel( accountTransferHandler, this ) ;
        
        newChart() ;
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;
        splitPane.add( treePanel ) ;
        splitPane.add( chartPanel ) ;
        splitPane.setDividerLocation( 250 ) ;
        splitPane.setDividerSize( 5 ) ;
        splitPane.setOneTouchExpandable( true ) ;
        
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
        chartPanel.addChart( new CapitalystChart( accountTransferHandler ) ) ;
    }
    
    public CapitalystChartPanel getChartPanel() {
        return chartPanel ;
    }
    
    // Note that the tree calls on this method after the universe has been
    // removed from the tree and hence this method removes the universe from
    // all components except the tree.
    public void removeUniverse( Universe u ) {
        chartPanel.removeUniverse( u ) ;
    }
}