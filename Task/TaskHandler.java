package Task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *  Represents the handler for tasks
 * 
 *  @author A0097689 Tan Si Kai
 *  @author A0009586 Jean Pierre Castillo
 *  @author A0118772  Audrey Tiah
 */
public class TaskHandler {
	// Define success messages here
	private static final String MESSAGE_WELCOME        = "Welcome to TaskBuddy!";
	private static final String MESSAGE_ADD_TASK       = "Successfully added task.";
	private static final String MESSAGE_GET_TASK       = "Task returned";
	private static final String MESSAGE_DISPLAY        = "All tasks displayed.";
	private static final String MESSAGE_SEARCH_TASK    = "Here are tasks matching your keywords:";
	private static final String MESSAGE_EDIT_TASK      = "Choose the task you want to edit";
	private static final String MESSAGE_EXIT           = "Thanks for using TaskBuddy! Changes saved to disk.";
	
	// Define error messages here
	private static final String ERROR_INVALID_COMMAND  = "Invalid Command.";
	private static final String ERROR_INVALID_DATETIME = "Invalid time specified. Please follow this format: 16 Aug 2015, 16:20:00";
	private static final String ERROR_EMPTY_TASKLIST   = "You have no tasks!";
	
	
	// Define help messages here
	private static final String HELP_TITLE             = "********************************************Help menu for TaskBuddy!****************************************************";
	private static final String HELP_SUBTITLE          = "[COMMAND]     [FORMAT]                                                                             [DESCRIPTION]";
	private static final String HELP_ADD_TASK          = "  1.ADD       : add    [description], [starttime], [endtime], [deadline], [venue], [priority]      |";
	private static final String HELP_GET_TASK          = "  2.GET       : get    [task-id]                                                                   |";
	private static final String HELP_DISPLAY           = "  3.DISPLAY   : display                                                                            |";
	private static final String HELP_SEARCH_TASK       = "  4.SEARCH    : search [value1=keyword1], [value2=keyword2],...                                    |";
	private static final String HELP_EDIT_TASK         = "  5.EDIT      : edit   [task-id], [value1=keyword1]                                                |";
	private static final String HELP_EXIT              = "  6.EXIT      : exit   []                                                                          |Terminate program";
	
	
	// Define minimum argument numbers here
	//TODO: Do these make sense to have now?
	private static final int NUM_ARGS_ADD_TASK     = 6;
	private static final int NUM_ARGS_GET_TASK     = 3;
	private static final int NUM_ARGS_DISPLAY_TASK = 0;
	private static final int NUM_ARGS_SEARCH_TASK  = 1;
	private static final int NUM_ARGS_EDIT_TASK    = 1;
	
	private static Scanner         		scanner         = new Scanner(System.in);
	private static Calendar        		calendar        = Calendar.getInstance();
	private static SimpleDateFormat 	dateFormat      = new SimpleDateFormat("HH:mm:ss");
	private static ArrayList<Task> 		taskList        = new ArrayList<Task>(50);
	private static ArrayList<Period> 	timetable       = new ArrayList<Period>(50);			// timetable that keeps track of startTime and endTime of tasks
	private static LinkedList<String> 	commandHistory 	= new LinkedList<String>();	// stack of userInputs history to implement undo action
	
	/**
	 * Starts the 
	 * @param args The file path to load the file in
	 */
	public static void main(String[] args) {
		// TODO @Audrey use args to load the file
		init();
		showToUser(MESSAGE_WELCOME);
		while(true) {
			System.out.print("> Enter command:");
			String command = scanner.nextLine();
			if(isValidCommand(command)) {
				String feedback = executeCommand(command);				
				showToUser(feedback);
			}			
		}
	}
	
	/**
	 * Initialize settings, search for application files etc.
	 */
	private static void init() {
		dateFormat.setCalendar(calendar);
		//System.out.println("current time is " + dateFormat.getCalendar().get(1));
		
	}
	
	/**
	 * Displays text to user, do not print if empty string
	 * @param text Text to show the user
	 */
	private static void showToUser(String text) {
		if(text.isEmpty()) {
			return;
		}
		
		System.out.println(text);
	}

	/**
	 * Pattern matching on expected command patterns to decide if it is a valid command
	 * @param userInput
	 * @return
	 */
	public static boolean isValidCommand(String userInput) {
		if(userInput.length() != 0 && userInput.split(" ").length > 1) {
			return true;
		} else {
			showHelpMenu();
			return false;
		}
	}
	
	/**
	 * Executes user input
	 * @param userInput The input to be executed
	 * @return The feedback to give to the user
	 */
	public static String executeCommand(String userInput) {
		COMMAND_TYPE command = determineCommandType(getFirstWord(userInput));
		StringParser parser = new StringParser();
		HashMap<PARAMETER, String> parsedParamTable;
		switch (command) {
			case ADD_TASK:
				parsedParamTable = parser.getValuesFromInput(command, removeFirstWord(userInput));
				//TODO: shouldn't it be if it has a description?
				boolean enoughParameters = parsedParamTable.size() >= NUM_ARGS_ADD_TASK ? true : false;
				if (enoughParameters) {
					boolean canAddTask  = validateAddTask(parsedParamTable.get(PARAMETER.DESC),
															parsedParamTable.get(PARAMETER.VENUE), 
															parsedParamTable.get(PARAMETER.START_DATE),
															parsedParamTable.get(PARAMETER.END_DATE), 
															parsedParamTable.get(PARAMETER.START_TIME),
															parsedParamTable.get(PARAMETER.END_TIME),
															parsedParamTable.get(PARAMETER.DEADLINE_DATE),
															parsedParamTable.get(PARAMETER.DEADLINE_TIME),
															parsedParamTable.get(PARAMETER.REMIND_TIMES));
					if (canAddTask) {
						addTask(parsedParamTable.get(PARAMETER.DESC),
								parsedParamTable.get(PARAMETER.VENUE), 
								parsedParamTable.get(PARAMETER.START_DATE),
								parsedParamTable.get(PARAMETER.END_DATE), 
								parsedParamTable.get(PARAMETER.START_TIME),
								parsedParamTable.get(PARAMETER.END_TIME),
								parsedParamTable.get(PARAMETER.DEADLINE_DATE),
								parsedParamTable.get(PARAMETER.DEADLINE_TIME),
								parsedParamTable.get(PARAMETER.REMIND_TIMES));
					}					
				} else {
					showHelpMenu();
					return ""; 
				}
				return MESSAGE_ADD_TASK;
			case GET_TASK:
				return MESSAGE_GET_TASK;
			case DISPLAY:
				if (taskList.isEmpty()) {
					return ERROR_EMPTY_TASKLIST;
				} else {
					displayAllTasks();
					return MESSAGE_DISPLAY;					
				}
			case SEARCH_TASK:
				return MESSAGE_SEARCH_TASK;
			case EDIT_TASK:
				return MESSAGE_EDIT_TASK;
			case INVALID_COMMAND:
				showHelpMenu();
				return "";
			case EXIT:
				showToUser(MESSAGE_EXIT);
				System.exit(0);
			default:
				return "There is an error in your code.";
		
		}
	}
	
	/**
	 * Adds a task to the task list
	 * @param task The task to be added to the taskList
	 */
	private static void addTask(String desc,String venue, String startDate, String endDate, String startTime, String endTime, String deadlineDate, String deadlineTime, String remindTimes) {
		try {
			Date _startDate    = dateFormat.parse(startTime);
			Date _endDate      = dateFormat.parse(endTime);
			Date _deadlineTime = dateFormat.parse(deadlineTime);
			
			//TODO: Modify task to match enum of params
			Task task = new Task(desc, _startDate, _endDate, _deadlineTime, venue);
			System.out.println(task.toString());
			taskList.add(task);
			
		} catch (ParseException e) {			
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Remove a specific task from the file
	 * @param task The task to be deleted from the taskList
	 */
	private static void removeTask(Task task) {
		// TODO remove the task from tasklist
	}
	
	/**
	 * 
	 * @param desc
	 * @param startTime
	 * @param endTime
	 * @param deadline
	 * @param venue
	 * @return
	 */
	private static boolean validateAddTask(String desc,String venue, String startDate, String endDate, String startTime, String endTime, String deadlineDate, String deadlineTime, String remindTimes) {
		// Short circuit style, returns false on first failed validation
		// Limitation is user cannot see all wrong inputs
		if(!isValidDate(startDate)) {
			return false;
		}
		if(!isValidDate(endDate)) {
			return false;
		}
		if(!isValidDate(deadlineDate)) {
			return false;
		}
		if(!isValidTime(startTime)) {
			return false;
		}
		if(!isValidTime(endTime)) {
			return false;
		}
		if(!isValidTime(deadlineTime)) {
			return false;
		}
		//TODO: needed?
		/*if(!isValidVenue(venue)) {
			
			// Google maps API validation
			return false;
		}*/
		
		return true;
	}
	
	/**
	 * Validates a date
	 * @param dateString The date to be validated
	 * @return A boolean representing if the date in question is valid
	 */
	private static boolean isValidDate(String dateString) {
		return true;
	}
	
	/**
	 * Validates a time
	 * @param timeString The time to be validated
	 * @return A boolean representing if the time in question is valid
	 */
	private static boolean isValidTime(String timeString) {
		return true;
	}
	
	/**
	 * Shows the Help menu to the user
	 */
	private static void showHelpMenu() {
		showToUser(ERROR_INVALID_COMMAND + "\n");
		showToUser(HELP_TITLE);
		showToUser(HELP_SUBTITLE);
		showToUser(HELP_ADD_TASK);
		showToUser(HELP_GET_TASK);
		showToUser(HELP_DISPLAY);
		showToUser(HELP_SEARCH_TASK);
		showToUser(HELP_EDIT_TASK);
		showToUser(HELP_EXIT);
	}
	
	/**
	 * Displays all the current tasks in the taskList
	 */
	private static void displayAllTasks() {
		for (int i = 0; i < taskList.size() ; i++) {
			showToUser(taskList.get(i).toString());
		}
	}
	
	/**
	 * Takes a command and returns the correct number of arguments expected
	 * @param command The command to be evaluated for number of arguments
	 * @return The number of arguments for that command
	 */
	public static int determineNumberOfArgs(COMMAND_TYPE command) {
		//TODO: unimplemented
		return 0;
	}
	
	/**
	 * Figure out free time slots
	 * @param timetable The timetable to get free time slots for
	 * @return An array of possible time slots
	 */
	public static ArrayList<Period> getFreeTimeSlots(ArrayList<Period> timetable) {
		ArrayList<Period> result = new ArrayList<Period>(50);
		
		return result;
	}
	
	/**
	 * Given a tag, and startTime and endTime, return all tasks with that tag
	 * @param tag 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static ArrayList<Task> getByTag (String tag, Date startTime, Date endTime) {
		return taskList;
	}
	
	/**
	 * Given a search keyword, and startTime and endTime, return all tasks with that keyword in their description within the search space
	 * @param keyword
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	//TODO:Delete?
	/*public static ArrayList<Task> searchByDescription (String keyword, Date startTime, Date endTime) {
		return taskList;
	}*/
	
	public ArrayList<Task> searchTasks(Task task){
		//TODO:Unimplemented
		return null;
	}
	
	/**
	 * Takes a single word and figure out the command
	 * @param commandTypeString The string containing a command
	 * @return The enum value corresponding to the commandTypeString
	 */
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if (commandTypeString == null) {
			throw new Error("command type string cannot be null!");
		}
		
		if(commandTypeString.equalsIgnoreCase("add")) {
			return COMMAND_TYPE.ADD_TASK;
		} else if (commandTypeString.equalsIgnoreCase("get")) {
			return COMMAND_TYPE.GET_TASK;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMAND_TYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return COMMAND_TYPE.SEARCH_TASK;
		} else if (commandTypeString.equalsIgnoreCase("edit")) {
			return COMMAND_TYPE.EDIT_TASK;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.INVALID_COMMAND;
		}
	}
	
	/**
	 * Gets the first word from a string
	 * @param userCommand The string containing one or more words
	 * @return The first word in the string
	 */
	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
	
	/**
	 * Removes the first word from a string
	 * @param userCommand The string to be split
	 * @return The original string without the first word
	 */
	private static String removeFirstWord(String userCommand) {
		String[] parameters = userCommand.trim().split(" ", 2);
		return parameters[1];
	}

}