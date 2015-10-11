package Task;

import Task.FileIO;
import Task.COMMAND_TYPE;
import Task.StringParser;
import Task.Validator;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;
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
	private static final String MESSAGE_DELETE_TASK    = "Task has been deleted";
	private static final String MESSAGE_EDIT_TASK      = "Task has been updated!";
	private static final String MESSAGE_EXIT           = "Thanks for using TaskBuddy! Changes saved to disk.";
	
	// Define error messages here
	private static final String ERROR_INVALID_COMMAND  = "Invalid Command.";
	private static final String ERROR_EMPTY_TASKLIST   = "You have no tasks!";
	private static final String ERROR_NOT_FOUND_TASK   = "The task was not found!";
	private static final String ERROR_IO_TASK   	   = "The task could not be changed!";
	private static final String ERROR_DATEFORMAT       = "The date and/or time you have entered is invalid. Date format is 'dd/M/yyyy' while time is 24 hrs 'HHmm e.g. 2359";
	
	// Define help messages here
	private static final String HELP_TITLE             = "****************************************************************************Help menu for TaskBuddy!*********************************************************************************************";
	private static final String HELP_SUBTITLE          = "[COMMAND]   [FORMAT]                                                                                                                                    [DESCRIPTION]                            ";
	private static final String HELP_ADD_TASK          = "  ADD       : add    do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"              | Adds a floating task, event or deadline";
	private static final String HELP_DISPLAY           = "  DISPLAY   : display                                                                                                                                   | Displays all tasks                     ";
	// private static final String HELP_SEARCH_TASK       = "  SEARCH    : search [value1=keyword1], [value2=keyword2],...                                    |";
	private static final String HELP_EDIT_TASK         = "  EDIT      : edit [task-id] do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"      | Edits an existing task                 ";
	private static final String HELP_DELETE_TASK       = "  DELETE    : delete [task-id]                                                                                                                          | Removes a task                         ";
	private static final String HELP_EXIT              = "  EXIT      : exit                                                                                                                                      | Terminate program                      ";
	
	
	// Define minimum argument numbers here
	//TODO: Do these make sense to have now?
	private static final int NUM_ARGS_ADD_TASK     = 6;
	private static final int NUM_ARGS_GET_TASK     = 3;
	private static final int NUM_ARGS_DISPLAY_TASK = 0;
	private static final int NUM_ARGS_SEARCH_TASK  = 1;
	private static final int NUM_ARGS_EDIT_TASK    = 1;
	
	private static Scanner         scanner           = new Scanner(System.in);
	private static Calendar        calendar          = Calendar.getInstance();
	private static SimpleDateFormat dateFormat       = new SimpleDateFormat("dd/M/yyyy HHmm");
	private static ArrayList<Task> taskList          = new ArrayList<Task>(50);
	private static ArrayList<Period> timetable       = new ArrayList<Period>(50);			// timetable that keeps track of startTime and endTime of tasks
	private static LinkedList<String> commandHistory = new LinkedList<String>();	// stack of userInputs history to implement undo action
	private static String 			filePath	     = "./data/calendar.json";		// relative path to calendar.json 
	private static int 				currentTaskId;          
	private static FileIO           fileIO;
	private static Validator        validate         = new Validator();
	
	/**
	 * Starts the program
	 * @param args The file path to load the file in
	 */
	public static void main(String[] args) {
		// TODO @Audrey use args to load the file
		String localFilePath = determineFilePath(args);
		init(localFilePath);
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
	
	public static void setFilePath(String localFilePath) {
		fileIO = new FileIO(localFilePath);
	}

	private static String determineFilePath(String[] args) {
		String localFilePath;
		if (args.length == 1) {
			localFilePath = args[0];
		} else {
			localFilePath = TaskHandler.filePath;
		}
		return localFilePath;
	}

	/**
	 * Initialize settings, search for application files etc.
	 */
	private static void init(String localFilePath) {
		dateFormat.setCalendar(calendar);
		fileIO = new FileIO(localFilePath);
		taskList = fileIO.readFromFile();
		currentTaskId = fileIO.getCurrentTaskId();
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
		if(userInput.length() != 0) {
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
		HashMap<PARAMETER, String> parsedParamTable;
		String message;
		switch (command) {
			case ADD_TASK:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
				//TODO: shouldn't it be if it has a description?
				if (parsedParamTable.get(PARAMETER.DESC) != null) {
					message = addTask(parsedParamTable.get(PARAMETER.DESC),
							parsedParamTable.get(PARAMETER.VENUE), 
							parsedParamTable.get(PARAMETER.START_DATE),
							parsedParamTable.get(PARAMETER.END_DATE), 
							parsedParamTable.get(PARAMETER.START_TIME),
							parsedParamTable.get(PARAMETER.END_TIME),
							parsedParamTable.get(PARAMETER.DEADLINE_DATE),
							parsedParamTable.get(PARAMETER.DEADLINE_TIME));
					
					fileIO.writeToFile(taskList);
					return message;
				} else {
					return ERROR_INVALID_COMMAND + "\n" + HELP_ADD_TASK; 
				}
			case DISPLAY:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
				if (taskList.isEmpty()) {
					return ERROR_EMPTY_TASKLIST;
				} else {
					parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
					displayTask(parsedParamTable.get(PARAMETER.TASKID));
					
					return MESSAGE_DISPLAY;					
				}
			case EDIT_TASK:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));

				message = editTask(parsedParamTable.get(PARAMETER.TASKID),
						parsedParamTable.get(PARAMETER.DESC),
						parsedParamTable.get(PARAMETER.VENUE), 
						parsedParamTable.get(PARAMETER.START_DATE),
						parsedParamTable.get(PARAMETER.END_DATE), 
						parsedParamTable.get(PARAMETER.START_TIME),
						parsedParamTable.get(PARAMETER.END_TIME),
						parsedParamTable.get(PARAMETER.DEADLINE_DATE),
						parsedParamTable.get(PARAMETER.DEADLINE_TIME));
				
				fileIO.writeToFile(taskList);
				return message;
			case DELETE_TASK:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
				message = removeTask(parsedParamTable.get(PARAMETER.TASKID));
				fileIO.writeToFile(taskList);
				return message;
			case INVALID_COMMAND:
				showHelpMenu();
				return "";
			case EXIT:
				showToUser(MESSAGE_EXIT);
				fileIO.writeToFile(taskList);
				System.exit(0);
			default:
				return "There is an error in your code.";
		
		}
	}

	/**
	 * Adds a task to the task list
	 * @param task The task to be added to the taskList
	 * @return 
	 */
	private static String editTask(String stringID, String desc,String venue, String startDate, String endDate, String startTime, String endTime, String deadlineDate, String deadlineTime) {
		// Declare local variables
		SimpleDateFormat timeFormat      = new SimpleDateFormat("HHmm");
		SimpleDateFormat localDateFormat = new SimpleDateFormat("dd/M/yyyy");

		Task task          = null;
		Date _startDate    = null;
		Date _endDate      = null;
		Date _deadlineDate = null;
		Date prevStartDate;
		Date prevEndDate;
		Date prevDeadlineDate;
		
		String prevStartTime;
		String prevEndTime;
		String prevDeadlineTime;

		boolean isUpdated = false;
		
		if (stringID != null){
			task = searchTasks(Integer.parseInt(stringID));
			if (task == null) {
				return ERROR_NOT_FOUND_TASK;
			}
		} else {
			return ERROR_NOT_FOUND_TASK;
		}
		
		// Set description
		if (desc != null){
			task.setDescription(desc);
			isUpdated = true;
		}
		
		// Set venue
		if (venue != null){
			task.setVenue(venue);
			isUpdated = true;
		}

		try {
			// Set startDate, startTime, endDate and endTime
			// E.g. edit 2 from 12/10/15 1200 to 13/10/15 1400
			// E.g. edit 2 on 12/10/15 from 1200 to 1400
			if (startDate != null && startTime != null && endDate != null && endTime != null){
				_startDate = dateFormat.parse(startDate + " " + startTime);
				_endDate   = dateFormat.parse(endDate + " " + endTime);
				task.setStartTime(_startDate);
				task.setEndTime(_endDate);
				isUpdated  = true;
			}

			// Set startDate and endDate, without changing startTime and endTime
			// E.g. edit 2 on 12/10/15
			if (startDate != null && startTime == null && endDate != null && endTime == null) {
				prevStartDate = task.getStartTime();
				prevEndDate   = task.getEndTime();
				// Get the original time of the day for start and end of the event
				prevStartTime = timeFormat.format(prevStartDate);
				prevEndTime   = timeFormat.format(prevEndDate);
				
				_startDate    = dateFormat.parse(startDate + " " + prevStartTime);
				_endDate      = dateFormat.parse(endDate + " " + prevEndTime);
				
				task.setStartTime(_startDate);
				task.setEndTime(_endDate);
				isUpdated     = true;
			}
			
			// Set startDate, startTime
			// E.g. edit 2 from 12/10/15 1200
			if (startDate != null && startTime != null && endDate == null && endTime == null) {
				_startDate = dateFormat.parse(startDate + " " + startTime);
				
				task.setStartTime(_startDate);
				isUpdated  = true;
			}

			// Set startDate, startTime according to user; endDate, endTime remains unchanged
			// E.g. edit 2 on 12/10/15 from 1400
			if (startDate != null && startTime != null && endDate != null && endTime == null) {
				_startDate = dateFormat.parse(startDate + " " + startTime);
				
				task.setStartTime(_startDate);
				isUpdated  = true;
			}
			
			// Set startDate only
			// E.g. edit 2 from 12/10/15
			if (startDate != null && startTime == null && endDate == null && endTime == null) {
				prevStartDate = task.getStartTime();
				// Get the original time of the day for start of the event
				prevStartTime = timeFormat.format(prevStartDate);
				
				_startDate    = dateFormat.parse(startDate + " " + prevStartTime);
				
				task.setStartTime(_startDate);
				isUpdated     = true;
			}
			
			// Set endDate, endTime
			// E.g. edit 2 to 12/10/15 1400
			if (startDate == null && startTime == null && endDate != null && endTime != null) {
				_endDate  = dateFormat.parse(endDate + " " + endTime);
				
				task.setEndTime(_endDate);
				isUpdated = true;
			}
			
			// Set endDate, endTime according to user; endDate, endTime remains unchanged
			// E.g. edit 2 on 12/10/15 to 1600
			if (startDate != null && startTime == null && endDate != null && endTime != null) {
				_endDate  = dateFormat.parse(endDate + " " + endTime);
				
				task.setEndTime(_endDate);
				isUpdated = true;
			}	

			// Set endDate only
			// E.g. edit 2 to 12/10/15
			if (startDate == null && startTime == null && endDate != null && endTime == null) {
				prevEndDate = task.getEndTime();
				// Get the original time of the day for end of the event
				prevEndTime = timeFormat.format(prevEndDate);
				
				_endDate    = dateFormat.parse(endDate + " " + prevEndTime);
				
				task.setEndTime(_endDate);	
				isUpdated   = true;			
			}

			// Set deadlineDate and deadlineTime
			if (deadlineDate != null && deadlineTime != null){
				_deadlineDate = dateFormat.parse(deadlineDate + " " + deadlineTime);
				task.setDeadline(_deadlineDate);
				isUpdated     = true;
			}
			
			// Set deadlineDate and deadlineTime
			if (deadlineDate != null && deadlineTime == null){
				prevDeadlineDate = task.getDeadline();
				prevDeadlineTime = timeFormat.format(prevDeadlineDate);
				
				_deadlineDate    = dateFormat.parse(deadlineDate + " " + prevDeadlineTime);
				task.setDeadline(_deadlineDate);
				isUpdated        = true;
			}

			if(isUpdated) {
				System.out.println(task.toString());
				return MESSAGE_EDIT_TASK;
			} else {
				return ERROR_INVALID_COMMAND + "\n" + HELP_EDIT_TASK;
			}
			
		} catch (ParseException e) {			
			e.printStackTrace();
			return ERROR_DATEFORMAT;
			
		}
	}
	
	/**
	 * Adds a task to the task list
	 * @param task The task to be added to the taskList
	 */
	private static String addTask(String desc,String venue, String startDate, String endDate, String startTime, String endTime, String deadlineDate, String deadlineTime) {
		Date _startDate = null;
		Date _endDate = null;
		Date _deadlineDate = null;
		
		try {
			
			// E.g. add do "sth" on 12/10/15 from 1200 to 1400
			// E.g. add do "sth" from 12/10/15 1200 to 12/10/15 1400
			if (startTime != null && startDate != null && endTime != null && endDate != null){
				_startDate    = dateFormat.parse(startDate + " " + startTime);
				_endDate      = dateFormat.parse(endDate + " " + endTime);
			} else if (startTime != null && startDate != null && endTime == null && endDate != null){
				// add do "sth" on 12/10/15 from 1200
				endTime       = String.valueOf(Integer.parseInt(startTime) + 100);
				_startDate    = dateFormat.parse(startDate + " " + startTime);
				_endDate      = dateFormat.parse(endDate + " " + endTime);
			} else if (startTime == null && startDate != null && endTime != null && endDate != null){
				// add do "sth" on 12/10/15 to 1200
				startTime     = String.valueOf(Integer.parseInt(endTime) - 100);
				_startDate    = dateFormat.parse(startDate + " " + startTime);
				_endDate      = dateFormat.parse(endDate + " " + endTime);
			} else if (startTime == null && startDate != null && endTime == null && endDate != null){
				// add do "sth" on 12/10/15
				startTime     = "1200";		// Default 12pm - 1pm
				endTime       = "1300";
				_startDate    = dateFormat.parse(startDate + " " + startTime);
				_endDate      = dateFormat.parse(endDate + " " + endTime);
			} 

			// E.g. add do "sth" on 
			if(deadlineDate != null && deadlineTime != null){
				_deadlineDate = dateFormat.parse(deadlineDate + " " + deadlineTime);
			}
			
			if(desc != null){
				Task task = null;
				if (startTime != null && endTime != null){
					task = new Task(currentTaskId+1, desc, _startDate, _endDate, venue);// Event
				} else if (deadlineDate != null){
					task = new Task(currentTaskId+1, desc, _deadlineDate, venue);		// Deadline task
				} else {
					task = new Task(currentTaskId+1, desc, venue);						// Floating task
				}
				
				taskList.add(task);
				System.out.println(task.toString());
			}
			currentTaskId += 1;
			return MESSAGE_ADD_TASK;
			
		} catch (ParseException e) {			
			e.printStackTrace();
			return ERROR_DATEFORMAT;
			
		}
	}
	
	/**
	 * Remove a specific task from the file
	 * @param task The task to be deleted from the taskList
	 */
	private static String removeTask(String stringID) {
		if(stringID != null){
			for(Task t:taskList){
				if(t.getTaskId() == Integer.parseInt(stringID)){
					String removedTask = Integer.toString(t.getTaskId());
					taskList.remove(t);
					System.out.println(t);
					return MESSAGE_DELETE_TASK;
				}
				
			}
		}
		return ERROR_NOT_FOUND_TASK;
		
	}
	
	/**
	 * Shows the Help menu to the user
	 */
	private static void showHelpMenu() {
		showToUser(ERROR_INVALID_COMMAND);
		showToUser("");
		showToUser(HELP_TITLE);
		showToUser(HELP_SUBTITLE);
		showToUser(HELP_ADD_TASK);
		showToUser(HELP_DELETE_TASK);
		showToUser(HELP_DISPLAY);
		// showToUser(HELP_SEARCH_TASK);
		showToUser(HELP_EDIT_TASK);
		showToUser(HELP_EXIT);
	}
	
	/**
	 * 
	 * @param string
	 */
	private static void displayTask(String taskID) {
		if(taskID != null){
			if(taskList.size() >= Integer.parseInt(taskID) && Integer.parseInt(taskID) > 0){
				showToUser(taskList.get(Integer.parseInt(taskID) - 1).toString());
			}
			//TODO: what if its out of bounds?
		} else {
			displayAllTasks();
		}
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
	
	public static Task searchTasks(int task){
		for(int i = 0; i < taskList.size(); i++){
			if(taskList.get(i).getTaskId() == task){
				return taskList.get(i);
			}
		}
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
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMAND_TYPE.DELETE_TASK;
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
		if(parameters.length > 1){
			return parameters[1];
		} else{
			return "";
		}
	}
	
	private static void updateCurrentTaskId() {
		currentTaskId = fileIO.getCurrentTaskId();
	}

}