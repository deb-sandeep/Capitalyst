package com.sandy.capitalyst.ui.panel.ledger;

import java.awt.Component ;
import java.text.SimpleDateFormat ;
import java.util.Date ;

import javax.swing.BorderFactory ;
import javax.swing.ImageIcon ;
import javax.swing.JLabel ;
import javax.swing.JTable ;
import javax.swing.table.DefaultTableCellRenderer ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.core.Txn.TxnType ;
import com.sandy.capitalyst.util.Utils ;

@SuppressWarnings( "serial" )
public class LedgerTableCellRenderer extends DefaultTableCellRenderer {

    static final Logger logger = Logger.getLogger( LedgerTableCellRenderer.class ) ;

    private static final SimpleDateFormat SDF = new SimpleDateFormat( "dd-MM-yyyy" ) ;
    
    private static final ImageIcon CREDIT_ICON  = new ImageIcon( 
        LedgerTableCellRenderer.class.getResource( "/img/credit_icon.png" ) ) ;
    private static final ImageIcon DEBIT_ICON   = new ImageIcon( 
         LedgerTableCellRenderer.class.getResource( "/img/debit_icon.png" ) ) ;

    @Override
    public Component getTableCellRendererComponent( JTable table, 
                                                    Object value,
                                                    boolean isSelected, 
                                                    boolean hasFocus, 
                                                    int row, 
                                                    int column ) {

        int modelCol = table.convertColumnIndexToModel( column ) ;
        int modelRow = table.convertRowIndexToModel( row ) ;

        final JLabel label = ( JLabel )super.getTableCellRendererComponent(
                             table, value, isSelected, hasFocus, row, column ) ;

        label.setText( "" ) ;
        label.setBorder( BorderFactory.createEmptyBorder(0,4,0,4) ) ;

        if( modelCol == LedgerTableModel.COL_TXN_TYPE_MARKER ) {
            if( value == TxnType.CREDIT ) {
                label.setIcon( CREDIT_ICON ) ;
            }
            else {
                label.setIcon( DEBIT_ICON ) ;
            }
        }
        else {
            setLabelText( value, label, modelRow, modelCol ) ;
            setLabelAlignment( label, modelCol ) ;
        }

        return label ;
    }

    private void setLabelText( final Object value, final JLabel label,
                               final int modelRow, final int modelCol ) {

        if( value != null ) {
            String displayText = "" ;
            if( value instanceof Number ) {
                displayText = Utils.formatLakh( ((Number)value).doubleValue() );
            }
            else if( value instanceof Date ) {
                displayText = SDF.format( (Date)value ) ;
            }
            else {
                displayText = value.toString() ;
            }
            label.setText( displayText ) ;
        }
    }

    private void setLabelAlignment( final JLabel label, final int modelCol ) {
        
        if( modelCol == LedgerTableModel.COL_TX_AMT ||
            modelCol == LedgerTableModel.COL_AC_BALANCE_AMT  ) {
            
            label.setHorizontalAlignment( JLabel.RIGHT ) ;
            label.setHorizontalTextPosition( JLabel.RIGHT ) ;
        }
        else {
            label.setHorizontalAlignment( JLabel.LEFT ) ;
            label.setHorizontalTextPosition( JLabel.LEFT ) ;
        }
    }
}
