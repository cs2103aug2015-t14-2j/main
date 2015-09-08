package Task;

import java.util.Date;
import java.lang.IllegalArgumentException;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;

public class Period {
	private Date startTime;
	private Date endTime;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, YYYY HH:mm:ss Z");
	
	public Period (Date startTime, Date endTime) throws IllegalArgumentException {
		if(startTime.after(endTime)) {
			throw new IllegalArgumentException("Start time is after end time!");
			
		}
		this.startTime = startTime;
		this.endTime   = endTime;
	}
	
	public String toString(Period period) {
		return dateFormat.format(period.getStartTime()) + " - " + dateFormat.format(period.getEndTime()); 
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	
}
