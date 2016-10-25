package com.sandy.capitalyst.ui.helper;

import java.awt.datatransfer.DataFlavor ;
import java.awt.datatransfer.Transferable ;
import java.awt.datatransfer.UnsupportedFlavorException ;
import java.io.IOException ;

public class AccountTransferable implements Transferable {
    
    public static final DataFlavor DATA_FLAVOR = new DataFlavor( AccountWrapper.class, "Account" ) ;
    
    private AccountWrapper accountWrapper = null ;
    
    public AccountTransferable( AccountWrapper a ) {
        this.accountWrapper = a ;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavors = new DataFlavor[1] ;
        flavors[0] = DATA_FLAVOR ;
        return flavors ;
    }

    @Override
    public boolean isDataFlavorSupported( DataFlavor flavor ) {
        return flavor.getRepresentationClass() == AccountWrapper.class ;
    }

    @Override
    public Object getTransferData( DataFlavor flavor )
            throws UnsupportedFlavorException, IOException {
        
        if( flavor.getRepresentationClass() != AccountWrapper.class ) {
            throw new UnsupportedFlavorException( flavor ) ;
        }
        return accountWrapper ;
    }
}
