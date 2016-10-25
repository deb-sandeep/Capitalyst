package com.sandy.capitalyst.ui.panel.tree;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;

import javax.swing.JPanel ;
import javax.swing.JTree ;
import javax.swing.SwingUtilities ;
import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.TreePath ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Universe ;

@SuppressWarnings( "serial" )
public class CapitalystTreePanel extends JPanel 
    implements ActionListener {

    private CapitalystProjectTreeModel treeModel = null ;
    private JTree tree = null ;
    //private JPopupMenu popupMenu = null ;
    
    public CapitalystTreePanel() {
        setUpUI() ;
        setUpListeners() ;
    }
    
    private void setUpUI() {
        
        treeModel = new CapitalystProjectTreeModel() ;
        
        tree = new JTree( treeModel ) ;
        tree.setRootVisible( false ) ;
        tree.setFont( new Font( "Helvetica", Font.PLAIN, 11 ) ) ;
        tree.setDragEnabled( true ) ;
        
        super.setLayout( new BorderLayout() ) ;
        add( tree, BorderLayout.CENTER ) ;
        
        setUpPopupMenu() ;
    }
    
    private void setUpPopupMenu() {
        //popupMenu = new JPopupMenu() ;
    }
    
    private void setUpListeners() {
        
        tree.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                
                int x = e.getX(), y = e.getY() ;
                TreePath path = null ;
                DefaultMutableTreeNode lastComp = null ;
                
                if( SwingUtilities.isRightMouseButton( e ) ) {
                    
                    path = tree.getPathForLocation( x, y ) ;
                    lastComp = ( DefaultMutableTreeNode ) path.getLastPathComponent() ;
                    
                    if( lastComp.getUserObject() instanceof Account ) {
                        //popupMenu.show( tree, e.getX(), e.getY() ) ;
                    }
                }
            }
        } ) ;
    }
    
    public void addUniverse( Universe universe ) {
        treeModel.addUniverse( universe ) ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
    }
}
