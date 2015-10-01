package Task;

import java.util.Date;
import java.lang.IllegalArgumentException;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;

public class Period {
	private Date startTime;
	private Date endTime;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM, yyyy HHmm");
	
	public Period (Date startTime, Date endTime) throws IllegalArgumentException {
		if(startTime.after(endTime)) {
			throw new IllegalArgumentException("Start time is after end time!");
			
		}
		this.startTime = startTime;
		this.endTime   = endTime;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Period)) {
			return false;
		}
		
		Period period = ((Period) obj);
		if (!this.startTime.equals(period.startTime)) {
			return false;
		}
		if (!this.endTime.equals(period.endTime)) {
			return false;
		}
		
		return true;
	}
	
	public String toString() {
		return dateFormat.format(this.getStartTime()) + " - " + dateFormat.format(this.getEndTime()); 
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
