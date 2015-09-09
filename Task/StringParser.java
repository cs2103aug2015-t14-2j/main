package Task;

import java.util.Date;

public class StringParser {
	
	private boolean _canAddTask = false;
	
	private Task _task;
	
	public StringParser(COMMAND_TYPE command,String userInput){
		
	}
	
	// Validates user inputs for adding a task are correct and valid before calling addTask
	private static boolean validateAddTask(String desc, String startTime, String endTime, String deadline, String venue) {
		// Short circuit style, returns false on first failed validation
		// Limitation is user cannot see all wrong inputs
		if(!isValidDate(startTime)) {
			return false;
		}
		if(!isValidDate(endTime)) {
			return false;
		}
		if(!isValidDate(deadline)) {
			return false;
		}
		if(!isValidVenue(venue)) {
			// Google maps API validation
			return false;
		}
		
		return true;
	}
	
	private static boolean isValidDate(String dateString) {
		return true;
	}
	
	private static boolean isValidVenue(String venueString) {
		return true;
	}
	
	private static Date getCurrentDate(){
		return new Date();
	}
	
	public Task getTask(){
		return _task;
	}
	
	public boolean canAddTask(){
		return _canAddTask;
	}
	
}


