/*
 * @author A0097689 Tan Si Kai
 * This class represents a task that the user adds in
 */

package Task;

import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class Task {
	// Helpers
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM, yyyy HHmm");
	
	// A task has these meta data
	private Date createdTime;
	private Date lastModifiedTime;
	private int  taskId;
	
	// A task has these properties
	private Period period;
	private Date deadline;
	private String venue;
	private PRIORITY priority;
	private String description;
	private boolean isDone;
	private boolean isPastDeadline;
	private boolean hasEnded;
	private ArrayList<String> tags = new ArrayList<String>();
	
	// These are the possible priority levels
	enum PRIORITY {
		LOW, MEDIUM, HIGH;
		
		public String toString(PRIORITY priority) {
			switch (priority) {
				case LOW:
					return "low";
				case MEDIUM:
					return "medium";
				case HIGH:
					return "high";
				default:
					return "ERROR";
			}
		}
	};
	
	/**
	 * Constructor for tasks without startTime, endTime and deadline. 
	 * To be called when user adds a new task.
	 * 
	 * @param taskId
	 * @param desc
	 */
	public Task (int taskId, String desc) {
		this.createdTime = new Date();
		this.lastModifiedTime = this.createdTime;
		this.taskId = taskId;
		
		this.period = null;
		this.deadline = null;
		this.venue = null;
		this.description = desc;
		this.isDone = false;
		this.isPastDeadline = false;
		this.hasEnded = false;
		this.tags = new ArrayList<String>();
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
	 * @param tags
	 */
	public Task (int taskId, String desc, Date startTime, Date endTime, String venue, ArrayList<String> tags) {
		this.createdTime = new Date();
		this.lastModifiedTime = this.createdTime;
		this.taskId = taskId;
		
		this.period = new Period(startTime, endTime);
		this.deadline = null;
		this.venue = venue;
		this.description = desc;
		this.isDone = false;
		this.isPastDeadline = false;
		this.hasEnded = hasEnded(this.createdTime , endTime);
		this.tags = tags;
	}
	
	/**
	 * Constructor for tasks with only deadline.
	 * Used when user adds a new task
	 * 
	 * @param taskId
	 * @param desc
	 * @param deadline
	 * @param venue
	 * @param tags
	 */
	public Task (int taskId, String desc, Date deadline, String venue, ArrayList<String> tags) {
		this.createdTime = new Date();
		this.lastModifiedTime = this.createdTime;
		this.taskId = taskId;
		
		this.period = null;
		this.deadline = deadline;
		this.venue = venue;
		this.description = desc;
		this.isDone = false;
		this.isPastDeadline = isPastDeadline(this.createdTime, deadline);
		this.hasEnded = false;
		this.tags = tags;
	}
	
	/**
	 * Constructor for tasks with startTime, endTime and deadline.
	 * Used when user adds a new task. 
	 * 
	 * @param taskId
	 * @param desc
	 * @param startTime
	 * @param endTime
	 * @param deadline
	 * @param venue
	 * @param tags
	 */
	public Task (int taskId, String desc, Date startTime, Date endTime, Date deadline, String venue, ArrayList<String> tags) {
		this.createdTime = new Date();
		this.lastModifiedTime = this.createdTime;
		this.taskId = taskId;
		
		this.period = new Period(startTime, endTime);
		this.deadline = deadline;
		this.venue = venue;
		this.description = desc;
		this.isDone = false;
		this.isPastDeadline = isPastDeadline(this.createdTime, deadline);
		this.hasEnded = hasEnded(this.createdTime , endTime);
		this.tags = tags;
		
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
	 * @param tags
	 */
	public Task (Date createdTime, Date lastModifiedTime, int taskId, String desc, Date startTime, Date endTime, Date deadline, String venue, boolean isDone, boolean isPastDeadline, boolean hasEnded, ArrayList<String> tags) {
		this.createdTime = createdTime;
		this.lastModifiedTime = lastModifiedTime;
		this.taskId = taskId;
		
		if (startTime == null && endTime == null) {
			this.period = null;
		} else {
			this.period = new Period(startTime, endTime);			
		}
		this.deadline = deadline;
		this.venue = venue;
		this.description = desc;
		this.isDone = isDone;
		this.isPastDeadline = isPastDeadline;
		this.hasEnded = hasEnded;
		this.tags = tags;
	}
	
	public String toString() {
		String startTime;
		String endTime;
		String deadline;
		
		if (this.period == null) {
			startTime = null;
			endTime   = null;
		} else {
			startTime = dateFormat.format(this.period.getStartTime());
			endTime   = dateFormat.format(this.period.getEndTime());
		}
			
		if (this.deadline == null) {
			deadline = null;
		} else {
			deadline = dateFormat.format(this.getDeadline());
		}
		
		String result = "";
		result += "Task :\n";
		result += "   Task ID        : " + this.taskId + "\n";
		result += "   Description    : " + this.description + "\n";
		result += "   Start Time     : " + startTime + "\n";
		result += "   End Time       : " + endTime + "\n";
		result += "   Deadline       : " + deadline + "\n";
//		result += "   Priority       : " + this.priority.toString() + "\n";
		result += "   Venue          : " + this.venue + "\n";
		result += "   isDone         : " + this.isDone + "\n";
		result += "   isPastDeadline : " + this.isPastDeadline + "\n";
		result += "   hasEnded       : " + this.isPastDeadline + "\n";
		result += "   Tags           : " + this.tags.toString() + "\n";
		
		return result;
	}
	
	public boolean equals(Task task) {
		if (!this.period.equals(task.period)) {
			return false;
		}
		if (!this.deadline.equals(task.deadline)) {
			return false;
		}
		if (!this.venue.equals(task.venue)) {
			return false;
		}
		if (!this.description.equals(task.description)) {
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
		// Hashtags are not compared
		
		return true;
	}
	
	public boolean isPastDeadline (Date now, Date deadline) {
		return true;
	}

	public boolean hasEnded ( Date now, Date endTime) {
		return true;
	}
	
	public void addTag(String tag) {
		
	}
	
	public void removeTag(String tag) {
		
	}
	
	public Date getCreatedTime() {
		return this.createdTime;
	}

	public Date getModifiedTime() {
		return this.lastModifiedTime;
	}

	public void setModifiedTime(Date time) {
		this.lastModifiedTime = time;
	}

	public int getTaskId() {
		return this.taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Date getStartTime() {
		return this.period.getStartTime();
	}

	public void setStartTime(Date startTime) {
		this.period.setStartTime(startTime);
	}

	public Date getEndTime() {
		return this.period.getEndTime();
	}

	public void setEndTime(Date endTime) {
		this.period.setEndTime(endTime);
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

	public PRIORITY getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		if(priority.equalsIgnoreCase("high")) {
			this.priority = PRIORITY.HIGH;
		} else if (priority.equalsIgnoreCase("medium")) {
			this.priority = PRIORITY.MEDIUM;
		} else if (priority.equalsIgnoreCase("low")) {
			this.priority = PRIORITY.LOW;
		} else {
			throw new Error("Invalid priority parameter!");
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public boolean isPastDeadline() {
		return isPastDeadline;
	}

	public void setPastDeadline(boolean isPastDeadline) {
		this.isPastDeadline = isPastDeadline;
	}

	public boolean isHasEnded() {
		return hasEnded;
	}

	public void setHasEnded(boolean hasEnded) {
		this.hasEnded = hasEnded;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
}
