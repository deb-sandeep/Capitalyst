package com.sandy.capitalyst.ui.panel.chart;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.util.LinkedHashMap ;
import java.util.Map ;

import javax.swing.JPanel ;

import org.jfree.chart.ChartFactory ;
import org.jfree.chart.ChartPanel ;
import org.jfree.chart.JFreeChart ;
import org.jfree.chart.plot.XYPlot ;
import org.jfree.data.time.TimeSeries ;
import org.jfree.data.time.TimeSeriesCollection ;

@SuppressWarnings( "serial" )
public class CapitalystChart extends JPanel {

    private class TimeSeriesWrapper {
        TimeSeries series ;
        boolean isHidden = false ;
        
        public TimeSeriesWrapper( TimeSeries series ) {
            this.series = series ;
        }
    }
    
    private Map<String, TimeSeriesWrapper> timeSeriesMap    = null ;
    private TimeSeriesCollection           seriesCollection = null ;
    private CapitalystChartPanel           parent           = null ;
    
    private String     title = null ;
    private JFreeChart chart = null ;
    private XYPlot     plot  = null ;
    
    public CapitalystChart() {
        
        timeSeriesMap    = new LinkedHashMap<String, CapitalystChart.TimeSeriesWrapper>() ;
        seriesCollection = new TimeSeriesCollection() ;
        
        createChart() ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        add( new ChartPanel( chart ) ) ;
    }
    
    private void createChart() {
        chart = ChartFactory.createTimeSeriesChart( 
                      this.title, 
                      null, 
                      "Amount", 
                      seriesCollection ) ;
        chart.setBackgroundPaint( Color.BLACK ) ;
        
        plot = ( XYPlot )chart.getPlot() ;
        plot.setBackgroundPaint( Color.BLACK ) ;
        plot.setDomainGridlinePaint( Color.DARK_GRAY ) ;
        plot.setRangeGridlinePaint( Color.DARK_GRAY ) ;
    }
    
    public void setParentPanel( CapitalystChartPanel panel ) {
        parent = panel ;
    }
    
    public void removeFromParentPanel() {
        parent.removeChartFromPanel( this ) ;
    }
    
    public void addSeries( TimeSeries series ) {
        timeSeriesMap.put( (String)series.getKey(), 
                           new TimeSeriesWrapper( series ) ) ;
        seriesCollection.addSeries( series ) ;
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
