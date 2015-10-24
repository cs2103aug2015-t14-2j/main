package Task;

import Task.Pair;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Context {
	// Display settings
	private static Context context = null;
	private static boolean DEBUG = true;
	
	// Define success messages here
	private static Pair MESSAGE_WELCOME        = new Pair("Welcome to TaskBuddy!");
	private static Pair MESSAGE_ADD_TASK       = new Pair("Successfully added task.");
	private static Pair MESSAGE_GET_TASK       = new Pair("Task returned");
	private static Pair MESSAGE_DISPLAY        = new Pair("All tasks displayed.");
	private static Pair MESSAGE_SEARCH_TASK    = new Pair("Here are tasks matching your keywords:");
	private static Pair MESSAGE_DELETE_TASK    = new Pair(" ID task has been deleted");
	private static Pair MESSAGE_EDIT_TASK      = new Pair("Task has been updated!");
	private static Pair MESSAGE_UNDO_TASK      = new Pair("Successfully rolled back 1 change.");
	private static Pair MESSAGE_REDO_TASK      = new Pair("Successfully redoed 1 change.");
	private static Pair MESSAGE_DONE_TASK      = new Pair("Successfully updated to done.");
	private static Pair MESSAGE_UNDONE_TASK    = new Pair("Successfully updated to undone.");
	private static Pair MESSAGE_EXIT           = new Pair("Thanks for using TaskBuddy! Changes saved to disk.");
	
	// Define warning messages here
	private static Pair WARNING_DEADLINE_BEFORE_NOW = new Pair("WARNING: You have specified a deadline that is before the current time");

	// Define error messages here
	private static Pair ERROR_PARAM_SUBTITLE   = new Pair("There are errors in the following parameters:");
	private static Pair ERROR_INVALID_COMMAND  = new Pair("Invalid Command.");
	private static Pair ERROR_EMPTY_TASKLIST   = new Pair("You have no tasks!");
	private static Pair ERROR_TASK_NOT_FOUND   = new Pair("The task was not found!");
	private static Pair ERROR_IO_TASK   	   = new Pair("The task could not be changed!");
	private static Pair ERROR_NO_DESC   	   = new Pair("You must have a description for your task!");
	private static Pair ERROR_CANNOT_UNDO      = new Pair("No more changes to undo.");
	private static Pair ERROR_CANNOT_REDO      = new Pair("No more changes to redo.");
	private static Pair ERROR_START_BEFORE_END = new Pair("You have entered an end time that is before start time!");
	private static Pair ERROR_DATEFORMAT       = new Pair("The date and/or time you have entered is invalid. Date format is 'dd/M/yyyy' while time is 24 hrs 'HHmm e.g. 2359");
	
	// Define help messages here
	private static Pair HELP_TITLE             = new Pair("****************************************************************************Help menu for TaskBuddy!*********************************************************************************************");
	private static Pair HELP_SUBTITLE          = new Pair("[COMMAND]   [FORMAT]                                                                                                                                    [DESCRIPTION]                            ");
	private static Pair HELP_ADD_TASK          = new Pair("  ADD       : add    do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"              | Adds a floating task, event or deadline");
	private static Pair HELP_DISPLAY           = new Pair("  DISPLAY   : display                                                                                                                                   | Displays all tasks                     ");
	// private static Pair HELP_SEARCH_TASK       = new Pair("  SEARCH    : search [value1=keyword1], [value2=keyword2],...                                    |");
	private static Pair HELP_EDIT_TASK         = new Pair("  EDIT      : edit [task-id] do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"      | Edits an existing task                 ");
	private static Pair HELP_UNDO              = new Pair("  UNDO      : undo                                                                                                                                      | Undo the last action                   ");
	private static Pair HELP_REDO              = new Pair("  REDO      : redo                                                                                                                                      | Redo the last undoed action            ");
	private static Pair HELP_DELETE_TASK       = new Pair("  DELETE    : delete [task-id]                                                                                                                          | Removes a task                         ");
	private static Pair HELP_EXIT              = new Pair("  EXIT      : exit                                                                                                                                      | Terminate program                      ");
	
	// Parameters	
	private static Pair PARAM_TASKID        = new Pair("TaskID");
	private static Pair PARAM_DESC          = new Pair("Description");
	private static Pair PARAM_VENUE         = new Pair("Venue");
	private static Pair PARAM_START_DATE    = new Pair("Start Date");
	private static Pair PARAM_END_DATE      = new Pair("End Date");
	private static Pair PARAM_START_TIME    = new Pair("Start Time");
	private static Pair PARAM_END_TIME      = new Pair("End Time");
	private static Pair PARAM_DEADLINE_DATE = new Pair("Deadline Date");
	private static Pair PARAM_DEADLINE_TIME = new Pair("Deadline Time");

	// TaskList
	private static ArrayList<Task> displayTaskSet = new ArrayList<Task>();

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

	public void printToTerminal() {
		Class thisClass = Context.class;

		Field[] fields = thisClass.getDeclaredFields();
		for(Field field:fields) {
			Object o;
			try {
				o = field.get(context);
//				System.out.println(field.getType());
//				System.out.println(Pair.class);
				if (field.getType().equals(Pair.class)) {
					field.setAccessible(true);
					Pair pair = (Pair) o;
					if (pair.getValue()) {
						System.out.println(pair.getKey());
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (!displayTaskSet.isEmpty()) {
			Iterator<Task> iterator = displayTaskSet.iterator();
			while (iterator.hasNext()) {
				Task task = iterator.next();
				System.out.println(task.toString());
			}
		}
	}
}
