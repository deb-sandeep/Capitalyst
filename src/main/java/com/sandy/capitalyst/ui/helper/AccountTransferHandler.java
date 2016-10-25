package com.sandy.capitalyst.ui.helper;

import java.awt.datatransfer.Transferable ;

import javax.swing.JComponent ;
import javax.swing.JTree ;
import javax.swing.TransferHandler ;
import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.TreePath ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.ui.panel.chart.CapitalystChart ;

@SuppressWarnings( "serial" )
public class AccountTransferHandler extends TransferHandler {

    static Logger log = Logger.getLogger( AccountTransferHandler.class ) ;
    
    @Override
    public boolean canImport( TransferSupport support ) {
        return support.getComponent() instanceof CapitalystChart ;
    }
    
    @Override
    public int getSourceActions( JComponent c ) { return COPY ; }

    @Override
    protected Transferable createTransferable( JComponent c ) {

        JTree                  tree       = null ;
        TreePath               selPath    = null ;
        DefaultMutableTreeNode lastNode   = null ;
        Object                 userObject = null ;
        
        tree = ( JTree )c ;
        selPath = tree.getSelectionPath() ;
        lastNode = ( DefaultMutableTreeNode )selPath.getLastPathComponent() ;
        userObject = lastNode.getUserObject() ;
        
        if( userObject instanceof AccountWrapper ) {
            return new AccountTransferable( (AccountWrapper)userObject ) ;
        }
        return null ;
    }

    @Override
    public boolean importData( TransferSupport support ) {
        
        CapitalystChart chart          = ( CapitalystChart )support.getComponent() ;
        Transferable    transferable   = support.getTransferable() ;
        AccountWrapper  accountWrapper = null ;
        
        try {
            accountWrapper = ( AccountWrapper )transferable.getTransferData( 
                                             AccountTransferable.DATA_FLAVOR ) ;
            chart.addSeries( accountWrapper.getTimeSeries() ) ;
        }
        catch( Exception e ) {
            log.error( "Error extracting account from drop.", e ) ;
            return false ;
        }
        return true ;
    }
}
