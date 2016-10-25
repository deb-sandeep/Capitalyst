package com.sandy.capitalyst.ui.panel;

import java.awt.BorderLayout ;
import java.io.File ;
import java.net.URL ;
import java.util.ArrayList ;
import java.util.List ;

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
    
    private List<Universe> universes = null ;
    
    private TransferHandler accountTransferHandler = null ;
    
    public CapitalystProjectPanel() {
        universes = new ArrayList<Universe>() ;
        accountTransferHandler = new AccountTransferHandler() ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        chartPanel = new CapitalystChartPanel() ;
        treePanel  = new CapitalystTreePanel( accountTransferHandler ) ;
        
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
        
        universes.add( universe ) ;
        treePanel.addUniverse( universe ) ;
    }
    
    public void newChart() {
        chartPanel.addChart( new CapitalystChart( accountTransferHandler ) ) ;
    }
    
    public void runSimulation() {
        for( Universe u : universes ) {
            Thread t = new Thread() {
                public void run() {
                    u.run() ;
                }
            } ;
            t.start() ;
        }
    }

    public CapitalystChartPanel getChartPanel() {
        return chartPanel ;
    }
}