package com.sandy.capitalyst.ui.panel.chart;

import java.awt.Color ;
import java.awt.GridLayout ;
import java.util.ArrayList ;
import java.util.List ;

import javax.swing.BorderFactory ;
import javax.swing.JPanel ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.ui.helper.AccountWrapper ;

@SuppressWarnings( "serial" )
public class CapitalystChartPanel extends JPanel {

    private List<CapitalystChart> charts = new ArrayList<CapitalystChart>() ;
    private GridLayout            layout = new GridLayout( 1, 1, 1, 1 ) ;
    
    private int numColsInLayout = 1 ;
    private CapitalystChart activeChart = null ;
    
    public CapitalystChartPanel() {
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( layout ) ;
    }
    
    public void addChart( CapitalystChart chart ) {
        chart.setParentPanel( this ) ;
        this.charts.add( chart ) ;
        addChartToPanel( chart ) ;
    }
    
    public void setActive( CapitalystChart chart ) {
        
        if( activeChart != chart ) {
            if( activeChart != null ) {
                activeChart.setBorder( null ) ;
            }
            activeChart = chart ;
            chart.setBorder( BorderFactory.createLineBorder( Color.GREEN, 1 ) ) ;
        }
    }
    
    private void addChartToPanel( CapitalystChart chart ) {
        
        int numKhopchas = layout.getRows() * layout.getColumns() ;
        super.add( chart ) ;
        if( charts.size() > numKhopchas ) {
            layout.setRows( layout.getRows()+1 ) ;
        }
        setActive( chart ) ;
        super.validate() ;
    }
    
    public void removeActiveChart() {
        
        if( activeChart != null ) {
            super.remove( activeChart ) ;
            charts.remove( activeChart ) ;
            setLayoutColumns( numColsInLayout ) ;
            if( charts.size() > 0 ) {
                setActive( charts.get( 0 ) ) ;
            }
        }
    }
    
    public void setLayoutColumns( int cols ) {
        
        numColsInLayout = cols ;
        
        int numRows = 1 ;
        int numKhopchas = charts.size() ;
        if( numKhopchas == 0 ) {
            numKhopchas = numColsInLayout ;
        }
        else {
            numRows = charts.size()/numColsInLayout ;
            if( numRows * numColsInLayout < charts.size() ) {
                numRows++ ;
            }
        }
        
        layout.setRows( numRows );
        layout.setColumns( numColsInLayout ) ;
        super.doLayout() ;
        super.validate() ;
    }

    public void changeNumChartCols( int i ) {
        if( numColsInLayout + i > 0 ) {
            setLayoutColumns( numColsInLayout + i ) ;
        }
    }
    
    public void removeUniverse( Universe u ) {
        for( CapitalystChart chart : charts ) {
            chart.removeUniverse( u ) ;
        }
    }

    public void updateTimeSeries( AccountWrapper wrapper ) {
        for( CapitalystChart chart : charts ) {
            chart.updateTimeSeries( wrapper ) ;
        }
    }
}
