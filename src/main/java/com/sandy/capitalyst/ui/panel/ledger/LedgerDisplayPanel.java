package com.sandy.capitalyst.ui.panel.ledger ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.util.Date ;

import javax.swing.JComboBox ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTable ;
import javax.swing.ListSelectionModel ;
import javax.swing.RowFilter ;
import javax.swing.table.TableColumn ;
import javax.swing.table.TableColumnModel ;
import javax.swing.table.TableRowSorter ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Txn.TxnType ;
import com.sandy.capitalyst.ui.helper.UIConstants ;
import com.sandy.common.util.StringUtil ;

@SuppressWarnings( "serial" )
public class LedgerDisplayPanel extends JPanel implements ActionListener {

    static final Logger logger = Logger.getLogger( LedgerDisplayPanel.class ) ;

    private final JComboBox<String> searchTF   = new JComboBox<String>() ;
    private final LedgerTableModel  tableModel = new LedgerTableModel() ;
    private final JTable            table      = new JTable( tableModel ) ;
    
    private final TableRowSorter<LedgerTableModel> sorter =
                   new TableRowSorter<LedgerTableModel>( this.tableModel ) ;
    
    public LedgerDisplayPanel( Account account ) {
        setUpUI() ;
        tableModel.setDataSource( account ) ;
    }

    public void setUpUI() {

        final JScrollPane tableSP = new JScrollPane( this.table ) ;
        tableSP.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ) ;

        // Add the table model, table column model etc for the table.
        this.table.setModel( this.tableModel ) ;
        this.table.setDefaultRenderer( TxnType.class, new LedgerTableCellRenderer() ) ;
        this.table.setDefaultRenderer( String.class,  new LedgerTableCellRenderer() ) ;
        this.table.setDefaultRenderer( Double.class,  new LedgerTableCellRenderer() ) ;
        this.table.setDefaultRenderer( Date.class,    new LedgerTableCellRenderer() ) ;
        this.table.setAutoCreateRowSorter( true ) ;
        
        this.table.setFont( UIConstants.TABLE_FONT ) ;
        this.table.getTableHeader().setFont( UIConstants.TABLE_FONT ) ;
        this.table.setRowHeight( 15 ) ;
        this.table.setDoubleBuffered( true ) ;
        this.table.setRowSorter( this.sorter ) ;
        this.table.setRowSelectionAllowed( true ) ;
        this.table.setColumnSelectionAllowed( false ) ;
        this.table.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ) ;
        this.sorter.setRowFilter( null ) ;

        setColumnProperties( LedgerTableModel.COL_TXN_TYPE_MARKER,    10 ) ;
        setColumnProperties( LedgerTableModel.COL_DATE,               70 ) ;
        setColumnProperties( LedgerTableModel.COL_TX_AMT,             70 ) ;
        setColumnProperties( LedgerTableModel.COL_AC_BALANCE_AMT,     90 ) ;
        setColumnProperties( LedgerTableModel.COL_DESCRIPTION,        50 ) ;

        // Set up the search entry text box and add it to the panel
        this.searchTF.setFont( UIConstants.TABLE_FONT ) ;
        this.searchTF.addActionListener( this ) ;
        this.searchTF.setEditable( true ) ;

        setLayout( new BorderLayout() ) ;
        add( tableSP, BorderLayout.CENTER ) ;
        add( searchTF, BorderLayout.NORTH ) ;
    }

    /**
     * A tiny helper method to set the properties of the columns in the ITD
     * table.
     *
     * @param colId The identifier of the column
     * @param width The preferred width
     */
    private void setColumnProperties( final int colId, final int width ) {
        final TableColumnModel colModel = this.table.getColumnModel() ;
        final TableColumn col = colModel.getColumn( colId ) ;
        col.setPreferredWidth( width ) ;
        col.setMinWidth( width ) ;
        col.setResizable( true ) ;
    }

    /**
     * Returns the preferred width of this panel.
     */
    public int getPreferredWidth() {
        int preferredWidth = 0 ;
        final int dispColId[] = {
                LedgerTableModel.COL_TXN_TYPE_MARKER,
                LedgerTableModel.COL_DATE,
                LedgerTableModel.COL_TX_AMT,
                LedgerTableModel.COL_AC_BALANCE_AMT,
                LedgerTableModel.COL_DESCRIPTION
        } ;

        final TableColumnModel colModel = this.table.getColumnModel() ;
        for( int colId=0; colId<dispColId.length; colId++ ) {
            final TableColumn col = colModel.getColumn( colId ) ;
            preferredWidth += col.getPreferredWidth() ;
        }

        // Account for the vertical scrollbar
        preferredWidth += 20 ;
        return preferredWidth ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {

        final String queryStr = this.searchTF.getSelectedItem().toString() ;
        if( StringUtil.isNotEmptyOrNull( queryStr ) ) {

            final LedgerQueryParser parser = new LedgerQueryParser( queryStr ) ;
            try {
                final RowFilter<Object, Object> filter = parser.parse() ;
                this.sorter.setRowFilter( filter ) ;
                this.searchTF.setBackground( Color.white ) ;
                this.searchTF.setToolTipText( "Enter filter query and enter" ) ;
                this.searchTF.insertItemAt( queryStr, 0 ) ;
            }
            catch ( final Exception e2 ) {
                this.searchTF.setBackground( Color.pink ) ;
                this.searchTF.setToolTipText( "ERROR: " + e2.getMessage() ) ;
            }
        }
        else {
            this.sorter.setRowFilter( null ) ;
            this.searchTF.setBackground( Color.white ) ;
            this.searchTF.setToolTipText( "Enter filter query and enter" ) ;
        }
    }
}
