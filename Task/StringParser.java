package Task;

import java.util.Date;

public class StringParser {
	
	/**
	 * The task made up of the parsed string
	 */
	private Task _task = null;
	
	/**
	 * Initiates the parser and parses the userInput based on the type of command
	 * @param command The type of command to be executed
	 * @param userInput
	 */
	public StringParser(COMMAND_TYPE command,String userInput){
		
		switch (command) {
		case ADD_TASK:
			String[] keywordsInInput={"do","on","from","to","by","on","at","with","remind","#"};
			addAttributesToTask(keywordsInInput);
		case GET_TASK:
			
		case DISPLAY:
			
		case SEARCH_TASK:
			
		case EDIT_TASK:
						
		default:
			
		}
	}

	private void addAttributesToTask(String[] keywordsInInput) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Validates user inputs for adding a task are correct and valid before calling addTask
	 * @param userInput the string 
	 * @return
	 */
	private static boolean validateAddTask(String[] userInput) {
		// Short circuit style, returns false on first failed validation
		// Limitation is user cannot see all wrong inputs
		
		return true;
	}
	
	/**
	 * Validates a date in many formats
	 * @param dateString the string containing a date
	 * @return If the string inputed is a recognized date
	 */
	private static boolean isValidDate(String dateString) {
		
		return true;
	}
	
	/**
	 * returns today's date. Used to extrapolate the date the user refers to when parsing input
	 * @return Today's date
	 */
	private static Date getCurrentDate(){
		return new Date();
	}
	
	/**
	 * Gets the synthesized task
	 * @return The task that was made
	 */
	public Task getTask(){
		return _task;
	}
	
	/**
	 * Used to verify if a task can be added; ie. does it have at least a description
	 * @return A boolean representing a task's ability to be added to a task collection
	 */
	public boolean canAddTask(){
		return (_task == null) && (_task.getDescription() != null);
	}
	
}


