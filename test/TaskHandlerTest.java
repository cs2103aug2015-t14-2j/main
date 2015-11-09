package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import Task.*;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class TaskHandlerTest {
	
	private static String DEFAULT_DATE;
	
	// Define success messages here
	private static String MESSAGE_WELCOME        ="Welcome to TaskBuddy!";
	private static String MESSAGE_PATH		   ="Your new path is: %s";
	private static String MESSAGE_SAVE		   ="Your calendar has been saved to %s!";
	private static String MESSAGE_OPEN		   ="Opening calendar at path %s";
	private static String MESSAGE_ADD_TASK       ="Successfully added task.";
	private static String MESSAGE_GET_TASK       ="Task %d returned";
	private static String MESSAGE_DISPLAY_ALL    ="All tasks displayed.";
	private static String MESSAGE_DISPLAY		   ="Search results:";
	private static String MESSAGE_SEARCH_TASK    ="Here are tasks matching your keywords:";
	private static String MESSAGE_DELETE_TASK    ="Task %d has been deleted";
	private static String MESSAGE_EDIT_TASK      ="Task %d has been updated!";
	private static String MESSAGE_UNDO_TASK      ="Successfully undoed change(s) to Task %d.";
	private static String MESSAGE_REDO_TASK      ="Successfully redoed change(s) to Task %d.";
	private static String MESSAGE_DONE_TASK      ="Successfully updated Task %d to completed.";
	private static String MESSAGE_UNDONE_TASK    ="Successfully updated Task %d to uncompleted.";
	private static String MESSAGE_EXIT           ="Thanks for using TaskBuddy! Changes saved to disk.";
	
	/** 
	 * Define warning messages here
	 * Warnings are less severe than errors.
	 * To communicate to the user they may have done something unintentional
	 */
	private static String WARNING_DEADLINE_BEFORE_NOW ="WARNING: You have specified a deadline that is before the current time";
	private static String WARNING_TASK_NOT_EDITED     ="Task %d was not edited.";

	// Define error messages here
	private static String ERROR_INVALID_COMMAND  ="Invalid Command.";
	private static String ERROR_EMPTY_TASKLIST   ="You have no tasks!";
	private static String ERROR_TASK_NOT_FOUND   ="The task was not found!";
	private static String ERROR_NO_RESUlTS_FOUND ="No results were found!";
	private static String ERROR_IO_TASK   	   ="The task could not be changed!";
	private static String ERROR_NO_DESC   	   ="You must have a description for your task!";
	private static String ERROR_CANNOT_UNDO      ="No more changes to undo.";
	private static String ERROR_CANNOT_REDO      ="No more changes to redo.";
	private static String ERROR_START_BEFORE_END ="You have entered an end time that is before start time!";
	private static String ERROR_DATEFORMAT       ="You have entered an invalid date and time. Note that we follow American date format mm/dd/yy.";
	private static String ERROR_TRIGGER_ERROR    ="ERROR! The trigger shortcut has been interrupted and aborted.";
	private static String ERROR_MALFORMED_TASK   ="ERROR! Corrupted task region. Task %d has been discarded.";
	private static String ERROR_MALFORMED_FILE   ="ERROR! Corrupted file region. Rest of file cannot be read.";
	private static String ERROR_MALFORMED_KEY    ="ERROR! File does not match expected format. Restart program with a new file location.";
	private static String ERROR_FILE_IO          ="ERROR! Cannot read from specified file location.";
	private static String ERROR_HTML_TEMPLATE    ="ERROR! Cannot read html template.";
	
	// Define help messages here
	private static String HELP_TITLE             ="**********************************************************************Help menu for TaskBuddy!*****************************************************************";
	private static String HELP_HEADING           ="Please follow the following command format:";
	private static String HELP_SUBTITLE          ="[COMMAND]   [FORMAT]                                                                                                                                    ";
	private static String HELP_PATH              ="  PATH          : path [absolute filepath]";
	private static String HELP_FILEOPEN          ="  FILEOPEN  : fileopen";
	private static String HELP_FILESAVE          ="  FILESAVE   : filesave";
	private static String HELP_ADD_TASK          ="  ADD            : add do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"              ";
	private static String HELP_DISPLAY           ="  DISPLAY    : display                                                                                                                                   ";
	private static String HELP_EDIT_TASK         ="  EDIT           : edit [task-id] do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"      ";
	private static String HELP_UNDO              ="  UNDO        : undo                                                                                                                                      ";
	private static String HELP_REDO              ="  REDO        : redo                                                                                                                                      ";
	private static String HELP_DONE              ="  DONE        : done [task-id]                                                                                                                            ";
	private static String HELP_UNDONE            ="  UNDONE   : undone [task-id]                                                                                                                          ";	
	private static String HELP_DELETE_TASK       ="  DELETE     : delete [task-id]                                                                                                                          ";
	private static String HELP_HELP              ="  HELP         : help                                                                                                                                      ";
	private static String HELP_EXIT              ="  EXIT          : exit                                                                                                                                      ";
	
	private static ArrayList<Task> taskList          = new ArrayList<Task>(50);
	private static SimpleDateFormat df               = new SimpleDateFormat("dd/M/yyyy HHmm"); 
	private static Context context 					 = Context.getInstance();
	
	@BeforeClass
	public static void setUp() {
		String[] path = {"./test/data/test10.json"};
		TaskHandler.init(path);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExecuteCommand() {
		String userInput 					= null;
		HashMap<String,Object> expected  	= new HashMap<String,Object>();
		HashMap<String,Object> actual  		= null;
		Task   task      					= null;
		Task   task5     					= null;
		Date   startTime 					= null;
		Date   endTime   					= null;
		Date   deadline  					= null;
		
		// Test display
		userInput = "display";
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add(ERROR_EMPTY_TASKLIST);
		expected = buildExpectedHashmap(null,null,null,null,errorList,null);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertEquals(expected, actual);
		
		
		// Test for misspelling
		userInput = "dsplay";
		expected = buildExpectedHashmap(null,null,null,null,null,null);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertEquals(expected, actual);
		
		/*
		// Test for add floating task
		userInput = "add do \"sth1\"";
		task      = new Task(1, "sth1", null);
		System.out.println(task.toString());
		expected  = task.toString() + "\n" + MESSAGE_ADD_TASK;
		TaskHandler.executeCommand(userInput);
		System.out.println(actual);
		assertEquals(expected, actual);
		
		// Test for add event task
		userInput = "add on 12/10/15 from 1200 to 1240 do \"sth2\"";
		try {
			startTime = df.parse("12/10/15 1200");
			endTime   = df.parse("12/10/15 1240");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task      = new Task(2, "sth2", startTime, endTime, null);
		expected  = task.toString() + "\n" + MESSAGE_ADD_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for add event task with different start and end dates
		userInput = "add do \"sth3\" from 12/10/15 1200 to 14/10/15 1340";
		try {
			startTime = df.parse("12/10/15 1200");
			endTime   = df.parse("14/10/15 1340");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task      = new Task(3, "sth3", startTime, endTime, null);
		expected  = task.toString() + "\n" + MESSAGE_ADD_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for add event task with unspecified end time
		userInput = "add do \"sth4\" on 12/10/15 from 1200";
		try {
			startTime = df.parse("12/10/15 1200");
			endTime   = df.parse("12/10/15 1300");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task      = new Task(4, "sth4", startTime, endTime, null);
		expected  = task.toString() + "\n" + MESSAGE_ADD_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for add event task with unspecified start time
		userInput = "add do \"sth5\" on 12/10/15 to 1240";
		try {
			startTime = df.parse("12/10/15 1100");
			endTime   = df.parse("12/10/15 1240");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5      = new Task(5, "sth5", startTime, endTime, null);
		expected  = task.toString() + "\n" + MESSAGE_ADD_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for add deadline task
		userInput = "add do \"sth6\" by 12/10/15 2359";
		try {
			deadline = df.parse("12/10/15 2359");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task      = new Task(6, "sth6", deadline, null);
		expected  = task.toString() + "\n" + MESSAGE_ADD_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit desc only
		userInput = "edit 1 do \"nothing-5\"";
		expected  = task5.toString() + MESSAGE_ADD_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit floating to event (set startDate, startTime, endDate and endTime
		userInput = "edit 1 on 12/10/15 from 1200 to 1400";

		task      = new Task(3, "sth4", startTime, endTime, null);
		expected  = task.toString() + "\n" + MESSAGE_ADD_TASK;
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit floating task startDate only
		userInput = "edit 5 from 15/10/15";
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit floating task startDate and startTime
		userInput = "edit 5 from 15/10/15 2115";
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task startDate, startTime and endTime
		userInput = "edit 5 on 25/10/15 from 1130 to 1409";
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task endDate and endTime 
		userInput = "edit 5 to 23/10/15 2358";
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task endDate only
		userInput = "edit 5 to 24/12/15";
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task startDate and endDate only
		userInput = "edit 5 on 25/12/15";
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);

		// Test for edit task startDate and startTime only
		userInput = "edit 5 on 25/09/15 from 1000";
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task endDate and endTime only
		userInput = "edit 5 on 25/10/15 to 0030";
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task venue
		userInput = "edit 5 at \"do from on to at by\"";
		expected  = MESSAGE_EDIT_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for delete task
		userInput = "delete 2";
		expected  = MESSAGE_DELETE_TASK;
		TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		*/
	}
	
	private HashMap<String, Object> stripJson(HashMap<String, Object> dataModel) {
		dataModel.remove("jsonData");
		dataModel.remove("default_date");
		dataModel.remove("view_messages");
		return dataModel;
	}

	private HashMap<String, Object> buildExpectedHashmap(ArrayList<String> success_messages, ArrayList<String> warning_messages, 
			ArrayList<String> help_messages, ArrayList<String> param_messages, ArrayList<String> error_messages, 
			ArrayList<Task> taskList) {	
		HashMap<String, Object> expected = new HashMap<String, Object> ();
		
		success_messages = (success_messages == null) ? new ArrayList<String>() :success_messages;
		warning_messages = (warning_messages == null) ? new ArrayList<String>() :warning_messages;
		help_messages	 = (help_messages == null) 	  ? new ArrayList<String>() :help_messages;
		param_messages 	 = (param_messages == null)   ? new ArrayList<String>() :param_messages;
		error_messages 	 = (error_messages == null)   ? new ArrayList<String>() :error_messages;
		taskList 		 = (taskList == null) 		  ? new ArrayList<Task>() :taskList;
		
		expected.put("success_messages", success_messages);
		expected.put("warning_messages", warning_messages);
		expected.put("help_messages", help_messages);
		expected.put("param_messages", param_messages);
		expected.put("error_messages", error_messages);
		expected.put("taskList", taskList);
		
		return expected;
		
	}

	@AfterClass
	public static void tearDown(){
		taskList.clear();
		File file1 = new File("./test/data/test10.json");
		
		file1.delete();
		
	}
}
