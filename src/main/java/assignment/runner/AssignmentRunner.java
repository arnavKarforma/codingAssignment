package assignment.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assignment.Helper.Constants;
import assignment.config.businessLogic.EventsInMemoryProcessingImpl;
import assignment.config.businessLogic.JsonReader;
import assignment.repo.EventsDaoImpl;
import assignment.repo.EventsDaoInterface;

/**
 * This is the main class where the execution starts
 * 
 * @author ARNAV
 *
 */
public class AssignmentRunner {
	private static final Logger logger = LoggerFactory.getLogger(AssignmentRunner.class);

	public static void main(String[] args) throws CloneNotSupportedException, InterruptedException {
		logger.info("Inside the main method");
		EventsDaoInterface eventDao = new EventsDaoImpl();
		eventDao.createTable(Constants.CREATE_EVENTS_TABLE_SQL_FILE, Constants.CONN_STRING);
		logger.info("Inside the main method after creating the table");
		logger.info("Inside the main method going to process the json File in MultiThreads");
		JsonReader.processJsonObjects(Constants.EVENTS_LOG_JSON_FILE, Constants.CONN_STRING);
		logger.info("Inside the main method going to read the saved Events from DataBase");
		new EventsInMemoryProcessingImpl().readSavedData(Constants.CONN_STRING);

	}
}
