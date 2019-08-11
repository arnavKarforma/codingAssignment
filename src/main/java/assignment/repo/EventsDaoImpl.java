package assignment.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assignment.Dto.InMemoryEventsDto;
import assignment.Helper.Constants;
import assignment.Helper.FileUtility;
import assignment.config.ConnectionConfig;
/**
 * * This class is implementation of EventsDaoInterface to
 * process the DB operations
 * 
 * @author ARNAV
 *
 *
 * 
 */
public class EventsDaoImpl implements EventsDaoInterface {
	private static final Logger logger = LoggerFactory.getLogger(EventsDaoImpl.class);
	//To Store all the data in the DB used for JUNIT test
	public static List<InMemoryEventsDto> persistedEvents = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see assignment.repo.EventsDaoInterface#createTable(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void createTable(String fileName, String connStr) {
		logger.info("Inside the createTable method");
		String createEventTable;
		try {
			createEventTable = FileUtility.parseSqlToString(fileName);
			Connection con = ConnectionConfig.getConnection(connStr);
			con.createStatement().executeUpdate(createEventTable);
			con.close();
			logger.info("Inside the createTable Connection has been closed ");
		} catch (SQLException e) {
			logger.error("Inside the createTable Unable to create Events Table");
			logger.error(e.getMessage());
		}
		logger.debug("Inside the createTable method Events Table has been Created ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see assignment.repo.EventsDaoInterface#saveEvent(assignment.Dto.
	 * InMemoryEventsDto, java.lang.String)
	 */
	@Override
	public void saveEvent(InMemoryEventsDto event, String connStr) {
		logger.info("Inside the saveEvent method");
		Connection con = null;
		try {
			con = ConnectionConfig.getConnection(connStr);
			logger.info("Inside the saveEvent method Connection is set");
			con.setAutoCommit(false);
			logger.info("Inside the saveEvent method set the Auto Commit to false");
			PreparedStatement prepStmt = con
					.prepareStatement("insert into Events(eventId,host,type,eventDuration,alert) values (?,?,?,?,?)");
			prepStmt.setString(1, event.getId());
			prepStmt.setString(2, event.getHost());
			prepStmt.setString(3, event.getType());
			prepStmt.setInt(4, event.getEventDuration());
			prepStmt.setBoolean(5, event.isAlert());
			logger.debug("Inside the saveEvent method going to execute the query " + prepStmt.toString());
			int status = prepStmt.executeUpdate();
			if (status == 1) {
				logger.debug("Inside the saveEvent method executed the query sucessfully" + prepStmt.toString());
			} else
				logger.debug(
						"Inside the saveEvent method unable to execut the query sucessfully" + prepStmt.toString());
			con.commit();
			logger.debug("Inside the saveEvent method Event saved in the database is " + event);

		} catch (SQLException e) {
			logger.error("Inside the saveEvent method execution of the query interrupted ");
			logger.error(e.getMessage());
		} finally {
			try {
				con.close();
				logger.info("Inside the saveEvent method connection has been closed");
			} catch (SQLException e) {
				logger.error("Inside the saveEvent method unable to close the Connection");
				logger.error(e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see assignment.repo.EventsDaoInterface#readAllFromEvents(java.lang.String)
	 */
	@Override
	public List<InMemoryEventsDto> readAllFromEvents(String connStr) {
		logger.info("Inside the readAllFromEvents method");

		Connection con = null;
		try {
			con = ConnectionConfig.getConnection(connStr);
			logger.info("Inside the readAllFromEvents method Connection is set");
			synchronized (con) {
				PreparedStatement pst = con.prepareStatement("select * from Events");
				pst.clearParameters();
				ResultSet rs = pst.executeQuery();
				logger.info("Inside the readAllFromEvents query has been executed" + pst.toString());

				while (rs.next()) {

					persistedEvents.add(new InMemoryEventsDto(rs.getString(Constants.DB_FIELD_ID),
							rs.getString(Constants.DB_FIELD_TYPE), rs.getString(Constants.DB_FIELD_HOST),
							rs.getInt(Constants.DB_EVENT_DURATION), rs.getBoolean(Constants.DB_FIELD_ALERT)));
				}
				logger.info("Inside the readAllFromEvents all the data has been fetched");
				persistedEvents.forEach(e -> logger.debug("SAVED in the database Events:  " + e));
			}
		} catch (SQLException e) {
			logger.error("Inside the readAllFromEvents method exception occured while executing the query");
			logger.error(e.getMessage());
		} finally {
			try {
				con.close();
				logger.info("Inside the readAllFromEvents method connection has been closed");
			} catch (SQLException e) {
				logger.error("Inside the readAllFromEvents method unable to close the Connection");
				logger.error(e.getMessage());
			}
		}
		return persistedEvents;
	}

}
