package com.sandy.capitalyst.ui.panel.ledger ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.io.File ;
import java.io.IOException ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import javax.swing.ImageIcon ;
import javax.swing.JButton ;
import javax.swing.JComboBox ;
import javax.swing.JComponent ;
import javax.swing.JFileChooser ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTable ;
import javax.swing.JTextArea ;
import javax.swing.ListSelectionModel ;
import javax.swing.RowFilter ;
import javax.swing.event.ListSelectionEvent ;
import javax.swing.event.ListSelectionListener ;
import javax.swing.table.TableColumn ;
import javax.swing.table.TableColumnModel ;
import javax.swing.table.TableRowSorter ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.core.Txn.TxnType ;
import com.sandy.capitalyst.ui.helper.UIConstants ;
import com.sandy.capitalyst.ui.panel.ledger.LedgerTableModel.LedgerEntry ;
import com.sandy.capitalyst.util.LedgerUtils ;
import com.sandy.common.util.StringUtil ;

@SuppressWarnings( "serial" )
public class LedgerDisplayPanel extends JPanel 
    implements ActionListener, ListSelectionListener {

    static final Logger log = Logger.getLogger( LedgerDisplayPanel.class ) ;

    private JComboBox<String> searchTF   = new JComboBox<String>() ;
    private LedgerTableModel  tableModel = new LedgerTableModel() ;
    private JTable            table      = new JTable( tableModel ) ;
    private JButton           download   = new JButton() ;
    private JFileChooser      fileChooser= new JFileChooser() ;
    private JTextArea         descrTA    = new JTextArea( 1, 20 ) ;
    
    private TableRowSorter<LedgerTableModel> sorter =
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
        this.table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION ) ;
        this.table.getSelectionModel().addListSelectionListener( this ) ;
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
        
        this.descrTA.setEditable( false ) ;
        this.descrTA.setWrapStyleWord( true ) ;
        this.descrTA.setFont( UIConstants.STD_FONT );

        setLayout( new BorderLayout() ) ;
        add( tableSP, BorderLayout.CENTER ) ;
        add( getTopPanel(), BorderLayout.NORTH ) ;
        add( getBottomPanel(), BorderLayout.SOUTH ) ;
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
    
    private JPanel getTopPanel() {
        
        download.setIcon( new ImageIcon( this.getClass().getResource( "/img/credit_icon.png" ) ) );
        download.addActionListener( this ) ;
        
        JPanel panel = new JPanel() ;
        panel.setLayout( new BorderLayout() ) ;
        panel.add( searchTF, BorderLayout.CENTER ) ;
        panel.add( download, BorderLayout.EAST ) ;
        
        return panel ;
    }
    
    private JComponent getBottomPanel() {
        
        JScrollPane sp = new JScrollPane( this.descrTA, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER  ) ; 
        return sp ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        Object src = e.getSource() ;
        if( src == this.searchTF ) {
            applySearchFilter() ;
        }
        else if( src == download ) {
            initiateLedgerDownload() ;
        }
    }
    
    private void applySearchFilter() {
        
        final String queryStr = this.searchTF.getSelectedItem().toString() ;
        if( StringUtil.isNotEmptyOrNull( queryStr ) ) {

            final LedgerQueryParser parser = new LedgerQueryParser( queryStr ) ;
            try {
                final RowFilter<Object, Object> filter = parser.parse() ;
                this.table.getSelectionModel().removeListSelectionListener( this ) ;
                
                this.sorter.setRowFilter( filter ) ;
                this.searchTF.setBackground( Color.white ) ;
                this.searchTF.setToolTipText( "Enter filter query and enter" ) ;
                this.searchTF.insertItemAt( queryStr, 0 ) ;
                
                this.table.getSelectionModel().addListSelectionListener( this ) ;
            }
            catch ( final Exception e2 ) {
                log.error( "parser error", e2 );
                JOptionPane.showMessageDialog( this, "Error in search query" );
            }
        }
        else {
            this.sorter.setRowFilter( null ) ;
            this.searchTF.setBackground( Color.white ) ;
            this.searchTF.setToolTipText( "Enter filter query and enter" ) ;
        }
    }
    
    private void initiateLedgerDownload() {
        
        int choice = fileChooser.showSaveDialog( this ) ;
        if( choice != JFileChooser.APPROVE_OPTION ) return ;
        
        File file = fileChooser.getSelectedFile() ;    
        List<LedgerEntry> entries = new ArrayList<LedgerTableModel.LedgerEntry>() ;
        int numRows = table.getRowCount() ;
        for( int viewRowIndex=0; viewRowIndex<numRows; viewRowIndex++ ) {
            int modelRowIndex = table.convertRowIndexToModel( viewRowIndex ) ;
            entries.add( tableModel.getEntry( modelRowIndex ) ) ;
        }
        
        String string = LedgerUtils.getFormattedLedger( tableModel.getAccount(), entries ) ;
        try {
            FileUtils.writeStringToFile( file, string ) ;
            JOptionPane.showMessageDialog( this, "File successfully written." );
        }
        catch( IOException e ) {
            log.error( "File saving error", e ) ;
            JOptionPane.showMessageDialog( 
                                this, "Error saving file. " + e.getMessage() ) ;
        }
    }

    @Override
    public void valueChanged( ListSelectionEvent e ) {
        
        if( !e.getValueIsAdjusting() ) {
            int viewRow = this.table.getSelectedRow() ;
            int modelRow = this.table.convertRowIndexToModel( viewRow ) ;
            
            LedgerEntry entry = this.tableModel.getEntry( modelRow ) ;
            
            String newText = entry.getDescription() ;
            newText = ( newText == null ) ? "" : newText ;
            
            this.descrTA.setText( newText ) ;
        }
    }
}
