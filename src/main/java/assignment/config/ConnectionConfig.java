package assignment.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assignment.Helper.Constants;

/**
 * This class is used for providing connection to the DB
 * 
 * @author ARNAV
 * 
 * 
 */
public class ConnectionConfig {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionConfig.class);

	/**
	 * This method is used to give connection to any of the DB operations carried
	 * 
	 * @param connString
	 * @return Connection
	 */
	public static Connection getConnection(String connString) {
		Connection con = null;
		try {
			Class.forName(Constants.DRIVER);
		} catch (ClassNotFoundException ex) {

			logger.error("Inside the getConnection method JDBC driver not found");
			logger.error(ex.getMessage());
		}
		logger.info("Inside the getConnection method JDBC driverfound in classPath");
		logger.debug("Inside the getConnection method JDBC driverfound in classPath is " + Constants.DRIVER);
		try {
			con = DriverManager.getConnection(connString, "SA", "");
		} catch (SQLException e) {
			logger.error("Inside the getConnection method JDBC unable to get connection");
			logger.error(e.getMessage());
		}
		logger.info("Inside the getConnection method Connection succeessfully set");
		logger.debug(
				"Inside the getConnection method Connection succeessfully set with connection string " + connString);
		return con;
	}

}
