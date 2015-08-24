/*
 *  @author A0097689 Tan Si Kai
 *  
 */
import java.io.File;
import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;

public class TaskHandler {
	// Define success messages here
	private static final String WELCOME_MESSAGE = "Welcome to TaskBuddy!";
	private static final String MESSAGE_ADD = "Successfully added task.";
	
	// Define error messages here
	
	// These are the possible command types
	enum COMMAND_TYPE {
		ADD_TASK, GET_TASK, INVALID_COMMAND, SEARCH_TASK, EDIT_TASK, EXIT
	};
	
	private static Scanner         scanner = new Scanner(System.in);
	private static Calendar        calendar = Calendar.getInstance();
	private static ArrayList<Task> taskList = new ArrayList<Task>(100);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		showToUser(WELCOME_MESSAGE);
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
	public static String executeCommand(String userCommand) {
		switch userCommand
		case default:
			return "";
		return "";
	}
	
	// Takes a command and returns the correct number of arguments expected
	public static int determineNumberOfArgs(COMMAND_TYPE command) {
		
	}
	
	// Figure out free time slots
	public static ArrayList<String> getFreeTimeSlots(ArrayList<Task>) {
		
	}
	
	// Given a tag, and startTime and endTime, return all tasks with that tag
	public static ArrayList<Task> getByTag (String tag, Date startTime, Date endTime) {
		
	}
	
	// Given a search keyword, and startTime and endTime, return all tasks with that keyword in their description within the search space
	public static ArrayList<Task> searchByDescription (String keyword, Date startTime, Date endTime) {
		
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
	
}
