package Task;

import java.util.Date;
import java.lang.IllegalArgumentException;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;

public class Period {
	private Date startDateTime;
	private Date endDateTime;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YY HHmm");
	
	public Period (Date startDateTime, Date endDateTime) throws IllegalArgumentException {
		if(startDateTime.after(endDateTime)) {
			throw new IllegalArgumentException("Start time is after end time!");
			
		}
		this.startDateTime = startDateTime;
		this.endDateTime   = endDateTime;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Period)) {
			return false;
		}
		
		Period period = ((Period) obj);
		if (!this.startDateTime.equals(period.startDateTime)) {
			return false;
		}
		if (!this.endDateTime.equals(period.endDateTime)) {
			return false;
		}
		
		return true;
	}
	
	public String toString() {
		return dateFormat.format(this.getStartDateTime()) + " - " + dateFormat.format(this.getEndDateTime()); 
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}
}
