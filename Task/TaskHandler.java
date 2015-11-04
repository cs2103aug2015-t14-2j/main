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
import javafx.application.Platform;

/**
 *  Represents the handler for tasks
 * 
 *  @author A0097689 Tan Si Kai
 *  @author A0009586 Jean Pierre Castillo
 *  @author A0118772  Audrey Tiah
 */
public class TaskHandler {	
	private static final Logger LOGGER = Logger.getLogger(TaskHandler.class.getName());
	
	private static final boolean TASK_DONE    	= true;
	private static final boolean TASK_NOT_DONE	= false;
	
	private static Scanner         scanner           = new Scanner(System.in);
	private static Calendar        calendar          = Calendar.getInstance();
	private static SimpleDateFormat dateFormat       = new SimpleDateFormat("dd/M/yyyy HHmm");
	private static ArrayList<Task> taskList          = new ArrayList<Task>(50);
	private static ArrayList<Period> timetable       = new ArrayList<Period>(50);			// timetable that keeps track of startTime and endTime of tasks
	private static LinkedList<String> commandHistory = new LinkedList<String>();	// stack of userInputs history to implement undo action
	private static String 			filePath         = "./data/calendar.json";		// relative path to calendar.json 
	private static int 				currentTaskId;          
	private static FileIO           fileIO;
	private static Validator        validate         = new Validator();
	private static TaskUndoManager      undoManager;
	private static Context context                   = Context.getInstance();
	
	/**
	 * Starts the program
	 * @param args The file path to load the file in
	 * @return 
	 */
	public static void startTasks(String[] args) {
		String localFilePath = determineFilePath(args);
		init(localFilePath);
	}
	
	public static void inputFeedBack(String input){
		if(isValidCommand(input)) {
			executeCommand(input);
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
		LOGGER.setLevel(Level.INFO);
		undoManager   = new TaskUndoManager();
		dateFormat.setCalendar(calendar);
		fileIO        = new FileIO(localFilePath);
		taskList      = fileIO.readFromFile();
		currentTaskId = fileIO.getMaxTaskId();
		context.clearAllMessages();
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
	public static void executeCommand(String userInput) {
		COMMAND_TYPE command = determineCommandType(getFirstWord(userInput));
		HashMap<PARAMETER, Object> parsedParamTable;
		switch (command) {
			case ADD_TASK:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
				//TODO: shouldn't it be if it has a description?
				if (parsedParamTable.get(PARAMETER.DESC) != null) {
					addTask((String)parsedParamTable.get(PARAMETER.DESC),
						(String)parsedParamTable.get(PARAMETER.VENUE), 
						(Date)parsedParamTable.get(PARAMETER.START_DATE),
						(Date)parsedParamTable.get(PARAMETER.END_DATE), 
						(Date)parsedParamTable.get(PARAMETER.START_TIME),
						(Date)parsedParamTable.get(PARAMETER.END_TIME),
						(Date)parsedParamTable.get(PARAMETER.DEADLINE_DATE),
						(Date)parsedParamTable.get(PARAMETER.DEADLINE_TIME));
	
					fileIO.writeToFile(taskList);
				} else {
					context.displayMessage("ERROR_INVALID_COMMAND");
					context.displayMessage("HELP_ADD_TASK");
				}
				break;
			case DISPLAY:
				if (taskList.isEmpty()) {
					context.displayMessage("ERROR_EMPTY_TASKLIST");
				} else if(removeFirstWord(userInput).length() != 0){
					parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
					if(searchTasks(parsedParamTable).size() == 0){
						context.displayMessage("ERROR_NO_RESUlTS_FOUND");
					} else {
						context.displayMessage("MESSAGE_DISPLAY");
						displayAllTasks(searchTasks(parsedParamTable));	
					}			
				} else {
					context.displayMessage("MESSAGE_DISPLAY_ALL");
					displayAllTasks(taskList);
				}
				break;
			case EDIT_TASK:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));

				editTask((int)parsedParamTable.get(PARAMETER.TASKID),
					(String)parsedParamTable.get(PARAMETER.DESC),
					(String)parsedParamTable.get(PARAMETER.VENUE), 
					(Date)parsedParamTable.get(PARAMETER.START_DATE),
					(Date)parsedParamTable.get(PARAMETER.END_DATE), 
					(Date)parsedParamTable.get(PARAMETER.START_TIME),
					(Date)parsedParamTable.get(PARAMETER.END_TIME),
					(Date)parsedParamTable.get(PARAMETER.DEADLINE_DATE),
					(Date)parsedParamTable.get(PARAMETER.DEADLINE_TIME));
				
				fileIO.writeToFile(taskList);
				break;
			case DELETE_TASK:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));

				removeTask((int)parsedParamTable.get(PARAMETER.TASKID));
				fileIO.writeToFile(taskList);
				break;
			case UNDO:
				undoSingleCommand();
				fileIO.writeToFile(taskList);
				break;
			case REDO:
				redoSingleCommand();
				fileIO.writeToFile(taskList);
				break;
			case DONE:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
				completeTask((int)parsedParamTable.get(PARAMETER.TASKID),TASK_DONE);
				fileIO.writeToFile(taskList);
				break;
			case UNDONE:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
				completeTask((int)parsedParamTable.get(PARAMETER.TASKID), TASK_NOT_DONE);
				fileIO.writeToFile(taskList);
				break;
			case HELP:
				showHelpMenu();
				break;
			case EXIT:
				context.displayMessage("MESSAGE_EXIT");
				context.printToTerminal(); 				// Only call print here just before program exits
				fileIO.writeToFile(taskList);
				Platform.exit();
				System.exit(0);
			default:
				parsedParamTable = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK, userInput);
				//TODO: shouldn't it be if it has a description?
				if (parsedParamTable.get(PARAMETER.DESC) != null) {
					addTask((String)parsedParamTable.get(PARAMETER.DESC),
						(String)parsedParamTable.get(PARAMETER.VENUE), 
						(Date)parsedParamTable.get(PARAMETER.START_DATE),
						(Date)parsedParamTable.get(PARAMETER.END_DATE), 
						(Date)parsedParamTable.get(PARAMETER.START_TIME),
						(Date)parsedParamTable.get(PARAMETER.END_TIME),
						(Date)parsedParamTable.get(PARAMETER.DEADLINE_DATE),
						(Date)parsedParamTable.get(PARAMETER.DEADLINE_TIME));
	
					fileIO.writeToFile(taskList);
				} else {
					context.displayMessage("ERROR_INVALID_COMMAND");
					context.displayMessage("HELP_ADD_TASK");
				}
				break;
		
		}
	}

	/**
	 * TODO:
	 * @param i
	 * @param taskDone
	 * @return
	 */
	private static void completeTask(int taskID, boolean isDone) {
		boolean notFound = true;
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
				if (isDone) {
					context.displayMessage("MESSAGE_DONE_TASK");
				} else {
					context.displayMessage("MESSAGE_UNDONE_TASK");					
				}
				context.setTaskId(t.getTaskId());
				context.addTask(t);
				notFound = false;
			}
		}
		if (notFound) {
			context.displayMessage("ERROR_TASK_NOT_FOUND");
		}
	}

	private static void undoSingleCommand() {
		try {
			if (undoManager.canUndo()) {
				UndoableEdit nextEdit = undoManager.editToBeUndone();
				TaskEdit taskEdit = (TaskEdit) nextEdit;
				undoManager.undo();
				context.addTask(taskEdit.getTask());
				context.displayMessage("MESSAGE_UNDO_TASK");
				context.setTaskId(taskEdit.getTask().getTaskId());
			} else {
				context.displayMessage("ERROR_CANNOT_UNDO");
			}
		} catch (CannotUndoException e) {
			context.displayMessage("ERROR_CANNOT_REDO");
		}
	}

	private static void redoSingleCommand() {
		try {
			if (undoManager.canRedo()) {
				UndoableEdit nextEdit = undoManager.editToBeRedone();
				TaskEdit taskEdit = (TaskEdit) nextEdit;
				undoManager.redo();
				context.addTask(taskEdit.getTask());
				context.displayMessage("MESSAGE_REDO_TASK");
				context.setTaskId(taskEdit.getTask().getTaskId());
			} else {
				context.displayMessage("ERROR_CANNOT_REDO");
			}
		} catch (CannotRedoException e) {
			context.displayMessage("ERROR_CANNOT_UNDO");
		}
	}

	/**
	 * Edits a task in the task list
	 * @param task The task to be added to the taskList
	 * @return 
	 */
	private static void editTask(int ID, String desc,String venue, Date startDate, Date endDate, Date startTime, Date endTime, Date deadlineDate, Date deadlineTime) {
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
			task = getTask(ID);
			if (task == null) {
				context.displayMessage("ERROR_TASK_NOT_FOUND");
				return;			// Terminate execution
			}
		} else {
			context.displayMessage("PARAM_SUBTITLE");
			context.displayMessage("PARAM_TASKID_NULL");
			return; 			// Terminate execution
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
			oldPeriod        = new Period(prevStartDate, prevEndDate);
		}
		
		if (prevDeadlineDate != null) {
			prevDeadlineTime = timeFormat.format(prevDeadlineDate);
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
				context.addTask(task);
				context.displayMessage("MESSAGE_EDIT_TASK");
			} else {
				context.displayMessage("WARNING_TASK_NOT_EDITED");
				context.displayMessage("HELP_HEADING");				
				context.displayMessage("HELP_EDIT_TASK");
			}
			context.setTaskId(task.getTaskId());
		} catch (ParseException e) {			
			e.printStackTrace();
			context.displayMessage("ERROR_DATEFORMAT");
		} 
	}
	
	// Utility function
	private static Date changeDateTime(Date date, String prevTimeString) throws ParseException {
		String test1 = dateFormat.format(date);
		return new SimpleDateFormat("dd/M/yyyy HHmm").parse(test1.split(" ")[0] + " " + prevTimeString);
	}

	/**
	 * Adds a task to the task list
	 * @param task The task to be added to the taskList
	 */
	private static void addTask(String desc, String venue, Date startDate, Date endDate, Date startTime, Date endTime, Date deadlineDate, Date deadlineTime) {
		if(desc != null){
			Task task = null;
			
			try{
				task = createTask(currentTaskId+1, desc, venue, startDate, endDate, startTime, endTime, deadlineDate, deadlineTime);
			} catch (IllegalArgumentException e){
				context.displayMessage("ERROR_START_BEFORE_END");
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
			context.addTask(task);
			context.displayMessage("MESSAGE_ADD_TASK");
		} else {
			context.displayMessage("ERROR_NO_DESC");
		}
	}
	
	private static Task createTask(int taskID, String desc, String venue, Date startDate, 
			Date endDate, Date startTime, Date endTime, Date deadlineDate, Date deadlineTime) throws IllegalArgumentException{
		if (startTime != null && endTime != null){
			return new Task(taskID, desc, startDate, endDate, venue);// Event
		} else if (deadlineDate != null){
			return new Task(taskID, desc, deadlineDate, venue);		// Deadline task
		} else {
			return new Task(taskID, desc, venue);						// Floating task
		}
	}
	
	/**
	 * Remove a specific task from the file
	 * @param task The task to be deleted from the taskList
	 */
	private static void removeTask(int taskID) {
		Task task = getTask(taskID);

		if (taskList.remove(task)) {
			TaskEdit compoundEdit = new TaskEdit(task);
			TaskListEdit edit     = new TaskListEdit(task, taskList, currentTaskId, currentTaskId, false);
			edit.setSignificant();
			compoundEdit.addEdit(edit);
			compoundEdit.end();
			undoManager.addEdit(compoundEdit);
			context.addTask(task);
			context.displayMessage("MESSAGE_DELETE_TASK");
			context.setTaskId(task.getTaskId());

		} else {
			context.displayMessage("ERROR_TASK_NOT_FOUND");		
		}
	}
	
	/**
	 * Shows the Help menu to the user
	 */
	private static void showHelpMenu() {
		context.displayMessage("HELP_TITLE");
		context.displayMessage("HELP_SUBTITLE");
		context.displayMessage("HELP_ADD_TASK");
		context.displayMessage("HELP_DISPLAY");
		context.displayMessage("HELP_EDIT_TASK");
		context.displayMessage("HELP_UNDO");
		context.displayMessage("HELP_REDO");
		context.displayMessage("HELP_DONE");
		context.displayMessage("HELP_UNDONE");
		context.displayMessage("HELP_DELETE_TASK");
		context.displayMessage("HELP_HELP");
		context.displayMessage("HELP_EXIT");

	}
	
	/**
	 * Displays all the current tasks in the taskList
	 */
	private static void displayAllTasks(ArrayList<Task> list) {
		for(Task task:list) {
			context.addTask(task);
		}
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
	
	/**
	 * Returns a task with taskID if found, null otherwise
	 * @param taskId
	 * @return Task or null
	 */
	public static Task getTask(int taskID){
		for(int i = 0; i < taskList.size(); i++){
			if(taskList.get(i).getTaskId() == taskID){
				return taskList.get(i);
			}
		}
		return null;
	}
	
	public static ArrayList<Task> searchTasks(HashMap<PARAMETER, Object> parsedParamTable){
		ArrayList<Task> searchResult = new ArrayList<Task>(50);
		Task compareTask = null;
		
		compareTask = new Task((int)parsedParamTable.get(PARAMETER.TASKID),
				(String)parsedParamTable.get(PARAMETER.DESC),
				(String)parsedParamTable.get(PARAMETER.VENUE), 
				(Date)parsedParamTable.get(PARAMETER.START_DATE),
				(Date)parsedParamTable.get(PARAMETER.END_DATE), 
				//(Date)parsedParamTable.get(PARAMETER.START_TIME),
				//(Date)parsedParamTable.get(PARAMETER.END_TIME),
				(Date)parsedParamTable.get(PARAMETER.DEADLINE_DATE),
				//(Date)parsedParamTable.get(PARAMETER.DEADLINE_TIME),
				false,
				false,
				false);
		
		for(Task t : taskList){
			if(isTaskSameFields(compareTask, t)){
				searchResult.add(t);
			}
		}
		return searchResult;
	}
	
	private static boolean isTaskSameFields(Task compareTask, Task taskListTask) {
		return
			(compareTask.getTaskId()		== -1 	|| 
				compareTask.getTaskId() == taskListTask.getTaskId() 						)&&
			(compareTask.getStartDateTime()	== null	|| (taskListTask.getStartDateTime() != null &&
				compareTask.getStartDateTime().before(taskListTask.getStartDateTime()) 				))&&
			(compareTask.getEndDateTime()	== null	|| (taskListTask.getEndDateTime() != null   &&
				compareTask.getEndDateTime().after(taskListTask.getEndDateTime()) 					))&&
			(compareTask.getDeadline() 		== null	|| (taskListTask.getDeadline() != null 	    &&
				compareTask.getDeadline().before(taskListTask.getDeadline()) 						))&&
			(compareTask.getVenue()			== null || (taskListTask.getVenue() != null 	    &&
					taskListTask.getVenue().toLowerCase().contains(
							compareTask.getVenue().toLowerCase())									))&&
			(compareTask.getDescription()	== null || (taskListTask.getDescription() != null 	&&
					taskListTask.getDescription().toLowerCase().contains(
							compareTask.getDescription().toLowerCase())								))&&
			
			//TODO:search for boolean values 
			//compareTask.isDone() == taskListTask.isDone()
			//compareTask.isPastDeadline() == taskListTask.isPastDeadline()
			//compareTask.isHasEnded() == taskListTask.isHasEnded()
			
			(!compareTask.isEmpty()															)
			;
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
		} else if (commandTypeString.equalsIgnoreCase("help")) {
			return COMMAND_TYPE.HELP;
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
		currentTaskId = fileIO.getMaxTaskId();
	}

	public static void setCurrentTaskId(int _currentTaskId) {
		currentTaskId = _currentTaskId;
	}
}