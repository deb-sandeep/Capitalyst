package com.sandy.capitalyst.ui.panel;

import java.awt.BorderLayout ;
import java.io.File ;
import java.net.URL ;

import javax.swing.JComponent ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JSplitPane ;
import javax.swing.TransferHandler ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.UniverseLoader ;
import com.sandy.capitalyst.ui.helper.AccountTransferHandler ;
import com.sandy.capitalyst.ui.panel.chart.CapitalystChart ;
import com.sandy.capitalyst.ui.panel.chart.CapitalystChartPanel ;
import com.sandy.capitalyst.ui.panel.ledger.LedgerTabbedPane ;
import com.sandy.capitalyst.ui.panel.property.EntityPropertyEditPanel ;
import com.sandy.capitalyst.ui.panel.tree.CapitalystTreePanel ;
import com.sandy.common.ui.SwingUtils ;

@SuppressWarnings( "serial" )
public class CapitalystProjectPanel extends JPanel {
    
    private static Logger log = Logger.getLogger( CapitalystProjectPanel.class ) ;
    
    private CapitalystTreePanel     treePanel  = null ;
    private CapitalystChartPanel    chartPanel = null ;
    private EntityPropertyEditPanel propPanel  = null ;
    private LedgerTabbedPane        ledgerTabPane = null ;
    
    private TransferHandler accountTransferHandler = null ;
    
    
    public CapitalystProjectPanel() {
        accountTransferHandler = new AccountTransferHandler() ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        chartPanel    = new CapitalystChartPanel() ;
        propPanel     = new EntityPropertyEditPanel() ;
        ledgerTabPane = new LedgerTabbedPane() ;

        treePanel  = new CapitalystTreePanel( accountTransferHandler, 
                                              chartPanel, propPanel, 
                                              ledgerTabPane ) ;
        newChart() ;
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;
        splitPane.add( getLeftSidePanel() ) ;
        splitPane.add( getRightSidePanel() ) ;
        splitPane.setDividerLocation( 300 ) ;
        splitPane.setDividerSize( 5 ) ;
        splitPane.setOneTouchExpandable( true ) ;
        
        super.setLayout( new BorderLayout() ) ;
        super.add( splitPane ) ;
    }
    
    private JComponent getLeftSidePanel() {
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT ) ;
        splitPane.add( treePanel ) ;
        splitPane.add( propPanel ) ;
        splitPane.setDividerLocation( (int)(SwingUtils.getScreenHeight()*0.7) ) ;
        splitPane.setDividerSize( 2 ) ;
        splitPane.setOneTouchExpandable( false ) ;
        
        return splitPane ;
    }
    
    private JComponent getRightSidePanel() {
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;
        splitPane.add( chartPanel ) ;
        splitPane.add( ledgerTabPane ) ;
        splitPane.setDividerLocation( (int)(SwingUtils.getScreenWidth()*0.5) ) ;
        splitPane.setDividerSize( 5 ) ;
        splitPane.setOneTouchExpandable( true ) ;
        
        return splitPane ;
    }
    
    public void loadUniverse( File file ) throws Exception {
        
        try {
            URL url = file.toURI().toURL() ;
            UniverseLoader loader = new UniverseLoader( url ) ;
            Universe universe = loader.loadUniverse() ;
            
            treePanel.addUniverse( universe ) ;
        }
        catch( Exception e ) {
            log.error( "Error loading universe.", e ) ;
            JOptionPane.showMessageDialog( null, e.getMessage() );
        }
    }
    
    public void newChart() {
        chartPanel.addChart( new CapitalystChart( accountTransferHandler ) ) ;
    }
    
    public void removeChart() {
        chartPanel.removeActiveChart() ;
    }
    
    public CapitalystChartPanel getChartPanel() {
        return chartPanel ;
    }
}