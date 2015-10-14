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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class TaskHandlerTest {
	// Define success messages here
	private static final String MESSAGE_WELCOME        = "Welcome to TaskBuddy!";
	private static final String MESSAGE_ADD_TASK       = "Successfully added task.";
	private static final String MESSAGE_GET_TASK       = "Task returned";
	private static final String MESSAGE_DISPLAY        = "All tasks displayed.";
	private static final String MESSAGE_SEARCH_TASK    = "Here are tasks matching your keywords:";
	private static final String MESSAGE_DELETE_TASK    = "Task has been deleted";
	private static final String MESSAGE_EDIT_TASK      = "Task has been updated!";
	private static final String MESSAGE_EXIT           = "Thanks for using TaskBuddy! Changes saved to disk.";
	
	// Define error messages here
	private static final String ERROR_INVALID_COMMAND  = "Invalid Command.";
	private static final String ERROR_EMPTY_TASKLIST   = "You have no tasks!";
	private static final String ERROR_NOT_FOUND_TASK   = "The task was not found!";
	private static final String ERROR_IO_TASK   	   = "The task could not be changed!";
	private static final String ERROR_DATEFORMAT       = "The date and/or time you have entered is invalid. Date format is 'dd/M/yyyy' while time is 24 hrs 'HHmm e.g. 2359";
	
	// Define help messages here
	private static final String HELP_TITLE             = "****************************************************************************Help menu for TaskBuddy!*********************************************************************************************";
	private static final String HELP_SUBTITLE          = "[COMMAND]   [FORMAT]                                                                                                                                    [DESCRIPTION]                            ";
	private static final String HELP_ADD_TASK          = "  ADD       : add    do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"              | Adds a floating task, event or deadline";
	private static final String HELP_DISPLAY           = "  DISPLAY   : display                                                                                                                                   | Displays all tasks                     ";
	// private static final String HELP_SEARCH_TASK       = "  SEARCH    : search [value1=keyword1], [value2=keyword2],...                                    |";
	private static final String HELP_EDIT_TASK         = "  EDIT      : edit [task-id] do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"      | Edits an existing task                 ";
	private static final String HELP_DELETE_TASK       = "  DELETE    : delete [task-id]                                                                                                                          | Removes a task                         ";
	private static final String HELP_EXIT              = "  EXIT      : exit                                                                                                                                      | Terminate program                      ";

	private static ArrayList<Task> taskList          = new ArrayList<Task>(50);
	private static SimpleDateFormat df               = new SimpleDateFormat("dd/M/yyyy HHmm"); 
	
	@BeforeClass
	public static void setUp() {
		String path = "./test/data/test10.json";
		TaskHandler.setFilePath(path);
		
	}
	
	@Test
	public void testExecuteCommand() {
		String userInput = null;
		String expected  = null;
		String actual    = null;
		Task   task      = null;
		Task   task5     = null;
		Date   startTime = null;
		Date   endTime   = null;
		Date   deadline  = null;
		
		// Test display
		userInput = "display";
		expected  = ERROR_EMPTY_TASKLIST;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for misspelling
		userInput = "dsplay";
		expected  = "";
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for add floating task
		userInput = "add do \"sth1\"";
		task      = new Task(1, "sth1", null);
		System.out.println(task.toString());
		expected  = task.toString() + "\n" + MESSAGE_ADD_TASK;
		actual    = TaskHandler.executeCommand(userInput);
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
		actual    = TaskHandler.executeCommand(userInput);
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
		actual    = TaskHandler.executeCommand(userInput);
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
		actual    = TaskHandler.executeCommand(userInput);
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
		actual    = TaskHandler.executeCommand(userInput);
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
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit desc only
		userInput = "edit 1 do \"nothing-5\"";
		expected  = task5.toString() + MESSAGE_ADD_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit floating to event (set startDate, startTime, endDate and endTime
		userInput = "edit 1 on 12/10/15 from 1200 to 1400";

		task      = new Task(3, "sth4", startTime, endTime, null);
		expected  = task.toString() + "\n" + MESSAGE_ADD_TASK;
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit floating task startDate only
		userInput = "edit 5 from 15/10/15";
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit floating task startDate and startTime
		userInput = "edit 5 from 15/10/15 2115";
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task startDate, startTime and endTime
		userInput = "edit 5 on 25/10/15 from 1130 to 1409";
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task endDate and endTime 
		userInput = "edit 5 to 23/10/15 2358";
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task endDate only
		userInput = "edit 5 to 24/12/15";
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task startDate and endDate only
		userInput = "edit 5 on 25/12/15";
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);

		// Test for edit task startDate and startTime only
		userInput = "edit 5 on 25/09/15 from 1000";
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task endDate and endTime only
		userInput = "edit 5 on 25/10/15 to 0030";
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for edit task venue
		userInput = "edit 5 at \"do from on to at by\"";
		expected  = MESSAGE_EDIT_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
		
		// Test for delete task
		userInput = "delete 2";
		expected  = MESSAGE_DELETE_TASK;
		actual    = TaskHandler.executeCommand(userInput);
		assertEquals(expected, actual);
	}
	
	@AfterClass
	public static void tearDown(){
		taskList.clear();
		File file1 = new File("./test/data/test10.json");
		
		file1.delete();
		
	}
}
