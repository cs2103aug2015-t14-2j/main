
package Task;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This class represents a task that the user adds in
 * 
 *  @author A0097689 Tan Si Kai
 *  @author A0009586 Jean Pierre Castillo
 *  @author A0118772  Audrey Tiah
 */

public class Task {
	// Helpers
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HHmm");
	
	// A task has these meta data
	private Date createdTime;
	private int  taskId;
	
	// A task has these properties
	private Period period          = null;
	private Date deadline          = null;
	private String venue           = null;
	private String description     = null;
	private Boolean isDone         = null;
	private Boolean isPastDeadline = null;
	private Boolean hasEnded       = null;
	
	/**
	 * Constructor for tasks without startTime, endTime and deadline. 
	 * To be called when user adds a new task.
	 * 
	 * @param taskId
	 * @param desc
	 */
	public Task (int taskId, String desc, String venue) {
		this.createdTime = new Date();
		this.taskId = taskId;
		
		this.period = null;
		this.deadline = null;
		this.venue = venue;
		this.description = desc;
		setFlags(false);
	}
	
	/**
	 * Constructor for tasks with startTime and endTime only.
	 * Used when user adds a new task
	 * 
	 * @param taskId
	 * @param desc
	 * @param startTime
	 * @param endTime
	 * @param venue
	 */
	public Task (int taskId, String desc, Date startTime, Date endTime, String venue) throws IllegalArgumentException{
		this.createdTime = new Date();
		this.taskId = taskId;
		
		try {
			this.period = new Period(startTime, endTime);
		} catch (IllegalArgumentException e) {
			this.period = null;
			throw e;
		}
		this.deadline = null;
		this.venue = venue;
		this.description = desc;
		setFlags(null);
	}
		
	/**
	 * Constructor for tasks with only deadline and desc.
	 * Used when user adds a new task
	 * 
	 * @param taskId
	 * @param desc
	 * @param deadline
	 * @param venue
	 */
	public Task (int taskId, String desc, Date deadline, String venue) {
		this.createdTime = new Date();
		this.taskId = taskId;
		
		this.period = null;
		this.deadline = deadline;
		this.venue = venue;
		this.description = desc;
		setFlags(false);
	}
	
	/**
	 * Constructor for tasks with startTime, endTime and/or deadline.
	 * To be used when reading from JSON file
	 * 
	 * @param createdTime
	 * @param lastModifiedTime
	 * @param taskId
	 * @param desc
	 * @param startTime
	 * @param endTime
	 * @param deadline
	 * @param venue
	 * @param isDone
	 * @param isPastDeadline
	 * @param hasEnded
	 */
	public Task (Date createdTime, int taskId, String desc, Date startTime, Date endTime, Date deadline, String venue, Boolean isDone) {
		this.createdTime = createdTime;
		this.taskId = taskId;
		
		if (startTime == null || endTime == null) {
			this.period = null;
		} else {
			try {
				this.period = new Period(startTime, endTime);			
			} catch (IllegalArgumentException e) {
				this.period = null;
				System.out.println("Invalid start and end time. Please specify a start time that is before end time.");
			}
		}
		this.deadline = deadline;
		this.venue = venue;
		this.description = desc;
		setFlags(isDone);
	}

	//To be used for tempory tasks
	public Task(int taskId, String desc, String venue, Date startTime, Date endTime, Date deadline, Boolean isDone, Boolean isPastDeadline, Boolean hasEnded) {
		this.createdTime = null;
		this.period = null;
		
		try {
			this.period = new Period(startTime, endTime);			
		} catch (IllegalArgumentException e) {
			this.period = null;
			System.out.println("Invalid start and end time for search");
		}
		
		this.taskId = taskId;
		this.deadline = deadline;
		this.venue = venue;
		this.description = desc;
		this.isDone = isDone;
		this.isPastDeadline = isPastDeadline;
		this.hasEnded = hasEnded;
	}

	public String toString() {
		String startTime;
		String endTime;
		String deadline;
		
		if (this.period == null || this.period.getStartDateTime() == null || this.period.getEndDateTime() == null) {
			startTime = null;
			endTime   = null;
		} else {
			startTime = dateFormat.format(this.period.getStartDateTime());
			endTime   = dateFormat.format(this.period.getEndDateTime());
		}
			
		if (this.deadline == null) {
			deadline = null;
		} else {
			deadline = dateFormat.format(this.getDeadline());
		}
		
		String result = "";
		result += "Task :\n";
		result += "   Task ID           : " + this.taskId + "\n";
		result += "   Description       : " + this.description + "\n";
		result += "   Start Time        : " + startTime + "\n";
		result += "   End Time          : " + endTime + "\n";
		result += "   Deadline          : " + deadline + "\n";
		result += "   Venue             : " + this.venue + "\n";
		result += "   Completed?        : " + this.isDone + "\n";
		result += "   Is Past Deadline? : " + this.isPastDeadline + "\n";
		result += "   Has Ended?        : " + this.hasEnded + "\n";
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Task)) {
			return false;
		}
		Task task = ((Task)obj);

		if (!bothNullOrEqual(this.period, task.period)) {
			return false;
		}
		if (!bothNullOrEqual(this.deadline, task.deadline)) {
			return false;
		}
		if (!bothNullOrEqual(this.venue, task.venue)) {
			return false;
		}
		if (!bothNullOrEqual(this.description, task.description)) {
			return false;
		}
		if (this.isDone != task.isDone) {
			return false;
		}
		if (this.isPastDeadline != task.isPastDeadline) {
			return false;
		}
		if (this.hasEnded != task.hasEnded) {
			return false;
		}
		
		return true;
	}
	
	// Utility method
	private static boolean bothNullOrEqual(Object x, Object y) {
		return ( x == null ? y == null : x.equals(y));
	}
	
	public void setFlags(Boolean _isDone) {
		if(this.getStartDateTime() == null || this.getEndDateTime() == null){
			setDone(_isDone);
		}
		if(this.getDeadline() != null){
			setPastDeadline();
		}
		if(this.getStartDateTime() != null && this.getEndDateTime() != null){
			setEnded();
		}
	}
	
	public void setPastDeadline () {
		this.isPastDeadline = isPastDeadline(new Date() , this.deadline);
	}
	
	public Boolean isPastDeadline (Date now, Date deadline) {
		if(now == null || deadline == null){
			return null;
		}
		return now.after(deadline);
	}
	
	public void setEnded () {
		this.hasEnded = hasEnded(new Date() , this.getEndDateTime());
	}

	public Boolean hasEnded ( Date now, Date endTime) {
		if(now == null || endTime == null){
			return null;
		}
		return now.after(endTime);
	}
	
	public Date getCreatedTime() {
		return this.createdTime;
	}

	public int getTaskId() {
		return this.taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Date getStartDateTime() {
		if (this.period == null) {
			return null;
		} else {
			return this.period.getStartDateTime();			
		}
	}

	public void setStartDateTime(Date startTime) {
		if (this.period == null) {
			// Assume default endTime one hour from startTime
			Date endTime = new Date(startTime.getTime() + (60L * 60L * 1000L));
			this.period = new Period(startTime, endTime);
		} else {			
			this.period.setStartDateTime(startTime);
		}
	}

	public Date getEndDateTime() {
		if (this.period == null) {
			return null;
		} else {
			return this.period.getEndDateTime();			
		}
	}

	public void setEndDateTime(Date endTime) {
		if (this.period == null) {
			// Assume default startTime one hour before startTime
			Date startTime = new Date(endTime.getTime() - (60L * 60L * 1000L));
			this.period = new Period(startTime, endTime);
		} else {
			this.period.setEndDateTime(endTime);			
		}
	}

	public Period getPeriod() {
		return this.period;
	}
	
	public void setPeriod(Period _period) {
		this.period = _period;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isDone() {
		return isDone;
	}

	public void setDone(Boolean isDone) {
		this.isDone = isDone;
	}

	public Boolean isPastDeadline() {
		return isPastDeadline;
	}

	// Determines if a deadline task is past deadline
	public Boolean determinePastDeadline() {
		if (this.deadline != null) {
			if (this.deadline.after(new Date())) {
				return false;
			} else {
				return true;
			}
		} else {
			return null;
		}
	}

	public void setPastDeadline(Boolean isPastDeadline) {
		this.isPastDeadline = isPastDeadline;
	}

	public Boolean isHasEnded() {
		return hasEnded;
	}

	public void setHasEnded(Boolean hasEnded) {
		this.hasEnded = hasEnded;
	}

	public boolean isFloating() {
		if (this.period == null && this.deadline == null) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isEmpty() {
		return
			taskId			== -1 				&&
			(period          == null			||
			(period.getEndDateTime() == null && 
			period.getStartDateTime() == null))	&&
			deadline   		== null				&&
			venue           == null				&&
			description     == null				&&
			isDone         	== null				&&
			isPastDeadline 	== null				&&
			hasEnded       	== null;
	}
}
