package assignment.Dto;

/**
 * This DTO is used collect data in a format to make it ready for persisting
 * 
 * @author ARNAV
 *
 */
public class InMemoryEventsDto {
	private String id;
	private String type;
	private String host;
	private long startTime;
	private long endTime;
	private int eventDuration;
	private boolean alert;

	public InMemoryEventsDto(String id, String type, String host, int eventDuration, boolean alert) {
		super();
		this.id = id;
		this.type = type;
		this.host = host;
		this.eventDuration = eventDuration;
		this.alert = alert;
	}

	public InMemoryEventsDto(String id, String type, String host, long startTime, long endTime, int eventDuration,
			boolean alert) {
		super();
		this.id = id;
		this.type = type;
		this.host = host;
		this.startTime = startTime;
		this.endTime = endTime;
		this.eventDuration = eventDuration;
		this.alert = alert;
	}

	public InMemoryEventsDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getEventDuration() {
		return eventDuration;
	}

	public void setEventDuration(int eventDuration) {
		this.eventDuration = eventDuration;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	@Override
	public String toString() {
		return "InMemoryDto [id=" + id + ", type=" + type + ", host=" + host + ", startTime=" + startTime + ", endTime="
				+ endTime + ", eventDuration=" + eventDuration + ", alert=" + alert + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (alert ? 1231 : 1237);
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + eventDuration;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InMemoryEventsDto other = (InMemoryEventsDto) obj;
		if (alert != other.alert)
			return false;
		if (endTime != other.endTime)
			return false;
		if (eventDuration != other.eventDuration)
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (startTime != other.startTime)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
