package com.sandy.capitalyst.ui.panel.tree;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import java.util.Enumeration ;

import javax.swing.JMenuItem ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JPopupMenu ;
import javax.swing.JScrollPane ;
import javax.swing.JTree ;
import javax.swing.SwingUtilities ;
import javax.swing.TransferHandler ;
import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.TreePath ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.UniverseLoader ;
import com.sandy.capitalyst.ui.panel.CapitalystProjectPanel ;

@SuppressWarnings( "serial" )
public class CapitalystTreePanel extends JPanel 
    implements ActionListener {

    static final Logger log = Logger.getLogger( CapitalystTreePanel.class ) ;
    
    private CapitalystProjectPanel     parent = null ;
    private CapitalystProjectTreeModel treeModel = null ;
    private JTree                      tree = null ;
    private TransferHandler            transferHandler = null ;
    
    private JPopupMenu popupMenu = null ;
    private JMenuItem  runSimulationMI = null ;
    private JMenuItem  cloneUniverseMI = null ;
    private JMenuItem  removeUniverseMI= null ;
    
    public CapitalystTreePanel( TransferHandler th, CapitalystProjectPanel parent ) {
        this.parent = parent ;
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
        
        runSimulationMI = new JMenuItem( "Run Simulation" ) ;
        runSimulationMI.addActionListener( this ) ;
        
        cloneUniverseMI = new JMenuItem( "Clone Universe" ) ;
        cloneUniverseMI.addActionListener( this ) ;
        
        removeUniverseMI = new JMenuItem( "Remove Universe" ) ;
        removeUniverseMI.addActionListener( this ) ;
        
        popupMenu = new JPopupMenu() ;
        popupMenu.add( runSimulationMI ) ;
        popupMenu.add( cloneUniverseMI ) ;
        popupMenu.add( removeUniverseMI ) ;
    }
    
    private void setUpListeners() {
        
        tree.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                
                int x = e.getX(), y = e.getY() ;
                TreePath path = null ;
                DefaultMutableTreeNode lastComp = null ;
                Object userObj = null ;
                
                if( SwingUtilities.isRightMouseButton( e ) ) {
                    
                    path = tree.getPathForLocation( x, y ) ;
                    lastComp = ( DefaultMutableTreeNode ) path.getLastPathComponent() ;
                    userObj = lastComp.getUserObject() ; 
                    
                    tree.setSelectionPath( path ) ;
                    
                    if( userObj instanceof Universe ) {
                        if( ((Universe)userObj).isVirgin() ) {
                            runSimulationMI.setEnabled( true ) ;
                        }
                        else {
                            runSimulationMI.setEnabled( false ) ;
                        }
                        popupMenu.show( tree, e.getX(), e.getY() ) ;
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
        JMenuItem mi = ( JMenuItem )e.getSource() ;
        if( mi == runSimulationMI ) {
            simulateSelectedUniverse() ;
        }
        else if( mi == cloneUniverseMI ) {
            cloneSelectedUniverse( true ) ;
        }
        else if( mi == removeUniverseMI ) {
            removeSelectedUniverse() ;
        }
    }
    
    private void simulateSelectedUniverse() {
        
        final Universe u = getSelectedUniverse() ;
        if( u != null ) {
            Thread t = new Thread() {
                public void run() {
                    u.runSimulation() ;
                }
            } ;
            t.start() ;
        }
    }
    
    private void cloneSelectedUniverse( boolean seekNewName ) {
        
        final Universe u = getSelectedUniverse() ;
        if( u != null ) {
            UniverseConfig config      = u.getConfig().clone() ;
            UniverseLoader loader      = new UniverseLoader( config ) ;
            Universe       newUniverse = null ;
            String         newName     = u.getName() ;
            
            newName = JOptionPane.showInputDialog( "Name of the cloned universe?",
                                                   u.getName() + "(clone)" ) ;
            if( newName != null ) {
                try {
                    newUniverse = loader.loadUniverse() ;
                    newUniverse.setName( newName );
                    addUniverse( newUniverse ) ;
                }
                catch( Exception e ) {
                    log.error( "Could not create new universe", e ) ;
                }
            }
        }
    }
    
    private Universe getSelectedUniverse() {
        
        TreePath treePath = null ;
        DefaultMutableTreeNode treeNode = null ; 
        
        treePath = tree.getSelectionPath() ;
        if( treePath != null ) {
            treeNode = ( DefaultMutableTreeNode )treePath.getLastPathComponent() ;
            return ( Universe )treeNode.getUserObject() ;
        }
        return null ;
    }
    
    private void removeSelectedUniverse() {
        
        TreePath selPath = tree.getSelectionPath() ;
        Universe u       = getSelectedUniverse() ;
        
        if( u != null ) {
            DefaultMutableTreeNode lastNode = null ;
            
            lastNode = ( DefaultMutableTreeNode ) selPath.getLastPathComponent() ;
            treeModel.removeNodeFromParent( lastNode ) ;
            parent.removeUniverse( u ) ;
        }
    }
}
