package com.sandy.capitalyst.ui.panel.chart;

import java.awt.GridLayout ;
import java.util.ArrayList ;
import java.util.List ;

import javax.swing.JPanel ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.ui.helper.AccountWrapper ;

@SuppressWarnings( "serial" )
public class CapitalystChartPanel extends JPanel {

    private List<CapitalystChart> charts = new ArrayList<CapitalystChart>() ;
    private GridLayout            layout = new GridLayout( 1, 1, 1, 1 ) ;
    
    private int numColsInLayout = 1 ;
    
    public CapitalystChartPanel() {
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        setLayout( layout ) ;
    }

    public void addChart( CapitalystChart chart ) {
        
        this.charts.add( chart ) ;
        addChartToPanel( chart ) ;
    }
    
    private void addChartToPanel( JPanel chart ) {
        
        int numKhopchas = layout.getRows() * layout.getColumns() ;
        super.add( chart ) ;
        if( charts.size() > numKhopchas ) {
            layout.setRows( layout.getRows()+1 ) ;
        }
        super.validate() ;
    }
    
    public void removeChartFromPanel( JPanel chart ) {
        super.remove( chart ) ;
        charts.remove( chart ) ;
        setLayoutColumns( numColsInLayout ) ;
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
