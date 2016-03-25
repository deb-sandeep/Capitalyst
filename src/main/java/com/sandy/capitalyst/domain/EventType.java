package com.sandy.capitalyst.domain;

import com.sandy.capitalyst.domain.core.AccountingBook ;
import com.sandy.capitalyst.domain.core.AccountingItem ;

public class EventType {
    
    public static class CapitalystEvent {
        public AccountingBook book ;
        
        protected CapitalystEvent( AccountingBook book ) {
            this.book = book ;
        }
    }

    public static int ACCOUNTING_ITEM_GROUP_ADDED = 1 ;
    public static int ACCOUNTING_ITEM_ADDED = 2 ;
    
    public static class AccountingItemAddEvent extends CapitalystEvent{
        public AccountingItem item ;
        public AccountingItemAddEvent( AccountingBook book, 
                                       AccountingItem item ) {
            super( book ) ;
            this.item = item ;
        }
    }
}
