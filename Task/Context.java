package Task;

/**
 *  @@author A0097689
 */

import Task.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.Locale;
import java.util.HashMap;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.Writer;

@SuppressWarnings("unused")
public class Context {
	private static Context context = null;
	
	// Variables for displaying on screen based on user input
	private static String FILEPATH;
	private static int    TASKID      = 0;
	private static String DEFAULT_DATE;

	// Define fullCalendar view options here, display on HTML template later to affect calendar view
	private static Pair   VIEW_DAY    = new Pair("agendaDay");
	private static Pair   VIEW_MONTH  = new Pair("month");
	private static Pair   VIEW_WEEK   = new Pair("agendaWeek");

	// Define success messages here
	private static Pair MESSAGE_WELCOME        = new Pair("Welcome to TaskBuddy!");
	private static Pair MESSAGE_PATH		   = new Pair("Your new path is: %s");
	private static Pair MESSAGE_SAVE		   = new Pair("Your calendar has been saved to %s!");
	private static Pair MESSAGE_OPEN		   = new Pair("Opening calendar at path %s");
	private static Pair MESSAGE_ADD_TASK       = new Pair("Successfully added task.");
	private static Pair MESSAGE_GET_TASK       = new Pair("Task %d returned");
	private static Pair MESSAGE_DISPLAY_ALL    = new Pair("All tasks displayed.");
	private static Pair MESSAGE_DISPLAY		   = new Pair("Search results:");
	private static Pair MESSAGE_SEARCH_TASK    = new Pair("Here are tasks matching your keywords:");
	private static Pair MESSAGE_DELETE_TASK    = new Pair("Task %d has been deleted");
	private static Pair MESSAGE_EDIT_TASK      = new Pair("Task %d has been updated!");
	private static Pair MESSAGE_UNDO_TASK      = new Pair("Successfully undoed change(s) to Task %d.");
	private static Pair MESSAGE_REDO_TASK      = new Pair("Successfully redoed change(s) to Task %d.");
	private static Pair MESSAGE_DONE_TASK      = new Pair("Successfully updated Task %d to completed.");
	private static Pair MESSAGE_UNDONE_TASK    = new Pair("Successfully updated Task %d to uncompleted.");
	private static Pair MESSAGE_EXIT           = new Pair("Thanks for using TaskBuddy! Changes saved to disk.");
	private static Pair MESSAGE_FILE_FOUND     = new Pair("Found file at location %s");
	
	/** 
	 * Define warning messages here
	 * Warnings are less severe than errors.
	 * To communicate to the user they may have done something unintentional
	 */
	private static Pair WARNING_DEADLINE_BEFORE_NOW = new Pair("WARNING: You have specified a deadline that is before the current time");
	private static Pair WARNING_TASK_NOT_EDITED     = new Pair("Task %d was not edited.");
	private static Pair WARNING_EMPTY_FILE          = new Pair("WARNING: No file detected at path. Created new empty json file.");

	// Define error messages here
	private static Pair ERROR_INVALID_COMMAND  = new Pair("Invalid Command.");
	private static Pair ERROR_EMPTY_TASKLIST   = new Pair("You have no tasks!");
	private static Pair ERROR_TASK_NOT_FOUND   = new Pair("The task was not found!");
	private static Pair ERROR_NO_RESUlTS_FOUND = new Pair("No results were found!");
	private static Pair ERROR_IO_TASK   	   = new Pair("The task could not be changed!");
	private static Pair ERROR_NO_DESC   	   = new Pair("You must have a description for your task!");
	private static Pair ERROR_CANNOT_UNDO      = new Pair("No more changes to undo.");
	private static Pair ERROR_CANNOT_REDO      = new Pair("No more changes to redo.");
	private static Pair ERROR_START_BEFORE_END = new Pair("You have entered an end time that is before start time!");
	private static Pair ERROR_DATEFORMAT       = new Pair("You have entered an invalid date and time. Note that we follow American date format mm/dd/yy.");
	private static Pair ERROR_TRIGGER_ERROR    = new Pair("ERROR! The trigger shortcut has been interrupted and aborted.");
	private static Pair ERROR_MALFORMED_TASK   = new Pair("ERROR! Corrupted task region. Task %d has been discarded.");
	private static Pair ERROR_MALFORMED_FILE   = new Pair("ERROR! Corrupted file region. Rest of file cannot be read.");
	private static Pair ERROR_MALFORMED_KEY    = new Pair("ERROR! File does not match expected format. Restart program with a new file location.");
	private static Pair ERROR_FILE_IO          = new Pair("ERROR! Cannot read from specified file location.");
	private static Pair ERROR_HTML_TEMPLATE    = new Pair("ERROR! Cannot read html template.");
	
	// Define help messages here
	private static Pair HELP_TITLE             = new Pair("**********************************************************************Help menu for TaskBuddy!*****************************************************************");
	private static Pair HELP_HEADING           = new Pair("Please follow the following command format:");
	private static Pair HELP_SUBTITLE          = new Pair("[COMMAND]   [FORMAT]                                                                                                                                    ");
	private static Pair HELP_PATH              = new Pair("  PATH          : path [absolute filepath]");
	private static Pair HELP_FILEOPEN          = new Pair("  FILEOPEN  : fileopen");
	private static Pair HELP_FILESAVE          = new Pair("  FILESAVE   : filesave");
	private static Pair HELP_ADD_TASK          = new Pair("  ADD            : add do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"              ");
	private static Pair HELP_DISPLAY           = new Pair("  DISPLAY    : display                                                                                                                                   ");
	private static Pair HELP_EDIT_TASK         = new Pair("  EDIT           : edit [task-id] do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"      ");
	private static Pair HELP_UNDO              = new Pair("  UNDO        : undo                                                                                                                                      ");
	private static Pair HELP_REDO              = new Pair("  REDO        : redo                                                                                                                                      ");
	private static Pair HELP_DONE              = new Pair("  DONE        : done [task-id]                                                                                                                            ");
	private static Pair HELP_UNDONE            = new Pair("  UNDONE   : undone [task-id]                                                                                                                          ");	
	private static Pair HELP_DELETE_TASK       = new Pair("  DELETE     : delete [task-id]                                                                                                                          ");
	private static Pair HELP_HELP              = new Pair("  HELP         : help                                                                                                                                      ");
	private static Pair HELP_EXIT              = new Pair("  EXIT          : exit                                                                                                                                      ");
	
	// Define parameter specific messages here
	private static Pair PARAM_SUBTITLE      = new Pair("There are errors in the following parameters:");
	private static Pair PARAM_TASKID_NUM    = new Pair("TaskID          : Invalid number. Please enter a number greater than 0.");
	private static Pair PARAM_TASKID_NULL   = new Pair("TaskID          : Missing value. Please enter a number.");
	private static Pair PARAM_DESC          = new Pair("Description     : Invalid value. Please try again.");
	private static Pair PARAM_VENUE         = new Pair("Venue           : Invalid value. Please try again");
	private static Pair PARAM_START_DATE    = new Pair("Start Date      : Invalid date format.");
	private static Pair PARAM_END_DATE      = new Pair("End Date        : Invaild date format.");
	private static Pair PARAM_START_TIME    = new Pair("Start Time      : Invalid time format. Please be more specfic.");
	private static Pair PARAM_END_TIME      = new Pair("End Time        : Invalid time format. Please be more specific.");
	private static Pair PARAM_DEADLINE_DATE = new Pair("Deadline Date   : Invalid date format.");
	private static Pair PARAM_DEADLINE_TIME = new Pair("Deadline Time   : Invalid time format. Please be more specific.");

	// TaskList - set of tasks that will be displayed on the right panel
	private static ArrayList<Task> displayTaskSet = new ArrayList<Task>();
	
	private Context () {}
	
	// Singleton pattern, only one context object throughout application
	public static Context getInstance() {
		if (context==null) {
			context = new Context();
			return context;
		} else {
			return context;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void displayMessage(String fieldName) {
		Class thisClass = Context.class;
		
		try {
			Field field = thisClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			Pair pair = (Pair) field.get(context);
			pair.displayMessage();

		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public void clearMessage(String fieldName) {
		Class thisClass = Context.class;
		
		try {
			Field field = thisClass.getDeclaredField(fieldName);
			if (field.getType().isInstance(Pair.class)) {
				Object o = field.get(context);
				field.setAccessible(true);
				Pair pair = (Pair) o;
				pair.clearMessage();
			}

		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}


	}

	public void addTask(Task task) {
		displayTaskSet.add(task);
	}

	@SuppressWarnings("rawtypes")
	public void clearAllMessages() {
		Class thisClass = Context.class;

		Field[] fields = thisClass.getDeclaredFields();
		for(Field field:fields) {
			Object o;
			try {
				o = field.get(context);
				if (field.getType().equals(Pair.class)) {
					field.setAccessible(true);
					Pair pair = (Pair) o;
					pair.clearMessage();
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			displayTaskSet.clear();
		} 
	}

	public void setTaskId(int _taskId) {
		TASKID = _taskId;
	}

	public void setFilePath(String path) {
		FILEPATH = path;
	}

	public void setDefaultDate(String datestring) {
		DEFAULT_DATE = datestring;
	}

	@SuppressWarnings("rawtypes")
	public HashMap<String, Object> getDataModel() {
		Class thisClass = Context.class;
		HashMap<String, Object> dataModel = new HashMap<String, Object>();
		ArrayList<String> success_messages  = new ArrayList<String>();
		ArrayList<String> warning_messages  = new ArrayList<String>();
		ArrayList<String> help_messages     = new ArrayList<String>();
		ArrayList<String> param_messages    = new ArrayList<String>();
		ArrayList<String> error_messages    = new ArrayList<String>();
		ArrayList<String> view_messages     = new ArrayList<String>();
		ArrayList<Task>   taskList          = new ArrayList<Task>();

		Field[] fields = thisClass.getDeclaredFields();
		for(Field field:fields) {
			Object o;
			try {
				o = field.get(context);
				if (field.getType().equals(Pair.class)) {
					field.setAccessible(true);
					Pair pair = (Pair) o;
					if (pair.getValue()) {
						if (field.getName().contains("MESSAGE")) {
							success_messages.add(formatString(field.getName(), pair.getKey()));
						}
						if (field.getName().contains("WARNING")) {
							warning_messages.add(formatString(field.getName(), pair.getKey()));
						}
						if (field.getName().contains("HELP")) {
							help_messages.add(formatString(field.getName(), pair.getKey()));
						}
						if (field.getName().contains("PARAM")) {
							param_messages.add(formatString(field.getName(), pair.getKey()));
						}
						if (field.getName().contains("ERROR")) {
							error_messages.add(formatString(field.getName(), pair.getKey()));
						}
						if (field.getName().contains("VIEW")) {
							view_messages.add(formatString(field.getName(), pair.getKey()));
						}
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		// Add tasks to dataModel
		if (!displayTaskSet.isEmpty()) {
			Iterator<Task> iterator = displayTaskSet.iterator();
			while (iterator.hasNext()) {
				Task task = iterator.next();
				taskList.add(task);
			}
		}
		// Read Json file as string and inject into the HTML template for fullCalendar to render on canvas
		String jsonData = "";
		try {
			String read_string;
			FileReader fr     = new FileReader(FILEPATH);
			BufferedReader br = new BufferedReader(fr);
			
			while ((read_string = br.readLine())!= null) {
				jsonData += read_string;
				
			}								
			br.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
			
		}
		dataModel.put("success_messages", success_messages);
		dataModel.put("warning_messages", warning_messages);
		dataModel.put("help_messages", help_messages);
		dataModel.put("param_messages", param_messages);
		dataModel.put("error_messages", error_messages);
		dataModel.put("view_messages", view_messages);
		dataModel.put("default_date", DEFAULT_DATE);
		dataModel.put("taskList", taskList);
		dataModel.put("jsonData", jsonData);
		return dataModel;
	}

	// Helper function to add variables into messages just before rendering.
	private String formatString(String field, String original){
		String result;
		if (field == "MESSAGE_ADD_TASK" || field == "MESSAGE_DELETE_TASK" 
			|| field == "MESSAGE_EDIT_TASK" || field == "MESSAGE_UNDO_TASK" 
			|| field == "MESSAGE_REDO_TASK" || field == "MESSAGE_UNDONE_TASK" 
			|| field == "MESSAGE_DONE_TASK" || field == "WARNING_TASK_NOT_EDITED") {
			result = String.format(original, TASKID);
		} else if (field == "MESSAGE_PATH" || field == "MESSAGE_OPEN" 
			|| field == "MESSAGE_SAVE" || field == "MESSAGE_FILE_FOUND") {
			result = String.format(original, FILEPATH);
		} else {
			result = original;
		}
		return result;
	}
}
