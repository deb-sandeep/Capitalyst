package com.sandy.capitalyst.ui.panel.ledger;

import java.text.ParseException ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import javax.swing.RowFilter ;
import javax.swing.RowFilter.ComparisonType ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.util.Utils ;
import com.sandy.common.util.StringUtil ;

public class LedgerQueryParser {

    static final Logger log = Logger.getLogger( LedgerQueryParser.class ) ;

    /** The operators supported for the input string. */
    private enum OP { AND, OR, GT, LT, NE, EQ } ;

    // The tokens of the operators supported by this parser.
    private final static String OPS_TOKEN_AND = " AND " ;
    private final static String OPS_TOKEN_OR  = " OR " ;
    private final static String OPS_TOKEN_GT  = ">" ;
    private final static String OPS_TOKEN_LT  = "<" ;
    private final static String OPS_TOKEN_EQ  = "=" ;
    private final static String OPS_TOKEN_NE  = "<>" ;

    // An aggregation of the all the supported operators.
    private final String[] OPS_TOKENS = {
            OPS_TOKEN_AND, OPS_TOKEN_OR, OPS_TOKEN_GT,
            OPS_TOKEN_LT,  OPS_TOKEN_EQ, OPS_TOKEN_NE
    } ;

    // A map to store a easy lookup for the token enumeration value.
    private static final Map<String, OP> TOKEN_OP_MAP = new HashMap<String, OP>() ;
    static {
        TOKEN_OP_MAP.put( OPS_TOKEN_AND, OP.AND ) ;
        TOKEN_OP_MAP.put( OPS_TOKEN_OR,  OP.OR ) ;
        TOKEN_OP_MAP.put( OPS_TOKEN_GT,  OP.GT ) ;
        TOKEN_OP_MAP.put( OPS_TOKEN_LT,  OP.LT ) ;
        TOKEN_OP_MAP.put( OPS_TOKEN_EQ,  OP.EQ ) ;
        TOKEN_OP_MAP.put( OPS_TOKEN_NE,  OP.NE ) ;
    }

    // A map storing the possible combination of column names to their
    // indices. This map is created to maintain multiple mappings of column
    // names to the indexes. This will facilitate ease of query writing by
    // not restricting the user to fixed column names.
    private static final Map<String, Integer> COL_INDEX_MAP = new HashMap<String, Integer>() ;
    static {
        COL_INDEX_MAP.put( "IS_CREDIT",   Integer.valueOf( LedgerTableModel.COL_TXN_TYPE_MARKER ) ) ;
        COL_INDEX_MAP.put( "CREDIT",      Integer.valueOf( LedgerTableModel.COL_TXN_TYPE_MARKER ) ) ;
        COL_INDEX_MAP.put( "IS_DEBIT",    Integer.valueOf( LedgerTableModel.COL_TXN_TYPE_MARKER ) ) ;
        COL_INDEX_MAP.put( "DEBIT",       Integer.valueOf( LedgerTableModel.COL_TXN_TYPE_MARKER ) ) ;

        COL_INDEX_MAP.put( "DATE",        Integer.valueOf( LedgerTableModel.COL_DATE ) ) ;

        COL_INDEX_MAP.put( "AMOUNT",      Integer.valueOf( LedgerTableModel.COL_TX_AMT ) ) ;
        COL_INDEX_MAP.put( "AMT",         Integer.valueOf( LedgerTableModel.COL_TX_AMT ) ) ;

        COL_INDEX_MAP.put( "BALANCE",     Integer.valueOf( LedgerTableModel.COL_AC_BALANCE_AMT ) ) ;

        COL_INDEX_MAP.put( "DESCRIPTION", Integer.valueOf( LedgerTableModel.COL_DESCRIPTION ) ) ;
        COL_INDEX_MAP.put( "DESCR",       Integer.valueOf( LedgerTableModel.COL_DESCRIPTION ) ) ;
        COL_INDEX_MAP.put( "DESC",        Integer.valueOf( LedgerTableModel.COL_DESCRIPTION ) ) ;
    }

    /**
     * This class represents a recursive, two children node which will keep the
     * parsed expression tree of the ITD filter search query. A sub query node
     * can contain either a left and node subtree (if the node is not a
     * terminal node) or an empty left, right sub tree (in case of terminal
     * nodes) and a not null value of column and value.
     * <p>
     * Each sub query node is capable of formulating a row filter based on
     * the data it contains.
     *
     * @author Sandeep Deb [deb.sandeep@gmail.com]
     */
    private class SubQuery {

        private SubQuery leftQuery  = null ;
        private SubQuery rightQuery = null ;
        private OP       operator   = null ;
        private Integer  colIndex   = null ;
        private String   value      = null ;
        
        private RowFilter<Object, Object> rowFilter = null ;

        /**
         * This method recursively parses the input string and creates a
         * parse tree out of the same.
         *
         * @param input The input to parse
         *
         * @throws ParseException In case the input is not a valid input,
         *         this method will generate a parse exception with the
         *         appropriate error message.
         */
        public void parse( final String input ) throws ParseException {

            int     index = -1 ;
            String  token = null ;
            String  lhsVal= null ;
            String  rhsVal= null ;

            for( int i=0; i<LedgerQueryParser.this.OPS_TOKENS.length; i++ ) {
                
                token = LedgerQueryParser.this.OPS_TOKENS[i] ;
                index = input.toUpperCase().indexOf( token ) ;

                if( index > 0 ) {

                    lhsVal = input.substring( 0, index ).trim() ;
                    rhsVal = input.substring( index + token.length() ).trim() ;

                    if( StringUtil.isEmptyOrNull( lhsVal ) ) {
                        throw new ParseException( "Left hand expression for " +
                                "token " + token + " is not specified.", 0 ) ;
                    }

                    if( StringUtil.isEmptyOrNull( rhsVal ) ) {
                        throw new ParseException( "Right hand expression for " +
                                "token " + token + " is not specified.", 0 ) ;
                    }

                    if( token.equals( OPS_TOKEN_AND ) || token.equals( OPS_TOKEN_OR ) ) {
                        this.leftQuery = new SubQuery() ;
                        this.leftQuery.parse( lhsVal ) ;
                        this.rightQuery = new SubQuery() ;
                        this.rightQuery.parse( rhsVal ) ;
                    }
                    else {
                        this.colIndex = COL_INDEX_MAP.get( lhsVal.toUpperCase() ) ;
                        this.value  = rhsVal ;

                        if( this.colIndex == null ) {
                            throw new ParseException( "Invalid column : " + lhsVal +
                                                 " specified in the query", 0 ) ;
                        }
                        validateQueryValue( colIndex, value ) ;
                    }

                    this.operator = TOKEN_OP_MAP.get( token ) ;
                    return ;
                }
            }

            // If the control reaches here, it implies that the query string
            // does not contain any tokens - oops wrong input. Unless the user
            // is using single word instructions like credit, is_credit, debit
            // is_debit etc.
            if( input.equalsIgnoreCase( "CREDIT" ) ||
                input.equalsIgnoreCase( "IS_CREDIT" ) ||
                input.equalsIgnoreCase( "DEBIT" ) ||
                input.equalsIgnoreCase( "IS_DEBIT" ) ) {
                
                this.colIndex = COL_INDEX_MAP.get( input.toUpperCase() ) ;
                this.value = input ;
            }
            else {
                throw new ParseException( "No tokens found in the input string", 0 ) ;
            }
        }

        private boolean isLeafNode() {
            return this.leftQuery == null ;
        }
        
        private void validateQueryValue( int colIndex, Object val ) 
            throws IllegalArgumentException {
            
            if( colIndex == LedgerTableModel.COL_DATE ) {
                validateDate( (String)val ) ;
            }
            else if( colIndex == LedgerTableModel.COL_TX_AMT || 
                     colIndex == LedgerTableModel.COL_AC_BALANCE_AMT ) {
                validateNumber( (String)val ) ;
            }
        }
        
        private void validateDate( String d ) {
            try {
                Utils.SDF.parse( d ) ;
            }
            catch( Exception e ) {
                throw new IllegalArgumentException( "Illegal date", e ) ;
            }
        }
        
        private void validateNumber( String n ) {
            try {
                Double.parseDouble( n ) ;
            }
            catch( Exception e ) {
                throw new IllegalArgumentException( "Illegal number", e ) ;
            }
        }
        
        private ComparisonType getComparisonType() {
            ComparisonType compType = null ;
            switch( this.operator ) {
                case EQ:
                    compType = ComparisonType.EQUAL ;
                    break ;
                case NE:
                    compType = ComparisonType.NOT_EQUAL ;
                    break ;
                case LT:
                    compType = ComparisonType.BEFORE ;
                    break ;
                case GT:
                    compType = ComparisonType.AFTER ;
                    break ;
                default:
                    throw new IllegalArgumentException( "Illegal operator " + 
                                                        this.operator ) ;
            }
            return compType ;
        }

        /**
         * Returns a row filter for the current subtree. If this node represents
         * the root of the query parse tree, it returns the row filter
         * for the complete query string.
         *
         * @return A row filter instance corresponding to the query string
         *         parsed.
         */
        @SuppressWarnings( "incomplete-switch" )
        public RowFilter<Object, Object> getRowFilter() {

            if( this.rowFilter == null ) {

                if( !isLeafNode() ) {
                    final List<RowFilter<Object, Object>> filters =
                            new ArrayList<RowFilter<Object,Object>>() ;

                    filters.add( this.leftQuery.getRowFilter() ) ;
                    filters.add( this.rightQuery.getRowFilter() ) ;

                    switch( this.operator ) {
                        case AND:
                            this.rowFilter = RowFilter.andFilter( filters ) ;
                            break ;
                        case OR:
                            this.rowFilter = RowFilter.orFilter( filters ) ;
                            break ;
                    }
                }
                else {
                    if( this.colIndex == LedgerTableModel.COL_DATE ) {
                        this.rowFilter = RowFilter.dateFilter( 
                                                getComparisonType(), 
                                                Utils.parseDate( this.value ), 
                                                this.colIndex ) ;
                    }
                    else if( this.colIndex == LedgerTableModel.COL_AC_BALANCE_AMT || 
                             this.colIndex == LedgerTableModel.COL_TX_AMT ){
                        
                        final Number number = Double.parseDouble( this.value ) ;
                        this.rowFilter = RowFilter.numberFilter( 
                                                            getComparisonType(), 
                                                            number, 
                                                            this.colIndex ) ;
                    }
                    else if( this.colIndex == LedgerTableModel.COL_DESCRIPTION ){
                        this.rowFilter = RowFilter.regexFilter( 
                                            value, 
                                            LedgerTableModel.COL_DESCRIPTION ) ;
                    }
                    else if( this.colIndex == LedgerTableModel.COL_TXN_TYPE_MARKER ) {

                       if( value.equalsIgnoreCase( "IS_CREDIT" ) || 
                           value.equalsIgnoreCase( "CREDIT" ) ) { 
                           this.rowFilter = RowFilter.regexFilter( "CREDIT", 
                                             LedgerTableModel.COL_TXN_TYPE_MARKER ) ;
                       }
                       else {
                           this.rowFilter = RowFilter.regexFilter( "DEBIT", 
                                             LedgerTableModel.COL_TXN_TYPE_MARKER ) ;
                       }
                    }
                }
            }
            return this.rowFilter ;
        }
    }

    // The query string to be parsed.
    private final String inputQueryStr ;

    // The root node of the query.
    private SubQuery queryRoot = null ;

    /**
     * Constructor, which takes in the input string to be parsed. The user
     * can call on the parse method on this instance to convert the input
     * string into an instance of {@link RowFilter}.
     *
     * @param input The filter query string.
     */
    public LedgerQueryParser( final String inputQuery ) {
        this.inputQueryStr = inputQuery ;
    }

    public RowFilter<Object, Object> parse() throws ParseException {
        if( this.queryRoot == null ) {
            this.queryRoot = new SubQuery() ;
            this.queryRoot.parse( this.inputQueryStr.trim() ) ;
        }
        return this.queryRoot.getRowFilter() ;
    }
}
