/*
 *  @author A0097689 Tan Si Kai
 *  @author A******  Jean Pierre
 *  @author A******  Audrey Tiah
 */

import java.io.File;
import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class TaskHandler {
	// Define success messages here
	private static final String MESSAGE_WELCOME = "Welcome to TaskBuddy!";
	private static final String MESSAGE_ADD_TASK = "Successfully added task.";
	private static final String MESSAGE_GET_TASK = "Task returned";
	private static final String MESSAGE_SEARCH_TASK = "Here are tasks matching your keywords:";
	private static final String MESSAGE_EDIT_TASK  = "Choose the task you want to edit";
	private static final String MESSAGE_EXIT = "Thanks for using TaskBuddy! Changes saved to disk.";
	
	// Define error messages here
	private static final String ERROR_INVALID_COMMAND = "Invalid Command.";
	
	
	// Define help messages here
	
	
	// These are the possible command types
	enum COMMAND_TYPE {
		ADD_TASK, 
		GET_TASK, 
		SEARCH_TASK, 
		EDIT_TASK, 
		INVALID_COMMAND, 
		EXIT
	};
	
	private static Scanner         scanner = new Scanner(System.in);
	private static Calendar        calendar = Calendar.getInstance();
	private static ArrayList<Task> taskList = new ArrayList<Task>(50);
	private static ArrayList<Period> timetable = new ArrayList<Period>(50);			// timetable that keeps track of startTime and endTime of tasks
	private static LinkedList<String> commandHistory = new LinkedList<String>();	// stack of userInputs history to implement undo action
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		showToUser(MESSAGE_WELCOME);
		while(true) {
			System.out.print("Enter command:");
			String command = scanner.nextLine();
			String feedback = executeCommand(command);
			showToUser(feedback);
			
		}
	}

	// Displays text to user
	private static void showToUser(String text) {
		System.out.println(text);
	}
	
	// Executes user input
	public static String executeCommand(String userInput) {
		COMMAND_TYPE command = determineCommandType(getFirstWord(userInput));
		
		switch (command) {
			case ADD_TASK:
				return MESSAGE_ADD_TASK;
			case GET_TASK:
				return MESSAGE_GET_TASK;
			case SEARCH_TASK:
				return MESSAGE_SEARCH_TASK;
			case EDIT_TASK:
				return MESSAGE_EDIT_TASK;
			case INVALID_COMMAND:
				return ERROR_INVALID_COMMAND;
			case EXIT:
				showToUser(MESSAGE_EXIT);
				System.exit(0);
			default:
				return "There is an error in your code.";
		
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
		
		if(commandTypeString.equalsIgnoreCase("addtask")) {
			return COMMAND_TYPE.ADD_TASK;
		} else if (commandTypeString.equalsIgnoreCase("gettask")) {
			return COMMAND_TYPE.GET_TASK;
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
}
