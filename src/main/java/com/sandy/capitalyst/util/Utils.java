
package com.sandy.capitalyst.util;

import java.lang.annotation.Annotation ;
import java.lang.reflect.Field ;
import java.text.DecimalFormat ;
import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.time.Duration ;
import java.time.ZoneId ;
import java.time.ZonedDateTime ;
import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.Comparator ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Locale ;
import java.util.Map ;

import org.apache.commons.beanutils.BeanUtils ;
import org.apache.commons.lang.time.DateUtils ;
import org.apache.log4j.Logger ;

import com.cronutils.model.definition.CronDefinition ;
import com.cronutils.model.definition.CronDefinitionBuilder ;
import com.cronutils.model.time.ExecutionTime ;
import com.cronutils.parser.CronParser ;
import com.sandy.capitalyst.account.Account ;
import com.sandy.capitalyst.cfg.InvalidConfigException ;
import com.sandy.capitalyst.cfg.MissingConfigException ;
import com.sandy.capitalyst.cfg.PostConfigInitializable ;
import com.sandy.capitalyst.cfg.UniverseConfig ;
import com.sandy.capitalyst.core.Txn ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.core.UniverseConstituent ;

public class Utils {

    private static Logger log = Logger.getLogger( Utils.class ) ;
    
    public static final SimpleDateFormat SDF = new SimpleDateFormat( "dd/MM/yyyy" ) ;
    public static final DecimalFormat     DF = new DecimalFormat( "0.0" ) ;
    private static final CronDefinition CRON_DEF =
                                    CronDefinitionBuilder.defineCron()
                                    .withDayOfMonth()
                                    .supportsHash().supportsL().supportsW().and()
                                    .withMonth().and()
                                    .withDayOfWeek()
                                    .withIntMapping(7, 0)
                                    .supportsHash().supportsL().supportsW().and()
                                    .withYear().and()
                                    .lastFieldOptional()
                                    .instance();
    
    public static final CronParser CRON_PARSER = new CronParser( CRON_DEF ) ;
    
    public static boolean isMatch( ExecutionTime schedule, Date date ) {
        
        ZonedDateTime dt = null ;
        dt = ZonedDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() ) ;
        return schedule.isMatch( dt ) ; 
    }
    
    public static Date parseDate( String dateStr ) throws IllegalArgumentException {
        try {
            return SDF.parse( dateStr ) ;
        }
        catch( ParseException e ) {
            throw new IllegalArgumentException( "Invalid date specified. " + dateStr ) ;
        }
    }
    
    public static String formatLakh( double d ) {
        
        String s = String.format( Locale.UK, "%1.2f", Math.abs(d) ) ;
        s = s.replaceAll( "(.+)(...\\...)", "$1,$2" ) ;
        while( s.matches("\\d{3,},.+") ) {
            s = s.replaceAll( "(\\d+)(\\d{2},.+)", "$1,$2" ) ;
        }
        s = s.substring( 0, s.indexOf( '.' ) ) ;
        return d < 0 ? ("-" + s) : s;
    }    
    
    public static int getMonth( Date date ) {
        return DateUtils.toCalendar( date ).get( Calendar.MONTH ) ;
    }
    
    public static int getYear( Date date ) {
        return DateUtils.toCalendar( date ).get( Calendar.YEAR ) ;
    }
    
    public static String formatDate( Date date ) {
        return SDF.format( date ) ;
    }
    
    public static Date addDays( int numDays, Date date ) {
        return DateUtils.addDays( date, numDays ) ;
    }
    
    public static boolean isBefore( Date toCompare, Date milestone ) {
        if( DateUtils.truncatedCompareTo( toCompare, milestone, Calendar.DAY_OF_MONTH ) < 0 ) {
            return true ;
        }
        return false ;
    }
    
    public static boolean isSame( Date toCompare, Date milestone ) {
        if( DateUtils.truncatedCompareTo( toCompare, milestone, Calendar.DAY_OF_MONTH ) == 0 ) {
            return true ;
        }
        return false ;
    }
    
    public static boolean isAfter( Date toCompare, Date milestone ) {
        if( DateUtils.truncatedCompareTo( toCompare, milestone, Calendar.DAY_OF_MONTH ) > 0 ) {
            return true ;
        }
        return false ;
    }
    
    public static boolean isSameOrAfter( Date toCompare, Date milestone ) {
        if( DateUtils.truncatedCompareTo( toCompare, milestone, Calendar.DAY_OF_MONTH ) >= 0 ) {
            return true ;
        }
        return false ;
    }
    
    public static boolean isSameOrBefore( Date toCompare, Date milestone ) {
        if( DateUtils.truncatedCompareTo( toCompare, milestone, Calendar.DAY_OF_MONTH ) <= 0 ) {
            return true ;
        }
        return false ;
    }
    
    public static boolean isEndOfMonth( Date date ) {
        return isEndOfMonth( DateUtils.toCalendar( date ) ) ;
    }
    
    public static boolean isEndOfYear( Date date ) {
        return isEndOfYear( DateUtils.toCalendar( date ) ) ;
    }
    
    public static boolean isEndOfMonth( Calendar cal ) {
        int maxDays = cal.getActualMaximum( Calendar.DAY_OF_MONTH ) ;
        int dayNum  = cal.get( Calendar.DAY_OF_MONTH ) ;
        
        return maxDays == dayNum ;
    }
    
    public static boolean isEndOfQuarter( Calendar cal ) {
        if( isEndOfMonth( cal ) ) {
            int monthNum  = cal.get( Calendar.MONTH ) ;
            if( monthNum == Calendar.MARCH || 
                monthNum == Calendar.JUNE || 
                monthNum == Calendar.SEPTEMBER ||
                monthNum == Calendar.DECEMBER ) {
                return true ;
            }
        }
        return false ;
    }
    
    public static boolean isEndOfQuarter( Date date ) {
        Calendar cal = Calendar.getInstance() ;
        cal.setTime( date ) ;
        return isEndOfQuarter( cal ) ;
    }
    
    public static boolean isEndOfYear( Calendar cal ) {
        if( isEndOfMonth( cal ) ) {
            int monthNum  = cal.get( Calendar.MONTH ) ;
            if( monthNum == Calendar.DECEMBER ) {
                return true ;
            }
        }
        return false ;
    }
    
    public static int getNumDaysBetween( Date fromDate, Date toDate ) {

        Duration duration = Duration.between( fromDate.toInstant(), 
                                              toDate.toInstant() ) ;
        return (int)duration.toDays() ;
    }
    
    public static void transfer( Account fromAcc, Account toAcc ) {
        
        transfer( fromAcc, toAcc, fromAcc.getUniverse().now(), "" ) ;
    }

    public static void transfer( Account fromAcc, Account toAcc, 
                                 Date date, String preamble ) {
        
        transfer( fromAcc.getAmount(), fromAcc, toAcc, date, preamble ) ;
    }
    
    public static void transfer( double amt, 
                                 Account fromAcc, Account toAcc, 
                                 Date date, String preamble ) {
        
        if( preamble == null ) {
            preamble = "" ;
        }
        else if( !(preamble == null || preamble.trim().equals( "" )) ) {
            preamble = preamble + ". " ;
        }
        
        transfer( amt, fromAcc, toAcc,
                  preamble + "Transfer to A/C " + toAcc.getAccountNumber(),
                  preamble + "Transfer from A/C " + fromAcc.getAccountNumber(),
                  date ) ;
    }
    
    public static void transfer( double amt, Account fromAcc, Account toAcc,
                                 String debitDesc, String creditDesc, Date date ) {
        
        Txn debitTxn  = new Txn( fromAcc.getAccountNumber(), -amt, date, debitDesc ) ;
        Txn creditTxn = new Txn( toAcc.getAccountNumber(),    amt, date, creditDesc ) ;
        
        fromAcc.getUniverse().postTransaction( debitTxn ) ;
        fromAcc.getUniverse().postTransaction( creditTxn ) ;
    }

    public static List<ConfigurableField> getAllConfigurableFields( Class<?> cls ) {
        
        List<ConfigurableField> allFields = new ArrayList<>() ;
        
        do {
            Field[] fields = cls.getDeclaredFields() ;
            for( Field f : fields ) {
                Annotation[] annotations = f.getAnnotations() ;
                if( annotations.length > 0 ) {
                    for( Annotation a : annotations ) {
                        if( a instanceof com.sandy.capitalyst.cfg.Cfg ) {
                            allFields.add( new ConfigurableField( f,
                          ((com.sandy.capitalyst.cfg.Cfg)a).mandatory() ) ) ;
                        }
                    }
                }
            }
            cls = cls.getSuperclass() ;
        }
        while( cls != null ) ;

        allFields.sort( new Comparator<ConfigurableField>() {
            @Override public int compare( ConfigurableField f1, ConfigurableField f2 ) {
                return f1.getField().getName().compareTo( f2.getField().getName() ) ;
            }
        } ) ;
        
        return allFields ;
    }
    
    public static Map<String, ConfigurableField> getAllConfigurableFieldsMap( Class<?> cls ) {
        Map<String, ConfigurableField> map = new HashMap<String, ConfigurableField>() ;
        for( ConfigurableField field : getAllConfigurableFields( cls ) ) {
            map.put( field.getName(), field ) ;
        }
        return map ;
    }
    
    public static Object createEntity( Class<?> cls, UniverseConfig attrCfg,
                                       String objId, Universe universe ) 
        throws Exception {

        Object  obj = cls.newInstance() ;
        
        if( obj instanceof UniverseConstituent ) {
            UniverseConstituent uc = ( UniverseConstituent )obj ;
            uc.setId( objId ) ;
            uc.setUniverse( universe ) ;
        }
        
        Utils.injectFieldValues( obj, attrCfg ) ;
        
        if( obj instanceof PostConfigInitializable ) {
            ( (PostConfigInitializable)obj ).initializePostConfig() ;
        }
        
        return obj ;
        
    }

    public static void injectFieldValues( Object obj, UniverseConfig attrCfg ) {
        
        List<ConfigurableField> fields = Utils.getAllConfigurableFields( obj.getClass() ) ;
        for( ConfigurableField field :  fields ) {
            populateField( obj, field, attrCfg );
        }
    }
    
    private static void populateField( Object obj, ConfigurableField f, 
                                       UniverseConfig attrValues ) {
        
        boolean mandatory      = f.isMandatory() ;
        String  fieldName      = f.getField().getName() ;
        String  fieldRawVals[] = attrValues.getStringArray( fieldName ) ;
        
        if( mandatory && ( fieldRawVals == null || fieldRawVals.length==0 ) ) {
            throw new MissingConfigException( fieldName ) ;
        }
        else if( fieldRawVals != null ) {
            try {
                if( fieldRawVals.length == 1 ) {
                    String rawVal = fieldRawVals[0] ;
                    log.debug( "\t" + fieldName + " = " + rawVal ) ;
                    BeanUtils.setProperty( obj, fieldName, rawVal ) ;
                }
                else if( fieldRawVals.length > 1 ){
                    for( String rawVal : fieldRawVals ) {
                        log.debug( "\t" + fieldName + " = " + rawVal ) ;
                    }
                    BeanUtils.setProperty( obj, fieldName, fieldRawVals ) ;
                }
            }
            catch( Exception e ) {
                log.error( "Unable to set property - " + fieldName + 
                           " = " + fieldRawVals, e ) ;
                throw new InvalidConfigException( fieldName ) ;
            }
        }
    }
    
}
