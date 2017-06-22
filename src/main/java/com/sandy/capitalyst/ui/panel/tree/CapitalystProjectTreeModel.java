package com.sandy.capitalyst.ui.panel.tree;

import java.util.List ;

import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.DefaultTreeModel ;

import org.jfree.util.Log ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.account.AggregateAccount ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.txgen.TxnGenerator ;
import com.sandy.capitalyst.ui.helper.AccountWrapper ;

@SuppressWarnings( "serial" )
public class CapitalystProjectTreeModel extends DefaultTreeModel {

    public static final String ACCOUNT_NODE_NAME = "Accounts" ;
    public static final String TXGENS_NODE_NAME  = "Txn Generators" ;
    public static final String DESER_NODE_NAME   = "Loaded Accounts" ;
    
    private DefaultMutableTreeNode rootNode = null ;
    private DefaultMutableTreeNode deserLedgerNode = null ;
    
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
        node.setUserObject( u ) ;
        node.add( createAccountsNode( u ) );
        node.add( createTxGensNode( u ) );
        node.add( createDeserializedAccountsNode( u ) ) ;
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
        
        DefaultMutableTreeNode holdingNode = findHoldingNode( root, classifiers, a ) ;
        DefaultMutableTreeNode acNode = new DefaultMutableTreeNode( a.getName() ) ;
        acNode.setUserObject( new AccountWrapper( a ) ) ;
        holdingNode.add( acNode ) ;
        
        addAccountToParentNodes( holdingNode, a ) ;
    }
    
    private DefaultMutableTreeNode createTxGensNode( Universe u ) {
        
        DefaultMutableTreeNode node = null ;
        node = new DefaultMutableTreeNode( TXGENS_NODE_NAME ) ;
        for( TxnGenerator txGen : u.getAllTxGens() ) {
            if( !(txGen instanceof Account) ) {
                addTxGenNode( node, txGen ) ;
            }
        }
        return node ;
    }
    
    private void addTxGenNode( DefaultMutableTreeNode root, TxnGenerator g ) {
        
        String[] classifiers = null ;
        if( g.getClassifiers() != null ) {
            classifiers = g.getClassifiers().split( ">" ) ;
        }
        
        DefaultMutableTreeNode holdingNode = findHoldingNode( root, classifiers, null ) ;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( g.getName() ) ;
        node.setUserObject( g ) ;
        holdingNode.add( node ) ;
    }
    
    private DefaultMutableTreeNode findHoldingNode( DefaultMutableTreeNode root,
                                                    String[] classifiers,
                                                    Account account ) {
        
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
                
                if( account != null ) {
                    AggregateAccount aggAc = new AggregateAccount() ;
                    aggAc.setId( classifier ) ;
                    aggAc.setName( classifier ) ;
                    aggAc.setUniverse( account.getUniverse() );
                    newNode.setUserObject( new AccountWrapper( aggAc ) ) ;
                    
                    addAccountToParentNodes( node, account ) ;
                }
                
                node = newNode ;
            }
        }
        
        return node ;
    }

    private void addAccountToParentNodes( DefaultMutableTreeNode root, Account a ) {
        
        if( root != null && root.getLevel() >= 1 ) {
            AccountWrapper acWrapper = ( AccountWrapper )root.getUserObject() ;
            AggregateAccount aggAc = ( AggregateAccount )acWrapper.getAccount() ;
            aggAc.addAccount( a ) ;
            Log.debug( "Added account tp agg " + acWrapper.toString() );
            addAccountToParentNodes( (DefaultMutableTreeNode)root.getParent(), a ) ;
        }
    }
    
    private DefaultMutableTreeNode createDeserializedAccountsNode( Universe u ) {
        
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( DESER_NODE_NAME ) ;
        node.setUserObject( DESER_NODE_NAME ) ;
        deserLedgerNode = node ;
        return node ;
    }

    public void addDeserializedAccount( String name, List<Txn> txnList ) {
        
        if( name.endsWith( ".ledger" ) ) {
            name = name.substring( name.lastIndexOf( "." ) + 1 ) ;
        }
        
        Account account = new Account() ;
        account.setId( name ) ;
        account.setName( name ) ;
        account.getLedger().addAll( txnList ) ;
        // TODO: SEt the universe.. this will cause a redesign as I am not 
        //       storing the universe name in the serialized ledger. Think!
        
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( account.getName() ) ;
        node.setUserObject( new AccountWrapper( account ) ) ;
        
        deserLedgerNode.add( node ) ;
    }
}
