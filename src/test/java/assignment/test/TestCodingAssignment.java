package assignment.test;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Test;

import assignment.Dto.InMemoryEventsDto;
import assignment.Helper.Constants;
import assignment.config.ConnectionConfig;
import assignment.config.businessLogic.EventsInMemoryProcessingImpl;
import assignment.config.businessLogic.JsonReader;
import assignment.repo.EventsDaoImpl;
import assignment.repo.EventsDaoInterface;

/**
 * Test Class
 * 
 * @author ARNAV
 *
 */
public class TestCodingAssignment {
	/**
	 * To drop the table created after each test
	 * 
	 * @throws SQLException
	 */
	@After
	public void cleanup() throws SQLException {
		Connection con = ConnectionConfig.getConnection(Constants.CONN_STRING_TEST);
		Statement stmt = con.createStatement();
		String sql = "DROP TABLE EVENTS";
		stmt.executeUpdate(sql);
		con.close();
	}

	/**
	 * To test if table can be created
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testTableCreated() throws SQLException {
		EventsDaoInterface dao = new EventsDaoImpl();
		dao.createTable(Constants.CREATE_EVENTS_TABLE_SQL_FILE, Constants.CONN_STRING_TEST);
		Connection con = ConnectionConfig.getConnection(Constants.CONN_STRING_TEST);
		DatabaseMetaData dbm = con.getMetaData();
		ResultSet tables = dbm.getTables(null, null, "EVENTS", new String[] { "TABLE" });
		con.close();
		assertTrue(tables.next());
	}

	/**
	 * To test the full functionality of the application
	 */
	@Test
	public void testDataProcessed() {
		boolean outputISCorrect = true;
		EventsDaoInterface dao = new EventsDaoImpl();
		dao.createTable(Constants.CREATE_EVENTS_TABLE_SQL_FILE, Constants.CONN_STRING_TEST);
		JsonReader.processJsonObjects(Constants.EVENTS_LOG_JSON_FILE_TEST, Constants.CONN_STRING_TEST);

		new EventsInMemoryProcessingImpl().readSavedData(Constants.CONN_STRING_TEST);

		while (true) {
			if (EventsInMemoryProcessingImpl.startTest) {
				break;
			}
		}
		List<InMemoryEventsDto> expectedData = new ArrayList<>();
		expectedData.add(new InMemoryEventsDto("hgfdhg", "APPLICATION_LOG", "12345", 0, 0, 78, true));
		expectedData.add(new InMemoryEventsDto("jhjhv", "APPLICATION_LOG", "12345", 0, 0, 3, false));
		expectedData.add(new InMemoryEventsDto("nhgvfcgn", null, null, 0, 0, 22, true));

		for (InMemoryEventsDto savedEvent : EventsDaoImpl.persistedEvents) {
			Optional<InMemoryEventsDto> foundEvent = expectedData.stream()
					.filter(event -> event.getId().equals(savedEvent.getId())).findFirst();
			if (foundEvent.isPresent()) {
				if (!(foundEvent.get().equals(savedEvent))) {
					outputISCorrect = false;
					break;
				}
			} else {
				outputISCorrect = false;
				break;
			}
			assertTrue(outputISCorrect);
		}
	}
}
