package com.sandy.capitalyst.ui.panel.tree;

import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.DefaultTreeModel ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.ui.helper.AccountWrapper ;

@SuppressWarnings( "serial" )
public class CapitalystProjectTreeModel extends DefaultTreeModel {

    private static final String ACCOUNT_NODE_NAME = "Accounts" ;
    
    private DefaultMutableTreeNode rootNode = null ;
    
    public CapitalystProjectTreeModel() {
        
        super( new DefaultMutableTreeNode() ) ;
        this.rootNode = ( DefaultMutableTreeNode )super.getRoot() ;
    }
    
    public DefaultMutableTreeNode addUniverse( Universe universe ) {
        
        DefaultMutableTreeNode universeNode = createUniverseNode( universe ) ;
        this.rootNode.add( universeNode ) ;
        super.reload() ;
        
        return universeNode ;
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
            addAccountNode( node, a ) ;
        }
        return node ;
    }
    
    private void addAccountNode( DefaultMutableTreeNode root, Account a ) {
        
        String[] classifiers = null ;
        if( a.getClassifiers() != null ) {
            classifiers = a.getClassifiers().split( ">" ) ;
        }
        
        DefaultMutableTreeNode holdingNode = findHoldingNode( root, classifiers ) ;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( a.getName() ) ;
        node.setUserObject( new AccountWrapper( a ) ) ;
        holdingNode.add( node ) ;
    }
    
    private DefaultMutableTreeNode findHoldingNode( DefaultMutableTreeNode root,
                                                    String[] classifiers ) {
        
        if( classifiers == null ) {
            return root ;
        }
        
        DefaultMutableTreeNode node, matchedNode, child, newNode ;

        node = root ;
        for( String classifier : classifiers ) {
            classifier = classifier.trim() ;
            matchedNode = null ;
            for( int i=0; i<node.getChildCount(); i++ ) {
                child = ( DefaultMutableTreeNode )node.getChildAt( i ) ;
                if( child.toString().equals( classifier ) ) {
                    matchedNode = child ;
                    break ;
                }
            }
            
            if( matchedNode != null ) {
                node = matchedNode ;
            }
            else {
                newNode = new DefaultMutableTreeNode( classifier ) ;
                node.add( newNode ) ;
                node = newNode ;
            }
        }
        
        return node ;
    }
}
