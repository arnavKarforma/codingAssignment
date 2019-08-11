package assignment.config.businessLogic;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assignment.Dto.EventLog;
import assignment.Helper.Constants;

/**
 * This class is to read JSON Data from the JSon file not at once but in
 * Streaming mode
 * 
 * @author ARNAV
 *
 */

public class JsonReader {

	// This is BlockingQueue used to circulate JsonObject being read
	public static BlockingQueue<EventLog> events = new ArrayBlockingQueue<>(100);
	static EventLog event = new EventLog();
	private static final Logger logger = LoggerFactory.getLogger(JsonReader.class);

	/**
	 * This method start processing the JSON file in streams and spawns thread for
	 * each JSON object
	 * 
	 * @param jsonFile
	 * @param connStr
	 */
	public static void processJsonObjects(String jsonFile, String connStr) {
		logger.info("Inside the processJsonObjects method");
		try {
			JsonFactory jsonfactory = new JsonFactory();
			File source = new File(jsonFile);
			if (source.exists()) {
				logger.debug("Inside the processJsonObjects method file was found");
				JsonParser parser = jsonfactory.createJsonParser(source);
				logger.info("Inside the processJsonObjects Json Parser of Jackson was created");
				JsonToken jsonToken = parser.nextToken();

				while (jsonToken != JsonToken.END_ARRAY) {
					String token = parser.getCurrentName();
					if (Constants.JSON_FIELD_ID.equals(token)) {
						parser.nextToken();
						event.setId(parser.getText());
					}

					if (Constants.JSON_FIELD_STATE.equals(token)) {
						parser.nextToken();
						event.setState(parser.getText());
					}

					if (Constants.JSON_FIELD_TYPE.equals(token)) {
						parser.nextToken();
						event.setType(parser.getText());
					}

					if (Constants.JSON_FIELD_HOST.equals(token)) {
						parser.nextToken();
						event.setHost(parser.getText());
					}

					if (Constants.JSON_FIELD_TIMESTAMP.equals(token)) {
						parser.nextToken();
						event.setTimeStamp(parser.getText());
					}
					if (jsonToken == JsonToken.END_OBJECT && (event.getId() != null)) {
						logger.debug("Inside the processJsonObjects method file one JSON object has been read");
						try {
							events.put((EventLog) event.clone());
							logger.info("Inside the processJsonObjects added event to Blocking queue");
						} catch (InterruptedException e1) {
							logger.error(
									"Inside the processJsonObjects method file interreupted during adding to blocking queue");
							logger.error(e1.getMessage());
						} catch (CloneNotSupportedException e1) {
							logger.error(
									"Inside the processJsonObjects method file error during creating clone of Event to be procesed");
							logger.error(e1.getMessage());
						}

						logger.debug("Inside the processJsonObjects method file JSON object is" + event);
						new Thread(() -> {
							logger.debug("Inside the processJsonObjects method one thread has spawn for event" + event);
							EventsInMemoryProcessingImpl transformation = new EventsInMemoryProcessingImpl();
							try {
								transformation.convertToInMemoryEvents(events.take(), connStr);
							} catch (InterruptedException e) {
								logger.error(
										"Inside the processJsonObjects method file interreupted during executing a thread reading from JSON");
								logger.error(e.getMessage());
							}
						}).start();
						event = new EventLog();
					}
					jsonToken = parser.nextToken();
				}
				parser.close();
				logger.info("Inside the processJsonObjects parser is closed");
			} else {
				logger.error("Inside the processJsonObjects method Json File " + jsonFile + "was not found");
			}

		} catch (JsonMappingException jme) {
			logger.error(jme.getMessage());
		} catch (IOException ioex) {
			logger.error(ioex.getMessage());
		}

	}

}
