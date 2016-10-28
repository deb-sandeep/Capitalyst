package com.sandy.capitalyst.ui.panel.ledger;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import javax.swing.table.AbstractTableModel ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.account.AccountListener ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Txn.TxnType ;

@SuppressWarnings( "serial" )
public class LedgerTableModel extends AbstractTableModel 
    implements AccountListener {

    static final Logger logger = Logger.getLogger( LedgerTableModel.class ) ;

    public static class LedgerEntry {
        
        private Txn txn = null ;
        private double acBalance = 0 ;
        
        public LedgerEntry( Txn t, double balance ) {
            this.txn = t ;
            this.acBalance = balance ;
        }
        
        public TxnType getTxnType() {
            return txn.getTxnType() ;
        }
        
        public Date getDate() {
            return txn.getDate() ;
        }
        
        public double getAmt() {
            return txn.getAmount() ;
        }
        
        public double getAccountBalance() {
            return this.acBalance ;
        }
        
        public String getDescription() {
            return txn.getDescription() ;
        }
        
        public Txn getTxn() {
            return this.txn ;
        }
    } ;
    
    public static final Object[][] COL_PROPERTIES = {
        { "",            TxnType.class },
        { "Date",        Date.class    },
        { "Amount",      Double.class  },
        { "Balance",     Double.class  },
        { "Description", String.class  }
    } ;

    public static final int COL_TXN_TYPE_MARKER = 0 ;
    public static final int COL_DATE            = 1 ;
    public static final int COL_TX_AMT          = 2 ;
    public static final int COL_AC_BALANCE_AMT  = 3 ;
    public static final int COL_DESCRIPTION     = 4 ;

    private Account account = null ;
    private List<LedgerEntry> ledgerEntries = 
                                 new ArrayList<LedgerTableModel.LedgerEntry>() ;

    @Override
    public int getColumnCount() { return COL_PROPERTIES.length ; }

    @Override 
    public int getRowCount() {
        return ledgerEntries.size() ;
    }

    @Override
    public String getColumnName( int column ) {
        return ( String )COL_PROPERTIES[column][0] ;
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex ) {
        return false ;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex ) {
        
        Object val = null ;
        LedgerEntry entry = ledgerEntries.get( rowIndex ) ;
        
        switch( columnIndex ) {
            case COL_TXN_TYPE_MARKER :
                val = entry.getTxnType() ;
                break ;

            case COL_DATE :
                val = entry.getDate() ;
                break ;

            case COL_TX_AMT :
                val = entry.getAmt() ;
                break ;

            case COL_AC_BALANCE_AMT :
                val = entry.getAccountBalance() ;
                break ;

            case COL_DESCRIPTION :
                val = entry.getDescription() ;
                break ;
        }
        return val ;
    }

    @Override
    public Class<?> getColumnClass( final int columnIndex ) {
        return ( Class<?> )COL_PROPERTIES[columnIndex][1] ;
    }
    
    public void setDataSource( Account account ) {
        if( this.account != null ) {
            this.account.removeListener( this ) ;
        }
        this.account = account ;
        this.account.addListener( this ) ;

        refreshModelData() ;
        super.fireTableDataChanged() ;
    }
    
    public Account getAccount() {
        return this.account ;
    }
    
    public LedgerEntry getEntry( int index ) {
        return this.ledgerEntries.get( index ) ;
    }
    
    private void refreshModelData() {
        
        ledgerEntries.clear() ;
        if( account == null ) return ;
        
        account.addListener( this ) ;
        double balance = account.getOpeningBalance() ;
        
        if( balance > 0 ) {
            Txn openingTxn = new Txn( account.getAccountNumber(), balance, null, 
                                      "Opening balance" ) ;
            ledgerEntries.add( new LedgerEntry( openingTxn, balance ) ) ;
        }
        
        for( Txn txn : account.getLedger() ) {
            balance += txn.getAmount() ;
            ledgerEntries.add( 0, new LedgerEntry( txn, balance ) ) ;
        }
    }
    
    @Override
    public void txnPosted( Txn txn, Account account ) {
        LedgerEntry lastEntry = null ;
        double balance = 0 ;
        
        if( !ledgerEntries.isEmpty() ) {
            lastEntry = ledgerEntries.get( 0 ) ;
            balance = lastEntry.getAccountBalance() ;
            balance += txn.getAmount() ;
        }
        else {
            balance = account.getOpeningBalance() ;
        }
        
        ledgerEntries.add( 0, new LedgerEntry( txn, balance ) ) ;
        super.fireTableRowsInserted( 0, 0 ) ;
    }
}
