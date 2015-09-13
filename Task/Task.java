
package Task;

import java.util.Date;
import java.util.ArrayList;
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
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, YYYY HH:mm:ss Z");
	
	// A task has these meta data
	private Date createdTime;
	private Date lastModifiedTime;
	
	// A task has these properties
	private Period period = null;
	private Date deadline = null;
	private String venue = null;
	private String description = null;
	private boolean isDone = false;
	private boolean isPastDeadline = false;
	private boolean hasEnded = false;
	private ArrayList<String> tags = new ArrayList<String>();
	
	// Constructor
	public Task (String desc) {
		this.description = desc;
		
	}
	
	public Task (){
		this.createdTime = new Date();
		this.lastModifiedTime = this.createdTime;
	}
	
	public Task (String desc, Date startTime, Date endTime, Date deadline, String venue) {
		this.createdTime = new Date();
		this.lastModifiedTime = this.createdTime;
		
		this.period = new Period(startTime, endTime);
		this.deadline = deadline;
		this.venue = venue;
		this.description = desc;
		this.isDone = false;
		this.isPastDeadline = isPastDeadline(this.createdTime, deadline);
		this.hasEnded = hasEnded(this.createdTime , endTime);
		this.tags = new ArrayList<String>();
		
	}
	
	public String toString() {
		String result = "";
		result += "Task :\n";
		result += "   Description    : " + this.description + "\n";
		result += "   Start Time     : " + dateFormat.format(this.period.getStartTime()) + "\n";
		result += "   End Time       : " + dateFormat.format(this.period.getEndTime()) + "\n";
		result += "   Venue          : " + this.venue + "\n";
		result += "   isDone         : " + this.isDone + "\n";
		result += "   isPastDeadline : " + this.isPastDeadline + "\n";
		result += "   hasEnded       : " + this.isPastDeadline + "\n";
		result += "   Tags           : " + this.tags.toString() + "\n";
		
		return result;
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
