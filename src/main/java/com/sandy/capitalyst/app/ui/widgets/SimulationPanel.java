package com.sandy.capitalyst.app.ui.widgets;

import java.awt.BorderLayout ;
import java.util.Stack ;

import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTree ;
import javax.swing.SwingUtilities ;
import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.DefaultTreeModel ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.core.AccountingBook.AccountingBookListenerAdapter ;
import com.sandy.capitalyst.domain.core.AccountingItem ;
import com.sandy.capitalyst.domain.core.AccountingItemGroup ;
import com.sandy.capitalyst.domain.util.IncomeItem ;

@SuppressWarnings( "serial" )
public class SimulationPanel extends JPanel {

    private static final Logger logger = Logger.getLogger( SimulationPanel.class ) ;
    
    private static int currentUnnamedPanelId = 1 ;
    
    private String panelName = null ;
    
    private AccountingBook book = null ;
    private Account account = new Account( "A", book ) ;

    private DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Root" ) ;
    private JTree tree = new JTree( root ) ;
    
    public SimulationPanel() {
        panelName = "[Accounting Book " + currentUnnamedPanelId++ + "]" ;
        setUpUI() ;
    }
    
    public String getName() {
        return this.panelName ;
    }
    
    public void initialize() {
        book = new AccountingBook( getName() ) ;
        book.addAccountingBookListener( new AccountingBookListenerAdapter() {
            
            public void accountingItemGroupAdded( AccountingItemGroup itemGroup ) {
                addAccountingItem( itemGroup ) ;
            }
            
            public void accountingItemAdded( AccountingItem item ) {
                addAccountingItem( item ) ;
            }
        } ) ;
        
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                buildBook() ;
            }
        } ) ;
    }
    
    private void buildBook() {
        book.addAccountingItem( new IncomeItem( "Salary > Income A", 100, account ) ) ;
    }
    
    private void addAccountingItem( AccountingItem item ) {
        Stack<AccountingItem> nodes = new Stack<AccountingItem>() ;
        nodes.push( item ) ;
        AccountingItem parent = item.getParent() ;
        while( parent != null ) {
            nodes.push( parent ) ;
            parent = parent.getParent() ;
        }
        
        DefaultMutableTreeNode n = root ;
        while( !nodes.isEmpty() ) {
            AccountingItem ai = nodes.pop() ;
            n = addNode( n, ai ) ;
        }
        
        ((DefaultTreeModel)tree.getModel()).reload( root ) ;
    }
    
    private DefaultMutableTreeNode addNode( DefaultMutableTreeNode r, AccountingItem a ) {
        int numChild = r.getChildCount() ;
        DefaultMutableTreeNode node = null ;
        for( int i=0; i<numChild; i++ ) {
            DefaultMutableTreeNode child = ( DefaultMutableTreeNode )r.getChildAt( i ) ;
            if( child.getUserObject() == a ) {
                node = child ;
                break ;
            }
        }
        
        if( node == null ) {
            node = new DefaultMutableTreeNode( a ) ;
            r.add( node ) ;
        }
        return node ;
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        tree.setRootVisible( false ) ;
        add( new JScrollPane( tree ) ) ;
    }
}
