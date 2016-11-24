package com.sandy.capitalyst.ui.panel.tree;

import java.awt.Component ;

import javax.swing.JTree ;
import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.DefaultTreeCellRenderer ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.ui.helper.UIConstants ;
import com.sandy.capitalyst.ui.panel.chart.SeriesColorManager ;

@SuppressWarnings( "serial" )
public class CapitalystTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent( 
            JTree tree, Object value, boolean sel, boolean expanded, 
            boolean leaf, int row, boolean hasFocus ) {
        
        Component comp =  super.getTreeCellRendererComponent( 
                                                     tree, value, sel, expanded, 
                                                     leaf, row, hasFocus ) ;
        
        if( value != null && value instanceof DefaultMutableTreeNode ) {
            Object userObj = (( DefaultMutableTreeNode )value).getUserObject() ;
            if( userObj instanceof Universe ) {
                Universe univ = ( Universe )userObj ;
                comp.setForeground( SeriesColorManager.instance().getColor( univ.getName() ) ) ;
                comp.setFont( UIConstants.TREE_UNIV_FONT ) ;
            }
            else {
                comp.setFont( UIConstants.TREE_FONT ) ;
            }
        }
        
        return comp ;
    }

    
}
