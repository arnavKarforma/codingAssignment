package assignment.config.businessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assignment.Dto.EventLog;
import assignment.Dto.InMemoryEventsDto;
import assignment.Helper.Constants;
import assignment.repo.EventsDaoImpl;
import assignment.repo.EventsDaoInterface;

/**
 * * This class is implementation of EventsInMemoryProcessingInterface to
 * process the in memory Events Object
 * 
 * @author ARNAV
 *
 *
 * 
 */
public class EventsInMemoryProcessingImpl implements EventsInMemoryProcessingInterface {

	public static List<InMemoryEventsDto> inMemoryEvents = new ArrayList<>();
	// To Notify The Reader thread that in memory processing has started
	public volatile static boolean executionHasStarted = false;
	// To Notify The Reader thread for test that all operation has been done
	public volatile static boolean startTest = false;
	private static final Logger logger = LoggerFactory.getLogger(EventsInMemoryProcessingImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see assignment.config.businessLogic.EventsInMemoryProcessingInterface#
	 * convertToInMemoryEvents(assignment.Dto.EventLog, java.lang.String)
	 */
	@Override
	public void convertToInMemoryEvents(EventLog readEvent, String connStr) {
		logger.info("Inside the convertToInMemoryEvents");
		synchronized (inMemoryEvents) {
			logger.debug(
					"Inside the convertToInMemoryEvents checking for existing Event with same id " + readEvent.getId());
			Optional<InMemoryEventsDto> memoryEvent = inMemoryEvents.stream()
					.filter(e -> e.getId().equals(readEvent.getId())).findFirst();
			if (memoryEvent.isPresent()) {
				logger.debug("Inside the convertToInMemoryEvents existing event found with eventId  "
						+ readEvent.getId() + memoryEvent.get());
				if (memoryEvent.get().getStartTime() != 0) {
					memoryEvent.get().setEndTime(Long.parseLong(readEvent.getTimeStamp()));
					logger.debug("Inside the convertToInMemoryEvents existing event modified with event id "
							+ readEvent.getId() + " is now with end time" + memoryEvent.get());
				} else if (memoryEvent.get().getEndTime() != 0) {
					memoryEvent.get().setStartTime(Long.parseLong(readEvent.getTimeStamp()));
					logger.debug("Inside the convertToInMemoryEvents existing event modified with event id "
							+ readEvent.getId() + " is now with start time" + memoryEvent.get());
				}
				processingInMemoryData(memoryEvent.get(), connStr);
			} else {
				logger.debug("Inside the convertToInMemoryEvents no any existing events found with same id "
						+ readEvent.getId());
				InMemoryEventsDto newMemoryEvent = new InMemoryEventsDto();
				logger.debug(
						"Inside the convertToInMemoryEvents new In Memory Event created with id " + readEvent.getId());
				newMemoryEvent.setId(readEvent.getId());
				newMemoryEvent.setHost(readEvent.getHost());
				newMemoryEvent.setType(readEvent.getType());
				if (readEvent.getState().equals("STARTED"))
					newMemoryEvent.setStartTime(Long.parseLong(readEvent.getTimeStamp()));
				else if (readEvent.getState().equals("FINISHED"))
					newMemoryEvent.setEndTime(Long.parseLong(readEvent.getTimeStamp()));
				logger.debug("Inside the convertToInMemoryEvents new In Memory Event created with id "
						+ readEvent.getId() + " is " + newMemoryEvent);
				processingInMemoryData(newMemoryEvent, connStr);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see assignment.config.businessLogic.EventsInMemoryProcessingInterface#
	 * processingInMemoryData(assignment.Dto.InMemoryEventsDto, java.lang.String)
	 */
	@Override
	public void processingInMemoryData(InMemoryEventsDto inMemoryEvent, String connStr) {
		logger.info("Inside the processingInMemoryData");
		inMemoryEvents.add(inMemoryEvent);
		logger.debug("Inside the processingInMemoryData event added is " + inMemoryEvent);
		executionHasStarted = true;
		logger.debug(
				"Inside the processingInMemoryData event processing has started hence executionHasStarted flag has been updated ");
		logger.debug(
				"Inside the processingInMemoryData event looking for event whose timestamp for STARTED and FINISHED state has been recorded");
		Optional<InMemoryEventsDto> completedEventOptional = inMemoryEvents.stream()
				.filter(event -> event.getStartTime() != 0 && event.getEndTime() != 0).findFirst();
		if (completedEventOptional.isPresent()) {
			logger.debug(
					"Inside the processingInMemoryData event completed event found" + completedEventOptional.get());
			InMemoryEventsDto completedEvent = completedEventOptional.get();

			EventsInMemoryProcessingInterface transformation = new EventsInMemoryProcessingImpl();

			logger.info(
					"Inside the processingInMemoryData going to calculate event duration of" + completedEvent.getId());
			transformation.calculateEventDuration(completedEvent);
			logger.info("Inside the processingInMemoryData going to check ALERT for more time consuming events"
					+ completedEvent.getId());
			transformation.setAlert(completedEvent);

			EventsDaoInterface dao = new EventsDaoImpl();
			logger.info("Inside the processingInMemoryData going to save the completed event" + completedEvent);
			dao.saveEvent(completedEvent, connStr);

			inMemoryEvents.removeIf(x -> x.getId().equals(completedEvent.getId()));
			logger.debug("Inside the processingInMemoryData after processing removed from In memory" + completedEvent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see assignment.config.businessLogic.EventsInMemoryProcessingInterface#
	 * readSavedData(java.lang.String)
	 */
	@Override
	public void readSavedData(String connStr) {
		logger.info("Inside the readSavedData");
		EventsDaoInterface eventDao = new EventsDaoImpl();

		new Thread(() -> {
			while (true) {
				if (EventsInMemoryProcessingImpl.executionHasStarted) {
					logger.debug("Inside the readSavedData InMemory Events processing has been started");
					synchronized (EventsInMemoryProcessingImpl.inMemoryEvents) {
						logger.debug("Inside the readSavedData current size of InMemoryEvents are "
								+ EventsInMemoryProcessingImpl.inMemoryEvents.size());
						if (EventsInMemoryProcessingImpl.inMemoryEvents.size() == 0) {
							logger.debug("Inside the readSavedData In Memory processing has been done");
							eventDao.readAllFromEvents(connStr);
							logger.info("Inside the readSavedData all the events has been read and logged");
							startTest = true;
							break;
						}
					}
				}
			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see assignment.config.businessLogic.EventsInMemoryProcessingInterface#
	 * calculateEventDuration(assignment.Dto.InMemoryEventsDto)
	 */
	@Override
	public void calculateEventDuration(InMemoryEventsDto eventDto) {
		logger.info("Inside the calculateEventDuration");
		eventDto.setEventDuration(Math.toIntExact(Math.abs(eventDto.getEndTime() - eventDto.getStartTime())));
		logger.debug("Inside the calculateEventDuration after calculating event Duration" + eventDto);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * assignment.config.businessLogic.EventsInMemoryProcessingInterface#setAlert(
	 * assignment.Dto.InMemoryEventsDto)
	 */
	@Override
	public void setAlert(InMemoryEventsDto eventDto) {
		logger.info("Inside the setAlert");
		if (eventDto.getEventDuration() > Constants.BUSINESS_ALERT_LIMIT) {
			eventDto.setAlert(true);
			logger.debug("Inside the setAlert found ALERT event" + eventDto);
		} else {
			eventDto.setAlert(false);
			logger.debug("Inside the setAlert found non ALERT event" + eventDto);
		}
	}
}
