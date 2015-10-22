package Task;

import Task.FileIO;
import Task.COMMAND_TYPE;
import Task.StringParser;
import Task.Validator;
import Task.TaskVenueEdit;
//import Task.TaskDescEdit;
//import Task.TaskPeriodEdit;
//import Task.TaskDeadlineEdit;
//import Task.TaskDoneEdit;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Scanner;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.undo.UndoManager;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

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
	private static final String MESSAGE_DELETE_TASK    = " ID task has been deleted";
	private static final String MESSAGE_EDIT_TASK      = "Task has been updated!";
	private static final String MESSAGE_UNDO_TASK      = "Successfully rolled back 1 change.";
	private static final String MESSAGE_REDO_TASK      = "Successfully redoed 1 change.";
	private static final String MESSAGE_DONE_TASK      = "Successfully updated to done.";
	private static final String MESSAGE_UNDONE_TASK    = "Successfully updated to undone.";
	private static final String MESSAGE_EXIT           = "Thanks for using TaskBuddy! Changes saved to disk.";
	
	// Define error messages here
	private static final String ERROR_INVALID_COMMAND  = "Invalid Command.";
	private static final String ERROR_EMPTY_TASKLIST   = "You have no tasks!";
	private static final String ERROR_NOT_FOUND_TASK   = "The task was not found!";
	private static final String ERROR_IO_TASK   	   = "The task could not be changed!";
	private static final String ERROR_NO_DESC   	   = "You must have a description for your task!";
	private static final String ERROR_CANNOT_UNDO      = "No more changes to undo.";
	private static final String ERROR_CANNOT_REDO      = "No more changes to redo.";
	private static final String ERROR_DATEFORMAT       = "The date and/or time you have entered is invalid. Date format is 'dd/M/yyyy' while time is 24 hrs 'HHmm e.g. 2359";
	
	// Define help messages here
	private static final String HELP_TITLE             = "****************************************************************************Help menu for TaskBuddy!*********************************************************************************************";
	private static final String HELP_SUBTITLE          = "[COMMAND]   [FORMAT]                                                                                                                                    [DESCRIPTION]                            ";
	private static final String HELP_ADD_TASK          = "  ADD       : add    do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"              | Adds a floating task, event or deadline";
	private static final String HELP_DISPLAY           = "  DISPLAY   : display                                                                                                                                   | Displays all tasks                     ";
	// private static final String HELP_SEARCH_TASK       = "  SEARCH    : search [value1=keyword1], [value2=keyword2],...                                    |";
	private static final String HELP_EDIT_TASK         = "  EDIT      : edit [task-id] do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"      | Edits an existing task                 ";
	private static final String HELP_UNDO              = "  UNDO      : undo                                                                                                                                      | Undo the last action                   ";
	private static final String HELP_REDO              = "  REDO      : redo                                                                                                                                      | Redo the last undoed action            ";
	private static final String HELP_DELETE_TASK       = "  DELETE    : delete [task-id]                                                                                                                          | Removes a task                         ";
	private static final String HELP_EXIT              = "  EXIT      : exit                                                                                                                                      | Terminate program                      ";
	
	private static final Logger LOGGER = Logger.getLogger(TaskHandler.class.getName());
	
	// Define minimum argument numbers here
	//TODO: Do these make sense to have now?
	private static final int NUM_ARGS_ADD_TASK     = 6;
	private static final int NUM_ARGS_GET_TASK     = 3;
	private static final int NUM_ARGS_DISPLAY_TASK = 0;
	private static final int NUM_ARGS_SEARCH_TASK  = 1;
	private static final int NUM_ARGS_EDIT_TASK    = 1;
	
	private static final boolean TASK_DONE    	= true;
	private static final boolean TASK_NOT_DONE	= false;
	
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
	private static TaskUndoManager      undoManager;
	
	/**
	 * Starts the program
	 * @param args The file path to load the file in
	 * @return 
	 */
	public static String startTasks(String[] args) {
		String localFilePath = determineFilePath(args);
		init(localFilePath);
		return MESSAGE_WELCOME;
	}
	
	public static String inputFeedBack(String input){
		if(isValidCommand(input)) {
			return executeCommand(input);
		}
		return null;
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
		LOGGER.setLevel(Level.INFO);
		undoManager = new TaskUndoManager();
		dateFormat.setCalendar(calendar);
		fileIO = new FileIO(localFilePath);
		taskList = fileIO.readFromFile();
		currentTaskId = fileIO.getCurrentTaskId();
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
		HashMap<PARAMETER, Object> parsedParamTable;
		String message;
		try{
			switch (command) {
				case ADD_TASK:
					parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
					//TODO: shouldn't it be if it has a description?
					if (parsedParamTable.get(PARAMETER.DESC) != null) {
						message = addTask((String)parsedParamTable.get(PARAMETER.DESC),
											(String)parsedParamTable.get(PARAMETER.VENUE), 
											(Date)parsedParamTable.get(PARAMETER.START_DATE),
											(Date)parsedParamTable.get(PARAMETER.END_DATE), 
											(Date)parsedParamTable.get(PARAMETER.START_TIME),
											(Date)parsedParamTable.get(PARAMETER.END_TIME),
											(Date)parsedParamTable.get(PARAMETER.DEADLINE_DATE),
											(Date)parsedParamTable.get(PARAMETER.DEADLINE_TIME));
						
						fileIO.writeToFile(taskList);
						return message;
					} else {
						return ERROR_INVALID_COMMAND + "\n" + HELP_ADD_TASK; 
					}
				case DISPLAY:
					if (taskList.isEmpty()) {
						return ERROR_EMPTY_TASKLIST;
					} else if(removeFirstWord(userInput).length() != 0){
						parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
						return displayTask((int)parsedParamTable.get(PARAMETER.TASKID));					
					} else {
						return displayAllTasks();
					}
				case EDIT_TASK:
					parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
	
					message = editTask((int)parsedParamTable.get(PARAMETER.TASKID),
										(String)parsedParamTable.get(PARAMETER.DESC),
										(String)parsedParamTable.get(PARAMETER.VENUE), 
										(Date)parsedParamTable.get(PARAMETER.START_DATE),
										(Date)parsedParamTable.get(PARAMETER.END_DATE), 
										(Date)parsedParamTable.get(PARAMETER.START_TIME),
										(Date)parsedParamTable.get(PARAMETER.END_TIME),
										(Date)parsedParamTable.get(PARAMETER.DEADLINE_DATE),
										(Date)parsedParamTable.get(PARAMETER.DEADLINE_TIME));
					
					fileIO.writeToFile(taskList);
					return message;
				case DELETE_TASK:
					parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));

					message = removeTask((int)parsedParamTable.get(PARAMETER.TASKID));
					fileIO.writeToFile(taskList);
					
					return message;
				case UNDO:
					message = undoSingleCommand();
					fileIO.writeToFile(taskList);
					return message;
				case REDO:
					message = redoSingleCommand();
					fileIO.writeToFile(taskList);
					return message;
				case DONE:
					parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
					message = markTask((int)parsedParamTable.get(PARAMETER.TASKID),TASK_DONE);
					fileIO.writeToFile(taskList);
					return message;
				case UNDONE:
					parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
					message = markTask((int)parsedParamTable.get(PARAMETER.TASKID), TASK_NOT_DONE);
					fileIO.writeToFile(taskList);
					return message;
				case INVALID_COMMAND:
					showHelpMenu();
					return "";
				case EXIT:
					fileIO.writeToFile(taskList);
					System.exit(0);
				default:
					return "There is an error in your code.";
			
			}
		} catch (ParseException p){
			return "There is an error in the parameter: " + p.getMessage();
		} catch (IllegalArgumentException a) {
			return "There is an error in your command: " + a.getMessage();
		}
	}

	/**
	 * TODO:
	 * @param i
	 * @param taskDone
	 * @return
	 */
	private static String markTask(int taskID, boolean isDone) {
		for (Task t:taskList){
			if (t.getTaskId() == taskID){
				String isDoneTask     = Integer.toString(t.getTaskId());
				boolean prevDone      = t.isDone();
				TaskEdit compoundEdit = new TaskEdit(t);
				TaskDoneEdit edit     = new TaskDoneEdit(t, prevDone, isDone);
				edit.setSignificant();
				compoundEdit.addEdit(edit);
				compoundEdit.end();
				t.setDone(isDone);
				undoManager.addEdit(compoundEdit);
				return isDoneTask + " " + MESSAGE_DONE_TASK + t.toString();
			}
		}
		return ERROR_NOT_FOUND_TASK;
	}

	private static String undoSingleCommand() {
		try {
			if (undoManager.canUndo()) {
				UndoableEdit nextEdit = undoManager.editToBeUndone();
				TaskEdit taskEdit = (TaskEdit) nextEdit;
				undoManager.undo();
				return taskEdit.getTask().toString() + "\n" + MESSAGE_UNDO_TASK;
			} else {
				return ERROR_CANNOT_UNDO;
			}
		} catch (CannotUndoException e) {
			return ERROR_CANNOT_UNDO;
		}
	}

	private static String redoSingleCommand() {
		try {
			if (undoManager.canRedo()) {
				UndoableEdit nextEdit = undoManager.editToBeRedone();
				TaskEdit taskEdit = (TaskEdit) nextEdit;
				undoManager.redo();
				return taskEdit.getTask().toString() + "\n" + MESSAGE_REDO_TASK;
			} else {
				return ERROR_CANNOT_REDO;
			}
		} catch (CannotRedoException e) {
			return ERROR_CANNOT_REDO;
		}
	}

	/**
	 * Edits a task in the task list
	 * @param task The task to be added to the taskList
	 * @return 
	 */
	private static String editTask(int ID, String desc,String venue, Date startDate, Date endDate, Date startTime, Date endTime, Date deadlineDate, Date deadlineTime) {
		// Declare local variables
		SimpleDateFormat timeFormat      = new SimpleDateFormat("HHmm");
		SimpleDateFormat localDateFormat = new SimpleDateFormat("dd/M/yyyy");

		
		Task task          = null;
		Date _deadlineDate = null;
		Date prevStartDate;
		Date prevEndDate;
		Date prevDeadlineDate;
		Period oldPeriod = null;
		Period newPeriod;
		
		String prevStartTime 	= "1200";
		String prevEndTime 		= "1300";
		String prevDeadlineTime = null;

		boolean isUpdated = false;
		
		if (ID != -1){
			task = searchTasks(ID);
			if (task == null) {
				return ERROR_NOT_FOUND_TASK;
			}
		} else {
			return ERROR_NOT_FOUND_TASK;
		}
		
		UndoableSignificantEdit edit = new UndoableSignificantEdit();
		TaskEdit compoundEdit        = new TaskEdit(task);
		// Set description
		if (desc != null){
			String oldDesc = task.getDescription();
			edit = new TaskDescEdit(task, oldDesc, desc);
			task.setDescription(desc);
			compoundEdit.addEdit(edit);
			isUpdated = true;
		}
		
		// Set venue
		if (venue != null){
			String oldVenue = task.getVenue();
			task.setVenue(venue);
			edit = new TaskVenueEdit(task, oldVenue, venue);
			compoundEdit.addEdit(edit);
			isUpdated = true;
		}

		prevStartDate    = task.getStartDateTime();
		prevEndDate      = task.getEndDateTime();
		prevDeadlineDate = task.getDeadline();
		if(prevStartDate != null && prevEndDate != null){
			// Get the original time of the day for start and end of the event
			prevStartTime    = timeFormat.format(prevStartDate);
			prevEndTime      = timeFormat.format(prevEndDate);
			prevDeadlineTime = timeFormat.format(prevDeadlineDate);
			
			oldPeriod        = new Period(prevStartDate, prevEndDate);
		}

		
		try {
			// Set startDate, startTime, endDate and endTime
			// E.g. edit 2 from 12/10/15 1200 to 13/10/15 1400
			// E.g. edit 2 on 12/10/15 from 1200 to 1400
			if (startDate != null && startTime != null && endDate != null && endTime != null){
				newPeriod = new Period(startTime, endTime);
				task.setStartDateTime(startTime);
				task.setEndDateTime(endTime);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				
				compoundEdit.addEdit(edit);
				isUpdated  = true;
			}

			// Set startDate and endDate, without changing startTime and endTime
			// E.g. edit 2 on 12/10/15
			if (startDate != null && startTime == null && endDate != null && endTime == null) {
				
				startDate	= changeDateTime(startDate, prevStartTime);
				endDate		= changeDateTime(endDate, prevEndTime);
				
				task.setStartDateTime(startDate);
				task.setEndDateTime(endDate);

				newPeriod = new Period(startDate, endDate);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated     = true;
			}
			
			// Set startDate, startTime
			// E.g. edit 2 from 12/10/15 1200
			if (startDate != null && startTime != null && endDate == null && endTime == null && prevEndDate != null) {
				newPeriod = new Period(startTime, prevEndDate);
				
				task.setStartDateTime(startTime);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated  = true;
			}

			// Set startDate, startTime according to user; endDate, endTime remains unchanged
			// E.g. edit 2 on 12/10/15 from 1400
			if (startDate != null && startTime != null && endDate != null && endTime == null && prevEndDate != null) {
				newPeriod = new Period(startTime, prevEndDate);

				task.setStartDateTime(startTime);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated  = true;
			}
			
			// Set startDate only
			// E.g. edit 2 from 12/10/15
			if (startDate != null && startTime == null && endDate == null && endTime == null && prevEndDate != null) {								
				startDate = changeDateTime(startDate, prevStartTime);
				newPeriod = new Period(startDate, prevEndDate);

				task.setStartDateTime(startDate);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated     = true;
			}
			
			// Set endDate, endTime
			// E.g. edit 2 to 12/10/15 1400
			if (startDate == null && startTime == null && endDate != null && endTime != null && prevEndDate != null) {
				newPeriod = new Period(prevStartDate, endTime);

				task.setEndDateTime(endTime);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated = true;
			}
			
			// Set endDate, endTime according to user; startDate, startTime remains unchanged
			// E.g. edit 2 on 12/10/15 to 1600
			if (startDate != null && startTime == null && endDate != null && endTime != null && prevStartDate != null && prevEndDate != null) {
				startDate = changeDateTime(startDate, prevStartTime);
				newPeriod = new Period(startDate, endTime);

				task.setEndDateTime(endTime);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated = true;
			}	

			// Set endDate only
			// E.g. edit 2 to 12/10/15
			if (startDate == null && startTime == null && endDate != null && endTime == null && prevStartDate != null) {				
				endDate    = changeDateTime(endDate, prevEndTime);
				
				newPeriod = new Period(prevStartDate, endDate);
				
				task.setEndDateTime(endDate);	
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated   = true;			
			}

			// Set deadlineDate and deadlineTime
			if (deadlineDate != null && deadlineTime != null){
				task.setDeadline(deadlineTime);
				edit = new TaskDeadlineEdit(task, prevDeadlineDate, deadlineTime);
				compoundEdit.addEdit(edit);
				isUpdated     = true;
			}
			
			// Set deadlineDate and deadlineTime
			if (deadlineDate != null && deadlineTime == null && prevDeadlineTime != null){				
				_deadlineDate = changeDateTime(deadlineDate, prevDeadlineTime);
				task.setDeadline(_deadlineDate);
				edit = new TaskDeadlineEdit(task, prevDeadlineDate, _deadlineDate);
				compoundEdit.addEdit(edit);
				isUpdated        = true;
			}

			if(isUpdated) {
				// Set one significant edit
				UndoableEdit lastEdit = compoundEdit.lastEdit();
				UndoableSignificantEdit edit1 = (UndoableSignificantEdit) lastEdit;
				compoundEdit.end();
				edit1.setSignificant();
				undoManager.addEdit(compoundEdit);
				return task.toString() + MESSAGE_EDIT_TASK;
			} else {
				return ERROR_INVALID_COMMAND + "\n" + HELP_EDIT_TASK;
			}
			
		} catch (IllegalArgumentException a) {
			// a.printStackTrace();
			//TODO:change message
			return ERROR_DATEFORMAT;
		} catch (ParseException e) {			
			e.printStackTrace();
			return ERROR_DATEFORMAT;	
		} 
	}
	
	private static Date changeDateTime(Date date, String prevTimeString) throws ParseException {
		String test1 = dateFormat.format(date);
		return new SimpleDateFormat("dd/M/yyyy HHmm").parse(test1.split(" ")[0] + " " + prevTimeString);
	}

	/**
	 * Adds a task to the task list
	 * @param task The task to be added to the taskList
	 */
	private static String addTask(String desc, String venue, Date startDate, Date endDate, Date startTime, Date endTime, Date deadlineDate, Date deadlineTime) {
		if(desc != null){
			Task task = null;
			try{
				if (startTime != null && endTime != null){
					task = new Task(currentTaskId+1, desc, startDate, endDate, venue);// Event
				} else if (deadlineDate != null){
					task = new Task(currentTaskId+1, desc, deadlineDate, venue);		// Deadline task
				} else {
					task = new Task(currentTaskId+1, desc, venue);						// Floating task
				}
			} catch (IllegalArgumentException e){
				return e.getMessage();
			}
			
			// Make sure that we are not adding a null Task
			assert(task!=null);

			TaskEdit compoundEdit = new TaskEdit(task);
			TaskListEdit edit     = new TaskListEdit(task, taskList, currentTaskId, currentTaskId+1, true);
			edit.setSignificant();
			compoundEdit.addEdit(edit);
			compoundEdit.end();

			taskList.add(task);
			undoManager.addEdit(compoundEdit);
			currentTaskId += 1;
			return task.toString() + "\n" + MESSAGE_ADD_TASK;
		}
		
		return ERROR_NO_DESC;
	}
	
	/**
	 * Remove a specific task from the file
	 * @param task The task to be deleted from the taskList
	 */
	private static String removeTask(int taskID) {
		for(Task t:taskList){
			if(t.getTaskId() == taskID){
				String removedTask = Integer.toString(t.getTaskId());
				TaskEdit compoundEdit = new TaskEdit(t);
				TaskListEdit edit     = new TaskListEdit(t, taskList, currentTaskId, currentTaskId, false);
				edit.setSignificant();
				taskList.remove(t);
				compoundEdit.addEdit(edit);
				compoundEdit.end();

				undoManager.addEdit(compoundEdit);
				return removedTask + MESSAGE_DELETE_TASK;
			}
		}
		return ERROR_NOT_FOUND_TASK;
		
	}
	
	/**
	 * Shows the Help menu to the user
	 */
	private static String showHelpMenu() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(ERROR_INVALID_COMMAND);
		sb.append("");
		sb.append(HELP_TITLE);
		sb.append(HELP_SUBTITLE);
		sb.append(HELP_ADD_TASK);
		sb.append(HELP_DELETE_TASK);
		sb.append(HELP_DISPLAY);
		// sb.append(HELP_SEARCH_TASK);
		sb.append(HELP_EDIT_TASK);
		sb.append(HELP_EXIT);
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param string
	 * @return 
	 */
	private static String displayTask(int taskID) {
		if(taskID != -1){
			for(Task t:taskList){
				if(t.getTaskId() == taskID){
					return t.toString();
				}
			}
		}
		return ERROR_NOT_FOUND_TASK;
	}
	
	/**
	 * Displays all the current tasks in the taskList
	 * @return 
	 */
	private static String displayAllTasks() {
		StringBuilder taskListString = new StringBuilder();
		for (int i = 0; i < taskList.size() ; i++) {
			taskListString.append(taskList.get(i).toString());
		}
		return taskListString.toString() + "\n" + MESSAGE_DISPLAY;
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
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return COMMAND_TYPE.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("done")) {
			return COMMAND_TYPE.DONE;
		} else if (commandTypeString.equalsIgnoreCase("undone")) {
			return COMMAND_TYPE.UNDONE;
		} else if (commandTypeString.equalsIgnoreCase("redo")) {
			return COMMAND_TYPE.REDO;
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

	public static void setCurrentTaskId(int _currentTaskId) {
		currentTaskId = _currentTaskId;
	}
}