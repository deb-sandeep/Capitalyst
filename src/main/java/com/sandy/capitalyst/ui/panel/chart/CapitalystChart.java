package com.sandy.capitalyst.ui.panel.chart;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseEvent ;
import java.util.ArrayList ;
import java.util.Collection ;
import java.util.LinkedHashMap ;
import java.util.Map ;

import javax.swing.JMenuItem ;
import javax.swing.JPanel ;
import javax.swing.JPopupMenu ;
import javax.swing.SwingUtilities ;
import javax.swing.TransferHandler ;

import org.apache.log4j.Logger ;
import org.jfree.chart.ChartFactory ;
import org.jfree.chart.ChartMouseEvent ;
import org.jfree.chart.ChartMouseListener ;
import org.jfree.chart.ChartPanel ;
import org.jfree.chart.JFreeChart ;
import org.jfree.chart.axis.ValueAxis ;
import org.jfree.chart.entity.ChartEntity ;
import org.jfree.chart.entity.LegendItemEntity ;
import org.jfree.chart.plot.XYPlot ;
import org.jfree.chart.title.LegendTitle ;
import org.jfree.data.time.TimeSeries ;
import org.jfree.data.time.TimeSeriesCollection ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.ui.helper.AccountWrapper ;
import com.sandy.capitalyst.ui.helper.UIConstants ;

@SuppressWarnings( "serial" )
public class CapitalystChart extends JPanel 
    implements ChartMouseListener, ActionListener {
    
    static final Logger log = Logger.getLogger( CapitalystChart.class ) ;

    private class TimeSeriesWrapper {
        
        AccountWrapper acWrapper = null ;
        TimeSeries series ;
        boolean isHidden = false ;
        
        public TimeSeriesWrapper( AccountWrapper accountWrapper ) {
            this.acWrapper = accountWrapper ;
            this.series = accountWrapper.getTimeSeries() ;
        }
    }
    
    private static final Font AXIS_FONT   = UIConstants.CHART_AXIS_FONT ;
    private static final Font LEGEND_FONT = UIConstants.CHART_LEGEND_FONT ;
    
    private Map<String, TimeSeriesWrapper> accountWrapperMap    = null ;
    private TimeSeriesCollection           seriesCollection = null ;
    private CapitalystChartPanel           parent           = null ;
    private TransferHandler                transferHandler  = null ;
    
    private String     title = null ;
    private JFreeChart chart = null ;
    private XYPlot     plot  = null ;
    private ChartPanel chartPanel = null ;
    
    private JPopupMenu legendPopupMenu = null ;
    private JMenuItem  removeSeriesMI = null ;
    private JMenuItem  removeAllSeriesMI = null ;
    
    private String seriesMarkedForRemoval = null ;
    
    public CapitalystChart( TransferHandler th ) {
        
        transferHandler  = th ;
        accountWrapperMap = new LinkedHashMap<String, CapitalystChart.TimeSeriesWrapper>() ;
        seriesCollection = new TimeSeriesCollection() ;
        
        createChart() ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        setTransferHandler( transferHandler );

        chartPanel = new ChartPanel( chart ) ;
        chartPanel.addChartMouseListener( this ) ;
        
        add( chartPanel ) ;
        configureLegendPopup() ;
    }
    
    private void createChart() {
        chart = ChartFactory.createTimeSeriesChart( 
                      this.title, 
                      null, 
                      "Amount (Lakhs)", 
                      seriesCollection ) ;
        chart.setBackgroundPaint( Color.BLACK ) ;
        
        configurePlot() ;
        configureAxes() ;
        configureLegends() ;
    }
    
    private void configurePlot() {
        
        plot = ( XYPlot )chart.getPlot() ;
        plot.setBackgroundPaint( Color.BLACK ) ;
        plot.setDomainGridlinePaint( Color.DARK_GRAY ) ;
        plot.setRangeGridlinePaint( Color.DARK_GRAY ) ;
    }
    
    private void configureLegendPopup() {
        
        removeSeriesMI = new JMenuItem( "Remove legend" ) ;
        removeSeriesMI.addActionListener( this )  ;
        
        removeAllSeriesMI = new JMenuItem( "Remove all legends" ) ;
        removeAllSeriesMI.addActionListener( this ) ;
        
        legendPopupMenu = new JPopupMenu() ;
        legendPopupMenu.add( removeSeriesMI ) ;
        legendPopupMenu.add( removeAllSeriesMI ) ;
    }
    
    private void configureAxes() {
        
        ValueAxis xAxis = plot.getRangeAxis() ;
        ValueAxis yAxis = plot.getDomainAxis() ;
        
        xAxis.setLabelFont( AXIS_FONT ) ;
        yAxis.setLabelFont( AXIS_FONT ) ;
        
        xAxis.setTickLabelFont( AXIS_FONT ) ;
        yAxis.setTickLabelFont( AXIS_FONT ) ;
        
        xAxis.setTickLabelPaint( Color.LIGHT_GRAY.darker() ) ;
        yAxis.setTickLabelPaint( Color.LIGHT_GRAY.darker() ) ;
    }
    
    private void configureLegends() {
        
        LegendTitle legend = chart.getLegend() ;
        legend.setItemFont( LEGEND_FONT ) ;
        legend.setItemPaint( Color.LIGHT_GRAY.darker() ) ;
        legend.setBackgroundPaint( Color.BLACK );
    }
    
    public void setParentPanel( CapitalystChartPanel panel ) {
        parent = panel ;
    }
    
    public void removeFromParentPanel() {
        parent.removeChartFromPanel( this ) ;
    }
    
    public void addSeries( AccountWrapper accountWrapper ) {
        
        TimeSeries series = accountWrapper.getTimeSeries() ;
        if( !accountWrapperMap.containsKey( (String)series.getKey() ) ) {
            accountWrapperMap.put( (String)series.getKey(), 
                                   new TimeSeriesWrapper( accountWrapper ) ) ;
            seriesCollection.addSeries( series ) ;
        }
    }
    
    public void removeSeries( TimeSeries series ) {
        accountWrapperMap.remove( series.getKey() ) ;
        seriesCollection.removeSeries( series ) ;
    }
    
    public void removeSeries( String key ) {
        TimeSeriesWrapper wrapper = accountWrapperMap.remove( key ) ;
        if( wrapper != null ) {
            seriesCollection.removeSeries( wrapper.series ) ;
        }
    }
    
    public void removeAllSeries() {
        Collection<String> seriesNames = new ArrayList<>() ;
        for( String key : accountWrapperMap.keySet() ) {
            seriesNames.add( key ) ;
        }
        
        for( String key : seriesNames ) {
            removeSeries( key ) ;
        }
    }
    
    public void hideSeries( String key ) {
        TimeSeriesWrapper wrapper = accountWrapperMap.get( key ) ;
        if( wrapper != null ) {
            seriesCollection.removeSeries( wrapper.series ) ;
            wrapper.isHidden = true ;
        }
    }
    
    public void unhideSeries( String key ) {
        TimeSeriesWrapper wrapper = accountWrapperMap.get( key ) ;
        if( wrapper != null && wrapper.isHidden ) {
            wrapper.isHidden = false ;
            seriesCollection.addSeries( wrapper.series ) ;
        }
    }
    
    public void removeUniverse( Universe u ) {
        for( TimeSeriesWrapper wrapper : accountWrapperMap.values() ) {
            Account  wrapperAccount  = wrapper.acWrapper.getAccount() ;
            Universe wrapperUniverse = wrapperAccount.getUniverse() ;
            
            if( wrapperUniverse == u ) {
                seriesCollection.removeSeries( wrapper.series ) ;
                accountWrapperMap.remove( (String)wrapper.series.getKey() ) ;
            }
        }
    }

    public void updateTimeSeries( AccountWrapper newACWrapper ) {
        
        TimeSeries newTimeSeries = newACWrapper.getTimeSeries() ;
        
        Collection<TimeSeriesWrapper> wrappers = null ;
        wrappers = new ArrayList<CapitalystChart.TimeSeriesWrapper>() ;
        for( TimeSeriesWrapper wrapper : accountWrapperMap.values() ) {
            wrappers.add( wrapper ) ;
        }
        
        for( TimeSeriesWrapper wrapper : wrappers ) {
            
            String oldSeriesName = (String)wrapper.series.getKey() ;
            String newSeriesName = (String)newTimeSeries.getKey() ;
            
            if( oldSeriesName.equals( newSeriesName ) ) {
                seriesCollection.removeSeries( wrapper.series ) ;
                accountWrapperMap.remove( (String)wrapper.series.getKey() ) ;
                addSeries( newACWrapper ) ;
            }
        }
    }

    @Override
    public void chartMouseClicked( ChartMouseEvent event ) {
        ChartEntity entity = event.getEntity() ;
        MouseEvent  mouse  = event.getTrigger() ;
        
        if( entity instanceof LegendItemEntity ) {
            LegendItemEntity legend = ( LegendItemEntity )entity ;
            String seriesKey = ( String )legend.getSeriesKey() ;
            
            if( SwingUtilities.isRightMouseButton( mouse ) && 
                mouse.isControlDown() ) {
                seriesMarkedForRemoval = seriesKey ;
                legendPopupMenu.show( this, mouse.getX(), mouse.getY() );
            }
        }
    }

    @Override
    public void chartMouseMoved( ChartMouseEvent event ) { /* NOOP */ }

    @Override
    public void actionPerformed( ActionEvent e ) {
        JMenuItem menu = ( JMenuItem )e.getSource() ;
        if( menu == removeSeriesMI ) {
            removeSeries( seriesMarkedForRemoval ) ;
        }
        else if( menu == removeAllSeriesMI ) {
            removeAllSeries() ;
        }
    } ;
}
