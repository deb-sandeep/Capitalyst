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

@SuppressWarnings( "serial" )
public class CapitalystChart extends JPanel {
    
    static final Logger log = Logger.getLogger( CapitalystChart.class ) ;

    private class TimeSeriesWrapper {
        TimeSeries series ;
        boolean isHidden = false ;
        
        public TimeSeriesWrapper( TimeSeries series ) {
            this.series = series ;
        }
    }
    
    private static final Font AXIS_FONT = new Font( "Helvetica", Font.PLAIN, 11 ) ;
    
    private Map<String, TimeSeriesWrapper> timeSeriesMap    = null ;
    private TimeSeriesCollection           seriesCollection = null ;
    private CapitalystChartPanel           parent           = null ;
    private TransferHandler                transferHandler  = null ;
    
    private String     title = null ;
    private JFreeChart chart = null ;
    private XYPlot     plot  = null ;
    
    public CapitalystChart( TransferHandler th ) {
        
        transferHandler  = th ;
        timeSeriesMap    = new LinkedHashMap<String, CapitalystChart.TimeSeriesWrapper>() ;
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
    
    public void addSeries( TimeSeries series ) {
        
        if( !timeSeriesMap.containsKey( (String)series.getKey() ) ) {
            timeSeriesMap.put( (String)series.getKey(), 
                    new TimeSeriesWrapper( series ) ) ;
            seriesCollection.addSeries( series ) ;
        }
    }
    
    public void removeSeries( TimeSeries series ) {
        timeSeriesMap.remove( series.getKey() ) ;
        seriesCollection.removeSeries( series ) ;
    }
    
    public void removeSeries( String key ) {
        TimeSeriesWrapper wrapper = timeSeriesMap.remove( key ) ;
        if( wrapper != null ) {
            seriesCollection.removeSeries( wrapper.series ) ;
        }
    }
    
    public void hideSeries( String key ) {
        TimeSeriesWrapper wrapper = timeSeriesMap.get( key ) ;
        if( wrapper != null ) {
            seriesCollection.removeSeries( wrapper.series ) ;
            wrapper.isHidden = true ;
        }
    }
    
    public void unhideSeries( String key ) {
        TimeSeriesWrapper wrapper = timeSeriesMap.get( key ) ;
        if( wrapper != null && wrapper.isHidden ) {
            wrapper.isHidden = false ;
            seriesCollection.addSeries( wrapper.series ) ;
        }
    }
}
