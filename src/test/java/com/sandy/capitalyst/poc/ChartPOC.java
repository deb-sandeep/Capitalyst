package com.sandy.capitalyst.poc;

import java.awt.BorderLayout ;
import java.awt.Container ;
import java.util.Date ;
import java.util.concurrent.ThreadLocalRandom ;

import javax.swing.JFrame ;

import org.jfree.chart.ChartFactory ;
import org.jfree.chart.ChartPanel ;
import org.jfree.chart.JFreeChart ;
import org.jfree.chart.plot.XYPlot ;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer ;
import org.jfree.data.time.Day ;
import org.jfree.data.time.TimeSeries ;
import org.jfree.data.time.TimeSeriesCollection ;

import com.sandy.capitalyst.util.Utils ;

@SuppressWarnings( "serial" )
public class ChartPOC extends JFrame {

    private TimeSeries series1 = null ;
    private TimeSeries series2 = null ;
    
    private TimeSeriesCollection seriesCollection = null ;
    
    private JFreeChart chart = null ;
    
    public ChartPOC() {
        super( "JFreeChart POC" ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        createChart() ;
        Container contentPane = super.getContentPane() ;
        contentPane.setLayout( new BorderLayout() );
        contentPane.add( new ChartPanel( chart ), BorderLayout.CENTER ) ;
        
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ) ;
        setExtendedState( getExtendedState()|JFrame.MAXIMIZED_BOTH );
        setVisible( true ) ;
    }
    
    private void createChart() {
        createSeries() ;
        chart = ChartFactory.createTimeSeriesChart( 
                      "Time series", 
                      "Time", 
                      "Amount", 
                      seriesCollection ) ;
        
        XYPlot plot = ( XYPlot )chart.getPlot() ;
        plot.setRenderer( new SamplingXYLineRenderer() ) ;
    }
    
    private void createSeries() {
        series1 = new TimeSeries( "Series 1", null, null ) ;
        series2 = new TimeSeries( "Series 2", null, null ) ;
        
        seriesCollection = new TimeSeriesCollection() ;
        seriesCollection.addSeries( series1 ) ;
        seriesCollection.addSeries( series2 ) ;
    }
    
    public void runPOC() {
        generateData() ;
    }
    
    private void generateData() {

        Thread thread = new Thread() {
            public void run() {
                Date startDate = Utils.parseDate( "01/01/2016" ) ;
                Date endDate   = Utils.parseDate( "01/01/2045" ) ;
                Date today     = startDate ;
                
                while( Utils.isBefore( today, endDate ) ) {
                    
                    int s1Val = ThreadLocalRandom.current().nextInt( -300, 300 ) ;
                    int s2Val = ThreadLocalRandom.current().nextInt( -300, 300 ) ;
                    
                    Number s1LastVal = null ;
                    Number s2LastVal = null ;
                    
                    if( series1.isEmpty() ) {
                        s1LastVal = Double.valueOf( 0 ) ;
                    }
                    else {
                        s1LastVal = series1.getValue( series1.getItemCount()-1 ) ;
                    }
                    
                    if( series2.isEmpty() ) {
                        s2LastVal = Double.valueOf( 0 ) ;
                    }
                    else {
                        s2LastVal = series2.getValue( series2.getItemCount()-1 ) ;
                    }
                    
                    series1.add( new Day( today ), s1LastVal.doubleValue() + s1Val );
                    series2.add( new Day( today ), s2LastVal.doubleValue() + s2Val );
                    
                    today = Utils.addDays( 1, today ) ;
                    
                    try {
                        Thread.sleep( 10 ) ;
                    }
                    catch( Exception e ) {
                        // Do nothing
                    }
                }
            }
        } ;
        thread.start() ;
    }
    
    public static void main( String[] args ) {
        
        ChartPOC poc = new ChartPOC() ;
        poc.runPOC() ;
    }
}
