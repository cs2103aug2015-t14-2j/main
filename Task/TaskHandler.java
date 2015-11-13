package Task;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
 *  @@author A0097689
 */
@SuppressWarnings("unused")
public class TaskHandler {
	private static final Logger LOGGER = Logger.getLogger(TaskHandler.class.getName());
	
	private static final boolean TASK_DONE    	= true;
	private static final boolean TASK_NOT_DONE	= false;
	
	private static final int ALL_TASKS = -2;
	
	private static Calendar             calendar        = Calendar.getInstance();
	private static SimpleDateFormat     dateFormat      = new SimpleDateFormat("dd/M/yyyy HHmm");
	private static ArrayList<Task>      taskList        = new ArrayList<Task>(50);
	private static String 			    defaultFilePath = "./data/calendar.json";		// relative path to calendar.json 
	private static int 				    currentTaskId;          
	private static FileIO               fileIO;
	private static TaskUndoManager      undoManager;
	private static Context              context         = Context.getInstance();
	
	/**
	 * Initialize settings, search for application files etc.
	 * @param args The file path to load the file in
	 * @return 
	 */
	public static void init(String[] args) {
		String localFilePath = determineFilePath(args);
		LOGGER.setLevel(Level.INFO);
		undoManager   = new TaskUndoManager();
		dateFormat.setCalendar(calendar);
		fileIO        = FileIO.getInstance();
		fileIO.setFilePath(localFilePath);
		taskList      = fileIO.readFromFile();
		currentTaskId = fileIO.getMaxTaskId();
		context.clearAllMessages();
		updateTaskStatus();
	}
	
	public static void inputFeedBack(String input){
		if(isValidCommand(input)) {
			executeCommand(input);
		}
	}

	private static String determineFilePath(String[] args) {
		String localFilePath;
		if (args.length == 1) {
			localFilePath = args[0];
		} else {
			localFilePath = TaskHandler.defaultFilePath;
		}
		return localFilePath;
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

		// Pre-processing
		context.setTaskId(0);
		
		switch (command) {
			case PATH:
				changeFilePath(userInput);
				break;
			case FILEOPEN:
				taskList = fileIO.readFromFile();
				setCurrentTaskId(fileIO.getMaxTaskId());
				context.displayMessage("MESSAGE_OPEN");
				context.displayMessage("MESSAGE_DISPLAY_ALL");
				displayFloatingTasks(taskList);
				break;
			case FILESAVE:
				fileIO.writeToFile(taskList);
				context.displayMessage("MESSAGE_SAVE");
				displayFloatingTasks(taskList);
				break;
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
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
				if (taskList.isEmpty()) {
					context.displayMessage("ERROR_EMPTY_TASKLIST");
				} else {
					if(removeFirstWord(userInput).length() == 0){
						context.displayMessage("MESSAGE_DISPLAY_ALL");
					} else if(searchTasks(parsedParamTable).size() == 0){
						context.displayMessage("ERROR_NO_RESUlTS_FOUND");
						break;
					} else {
						context.displayMessage("MESSAGE_DISPLAY");	
					}			
					displayAllTasks(searchTasks(parsedParamTable));
				}
				setCalendarView(parsedParamTable);
				break;
			case EDIT_TASK:
				parsedParamTable = StringParser.getValuesFromInput(command, removeFirstWord(userInput));
				editTask((int)parsedParamTable.get(PARAMETER.TASKID),
					(PARAMETER[])parsedParamTable.get(PARAMETER.DELETE_PARAMS),
					(String)parsedParamTable.get(PARAMETER.DESC),
					(String)parsedParamTable.get(PARAMETER.VENUE), 
					(Date)parsedParamTable.get(PARAMETER.START_DATE),
					(Date)parsedParamTable.get(PARAMETER.END_DATE), 
					(Date)parsedParamTable.get(PARAMETER.START_TIME),
					(Date)parsedParamTable.get(PARAMETER.END_TIME),
					(Date)parsedParamTable.get(PARAMETER.DEADLINE_DATE),
					(Date)parsedParamTable.get(PARAMETER.DEADLINE_TIME),
					(Date)parsedParamTable.get(PARAMETER.DATE));
				
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
				fileIO.writeToFile(taskList);
				Platform.exit();
				System.exit(0);
			default: //Add task
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
		// Common methods to execute regardless of command
		setCalendarData();
		updateAllTaskFlags(taskList);
	}

	/**
	 * Change file path based on user command
	 * @param user input 
	 */
	private static void changeFilePath(String userInput) {
		String filepath = removeFirstWord(userInput);
		if (getWordCount(filepath) == 0) {
			// Display current filepath
			context.setFilePath(fileIO.getCanonicalPath());
			context.displayMessage("MESSAGE_CURRENT_PATH");
		} else {
			if (fileIO.setFilePath(filepath)) {
				context.displayMessage("MESSAGE_FILE_FOUND");
			} else {
				context.displayMessage("HELP_PATH");
			};
			context.displayMessage("MESSAGE_PATH");
		}
		displayFloatingTasks(taskList);
	}

	private static void updateAllTaskFlags(ArrayList<Task> taskList) {
		for(Task t:taskList){
			t.setFlags(t.isDone());
		}
		
	}

	/**
	 * TODO:
	 * @param i
	 * @param taskDone
	 * @return
	 */
	private static void completeTask(int taskID, Boolean isDone) {
		boolean notFound = true;
		for (Task t:taskList){
			if (t.getTaskId() == taskID){
				Boolean prevDone      = t.isDone();
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
	private static void editTask(int ID, PARAMETER[] deleteParams, String desc,String venue, Date startDate, Date endDate, Date startTime, Date endTime, Date deadlineDate, Date deadlineTime, Date keyDate) {
		// Declare local variables
		SimpleDateFormat timeFormat      = new SimpleDateFormat("HHmm");

		Task task          = null;
		Date _deadlineDate = null;
		Date prevStartDate;
		Date prevEndDate;
		Date prevDeadlineDate;
		Period oldPeriod = null;
		Period newPeriod;
		
		Date prevStartTime = null;
		Date prevEndTime = null;
		Date prevDeadlineTime = null;

		boolean isUpdated = false;
		Boolean prevIsdone;
		
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

		
		try {
			prevStartDate       = task.getStartDateTime();
			prevEndDate         = task.getEndDateTime();
			prevDeadlineDate    = task.getDeadline();
			prevIsdone 			= task.isDone();
			if(prevStartDate != null && prevEndDate != null){
				// Get the original time of the day for start and end of the event
				prevStartTime    = getTimeOnly(prevStartDate);
				prevEndTime      = getTimeOnly(prevEndDate);
				oldPeriod        = new Period(prevStartDate, prevEndDate);
			}
			
			
			if (prevDeadlineDate != null) {
				prevDeadlineTime = getTimeOnly(prevDeadlineDate);
			}
			
			if (keyDate != null) {
				startDate = keyDate;
				endDate   = keyDate;
			}
			// Set startDate, startTime, endDate and endTime
			// E.g. edit 2 from 12/10/15 1200 to 13/10/15 1400
			// E.g. edit 2 on 12/10/15 from 1200 to 1400
			if (startDate != null && startTime != null && endDate != null && endTime != null){
				startDate = combineDateTime(startDate, startTime);
				endDate   = combineDateTime(endDate, endTime);

				newPeriod = new Period(startDate, endDate);
				task.setStartDateTime(startDate);
				task.setEndDateTime(endDate);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				
				compoundEdit.addEdit(edit);
				isUpdated  = true;
			}

			// Set startDate and endDate, without changing startTime and endTime
			// E.g. edit 2 on 12/10/15
			if (startDate != null && startTime == null && endDate != null && endTime == null && prevEndDate != null) {
				
				startDate	= combineDateTime(startDate, prevStartTime);
				endDate		= combineDateTime(endDate, prevEndTime);
				
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

			// Set startDate, startTime, endDate according to user; endTime remains unchanged
			// E.g. edit 2 on 12/10/15 from 1400
			if (startDate != null && startTime != null && endDate != null && endTime == null && prevEndDate != null) {
				startDate = combineDateTime(startDate, startTime);
				endDate   = combineDateTime(endDate, prevEndTime);

				newPeriod = new Period(startDate, endDate);

				task.setStartDateTime(startDate);
				task.setEndDateTime(endDate);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated  = true;
			}
			
			// Set startDate only
			// E.g. edit 2 from 12/10/15
			if (startDate != null && startTime == null && endDate == null && endTime == null && prevEndDate != null) {								
				startDate = combineDateTime(startDate, prevStartTime);
				newPeriod = new Period(startDate, prevEndDate);

				task.setStartDateTime(startDate);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated     = true;
			}

			// Set startTime only
			// E.g. edit 2 from 12pm
			if (startDate == null && startTime != null && endDate == null && endTime == null && prevEndDate != null) {								
				startDate = combineDateTime(prevStartDate, startTime);
				newPeriod = new Period(startDate, prevEndDate);

				task.setStartDateTime(startDate);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated     = true;
			}

			// Set endDate, endTime
			// E.g. edit 2 to 12/10/15 1400
			if (startDate == null && startTime == null && endDate != null && endTime != null && prevEndDate != null) {
				endDate   = combineDateTime(endDate, endTime);
				newPeriod = new Period(prevStartDate, endDate);

				task.setEndDateTime(endDate);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated = true;
			}
			
			// Set startDate, endDate, endTime according to user, startTime remains unchanged
			// E.g. edit 2 on 12/10/15 to 1600
			if (startDate != null && startTime == null && endDate != null && endTime != null && prevStartDate != null && prevEndDate != null) {
				startDate = combineDateTime(startDate, prevStartTime);
				endDate   = combineDateTime(endDate, endTime);

				newPeriod = new Period(startDate, endDate);

				task.setEndDateTime(endDate);
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated = true;
			}	

			// Set endDate only
			// E.g. edit 2 to 12/10/15
			if (startDate == null && startTime == null && endDate != null && endTime == null && prevStartDate != null) {				
				endDate    = combineDateTime(endDate, prevEndTime);
				
				newPeriod = new Period(prevStartDate, endDate);
				
				task.setEndDateTime(endDate);	
				edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
				compoundEdit.addEdit(edit);
				isUpdated   = true;			
			}

			// Set endTime only
			// E.g. edit 2 to 2pm
			if (startDate == null && startTime == null && endDate == null && endTime != null && prevStartDate != null) {				
				endDate    = combineDateTime(prevEndDate, endTime);
				
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
				_deadlineDate = combineDateTime(deadlineDate, prevDeadlineTime);
				task.setDeadline(_deadlineDate);
				edit = new TaskDeadlineEdit(task, prevDeadlineDate, _deadlineDate);
				compoundEdit.addEdit(edit);
				isUpdated        = true;
			}
			
			if(deleteParams != null){
				for(PARAMETER p:deleteParams){
					if(p != null){
						if(p.equals(PARAMETER.START_TIME)||p.equals(PARAMETER.END_TIME)){
							task.setPeriod(null);
							newPeriod = null;
							edit = new TaskPeriodEdit(task, oldPeriod, newPeriod);
							compoundEdit.addEdit(edit);
						} else if (p.equals(PARAMETER.DEADLINE_TIME)){
							task.setDeadline(null);
							edit = new TaskDeadlineEdit(task, prevDeadlineDate, _deadlineDate);
							compoundEdit.addEdit(edit);
							if(task.getStartDateTime() != null){
								edit = new TaskDoneEdit(task, prevIsdone, null);
								compoundEdit.addEdit(edit);
							}
						} else if(p.equals(PARAMETER.VENUE)){
							String oldVenue = task.getVenue();
							task.setVenue(null);
							edit = new TaskVenueEdit(task, oldVenue, venue);
							compoundEdit.addEdit(edit);
						} else if(p.equals(PARAMETER.DESC)){
							String oldDesc = task.getDescription();
							edit = new TaskDescEdit(task, oldDesc, null);
							task.setDescription(null);
							compoundEdit.addEdit(edit);
						}
						
						isUpdated        = true;
					}
				}
			}
			
			if(isUpdated) {
				// Set one significant edit
				UndoableEdit lastEdit = compoundEdit.lastEdit();
				UndoableSignificantEdit edit1 = (UndoableSignificantEdit) lastEdit;
				compoundEdit.end();
				edit1.setSignificant();
				undoManager.addEdit(compoundEdit);
				context.displayMessage("MESSAGE_EDIT_TASK");
			} else {
				context.displayMessage("WARNING_TASK_NOT_EDITED");
				context.displayMessage("HELP_HEADING");				
				context.displayMessage("HELP_EDIT_TASK");
			}
			context.addTask(task);
			context.setTaskId(task.getTaskId());
		} catch (ParseException e) {			
			e.printStackTrace();
			context.displayMessage("ERROR_DATEFORMAT");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			context.displayMessage("ERROR_START_BEFORE_END");
		}
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
			TaskListEdit edit     = new TaskListEdit(task, taskList, currentTaskId, currentTaskId+1,true);
			edit.setSignificant();
			compoundEdit.addEdit(edit);
			compoundEdit.end();

			taskList.add(task);
			undoManager.addEdit(compoundEdit);
			currentTaskId += 1;
			context.addTask(task);
			context.setTaskId(task.getTaskId());
			context.displayMessage("MESSAGE_ADD_TASK");
		} else {
			context.displayMessage("ERROR_NO_DESC");
		}
	}
	
	/**
	 * Creates a new task using the suitable contructor based on whatever information we have
	 */
	private static Task createTask(int taskID, String desc, String venue, Date startDate, 
			Date endDate, Date startTime, Date endTime, Date deadlineDate, Date deadlineTime) throws IllegalArgumentException{
		
		if (startTime != null && endTime != null){
			Date startDateTime = combineDateTime(startDate, startTime);
			Date endDateTime   = combineDateTime(endDate, endTime);
			return new Task(taskID, desc, startDateTime, endDateTime, venue);// Event
		} else if (deadlineTime != null){
			return new Task(taskID, desc, deadlineTime, venue);		// Deadline task
		} else {
			return new Task(taskID, desc, null, venue);				// Floating task
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
			TaskListEdit edit     = new TaskListEdit(task, taskList, currentTaskId, currentTaskId,false);
			edit.setSignificant();
			compoundEdit.addEdit(edit);
			compoundEdit.end();
			undoManager.addEdit(compoundEdit);
			context.addTask(task);
			context.displayMessage("MESSAGE_DELETE_TASK");
			context.setTaskId(task.getTaskId());

		} else if(taskID == ALL_TASKS && taskList.size() > 0){
			task = taskList.get(0);
			TaskEdit compoundEdit = new TaskEdit(task);
			TaskListEdit edit     = new TaskListEdit(taskList, currentTaskId, currentTaskId,false);
			edit.setSignificant();
			while(taskList.size() != 0){
				task = taskList.get(0);
				taskList.remove(task);
			}
			compoundEdit.addEdit(edit);
			compoundEdit.end();
			undoManager.addEdit(compoundEdit);
			context.displayMessage("MESSAGE_DISPLAY_ALL");
		} else {
			context.displayMessage("ERROR_TASK_NOT_FOUND");		
		}
	}
	
	/**
	 * Shows the Help menu to the user
	 */
	private static void showHelpMenu() {
		context.displayMessage("HELP_TITLE");
		context.displayMessage("HELP_HEADING");
		context.displayMessage("HELP_SUBTITLE");
		context.displayMessage("HELP_ADD_TASK");
		context.displayMessage("HELP_PATH");
		context.displayMessage("HELP_FILEOPEN");
		context.displayMessage("HELP_FILESAVE");
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
	 * Displays only floating tasks
	 */
	private static void displayFloatingTasks(ArrayList<Task> list) {
		for(Task task:list) {
			if (task.isFloating()) {
				context.addTask(task);
			}
		}
	}
	
	/**
	 * @@author A0145472E
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
	
	/**
	 * @@author A0145472E
	 * 
	 * @param parsedParamTable
	 * @return
	 */
	public static ArrayList<Task> searchTasks(HashMap<PARAMETER, Object> parsedParamTable){
		ArrayList<Task> searchResult = new ArrayList<Task>(50);
		Task compareTask = null;
		
		Boolean _isDone = null;
		Boolean _isPast = null;
		Boolean _hasEnded = null;
		
		if(!isDisplayAll(parsedParamTable)){
			_isDone 	= (parsedParamTable.get(PARAMETER.IS_DONE) != null)   ? 
					(Boolean)parsedParamTable.get(PARAMETER.IS_DONE) 	: false;
			_isPast 	= (Boolean)parsedParamTable.get(PARAMETER.IS_PAST);
			_hasEnded 	= (Boolean)parsedParamTable.get(PARAMETER.HAS_ENDED);
		}
		
		compareTask = new Task((int)parsedParamTable.get(PARAMETER.TASKID),
				(String)parsedParamTable.get(PARAMETER.DESC),
				(String)parsedParamTable.get(PARAMETER.VENUE), 
				(Date)parsedParamTable.get(PARAMETER.START_DATE),
				(Date)parsedParamTable.get(PARAMETER.END_DATE), 
				(Date)parsedParamTable.get(PARAMETER.DEADLINE_DATE),
				_isDone,
				_isPast,
				_hasEnded);
		System.out.println(compareTask.toString());
		
		for (Task t : taskList) {
			if (isTaskSameFields(compareTask, t)) {
				searchResult.add(t);
			}
		}

		// Sets the TaskID for display on fullCalendar if there is only 1 result
		if (searchResult.size() == 1) {
			context.setTaskId(searchResult.get(0).getTaskId());
		}
		return sortTasks(searchResult);
	}

	private static boolean isDisplayAll(HashMap<PARAMETER, Object> parsedParamTable) {
		return parsedParamTable.get(PARAMETER.SPECIAL) != null && (boolean)parsedParamTable.get(PARAMETER.SPECIAL);
	}
	
	/**
	 * @@author A0145472E
	 * 
	 * @param searchResult
	 * @return
	 */
	private static ArrayList<Task> sortTasks(ArrayList<Task> searchResult) {
		ArrayList<Task> periodsAndDeadlines = new ArrayList<>();
		ArrayList<Task> floating = new ArrayList<>();
		
		ArrayList<Task> result = new ArrayList<>();
		
		for(Task t:searchResult){
			if((t.getStartDateTime() != null && t.getEndDateTime() != null) || t.getDeadline() != null){
				periodsAndDeadlines.add(t);
			} else {
				floating.add(t);
			}
		}
		
		bubbleSortTasks(periodsAndDeadlines,containsEarlierThanToday(periodsAndDeadlines));
		
		result.addAll(periodsAndDeadlines);
		result.addAll(floating);
		
		return result;
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param periodsAndDeadlines
	 * @return
	 */
	private static boolean containsEarlierThanToday(ArrayList<Task> periodsAndDeadlines) {
		for(Task t :periodsAndDeadlines){
			if((t.getDeadline() != null && t.getDeadline().before(new Date())) 					|| 
			   (t.getDeadline() != null && t.getDeadline().equals(new Date()))					||
			   (t.getStartDateTime() != null && t.getStartDateTime().before(new Date()))		||
			   (t.getStartDateTime() != null && t.getStartDateTime().equals(new Date()))){
				return true;
			}
		}
		return false;
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param taskListToSort
	 */
	private static void bubbleSortTasks(ArrayList<Task> taskListToSort, boolean isSortAsending) {
		for (int i=0; i < taskListToSort.size() - 1;i++)
	    {
	        if(isPeriodDeadlineComp(taskListToSort.get(i),taskListToSort.get(i+1),isSortAsending))
	        {
	        	sendToEndOfList(taskListToSort, i);
	            bubbleSortTasks(taskListToSort,isSortAsending);
	        }
	    }
		
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param firstTask
	 * @param secondTask
	 * @return
	 */
	private static boolean isPeriodDeadlineComp(Task firstTask, Task secondTask, boolean isSortAsending) {
		
		Date compDateOne = deadlineOrPeriodDate(firstTask);
		Date compDateTwo = deadlineOrPeriodDate(secondTask);
		if(isSortAsending){
			return compDateOne.after(compDateTwo);
		} else {
			return compDateOne.before(compDateTwo);
		}
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param firstTask
	 * @return
	 */
	private static Date deadlineOrPeriodDate(Task firstTask) {
		if(firstTask.getDeadline() != null){
			return firstTask.getDeadline();
		} else {
			return firstTask.getStartDateTime();
		}
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param tasklist
	 * @param i
	 */
	private static void sendToEndOfList(ArrayList<Task> tasklist, int i) {
		tasklist.add(tasklist.get(i));
		tasklist.remove(i);
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param compareTask
	 * @param taskListTask
	 * @return
	 */
	private static boolean isTaskSameFields(Task compareTask, Task taskListTask) {
		if (compareTask.getDeadline() != null) {
			calendar.setTime(compareTask.getDeadline());
		} else {
			// do nothing, calendar displays current time by default
		}
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)-7);	// One week earlier
		
		return
			isSameTaskId(compareTask, taskListTask)				&&
			isBeforeDateTime(compareTask, taskListTask)			&&
			isAfterDateTime(compareTask, taskListTask)			&&
			isAfterDeadline(compareTask, taskListTask)			&&
			containsWithinVenue(compareTask, taskListTask)		&&
			containsWithinDescription(compareTask, taskListTask)&&
			isSameLogic(compareTask.isDone(), 
					taskListTask.isDone()) 						&&
			isSameLogic(compareTask.isPastDeadline(), 
					taskListTask.isPastDeadline()) 				&&
			isSameLogic(compareTask.isHasEnded(), 
					taskListTask.isHasEnded()) 					&&
			
			(!compareTask.isEmpty()															
					
			);
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param compareTask
	 * @param taskListTask
	 * @return
	 */
	private static boolean isSameLogic(Boolean compareTask, Boolean taskListTask) {
		return compareTask == null || 
				(compareTask == false && (taskListTask == null || taskListTask == false)) ||
				compareTask == taskListTask;
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param compareTask
	 * @param taskListTask
	 * @return
	 */
	private static boolean isAfterDeadline(Task compareTask, Task taskListTask) {
		return compareTask.getDeadline() 		== null	|| (taskListTask.getDeadline() != null 	    &&
			compareTask.getDeadline().after(taskListTask.getDeadline()) 							&& 
			(taskListTask.getDeadline().after(calendar.getTime())));
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param compareTask
	 * @param taskListTask
	 * @return
	 */
	private static boolean containsWithinDescription(Task compareTask, Task taskListTask) {
		return compareTask.getDescription()	== null || (taskListTask.getDescription() != null 		&&
				taskListTask.getDescription().toLowerCase().contains(
						compareTask.getDescription().toLowerCase()));
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param compareTask
	 * @param taskListTask
	 * @return
	 */
	private static boolean containsWithinVenue(Task compareTask, Task taskListTask) {
		return compareTask.getVenue()			== null || (taskListTask.getVenue() != null 	    &&
				taskListTask.getVenue().toLowerCase().contains(compareTask.getVenue().toLowerCase()));
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param compareTask
	 * @param taskListTask
	 * @return
	 */
	private static boolean isAfterDateTime(Task compareTask, Task taskListTask) {
		return compareTask.getEndDateTime()	== null	|| (taskListTask.getEndDateTime() != null   	&&
			(compareTask.getEndDateTime().after(taskListTask.getStartDateTime()) 					||
			 compareTask.getEndDateTime().equals(taskListTask.getStartDateTime())					));
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param compareTask
	 * @param taskListTask
	 * @return
	 */
	private static boolean isBeforeDateTime(Task compareTask, Task taskListTask) {
		return compareTask.getStartDateTime() == null || (taskListTask.getStartDateTime() != null 	&&
			(compareTask.getStartDateTime().before(taskListTask.getEndDateTime())					||
			 compareTask.getEndDateTime().equals(taskListTask.getStartDateTime())					));
	}

	/**
	 * @@author A0145472E
	 * 
	 * @param compareTask
	 * @param taskListTask
	 * @return
	 */
	private static boolean isSameTaskId(Task compareTask, Task taskListTask) {
		return compareTask.getTaskId()		== -1 	|| compareTask.getTaskId()		== ALL_TASKS ||
			compareTask.getTaskId() == taskListTask.getTaskId();
	}
	
	/** 
	 *  @@author A0097689
	 * Takes a single word and figure out the command
	 * @param commandTypeString The string containing a command
	 * @return The enum value corresponding to the commandTypeString
	 */
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if (commandTypeString == null) {
			throw new Error("command type string cannot be null!");
		}
		
		if (commandTypeString.equalsIgnoreCase("path")) {
			return COMMAND_TYPE.PATH;
		} else if (commandTypeString.equalsIgnoreCase("fileopen")) {
			return COMMAND_TYPE.FILEOPEN;
		} else if (commandTypeString.equalsIgnoreCase("filesave")) {
			return COMMAND_TYPE.FILESAVE;
		} else if (commandTypeString.equalsIgnoreCase("add")) {
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
	 * Removes the first word from a string and returns the second word
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

	/**
	 * Allow TaskListEdit to set currentTaskId for undo and redo operations
	 * @param taskID
	 */
	public static void setCurrentTaskId(int _currentTaskId) {
		currentTaskId = _currentTaskId;
	}

	private static void updateTaskStatus() {
		for (Task t : taskList) {
			Boolean new_status = t.determinePastDeadline();
			t.setPastDeadline(new_status);
		}
	}

	/**
	 * For display commands, set defaultDate in context with the correct defaultDate 
	 * so that fullCalendar can render the right date.
	 * @param paramTable
	 */
	private static void setCalendarView(HashMap<PARAMETER,Object> paramTable) {
		// FullCalendar uses moments.js which prefers ISO8601 formatted date strings
		SimpleDateFormat ISO8601 = new SimpleDateFormat("YYYY-MM-dd");
		if (paramTable.get(PARAMETER.START_DATE) != null) {
			context.setDefaultDate(ISO8601.format((Date)paramTable.get(PARAMETER.START_DATE)));
		} else if (paramTable.get(PARAMETER.DEADLINE_DATE) != null) {
			context.setDefaultDate(ISO8601.format((Date)paramTable.get(PARAMETER.DEADLINE_DATE)));
		}
	}

	/**
	 * Converts taskList into JSON string and pass to context object for fullCalendar
	 *
	 */
	private static void setCalendarData() {
		String jsonTaskString = fileIO.writeToString(taskList);
		context.setCalendarData(jsonTaskString);
	}

	// Utility function - return number of words in a string delimited by whitespace
	private static int getWordCount(String userInput) {
		// Trim leading and trailling whitespaces
		String trimmed = userInput.trim();
		if (trimmed.isEmpty()) {
			return 0;
		} else {
			return trimmed.split("\\s+").length;
		}
	}

	// Utility function
	private static Date changeDateTime(Date date, String prevTimeString) throws ParseException {
		String test1 = dateFormat.format(date);
		return new SimpleDateFormat("dd/M/yyyy HHmm").parse(test1.split(" ")[0] + " " + prevTimeString);
	}

	// Utility function that combines a date with date and date with time and returns a single date obj
	private static Date combineDateTime(Date datePart, Date timePart) {
		SimpleDateFormat dateFormat     = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat     = new SimpleDateFormat("HHmm");
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HHmm");

		String dateString = dateFormat.format(datePart);
		String timeString = timeFormat.format(timePart);

		try {
			Date date = dateTimeFormat.parse(dateString + " " + timeString);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Date getTimeOnly(Date dateTime) throws ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
		String timeString           = timeFormat.format(dateTime);
		return timeFormat.parse(timeString);
	}
}