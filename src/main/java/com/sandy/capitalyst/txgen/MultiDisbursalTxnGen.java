package com.sandy.capitalyst.txgen;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.InputStream ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.apache.poi.ss.usermodel.Cell ;
import org.apache.poi.ss.usermodel.Row ;
import org.apache.poi.ss.usermodel.Sheet ;
import org.apache.poi.ss.usermodel.Workbook ;
import org.apache.poi.xssf.usermodel.XSSFWorkbook ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.exception.AccountNotFoundException ;
import com.sandy.capitalyst.util.converter.ScheduleTxnDefConverter ;
import com.sandy.common.util.StringUtil ;

public class MultiDisbursalTxnGen extends AbstractTxnGen
    implements PostConfigInitializable {

    static final Logger log = Logger.getLogger( MultiDisbursalTxnGen.class ) ;
    
    @Cfg( mandatory=false )
    private ScheduledTxnDef[] txnDefs = null ;
    
    @Cfg( mandatory=false )
    private String defaultCreditAccount = null ;
    
    @Cfg( mandatory=false )
    private String defaultDebitAccount = null ;
    
    @Cfg( mandatory=false )
    private File definitionFile = null ;
    
    public ScheduledTxnDef[] getTxnDefs() {
        return txnDefs ;
    }

    public void setTxnDefs( ScheduledTxnDef[] txnDefs ) {
        this.txnDefs = txnDefs ;
    }
    
    public String getDefaultCreditAccount() {
        return defaultCreditAccount ;
    }

    public void setDefaultCreditAccount( String defaultCreditAccount ) {
        this.defaultCreditAccount = defaultCreditAccount ;
    }

    public String getDefaultDebitAccount() {
        return defaultDebitAccount ;
    }

    public void setDefaultDebitAccount( String defaultDebitAccount ) {
        this.defaultDebitAccount = defaultDebitAccount ;
    }
    
    public File getDefinitionFile() {
        return definitionFile ;
    }

    public void setDefinitionFile( File definitionFile ) {
        this.definitionFile = definitionFile ;
    }

    @Override
    public void getTransactionsForDate( Date date, List<Txn> txnList ) {
        for( ScheduledTxnDef def : txnDefs ) {
            if( def.isValidFor( date ) ) {
                createTxn( def, date, txnList ) ;
            }
        }
    }
    
    private void createTxn( ScheduledTxnDef def, Date date, 
                            List<Txn> txnList ) {
        
        String creditAC = def.getCreditACNo() ;
        String debitAC  = def.getDebitACNo() ;
        
        if( creditAC == null ) {
            creditAC = defaultCreditAccount ;
        }
        
        if( debitAC == null ) {
            debitAC = defaultDebitAccount ;
        }
        
        if( creditAC == null ) {
            throw new AccountNotFoundException( "creditAccount" ) ;
        }
        
        if( debitAC == null ) {
            throw new AccountNotFoundException( "debitAccount" ) ;
        }
        
        double amt = def.getAmount().getAmount() ;
        
        Txn debitTxn  = new Txn( debitAC, -amt, date, "Transfer for " + def.getDescription() ) ;
        Txn creditTxn = new Txn( creditAC, amt, date, "Transfer to "  + def.getDescription() ) ;
        
        txnList.add( debitTxn ) ;
        txnList.add( creditTxn ) ;
    }

    @Override
    public void initializePostConfig() {
        if( txnDefs == null && definitionFile == null ) {
            throw new IllegalStateException( "Both transaction definitions and "
                    + "definition file is null" ) ;
        }
        
        if( definitionFile != null ) {
            if( !definitionFile.exists() ) {
                throw new IllegalArgumentException( "Definition file does not exist" ) ;
            }
            try {
                loadTxnDefsFromFile() ;
            }
            catch( Exception e ) {
                throw new IllegalStateException( "Invalid definition file", e ) ;
            }
        }
    }
    
    private void loadTxnDefsFromFile() throws Exception {
        
        InputStream is = new FileInputStream( definitionFile ) ;
        Workbook workbook = new XSSFWorkbook( is ) ;
        
        Sheet sheet = workbook.getSheetAt( 0 ) ;
        int numLines = sheet.getLastRowNum() - sheet.getFirstRowNum() ;
        
        List<ScheduledTxnDef> defs = new ArrayList<ScheduledTxnDef>() ;
        ScheduleTxnDefConverter converter = new ScheduleTxnDefConverter() ;
        
        for( int rowNum=0; rowNum<=numLines; rowNum++ ) {
            Row row = sheet.getRow( rowNum ) ;
            StringBuilder buffer = new StringBuilder() ;
            boolean definitionCreated = true ;
            
            for( int colNum=0; colNum<ScheduleTxnDefConverter.NUM_COLS; colNum++ ) {
                String cellContent = "" ;
                Cell cell = row.getCell( colNum ) ;
            
                if( cell != null ) {
                    cellContent = cell.toString() ;
                }
                
                if( colNum==0 ) {
                    // If the row is either a comment row, title row, or blank
                    // row, ignore the line
                    if( cellContent.startsWith( "#" ) || 
                        cellContent.startsWith( "Amount" ) || 
                        StringUtil.isEmptyOrNull( cellContent ) ) {
                        definitionCreated = false ;
                        break ;
                    }
                }
                
                cellContent = getInterpolatedValue( cellContent ) ;
                buffer.append( cellContent ).append( ":" ) ;
            }
            
            if( definitionCreated ) {
                buffer.append( "EOR" ) ;
                log.debug( "\tTxn def = " + buffer ) ;
                defs.add( converter.createTxnDef( buffer.toString() ) ) ;
            }
        }
        
        txnDefs = defs.toArray( new ScheduledTxnDef[1] ) ;
    }
    
    private String getInterpolatedValue( String input ) {
        return getUniverse().getConfiguration().interpolate( input ) ;
    }
}
