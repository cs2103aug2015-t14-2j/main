/*
 *  @author A0097689 Tan Si Kai
 *  @author A******  Jean Pierre
 *  @author A******  Audrey Tiah
 */

package Task;

import java.io.*;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.Iterator;

import java.text.SimpleDateFormat;
//import java.text.ParseException;
import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import static Task.Task.PRIORITY;

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
	
	// These are the possible command types
	enum COMMAND_TYPE {
		ADD_TASK, 
		GET_TASK,
		DISPLAY,
		SEARCH_TASK, 
		EDIT_TASK, 
		INVALID_COMMAND, 
		EXIT
	};
	
	private static Scanner         scanner           = new Scanner(System.in);
	private static Calendar        calendar          = Calendar.getInstance();
	private static SimpleDateFormat dateFormat       = new SimpleDateFormat("EEE MMM dd yyyy HHmm");
	private static ArrayList<Task> taskList          = new ArrayList<Task>(50);
	private static ArrayList<Period> timetable       = new ArrayList<Period>(50);			// timetable that keeps track of startTime and endTime of tasks
	private static LinkedList<String> commandHistory = new LinkedList<String>();	// stack of userInputs history to implement undo action
	private static String 			filePath	     = "./data/calendar.json";		// relative path to calendar.json 
	private static FileWriter       writer; 
	private static FileReader       reader;
	private static int 				currentTaskId;          
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
	
	// Initialize settings, search for application files etc.
	private static void init() {
		dateFormat.setCalendar(calendar);
		readFromFile(TaskHandler.filePath);
	}
	
	// 
	private static void readFromFile(String path) {
		try {
			reader = new FileReader(path);
			//System.out.println("current time is " + dateFormat.getCalendar().get(1));
			JSONParser parser = new JSONParser();
			try {
				JSONObject jsonObject = (JSONObject) parser.parse(reader);
				
				JSONArray taskListFile = (JSONArray) jsonObject.get("Tasks");
				Iterator<JSONObject> taskIterator = taskListFile.iterator();
				while(taskIterator.hasNext()) {
					jsonObject                   = taskIterator.next();
					long taskId                  = (long) jsonObject.get("taskId");
					String createdTime           = (String) jsonObject.get("createdTime");
					String lastModifiedTime      = (String) jsonObject.get("lastModifiedTime");
					String startTime             = (String) jsonObject.get("startTime");
					String endTime               = (String) jsonObject.get("endTime");
					String deadline              = (String) jsonObject.get("deadline");
					String venue                 = (String) jsonObject.get("venue");
					String description           = (String) jsonObject.get("description");
					boolean isDone                = (boolean) jsonObject.get("isDone");
					boolean isPastDeadline        = (boolean) jsonObject.get("isPastDeadline");
					boolean hasEnded              = (boolean) jsonObject.get("hasEnded");
					
					JSONArray tagList            = (JSONArray) jsonObject.get("tags");
					Iterator<String> tagIterator = tagList.iterator();
					ArrayList<String> hashTags   = new ArrayList<String>();
					
					while (tagIterator.hasNext()) {
						hashTags.add(tagIterator.next());
						
					}
					
					Date createdTimeDate      = dateFormat.parse(createdTime);
					Date lastModifiedTimeDate = dateFormat.parse(lastModifiedTime);
					Date deadlineDate         = dateFormat.parse(deadline);
					Date startDate            = dateFormat.parse(startTime);
					Date endDate              = dateFormat.parse(endTime);

					Task memTask = new Task((int) taskId, createdTimeDate, lastModifiedTimeDate, description, startDate, endDate, deadlineDate, venue, isDone, isPastDeadline, hasEnded, hashTags);
					taskList.add(memTask);
					currentTaskId = (int) taskId;
//					System.out.println(taskId + " " + createdTime + " " + lastModifiedTime + " " + startTime + " " + endTime + " " + deadline + " " + venue + " " + description + " " + isDone + " " + isPastDeadline + " " + hasEnded + " " + hashTags);
					System.out.println(memTask.toString());

				}
				
				reader.close();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (java.text.ParseException e1) {
				e1.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeToFile(String path) {
		JSONObject calendarObj = new JSONObject();

		JSONArray taskListFile = new JSONArray();

		Iterator<Task> taskIterator = taskList.iterator();
		while (taskIterator.hasNext()) {
			Task currentTask = taskIterator.next();
			JSONObject obj = new JSONObject();
			obj.put("taskId", currentTask.getTaskId());
			obj.put("createdTime", dateFormat.format(currentTask.getCreatedTime()));
			obj.put("lastModifiedTime", dateFormat.format(currentTask.getModifiedTime()));
			obj.put("startTime", dateFormat.format(currentTask.getStartTime()));
			obj.put("endTime", dateFormat.format(currentTask.getEndTime()));
			obj.put("deadline", dateFormat.format(currentTask.getDeadline()));
			obj.put("venue", currentTask.getVenue());
			obj.put("description", currentTask.getDescription());
			obj.put("isDone", currentTask.isDone());
			obj.put("isPastDeadline", currentTask.isPastDeadline());
			obj.put("hasEnded", currentTask.isHasEnded());
			
			Iterator<String> tagIterator = currentTask.getTags().iterator();
			JSONArray tagListFile = new JSONArray();
			while (tagIterator.hasNext()) {
				tagListFile.add(tagIterator.next());
			}
			obj.put("tags", tagListFile);
			
			taskListFile.add(obj);
		}
		
		calendarObj.put("Tasks", taskListFile);

		try {
			writer = new FileWriter(path);
			writer.write(calendarObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Displays text to user, do not print if empty string
	private static void showToUser(String text) {
		if(text.isEmpty()) {
			return;
		}
		
		System.out.println(text);
	}

	// Pattern matching on expected command patterns to decide if it is a valid command
	public static boolean isValidCommand(String userInput) {
		if(true) {
			return true;
		} else {
			showHelpMenu();
			return false;
		}
	}
	
	// Executes user input
	public static String executeCommand(String userInput) {
		COMMAND_TYPE command = determineCommandType(getFirstWord(userInput));
		
		switch (command) {
			case ADD_TASK:
				String[] parameters = parseCommaSeparatedInput(userInput);
				boolean enoughParameters = parameters.length >= NUM_ARGS_ADD_TASK ? true : false;
				if (enoughParameters) {
					boolean canAddTask  = validateAddTask(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4]);
					if (canAddTask) {
						addTask(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5]);
						
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
				writeToFile(TaskHandler.filePath);
				System.exit(0);
			default:
				return "There is an error in your code.";
		
		}
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
	
	private static void addTask(String desc, String startTime, String endTime, String deadline, String venue, String priority) {
		try {
			Date startDate    = dateFormat.parse(startTime);
			Date endDate      = dateFormat.parse(endTime);
			Date deadlineDate = dateFormat.parse(deadline);
			
			Task task = new Task(currentTaskId+1, desc, startDate, endDate, deadlineDate, venue, priority);
			System.out.println(task.toString());
			taskList.add(task);
			
		} catch (java.text.ParseException e) {			
			e.printStackTrace();
			
		}
		
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
	
	// Takes in original line of user input, removes first word and parses the remainder into 
	// a string array delimited by commas
	private static String[] parseCommaSeparatedInput(String input) {
		String[] parametersArray = removeFirstWord(input).split("\\s*,\\s*"); 
		System.out.println(Arrays.toString(parametersArray));

		return parametersArray;
	}
	
	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
	
	private static String removeFirstWord(String userCommand) {
		String[] parameters = userCommand.trim().split(" ", 2);
		return parameters[1];
	}
	
	private static boolean isValidDate(String dateString) {
		return true;
	}
	
	private static boolean isValidVenue(String venueString) {
		return true;
	}
}