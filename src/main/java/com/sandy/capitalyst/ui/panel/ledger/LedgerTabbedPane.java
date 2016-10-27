package com.sandy.capitalyst.ui.panel.ledger;

import java.awt.Color ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.ui.helper.UIConstants ;
import com.sandy.common.ui.CloseableTabbedPane ;

@SuppressWarnings( "serial" )
public class LedgerTabbedPane extends CloseableTabbedPane {
    
    public LedgerTabbedPane() {
        setForeground( Color.BLACK ) ;
        setFont( UIConstants.TABLE_FONT );
    }

    public void showAccountLedger( Account account ) {
        String key = account.getUniverse().getId() + "." + account.getId() ;
        LedgerDisplayPanel panel = null ;
        
        for( int i=0; i<getTabCount(); i++ ) {
            String tabTitle = getTitleAt( i ) ;
            if( tabTitle.equals( key ) ) {
                panel = ( LedgerDisplayPanel )getTabComponentAt( i ) ;
                break ;
            }
        }
        
        if( panel == null ) {
            panel = new LedgerDisplayPanel( account ) ;
            addTab( key, panel ) ;
        }
        setSelectedComponent( panel ) ;
    }
}
