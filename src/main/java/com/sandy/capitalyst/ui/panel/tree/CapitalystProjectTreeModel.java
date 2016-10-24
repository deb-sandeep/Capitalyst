package com.sandy.capitalyst.ui.panel.tree;

import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.DefaultTreeModel ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Universe ;

@SuppressWarnings( "serial" )
public class CapitalystProjectTreeModel extends DefaultTreeModel {

    private static final String ACCOUNT_NODE_NAME = "Accounts" ;
    
    private DefaultMutableTreeNode rootNode = null ;
    
    public CapitalystProjectTreeModel() {
        super( new DefaultMutableTreeNode() ) ;
        this.rootNode = ( DefaultMutableTreeNode )super.getRoot() ;
    }
    
    public void addUniverse( Universe universe ) {
        this.rootNode.add( createUniverseNode( universe ) ) ;
        super.reload() ;
    }
    
    private DefaultMutableTreeNode createUniverseNode( Universe u ) {
        
        DefaultMutableTreeNode node = null ;
        node = new DefaultMutableTreeNode( u.getName() ) ;
        node.add( createAccountsNode( u ) );
        return node ;
    }
    
    private DefaultMutableTreeNode createAccountsNode( Universe u ) {
        
        DefaultMutableTreeNode node = null ;
        node = new DefaultMutableTreeNode( ACCOUNT_NODE_NAME ) ;
        for( Account a : u.getAllAccounts() ) {
            node.add( getAccountNode( a ) );
        }
        return node ;
    }
    
    private DefaultMutableTreeNode getAccountNode( Account a ) {
        
        DefaultMutableTreeNode node = null ;
        node = new DefaultMutableTreeNode( a.getName() ) ;
        node.setUserObject( a ) ;
        return node ;
    }
}
