package com.sandy.capitalyst;

/**
 * This class contains all the event types which can be published by the 
 * Capitalyst core.
 */
public class EventType {

    private static int CLOCK_EVENT_START_ID   = 100 ;
    private static int ACCOUNT_EVENT_START_ID = 150 ;
    private static int TXN_EVENT_START_ID     = 200 ; 

    // Clock events
    public static int DAY_START   = 0 + CLOCK_EVENT_START_ID ;
    public static int DAY_END     = 1 + CLOCK_EVENT_START_ID ;
    public static int MONTH_END   = 2 + CLOCK_EVENT_START_ID ;
    public static int QUARTER_END = 3 + CLOCK_EVENT_START_ID ;
    public static int YEAR_END    = 4 + CLOCK_EVENT_START_ID ;
    
    // Account events
    public static int ACCOUNT_CREATED = 0 + ACCOUNT_EVENT_START_ID ;
    public static int ACCOUNT_CLOSED  = 1 + ACCOUNT_EVENT_START_ID ;
    
    // Transaction events
    public static int TXN_POSTED = 0 + TXN_EVENT_START_ID ;
}
