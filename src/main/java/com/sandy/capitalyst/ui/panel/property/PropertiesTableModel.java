package com.sandy.capitalyst.ui.panel.property;

import java.util.Date ;
import java.util.List ;

import javax.swing.table.DefaultTableModel ;

import org.apache.commons.beanutils.PropertyUtils ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.UniverseLoader ;
import com.sandy.capitalyst.core.UniverseLoader.ConfigurableField ;
import com.sandy.capitalyst.util.Utils ;

@SuppressWarnings( "serial" )
public class PropertiesTableModel extends DefaultTableModel {
    
    static Logger log = Logger.getLogger( PropertiesTableModel.class ) ;
    
    private List<ConfigurableField> fieldCfgs = null ;
    private Object instance = null ;
    
    public PropertiesTableModel() {
        this( null ) ;
    }
    
    public PropertiesTableModel( Object instance ) {
        setModelDataSource( instance ) ;
    }
    
    public void setModelDataSource( Object instance ) {
        this.instance = instance ;
        if( instance != null ) {
            this.fieldCfgs = UniverseLoader.getAllConfigurableFields( instance.getClass() ) ;
            this.instance = instance ;
        }
        super.fireTableDataChanged() ;
    }
    
    @Override
    public int getRowCount() { 
        if( instance == null ) return 0 ;
        return fieldCfgs.size() ; 
    }
    
    @Override
    public Class<?> getColumnClass( int columnIndex ) {
        if( columnIndex == 0 ) return Boolean.class ;
        return super.getColumnClass( columnIndex ) ;
    }

    @Override
    public int getColumnCount() { 
        return 3 ; 
    }

    @Override
    public String getColumnName( int column ) {
        switch( column ) {
            case 0:
                return "M" ;
            case 1:
                return "Field" ;
            case 2:
                return "Value" ;
        }
        return "UNDEFINED" ;
    }

    @Override
    public boolean isCellEditable( int row, int column ) {
        return column == 2 ;
    }

    @Override
    public Object getValueAt( int row, int column ) {

        if( instance == null ) return null ;
        
        ConfigurableField fieldCfg = fieldCfgs.get( row ) ;
        switch( column ) {
            case 0:
                return fieldCfg.isMandatory() ;
            case 1:
                return fieldCfg.getField().getName() ;
            case 2:
                Object val = null ;
                try {
                    val = PropertyUtils.getProperty( instance, fieldCfg.getField().getName() ) ;
                    if( val instanceof Date ) {
                        val = Utils.SDF.format( ( Date )val ) ;
                    }
                    else if( val instanceof Number ) {
                        val = Utils.DF.format( ((Number)val).doubleValue() ) ;
                    }
                }
                catch( Exception e ) {
                    log.error( "Error gettign property value", e ) ;
                    val = "EXCEPTION" ;
                }
                return val.toString() ;
        }
        return "UNDEFINED" ;
    }
}