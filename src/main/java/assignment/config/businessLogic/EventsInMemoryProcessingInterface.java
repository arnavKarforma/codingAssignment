package assignment.config.businessLogic;

import assignment.Dto.EventLog;
import assignment.Dto.InMemoryEventsDto;

/**
 * This is an interface to specify the functionality to be perform during in
 * memory Events
 * 
 * @author ARNAV
 * 
 *
 */
public interface EventsInMemoryProcessingInterface {

	/**
	 * This method must have functionality to convert the Events with same Id but
	 * different state to one Event or to convert to InMemory DTO format
	 * 
	 * @param readEvent
	 * @param connStr
	 */
	void convertToInMemoryEvents(EventLog readEvent, String connStr);

	/**
	 * This method must have functionality to process data structure to format to be
	 * persisted and forwarded to DAO
	 * 
	 * @param inMemoryEvent
	 * @param connStr
	 */
	void processingInMemoryData(InMemoryEventsDto inMemoryEvent, String connStr);

	/**
	 * This method must have functionality to read saved data in the DB by calling
	 * from DAO
	 * 
	 * @param connStr
	 */
	void readSavedData(String connStr);

	/**
	 * This method must have functionality to calculate EventDuration for an Event
	 * based on Start and End Duration
	 * 
	 * @param eventDto
	 */
	void calculateEventDuration(InMemoryEventsDto eventDto);

	/**
	 * This method must have functionality to update Alert status for EventDuration
	 * exceeding the limit defined by Business
	 * 
	 * @param eventDto
	 */
	void setAlert(InMemoryEventsDto eventDto);

}