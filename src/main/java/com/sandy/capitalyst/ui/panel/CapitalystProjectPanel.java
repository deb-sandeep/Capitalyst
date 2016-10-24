package com.sandy.capitalyst.ui.panel;

import java.awt.BorderLayout ;
import java.io.File ;
import java.net.URL ;

import javax.swing.JPanel ;
import javax.swing.JSplitPane ;

import org.jfree.data.time.Day ;
import org.jfree.data.time.TimeSeries ;

import com.sandy.capitalyst.EventType ;
import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.UniverseLoader ;
import com.sandy.capitalyst.ui.panel.chart.CapitalystChart ;
import com.sandy.capitalyst.ui.panel.chart.CapitalystChartPanel ;
import com.sandy.capitalyst.ui.panel.tree.CapitalystTreePanel ;
import com.sandy.common.bus.Event ;
import com.sandy.common.bus.EventSubscriber ;

@SuppressWarnings( "serial" )
public class CapitalystProjectPanel extends JPanel 
    implements EventSubscriber {
    
    private Universe currentUniverse = null ;
    
    private CapitalystTreePanel  treePanel     = null ;
    private CapitalystChartPanel chartPanel    = null ;
    private CapitalystChart      salaryACChart = null ;
    
    private TimeSeries salarySeries = new TimeSeries( "Salary AC" ) ;
    
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
    
    public void loadUniverse( File file ) 
        throws Exception {
        
        URL url = file.toURI().toURL() ;
        UniverseLoader loader = new UniverseLoader( url ) ;
        currentUniverse = loader.loadUniverse() ;
        
        currentUniverse.getBus().addSubscriberForEventTypes( this, false, EventType.TXN_POSTED ) ;
        
        salaryACChart = new CapitalystChart() ;
        salaryACChart.addSeries( salarySeries ) ;
        chartPanel.addChart( salaryACChart ) ;
        
        treePanel.addUniverse( currentUniverse ) ;
    }
    
    public void runSimulation() {
        Thread thread = new Thread() {
            public void run() {
                currentUniverse.run() ;
            }
        } ;
        thread.start() ;
    }

    @Override
    public void handleEvent( Event event ) {
        Txn txn = ( Txn )event.getValue() ;
        String acNo = txn.getAccountNumber() ;
        if( acNo.equals( "000501005212" ) ) {
            Account account = currentUniverse.getAccount( "000501005212" ) ;
            salarySeries.addOrUpdate( new Day( txn.getDate() ), account.getAmount()/100000 ) ;
            try {
                Thread.sleep( 10 );
            }
            catch( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        }
    }
}