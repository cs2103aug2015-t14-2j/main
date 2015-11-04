package Task;

import Task.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public class Context {
	// Display settings
	private static Context context = null;
	private static boolean DEBUG = true;
	
	// HTML escape codes for color formatting in console output
	public static final String HTML_RESET = "</font>";
	public static final String HTML_BLACK = "<font color=\"black\">";
	public static final String HTML_RED = "<font color=\"red\">";
	public static final String HTML_ORANGE = "<font color=\"orange\">";
	public static final String HTML_GREEN = "<font color=\"green\">";
	public static final String HTML_YELLOW = "<font color=\"yellow\">";
	public static final String HTML_BLUE = "<font color=\"blue\">";
	public static final String HTML_PURPLE = "<font color=\"purple\">";
	public static final String HTML_CYAN = "<font color=\"cyan\">";
	public static final String HTML_WHITE = "<font color=\"white\">";
	
	// TaskID for editing, deleting or displaying a specific task
	private static int taskId = 0;

	// Define success messages here
	private static Pair MESSAGE_WELCOME        = new Pair(HTML_GREEN + "Welcome to TaskBuddy!" + HTML_RESET);
	private static Pair MESSAGE_ADD_TASK       = new Pair(HTML_GREEN + "Successfully added task." + HTML_RESET);
	private static Pair MESSAGE_GET_TASK       = new Pair(HTML_GREEN + "Task %d returned" + HTML_RESET);
	private static Pair MESSAGE_DISPLAY_ALL    = new Pair(HTML_GREEN + "All tasks displayed." + HTML_RESET);
	private static Pair MESSAGE_DISPLAY		   = new Pair(HTML_GREEN + "Search results:" + HTML_RESET);
	private static Pair MESSAGE_SEARCH_TASK    = new Pair(HTML_GREEN + "Here are tasks matching your keywords:" + HTML_RESET);
	private static Pair MESSAGE_DELETE_ALL_TASK= new Pair(HTML_GREEN + "All Tasks have been deleted" + HTML_RESET);
	private static Pair MESSAGE_DELETE_TASK    = new Pair(HTML_GREEN + "Task %d has been deleted" + HTML_RESET);
	private static Pair MESSAGE_EDIT_TASK      = new Pair(HTML_GREEN + "Task %d has been updated!" + HTML_RESET);
	private static Pair MESSAGE_UNDO_TASK      = new Pair(HTML_GREEN + "Successfully undoed change(s) to Task %d." + HTML_RESET);
	private static Pair MESSAGE_REDO_TASK      = new Pair(HTML_GREEN + "Successfully redoed change(s) to Task %d." + HTML_RESET);
	private static Pair MESSAGE_DONE_TASK      = new Pair(HTML_GREEN + "Successfully updated Task %d to completed." + HTML_RESET);
	private static Pair MESSAGE_UNDONE_TASK    = new Pair(HTML_GREEN + "Successfully updated Task %d to uncompleted." + HTML_RESET);
	private static Pair MESSAGE_EXIT           = new Pair(HTML_GREEN + "Thanks for using TaskBuddy! Changes saved to disk." + HTML_RESET);
	
	// Define warning messages here
	private static Pair WARNING_DEADLINE_BEFORE_NOW = new Pair(HTML_ORANGE + "WARNING: You have specified a deadline that is before the current time" + HTML_RESET);
	private static Pair WARNING_TASK_NOT_EDITED     = new Pair(HTML_ORANGE + "Task %d was not edited." + HTML_RESET);

	// Define error messages here
	private static Pair ERROR_INVALID_COMMAND  = new Pair(HTML_RED + "Invalid Command." + HTML_RESET);
	private static Pair ERROR_EMPTY_TASKLIST   = new Pair(HTML_RED + "You have no tasks!" + HTML_RESET);
	private static Pair ERROR_TASK_NOT_FOUND   = new Pair(HTML_RED + "The task was not found!" + HTML_RESET);
	private static Pair ERROR_NO_RESUlTS_FOUND = new Pair(HTML_RED + "No results were found!" + HTML_RESET);
	private static Pair ERROR_IO_TASK   	   = new Pair(HTML_RED + "The task could not be changed!" + HTML_RESET);
	private static Pair ERROR_NO_DESC   	   = new Pair(HTML_RED + "You must have a description for your task!" + HTML_RESET);
	private static Pair ERROR_CANNOT_UNDO      = new Pair(HTML_RED + "No more changes to undo." + HTML_RESET);
	private static Pair ERROR_CANNOT_REDO      = new Pair(HTML_RED + "No more changes to redo." + HTML_RESET);
	private static Pair ERROR_START_BEFORE_END = new Pair(HTML_RED + "You have entered an end time that is before start time!" + HTML_RESET);
	private static Pair ERROR_DATEFORMAT       = new Pair(HTML_RED + "The date and/or time you have entered is invalid. Date format is 'dd/M/yyyy' while time is 24 hrs 'HHmm e.g. 2359" + HTML_RESET);
	
	// Define help messages here
	private static Pair HELP_TITLE             = new Pair("****************************************************************************Help menu for TaskBuddy!*********************************************************************************************");
	private static Pair HELP_HEADING           = new Pair("Please follow the following command format:");
	private static Pair HELP_SUBTITLE          = new Pair("[COMMAND]   [FORMAT]                                                                                                                                    [DESCRIPTION]                            ");
	private static Pair HELP_ADD_TASK          = new Pair("  ADD       : add    do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"              | Adds a floating task, event or deadline");
	private static Pair HELP_DISPLAY           = new Pair("  DISPLAY   : display                                                                                                                                   | Displays all tasks                     ");
	// private static Pair HELP_SEARCH_TASK       = new Pair("  SEARCH    : search [value1=keyword1], [value2=keyword2],...                                    |");
	private static Pair HELP_EDIT_TASK         = new Pair("  EDIT      : edit [task-id] do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"      | Edits an existing task                 ");
	private static Pair HELP_UNDO              = new Pair("  UNDO      : undo                                                                                                                                      | Undo the last action                   ");
	private static Pair HELP_REDO              = new Pair("  REDO      : redo                                                                                                                                      | Redo the last undoed action            ");
	private static Pair HELP_DONE              = new Pair("  DONE      : done [task-id]                                                                                                                            | Mark a task as completed               ");
	private static Pair HELP_UNDONE            = new Pair("  UNDONE    : undone [task-id]                                                                                                                          | Mark a task as uncompleted             ");	
	private static Pair HELP_DELETE_TASK       = new Pair("  DELETE    : delete [task-id]                                                                                                                          | Removes a task                         ");
	private static Pair HELP_HELP              = new Pair("  HELP      : help                                                                                                                                      | Show this help menu                    ");
	private static Pair HELP_EXIT              = new Pair("  EXIT      : exit                                                                                                                                      | Terminate program                      ");
	
	// Parameter specific errors	
	private static Pair PARAM_SUBTITLE      = new Pair(HTML_PURPLE + "There are errors in the following parameters:" + HTML_RESET);
	private static Pair PARAM_TASKID_NUM    = new Pair(HTML_PURPLE + "TaskID          : Invalid number. Please enter a number greater than 1." + HTML_RESET);
	private static Pair PARAM_TASKID_NULL   = new Pair(HTML_PURPLE + "TaskID          : Missing value. Please enter a number." + HTML_RESET);
	private static Pair PARAM_DESC          = new Pair(HTML_PURPLE + "Description     : Invalid value. Please try again." + HTML_RESET);
	private static Pair PARAM_VENUE         = new Pair(HTML_PURPLE + "Venue           : Invalid value. Please try again" + HTML_RESET);
	private static Pair PARAM_START_DATE    = new Pair(HTML_PURPLE + "Start Date      : Invalid date format." + HTML_RESET);
	private static Pair PARAM_END_DATE      = new Pair(HTML_PURPLE + "End Date        : Invaild date format." + HTML_RESET);
	private static Pair PARAM_START_TIME    = new Pair(HTML_PURPLE + "Start Time      : Invalid time format. Use 24hr notation e.g. 0000-2359." + HTML_RESET);
	private static Pair PARAM_END_TIME      = new Pair(HTML_PURPLE + "End Time        : Invalid time format. Use 24hr notation e.g. 0000-2359." + HTML_RESET);
	private static Pair PARAM_DEADLINE_DATE = new Pair(HTML_PURPLE + "Deadline Date   : Invalid date format." + HTML_RESET);
	private static Pair PARAM_DEADLINE_TIME = new Pair(HTML_PURPLE + "Deadline Time   : Invalid time format. Use 24hr notation e.g. 0000-2359." + HTML_RESET);

	// TaskList
	private static ArrayList<Task> displayTaskSet = new ArrayList<Task>();
	
	// Formatting
	private static final String PARAM_INDENT = "    ";

	private Context () {}
	
	public static Context getInstance() {
		if (context==null) {
			context = new Context();
			return context;
		} else {
			return context;
		}
	}
	
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

	public void clearAllMessages() {
		Class thisClass = Context.class;

		Field[] fields = thisClass.getDeclaredFields();
		for(Field field:fields) {
			Object o;
			try {
				// System.out.println(field.getName());
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
		taskId = _taskId;
	}

	public void printToTerminal() {
		Class thisClass = Context.class;
		StringBuilder message = new StringBuilder();

		// Print messages
		Field[] fields = thisClass.getDeclaredFields();
		for(Field field:fields) {
			Object o;
			try {
				o = field.get(context);
				if (field.getType().equals(Pair.class)) {
					field.setAccessible(true);
					Pair pair = (Pair) o;
					if (pair.getValue()) {
						String output = "";
						if (field.getName().contains("PARAM") && !field.getName().contains("SUBTITLE")) {
							output = PARAM_INDENT + pair.getKey();
						} else {
							output = pair.getKey();
						}
						message.append(output+ "<br>");
						
						//System.out.format(output + "\n", taskId);
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			
		}
		
		Gui.getCurrentInstance().setFeedbackText(message.toString());
		message = new StringBuilder();

		// Print tasks
		if (!displayTaskSet.isEmpty()) {
			Iterator<Task> iterator = displayTaskSet.iterator();
			while (iterator.hasNext()) {
				Task task = iterator.next();
				message.append(task.toString()+"\n");
				//System.out.println(task.toString());
			}
		}

		// Newline
		message.append("\n");
		Gui.getCurrentInstance().setTaskText(message.toString());
		//System.out.println();
	}
}
