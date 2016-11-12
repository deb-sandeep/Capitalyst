package com.sandy.capitalyst.factory;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.InputStream ;
import java.util.HashMap ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.apache.poi.ss.usermodel.Cell ;
import org.apache.poi.ss.usermodel.Row ;
import org.apache.poi.ss.usermodel.Sheet ;
import org.apache.poi.ss.usermodel.Workbook ;
import org.apache.poi.xssf.usermodel.XSSFWorkbook ;

import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.account.BankAccount ;
import com.sandy.capitalyst.account.FixedInvestmentFixedAnnuityAccount ;
import com.sandy.capitalyst.account.FixedInvestmentFixedReturnAccount ;
import com.sandy.capitalyst.account.QuarterlyCompoundingAccount ;
import com.sandy.capitalyst.account.SavingAccount ;
import com.sandy.capitalyst.account.SuperannuationAccount ;
import com.sandy.capitalyst.account.YearlyCompoundingAccount ;
import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.util.ConfigurableField ;
import com.sandy.capitalyst.util.Utils ;
import com.sandy.common.util.StringUtil ;

public class AccountFactory extends Factory {

    static final Logger log = Logger.getLogger( AccountFactory.class ) ;
    
    private static Map<String, Class<? extends Account>> accClsMap = 
                               new HashMap<String, Class<? extends Account>>() ;
    static {
        accClsMap.put( "Account",                            Account.class ) ;
        accClsMap.put( "BankAccount",                        BankAccount.class ) ;
        accClsMap.put( "SavingAccount",                      SavingAccount.class ) ;
        accClsMap.put( "QuarterlyCompoundingAccount",        QuarterlyCompoundingAccount.class ) ;
        accClsMap.put( "YearlyCompoundingAccount",           YearlyCompoundingAccount.class ) ;
        accClsMap.put( "SuperannuationAccount",              SuperannuationAccount.class ) ;
        accClsMap.put( "FixedInvestmentFixedAnnuityAccount", FixedInvestmentFixedAnnuityAccount.class ) ;
        accClsMap.put( "FixedInvestmentFixedReturnAccount",  FixedInvestmentFixedReturnAccount.class ) ;
    }
    
    private String id = null ;
    private Universe universe = null ;
    
    private Class<? extends Account> currentACType = null ;
    private Map<String, ConfigurableField> cfgFields = null ;
    private String[] cfgAttributeOrder = null ;
    
    private boolean expectSecHdrRow = false ;
    
    @Cfg private File definitionFile = null ;
    
    public File getDefinitionFile() {
        return definitionFile ;
    }

    public void setDefinitionFile( File definitionFile ) {
        this.definitionFile = definitionFile ;
    }
    
    public void setId( String id ) { this.id = id ; }
    public String getId() { return this.id ; }
    public void setUniverse( Universe u ) { this.universe = u ; }
    public Universe getUniverse() { return this.universe ; }

    @Override
    public void initializePostConfig() {
        if( definitionFile != null ) {
            if( !definitionFile.exists() ) {
                throw new IllegalArgumentException( "Definition file does not exist" ) ;
            }
            try {
                loadAccountsFromFile() ;
            }
            catch( Exception e ) {
                throw new IllegalStateException( "Invalid definition file", e ) ;
            }
        }
    }
    
    private void loadAccountsFromFile() throws Exception {
        
        log.debug( "Loading accounts from " + definitionFile.getAbsolutePath() );
        InputStream is = new FileInputStream( definitionFile ) ;
        Workbook workbook = new XSSFWorkbook( is ) ;
        
        Sheet sheet = workbook.getSheetAt( 0 ) ;
        int numLines = sheet.getLastRowNum() - sheet.getFirstRowNum() ;
        
        for( int rowNum=0; rowNum<=numLines; rowNum++ ) {
            Row row = sheet.getRow( rowNum ) ;
            if( XLSXUtil.isIgnorableRow( row ) ) continue ;
            
            if( isSectionRow( row ) ) {
                processSectionRow( row ) ;
            }
            else if( isSectionColHdrRow( row ) ) {
                processSectionColHdrs( row ) ;
            }
            else {
                loadAccount( row ) ;
            }
        }
    }
    
    private boolean isSectionRow( Row row ) {

        String cell0Val = cellValue( row, 0 ) ;
        if( cell0Val.equals( "Account Type" ) ) {
            return true ;
        }
        return false ;
    }
    
    @SuppressWarnings( "unchecked" )
    private void processSectionRow( Row row ) {
        
        String cell1Val = cellValue( row, 1 ) ;
        if( cell1Val != null ) {
            if( accClsMap.containsKey( cell1Val ) ) {
                currentACType = accClsMap.get( cell1Val ) ;
            }
            else {
                try {
                    currentACType = (Class<Account>)Class.forName( cell1Val ) ;
                }
                catch( Exception e ) {
                    log.error( "Invalid account type specified. " + cell1Val ) ;
                    throw new IllegalStateException( "Invalid account type", e ) ;
                }
            }
            cfgFields = Utils.getAllConfigurableFieldsMap( currentACType ) ;
            expectSecHdrRow = true ;
            log.debug( "Account type section " + currentACType.getName() );
        }
    }
    
    private boolean isSectionColHdrRow( Row row ) {
        return expectSecHdrRow ;
    }
    
    private void processSectionColHdrs( Row row ) {
        
        int numExpectedCols = cfgFields.size() ;
        cfgAttributeOrder = getRowContents( row, numExpectedCols ) ;
        
        // Validate the columns. All the expected columns should be present.
        for( String attr : cfgAttributeOrder ) {
            if( attr == null ) {
                throw new IllegalStateException( "Empty column header found" ) ;
            }
            
            if( !cfgFields.containsKey( attr ) ) {
                throw new IllegalStateException( attr + " attribute not "
                                           + "found in account configuration" ) ;
            }
        }
        expectSecHdrRow = false ;
    }
    
    private void loadAccount( Row row ) throws Exception {
        
        int numAttrs = cfgFields.size() ;
        String[] attrVals = getRowContents( row, numAttrs ) ;
        UniverseConfig config = new UniverseConfig() ;
        
        String accName = null ;
        for( int i=0; i<numAttrs; i++ ) {
            String attrName = cfgAttributeOrder[i] ;
            String attrVal = attrVals[i] ;
            
            if( StringUtil.isNotEmptyOrNull( attrVal ) ) {
                config.setProperty( attrName, attrVal ) ;
                if( attrName.equals( "name" ) ) {
                    accName = attrVal ;
                }
            }
        }
        
        Object o = Utils.createEntity( currentACType, config, accName, universe ) ;
        Account account = Account.class.cast( o ) ;
        
        universe.addAccount( account ) ;
    }
    
    private String[] getRowContents( Row row, int numCols ) {
        
        String[] cols = new String[numCols] ;
        for( int c=0; c<numCols; c++ ) {
            cols[c] = cellValue( row, c ) ;
        }
        return cols ;
    }
    
    private String cellValue( Row row, int col ) {
        
        String val = null ;
        Cell cell = row.getCell( col ) ;
        if( cell != null ) {
            val = getInterpolatedValue( cell.toString() ) ;
        }
        return val ;
    }
    
    private String getInterpolatedValue( String input ) {
        return getUniverse().getConfiguration().interpolate( input ) ;
    }
}
