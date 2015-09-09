package Task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import static Task.Task.PRIORITY;

/**
 *  Represents the handler for tasks
 * 
 *  @author A0097689 Tan Si Kai
 *  @author A0145472E  Jean Pierre Castillo
 *  @author A******  Audrey Tiah
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
		if(userInput.length() != 0) {
			return true;
		} else {
			showHelpMenu();
			return false;
		}
	}
	
	// Executes user input
	public static String executeCommand(String userInput) {
		COMMAND_TYPE command = determineCommandType(getFirstWord(userInput));
		StringParser parser = new StringParser(command, userInput);
		switch (command) {
			case ADD_TASK:
				if (parser.canAddTask()) {
					addTask(parser.getTask());
					
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
	
	private static void addTask(Task task) {
		System.out.println(task.toString());
		taskList.add(task);
	}
	
	// Remove a specific task from the file
	private static void removeTask(Task task) {
		
	}
	
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
	
	private static void displayAllTasks() {
		for (int i = 0; i < taskList.size() ; i++) {
			showToUser(taskList.get(i).toString());
		}
	}
	
	// Takes a command and returns the correct number of arguments expected
	public static int determineNumberOfArgs(COMMAND_TYPE command) {
		return 0;
	}
	
	// Figure out free time slots
	public static ArrayList<Period> getFreeTimeSlots(ArrayList<Period> timetable) {
		ArrayList<Period> result = new ArrayList<Period>(50);
		
		return result;
	}
	
	// Given a tag, and startTime and endTime, return all tasks with that tag
	public static ArrayList<Task> getByTag (String tag, Date startTime, Date endTime) {
		return taskList;
	}
	
	// Given a search keyword, and startTime and endTime, return all tasks with that keyword in their description within the search space
	public static ArrayList<Task> searchByDescription (String keyword, Date startTime, Date endTime) {
		return taskList;
	}
	
	// Takes a single word and figure out the command
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
	
	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
	
	private static String removeFirstWord(String userCommand) {
		String[] parameters = userCommand.trim().split(" ", 2);
		return parameters[1];
	}

}