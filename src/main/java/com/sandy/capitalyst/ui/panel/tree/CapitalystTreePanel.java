package com.sandy.capitalyst.ui.panel.tree;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import java.util.Enumeration ;

import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTree ;
import javax.swing.SwingUtilities ;
import javax.swing.TransferHandler ;
import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.TreePath ;

import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.ui.helper.AccountWrapper ;

@SuppressWarnings( "serial" )
public class CapitalystTreePanel extends JPanel 
    implements ActionListener {

    private CapitalystProjectTreeModel treeModel = null ;
    private JTree tree = null ;
    private TransferHandler transferHandler = null ;
    
    //private JPopupMenu popupMenu = null ;
    
    public CapitalystTreePanel( TransferHandler th ) {
        this.transferHandler = th ;
        setUpUI() ;
        setUpListeners() ;
    }
    
    private void setUpUI() {
        
        treeModel = new CapitalystProjectTreeModel() ;
        
        tree = new JTree( treeModel ) ;
        tree.setRootVisible( false ) ;
        tree.setFont( new Font( "Helvetica", Font.PLAIN, 11 ) ) ;
        tree.setDragEnabled( true ) ;
        tree.setTransferHandler( transferHandler ) ;
        
        super.setLayout( new BorderLayout() ) ;
        
        JScrollPane sp = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                          JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
        sp.setViewportView( tree ) ;
        add( sp, BorderLayout.CENTER ) ;
        
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
                    
                    if( lastComp.getUserObject() instanceof AccountWrapper ) {
                        //popupMenu.show( tree, e.getX(), e.getY() ) ;
                    }
                }
            }
        } ) ;
    }
    
    public void addUniverse( Universe universe ) {
        DefaultMutableTreeNode universeNode = treeModel.addUniverse( universe ) ;
        expandNode( universeNode ) ;
    }
    
    @SuppressWarnings( "unchecked" )
    private void expandNode( DefaultMutableTreeNode node ) {
        
        if( node.getChildCount() > 0 ) {
            
            tree.expandPath( new TreePath( node.getPath() ) ) ;
            Enumeration<DefaultMutableTreeNode> children = node.children() ;
            while( children.hasMoreElements() ) {
                expandNode( children.nextElement() ) ;
            }
        }
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
    }
}
