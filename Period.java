import java.util.Date;
import java.lang.IllegalArgumentException;

public class Period {
	private Date startTime;
	private Date endTime;
	
	public Period (Date startTime, Date endTime) throws IllegalArgumentException {
		if(startTime.after(endTime)) {
			throw new IllegalArgumentException("Start time is after end time!");
			
		}
		this.startTime = startTime;
		this.endTime   = endTime;
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
