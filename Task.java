/*
 * @author A0097689 Tan Si Kai
 * This class represents a task that the user adds in
 */
import java.util.Date;
import java.util.ArrayList;

public class Task {
	// A task has these meta data
	private Date createdTime;
	private Date lastModifiedTime;
	
	// A task has these properties
	private Date startTime;
	private Date endTime;
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
		LOW, MEDIUM, HIGH
	};
	
	// Constructor
	public Task (Date startTime, Date endTime, Date deadline, String venue, PRIORITY priority, String desc) {
		this.createdTime = new Date();
		this.lastModifiedTime = this.createdTime;
		
		this.startTime = startTime;
		this.endTime = endTime;
		this.deadline = deadline;
		this.venue = venue;
		this.priority = priority;
		this.description = desc;
		this.isDone = false;
		this.isPastDeadline = isPastDeadline(this.createdTime, deadline);
		this.hasEnded = hasEnded(this.createdTime , endTime);
		this.tags = new ArrayList<String>();
		
		
		
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

	public void setPriority(PRIORITY priority) {
		this.priority = priority;
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
