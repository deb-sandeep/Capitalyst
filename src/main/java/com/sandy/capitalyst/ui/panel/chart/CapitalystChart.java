package com.sandy.capitalyst.ui.panel.chart;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.util.LinkedHashMap ;
import java.util.Map ;

import javax.swing.JPanel ;
import javax.swing.TransferHandler ;

import org.apache.log4j.Logger ;
import org.jfree.chart.ChartFactory ;
import org.jfree.chart.ChartPanel ;
import org.jfree.chart.JFreeChart ;
import org.jfree.chart.axis.ValueAxis ;
import org.jfree.chart.plot.XYPlot ;
import org.jfree.chart.title.LegendTitle ;
import org.jfree.data.time.TimeSeries ;
import org.jfree.data.time.TimeSeriesCollection ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.ui.helper.AccountWrapper ;

@SuppressWarnings( "serial" )
public class CapitalystChart extends JPanel {
    
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
    
    private static final Font AXIS_FONT = new Font( "Helvetica", Font.PLAIN, 11 ) ;
    
    private Map<String, TimeSeriesWrapper> accountWrapperMap    = null ;
    private TimeSeriesCollection           seriesCollection = null ;
    private CapitalystChartPanel           parent           = null ;
    private TransferHandler                transferHandler  = null ;
    
    private String     title = null ;
    private JFreeChart chart = null ;
    private XYPlot     plot  = null ;
    
    public CapitalystChart( TransferHandler th ) {
        
        transferHandler  = th ;
        accountWrapperMap    = new LinkedHashMap<String, CapitalystChart.TimeSeriesWrapper>() ;
        seriesCollection = new TimeSeriesCollection() ;
        
        createChart() ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        setTransferHandler( transferHandler );
        add( new ChartPanel( chart ) ) ;
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
        legend.setItemFont( AXIS_FONT ) ;
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
}
