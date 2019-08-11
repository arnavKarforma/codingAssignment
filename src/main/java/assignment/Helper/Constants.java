package assignment.Helper;

/**
 * This Class is used to define all hard coded constants of the code
 * 
 * @author ARNAV
 *
 */
public class Constants {

	public static final String CREATE_EVENTS_TABLE_SQL_FILE = "sql/createeventtable.sql";
	public static final String EVENTS_LOG_JSON_FILE = "input\\Log.json";
	public static final String EVENTS_LOG_JSON_FILE_TEST = "TestInput\\Log.json";
	public static final String CONN_STRING = "jdbc:hsqldb:file:db";
	public static final String CONN_STRING_TEST = "jdbc:hsqldb:file:dbTest";
	public static final String DRIVER = "org.hsqldb.jdbc.JDBCDriver";
	public static final String JSON_FIELD_ID = "id";
	public static final String JSON_FIELD_STATE = "state";
	public static final String JSON_FIELD_TYPE = "type";
	public static final String JSON_FIELD_HOST = "host";
	public static final String JSON_FIELD_TIMESTAMP = "timestamp";
	public static final int BUSINESS_ALERT_LIMIT = 4;
	public static final String DB_FIELD_ID = "eventId";
	public static final String DB_FIELD_ALERT = "alert";
	public static final String DB_FIELD_TYPE = "type";
	public static final String DB_FIELD_HOST = "host";
	public static final String DB_EVENT_DURATION = "eventDuration";

}
