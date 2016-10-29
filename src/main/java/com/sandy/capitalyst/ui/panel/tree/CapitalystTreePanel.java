package com.sandy.capitalyst.ui.panel.tree;

import java.awt.BorderLayout ;
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
import javax.swing.event.TreeSelectionEvent ;
import javax.swing.event.TreeSelectionListener ;
import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.TreePath ;
import javax.swing.tree.TreeSelectionModel ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.UniverseConstituent ;
import com.sandy.capitalyst.core.UniverseLoader ;
import com.sandy.capitalyst.txgen.TxnGenerator ;
import com.sandy.capitalyst.ui.helper.AccountWrapper ;
import com.sandy.capitalyst.ui.helper.UIConstants ;
import com.sandy.capitalyst.ui.panel.chart.CapitalystChartPanel ;
import com.sandy.capitalyst.ui.panel.ledger.LedgerTabbedPane ;
import com.sandy.capitalyst.ui.panel.property.EntityPropertyEditPanel ;

@SuppressWarnings( "serial" )
public class CapitalystTreePanel extends JPanel 
    implements ActionListener, TreeSelectionListener {

    static final Logger log = Logger.getLogger( CapitalystTreePanel.class ) ;
    
    private CapitalystChartPanel    chartPanel    = null ;
    private EntityPropertyEditPanel propPanel     = null ;
    private LedgerTabbedPane        ledgerTabPane = null ;
    
    private CapitalystProjectTreeModel treeModel       = null ;
    private JTree                      tree            = null ;
    private TransferHandler            transferHandler = null ;
    
    private JPopupMenu popupMenu         = null ;
    private JMenuItem  runSimulationMI   = null ;
    private JMenuItem  cloneUniverseMI   = null ;
    private JMenuItem  removeUniverseMI  = null ;
    private JMenuItem  resetSimulationMI = null ;
    
    public CapitalystTreePanel( TransferHandler th, 
                                CapitalystChartPanel chartPanel,
                                EntityPropertyEditPanel propPanel,
                                LedgerTabbedPane ledgerPanel ) {
        
        this.chartPanel = chartPanel ;
        this.propPanel = propPanel ;
        this.ledgerTabPane = ledgerPanel ;
        this.transferHandler = th ;
        
        setUpUI() ;
        setUpListeners() ;
    }
    
    private void setUpUI() {
        
        treeModel = new CapitalystProjectTreeModel() ;
        
        tree = new JTree( treeModel ) ;
        tree.setRootVisible( false ) ;
        tree.setFont( UIConstants.TREE_FONT ) ;
        tree.setDragEnabled( true ) ;
        tree.setTransferHandler( transferHandler ) ;
        tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION ) ;
        tree.addTreeSelectionListener( this ) ;
        
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
        
        resetSimulationMI = new JMenuItem( "Reset Simulation" ) ;
        resetSimulationMI.addActionListener( this ) ;
        
        cloneUniverseMI = new JMenuItem( "Clone Universe" ) ;
        cloneUniverseMI.addActionListener( this ) ;
        
        removeUniverseMI = new JMenuItem( "Remove Universe" ) ;
        removeUniverseMI.addActionListener( this ) ;
        
        
        popupMenu = new JPopupMenu() ;
        popupMenu.add( runSimulationMI ) ;
        popupMenu.add( resetSimulationMI ) ;
        popupMenu.addSeparator() ;
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
    
    public DefaultMutableTreeNode addUniverse( Universe universe ) {
        DefaultMutableTreeNode universeNode = treeModel.addUniverse( universe ) ;
        expandNode( universeNode ) ;
        return universeNode ;
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
        else if( mi == resetSimulationMI ) {
            resetSimulationOfSelectedUniverse() ;
        }
        else if( mi == cloneUniverseMI ) {
            final Universe u = getSelectedUniverse() ;
            Universe newUniv = cloneUniverse( u, true ) ;
            addUniverse( newUniv ) ;
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
                    try {
                        u.runSimulation() ;
                    }
                    catch( Exception e ) {
                        JOptionPane.showMessageDialog( null, e.getMessage() ) ;
                    }
                }
            } ;
            t.start() ;
        }
    }
    
    @SuppressWarnings( "unchecked" )
    private void resetSimulationOfSelectedUniverse() {
        
        // Get references to old and new universes
        Universe oldUniverse = getSelectedUniverse() ;
        Universe newUniverse = cloneUniverse( oldUniverse, false ) ;
        
        // Save the tree node of the old universe
        TreePath selPath = tree.getSelectionPath() ;
        DefaultMutableTreeNode oldUnivNode = null ;
        oldUnivNode = ( DefaultMutableTreeNode ) selPath.getLastPathComponent() ;
        
        // Add the new universe
        DefaultMutableTreeNode newUnivNode = addUniverse( newUniverse ) ;
        
        // Iterate through the descendants of the new universe and update
        // any existing time series with that from the new universe.
        Enumeration<DefaultMutableTreeNode> descendents = null ;
        descendents = newUnivNode.depthFirstEnumeration() ;
        
        while( descendents.hasMoreElements() ) {
            DefaultMutableTreeNode c = descendents.nextElement() ;
            Object userObj = c.getUserObject() ;
            if( userObj instanceof AccountWrapper ) {
                AccountWrapper wrapper = ( AccountWrapper )userObj ;
                chartPanel.updateTimeSeries( wrapper ) ;
            }
        }

        // Now remove the old universe node from the tree and instruct the 
        // chart panel to remove any associated time series from the old
        // universe.
        treeModel.removeNodeFromParent( oldUnivNode ) ;
        chartPanel.removeUniverse( oldUniverse ) ;
    }
    
    private Universe cloneUniverse( Universe u, boolean seekNewName ) {
        
        Universe newUniverse = null ;
        
        if( u != null ) {
            UniverseConfig config  = u.getConfiguration().clone() ;
            UniverseLoader loader  = new UniverseLoader( config ) ;
            String         newName = u.getName() ;
            
            if( seekNewName )  {
                newName = JOptionPane.showInputDialog( "Name of the cloned universe?",
                                                       u.getName() + "(clone)" ) ;
            }
            
            if( newName != null ) {
                try {
                    newUniverse = loader.loadUniverse() ;
                    newUniverse.setName( newName );
                }
                catch( Exception e ) {
                    log.error( "Could not create new universe", e ) ;
                }
            }
        }
        return newUniverse ;
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
    
    private Universe removeSelectedUniverse() {
        
        TreePath selPath = tree.getSelectionPath() ;
        Universe u       = getSelectedUniverse() ;
        
        if( u != null ) {
            DefaultMutableTreeNode lastNode = null ;
            
            lastNode = ( DefaultMutableTreeNode ) selPath.getLastPathComponent() ;
            treeModel.removeNodeFromParent( lastNode ) ;
            chartPanel.removeUniverse( u ) ;
        }
        return u ;
    }

    @Override
    public void valueChanged( TreeSelectionEvent e ) {
        
        Object newEntity = null ;
        DefaultMutableTreeNode lastNode = null ; 
        Object userObj = null ;
        
        TreePath selPath = e.getNewLeadSelectionPath() ;
        if( selPath != null ) {
            lastNode = ( DefaultMutableTreeNode )selPath.getLastPathComponent() ;
            userObj  = lastNode.getUserObject() ;
            
            if( userObj instanceof AccountWrapper ) {
                newEntity = (( AccountWrapper )userObj).getAccount() ;
            }
            else if( userObj instanceof TxnGenerator ) {
                newEntity = userObj ;
            }
        }
        propPanel.refreshEntity( (UniverseConstituent)newEntity ) ;
        
        if( userObj instanceof AccountWrapper ) {
            ledgerTabPane.showAccountLedger( (Account)newEntity ) ;
        }
    }
}
