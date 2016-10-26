package com.sandy.capitalyst.ui.panel.property;

import java.awt.BorderLayout ;

import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTable ;
import javax.swing.table.TableColumn ;
import javax.swing.table.TableColumnModel ;

import com.sandy.capitalyst.core.UniverseConstituent ;
import com.sandy.capitalyst.ui.helper.UIConstants ;

@SuppressWarnings( "serial" )
public class EntityPropertyEditPanel extends JPanel {

    private JTable table = null ;
    private PropertiesTableModel tableModel = null ;
    
    public EntityPropertyEditPanel() {
        setUpUI() ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        tableModel = new PropertiesTableModel() ;
        table = new JTable( tableModel ) ;
        table.setFont( UIConstants.TABLE_FONT );
        
        JScrollPane sp = new JScrollPane( table ) ;
        configureTable() ;
        
        setLayout( new BorderLayout() ) ;
        add( sp ) ;
    }
    
    private void configureTable() {
        table.setFillsViewportHeight( true ) ;
        
        TableColumnModel colModel = table.getColumnModel() ;
        TableColumn tcMandatory   = colModel.getColumn( 0 ) ;
        
        tcMandatory.setMaxWidth( 20 ) ;
    }

    public void refreshEntity( UniverseConstituent entity ) {
        tableModel.setModelDataSource( entity ) ;
    }
}
