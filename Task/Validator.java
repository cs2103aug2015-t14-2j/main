package Task;

import java.util.HashMap;
import Task.TaskHandler.COMMAND_TYPE;

// This class deals with validation of all user input.
// It specifies which validation functions are required for which parameters.
// It runs through every validation function and sets the respective error message.
// Error messages should specify which parameter failed and why it failed.
public class Validator {
	// private and static variables here
	
	private static final String ERROR_INVALID_DATE_FORMAT = "";
	private static final String ERROR_INVALID_TIME_FORMAT = "";
	private static final String ERROR_INSUFFICIENT_ARGUMENT = "";
	private static final String ERROR_STARTDATE_AFTER_ENDDATE = "";
	/* ...ADD IN MORE HERE */
	
	// This is optional, you can implement using other ways you like
	enum PARAMETER {
		DESC,
		VENUE,
		START_DATE,
		END_DATE,
		START_TIME,
		END_TIME,
		DEADLINE,
		PRIORITY,
		REMIND_TIME
	};
	
	// Constructor
	public Validator () {
		
	}
	
	// Takes a HashMap returned from the Parser class and runs through each of the key => value pairs
	// For each value, run through each of the relevant validation functions. If fails, set the error message
	// for that particular validation function. 
	// Returns a HashMap of PARAMETER => error_message_string 
	public HashMap<PARAMETER, String> validateUserInput(COMMAND_TYPE command, HashMap<PARAMETER, String> parsedUserInput) {
		
		return new HashMap<PARAMETER, String>();
	}
	
	// Takes command and hashmap of user input and runs through to see if all the required parameters are present
	private boolean isValidMinimumArgumentForCommand (COMMAND_TYPE command, HashMap<PARAMETER, String> parsedUserInput) {
		return true;
	}
	
	/* Validate individual parameter values here */
	private boolean isValidDesc(String value) {
		return true;
	}
	
	private boolean isValidVenue(String value) {
		return true;
	}
	
	private boolean isValidStartDate(String value) {
		return true;
	}
	
	private boolean isValidEndDate(String value) {
		return true;
	}
	
	private boolean isValidStartTime(String value) {
		return true;
	}
	
	private boolean isValidEndTime(String value) {
		return true;
	}
	
	private boolean isValidDeadline(String value) {
		return true;
	}
	
	private boolean isValidPriority(String value) {
		return true;
	}
	
	private boolean isValidRemindTime(String value) {
		return true;
	}
	
	/* Lowest level functions here. 
	 * Validate string, integer, Date fields here.
	 * Do not feel constrained by the function definitions.
	 * You can change them or add in more parameters, change types, or add new functions as you deem fit.
	 */
	private boolean isValidString(String value) {
		return true;
	}
	
	private boolean isValidDateFormat(String value) {
		return true;
	}
	
	private boolean isValidTimeFormat(String value) {
		return true;
	}
	
	private boolean isValidPeriod(String startTime, String endTime) {
		return true;
	}
}
