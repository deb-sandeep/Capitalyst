package com.sandy.capitalyst.ui.panel.tree;

import java.awt.BorderLayout ;

import javax.swing.JPanel ;
import javax.swing.JTree ;

import com.sandy.capitalyst.core.Universe ;

@SuppressWarnings( "serial" )
public class CapitalystTreePanel extends JPanel {

    private CapitalystProjectTreeModel treeModel = null ;
    private JTree tree = null ;
    
    public CapitalystTreePanel() {
        setUpUI() ;
    }
    
    private void setUpUI() {
        treeModel = new CapitalystProjectTreeModel() ;
        tree = new JTree( treeModel ) ;
        tree.setRootVisible( false ) ;
        
        super.setLayout( new BorderLayout() ) ;
        add( tree, BorderLayout.CENTER ) ;
    }
    
    public void addUniverse( Universe universe ) {
        treeModel.addUniverse( universe ) ;
    }
}
