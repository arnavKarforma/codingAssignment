package assignment.repo;

import java.util.List;

import assignment.Dto.InMemoryEventsDto;

/**
 * This Interface contains the DB operations carried in the application
 * 
 * @author ARNAV
 *
 */
public interface EventsDaoInterface {
	/**
	 * It must have functionality to create Table
	 * 
	 * @param fileName
	 * @param connStr
	 */
	void createTable(String fileName, String connStr);

	/**
	 * It must have functionality to save Event To DB
	 * 
	 * @param event
	 * @param connStr
	 */
	void saveEvent(InMemoryEventsDto event, String connStr);

	/**
	 * It must have functionality to readAll event from the DB
	 * 
	 * @param event
	 * @param connStr
	 */
	List<InMemoryEventsDto> readAllFromEvents(String connStr);

}