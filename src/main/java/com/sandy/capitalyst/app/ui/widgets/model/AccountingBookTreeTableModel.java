package com.sandy.capitalyst.app.ui.widgets.model;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel ;

public class AccountingBookTreeTableModel extends AbstractTreeTableModel {

    public AccountingBookTreeTableModel() {
    }

    @Override
    public int getColumnCount() {
        return 0 ;
    }
    
    @Override
    public Class<?> getColumnClass( int column ) {
        return super.getColumnClass( column ) ;
    }

    @Override
    public String getColumnName( int column ) {
        return super.getColumnName( column ) ;
    }

    @Override
    public Object getValueAt( Object node, int column ) {
        return null ;
    }

    @Override
    public Object getChild( Object parent, int index ) {
        return null ;
    }

    @Override
    public int getChildCount( Object parent ) {
        return 0 ;
    }

    @Override
    public int getIndexOfChild( Object parent, Object child ) {
        return 0 ;
    }
}
