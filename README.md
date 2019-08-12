
## Project title
Coding Assignment

## Tech/framework used

<b>Built with</b>
- JDK 8 (https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)
- Apache Commons IO (https://commons.apache.org/proper/commons-io/)
- logback-classic (https://logback.qos.ch/)
- File Hsqldb (http://hsqldb.org/)
- Jackson (https://github.com/codehaus/jackson)
- Junit (https://junit.org/junit4/)
- Gradle (https://gradle.org/)


## Installation and Run
 ```
 This application can be download and Imported in Eclipse and then "db" and "dbTest" Folder must be created 
 or else File path can be changed in the Constant Class to use inMemory File Db.
 assignment.runner.AssignmentRunner is the class with main method from where the execution starts
 
 Note:-
 input folder has the Log.json (input file)
 sql folder has the createeventtable.sql to create The Events Table
 ```
 
## Design Decision
- Attempt has been made to read the Json file in streams not as a whole to efficently maange large files.
- Attempt has been made to run the application in MultiThread.(As a new JSON object is created a new thread spawns 
  which looks for other InMemory Events in the application already recorded with different STATE and once start and 
  finished duration is retrived for a single event, the latter thread of same Event persistes it in the DB and clears   
  the InMemory data for the Event)
- TreadPool/ExecutoService is not used but each time thread is spawned because the target and the size of data this application 
  will deal with is not known, if it is known and tested with thread pool it will outperform the current implementation in time 
  efficency.

## If there was no any need of reading JSON objects in stream it can have been done in this way for better readability
```
public static List<EventLog> readFromJson(String jsonFile) {
		List<EventLog> events = new ArrayList<>();
		JSONParser parser = new JSONParser();

		try (Reader reader = new FileReader(jsonFile)) {
			Object jsonObject = parser.parse(reader);
			JSONArray eventLogList = (JSONArray) jsonObject;
			Iterator<JSONObject> iterator = eventLogList.iterator();
			while (iterator.hasNext()) {
				EventLog event = new EventLog();
				//One Student json object
				JSONObject jsonData = iterator.next();
				
				event.setId((String) jsonData.get("id"));
				event.setState((String) jsonData.get("state"));
				event.setType( (String) jsonData.get("type"));
				event.setHost((String) jsonData.get("host"));
				event.setTimeStamp(jsonData.get("timestamp")+"");
				events.add(event);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		return events;
	}
```

## If there was no any any need of Multi threaded solution and need of processing huge data, 
the whole JSON Objects could have been processed all at once like this 
```
public List<EventsDb> transformToEvensDb(List<EventLog> events) {
		List<EventsDb> eventsDb = new ArrayList<>();
		Map<String, List<String>> eventsMap = events.stream().collect(Collectors.groupingBy(EventLog::getId,
				Collectors.mapping(EventLog::getTimeStamp, Collectors.toList())));
		eventsMap.forEach((k, v) -> {
			EventsDb eventToPersist = new EventsDb();
			eventToPersist.setEventId(k);
			eventToPersist
					.setEventDuration(Math.toIntExact(Math.abs(Long.parseLong(v.get(0)) - Long.parseLong(v.get(1)))));
			EventLog rawEvent = events.stream().filter(e -> e.getId().equals(k)).findFirst().orElse(null);
			if (!"".equals(rawEvent.getHost()))
				eventToPersist.setHost(rawEvent.getHost());
			if (!"".equals(rawEvent.getType()))
				eventToPersist.setType(rawEvent.getType()); if
	  (eventToPersist.getEventDuration() > 4) { eventToPersist.setAlert(true); }
	  else eventToPersist.setAlert(false); eventsDb.add(eventToPersist);
	  
	  }); return eventsDb;
```
## And could have been saved in a batch process
```
public void loadListOfEvents(List<EventsDb> events)  {
		Connection con = null;
		try {
			con = ConnectionConfig.getConnection();
			   con.setAutoCommit(false);        
			  PreparedStatement prepStmt = con.prepareStatement(    
			    "insert into Events(eventId,host,type,eventDuration,alert) values (?,?,?,?,?)");
			  Iterator<EventsDb> it = events.iterator();
			  while(it.hasNext()){
				  EventsDb event = it.next();
			    prepStmt.setString(1,event.getEventId());            
			    prepStmt.setString(2,event.getHost());
			    prepStmt.setString(3,event.getType());
			    prepStmt.setInt(4,event.getEventDuration());
			    prepStmt.setBoolean(5,event.isAlert());
			    prepStmt.addBatch();                      

			  }      
			  int [] numUpdates=prepStmt.executeBatch();
			  for (int i=0; i < numUpdates.length; i++) {
			    if (numUpdates[i] == -2)
			      System.out.println("Execution " + i + 
			        ": unknown number of rows updated");
			    else
			      System.out.println("Execution " + i + 
			        "successful: " + numUpdates[i] + " rows updated");
			  }
			  con.commit();
			  con.close();
			} catch(SQLException e) {
			  // process BatchUpdateException
			} 
		finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
```


## Assumption that were made while developing the APplication
- Always there will be two states STARTED and FINISHED for any Event in the JSON LOG
- JSON Log will be provided for a single execution of each events, i.e eventId 
  must be unique with just two different states
  
## Future Work
- ExecutorService can be used for creating ThreadPool and not spawning each thread only 
  at the time of need
- If there is need to store data of Events occuring multiple time code has to be modified 
  and either each Events can be saved in a blob or a surrogate key can be generated based on Business Logic to keep in track same event run multiple time 
  
