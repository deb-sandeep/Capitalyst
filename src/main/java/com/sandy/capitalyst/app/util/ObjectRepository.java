package com.sandy.capitalyst.app.util;

import com.sandy.capitalyst.Capitalyst ;
import com.sandy.capitalyst.app.ui.MainFrame ;
import com.sandy.capitalyst.app.ui.actions.Actions ;
import com.sandy.common.bus.EventBus ;
import com.sandy.common.objfactory.SpringObjectFactory ;
import com.sandy.common.util.StateManager ;
import com.sandy.common.util.WorkspaceManager ;

public class ObjectRepository {

    private static SpringObjectFactory objFactory     = null ;
    private static WorkspaceManager    wkspMgr        = null ;
    private static EventBus            bus            = null ;
    private static MainFrame           mainFrame      = null ;
    private static Capitalyst          app            = null ;
    private static StateManager        stateMgr       = null ;
    private static AppConfig           appConfig      = null ;
    private static Actions             uiActions      = null ;
    
    public static Actions getUiActions() {
        return uiActions;
    }

    public static void setUiActions( Actions uiActions ) {
        ObjectRepository.uiActions = uiActions;
    }

    public static AppConfig getAppConfig() {
        return appConfig;
    }

    public static void setAppConfig( AppConfig appConfig ) {
        ObjectRepository.appConfig = appConfig;
    }

    public static StateManager getStateMgr() {
        return stateMgr;
    }

    public static void setStateMgr( StateManager stateMgr ) {
        ObjectRepository.stateMgr = stateMgr;
    }

    public static Capitalyst getApp() {
        return app;
    }

    public static void setApp( Capitalyst app ) {
        ObjectRepository.app = app;
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static void setMainFrame( MainFrame mainFrame ) {
        ObjectRepository.mainFrame = mainFrame;
    }

    public static void setObjectFactory( SpringObjectFactory obj ) {
        objFactory = obj ;
    }
    
    public static SpringObjectFactory getObjectFactory() {
        return objFactory ;
    }
    
    public static void setWkspManager( WorkspaceManager obj ) {
        wkspMgr = obj ;
    }
    
    public static WorkspaceManager getWkspManager() {
        return wkspMgr ;
    }
    
    public static void setBus( EventBus obj ) {
        bus = obj ;
    }
    
    public static EventBus getBus() {
        return bus ;
    }
}
