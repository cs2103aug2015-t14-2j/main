package test;

/**
 *  @@author A0145472E
 */
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import Task.*;
import java.util.ArrayList;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TaskHandlerTest {
	
	private static String MESSAGE_ADD_TASK       ="Successfully added task.";
	private static String MESSAGE_DELETE_TASK    ="Task %d has been deleted";
	private static String MESSAGE_EDIT_TASK      ="Task %d has been updated!";
	// Define error messages here
	private static String ERROR_INVALID_COMMAND  ="Invalid Command.";
	private static String ERROR_EMPTY_TASKLIST   ="You have no tasks!";
	private static String ERROR_START_BEFORE_END ="You have entered an end time that is before start time!";
	private static String HELP_ADD_TASK          ="  ADD            : add do \"[description]\" on [startDate/endDate] from [startTime] to [endTime] by [deadlineDate] [deadlineTime] at \"[venue]\"              ";
	private static ArrayList<Task> taskList          = new ArrayList<Task>(50);
	private static SimpleDateFormat df               = new SimpleDateFormat("M/dd/yyyy HHmm"); 
	private static Context context 					 = Context.getInstance();
	
	@BeforeClass
	public static void setUp() {
		deleteFile("./test/data/test10.json");
		String[] path = {"./test/data/test10.json"};
		TaskHandler.init(path);
	}

	private static void deleteFile(String path) {
		File testFile = new File(path);
	    if (testFile.exists()) {
	    	testFile.delete();     
	    }
	}
	
	@Test
	public void testExecuteCommand() throws ParseException {
		String userInput 					= null;
		
		HashMap<String,Object> expected  	= null;
		HashMap<String,Object> actual  		= null;
		ArrayList<String> errorList			= new ArrayList<String>();
		ArrayList<String> successList	  	= new ArrayList<String>();
		ArrayList<String> warningList  		= new ArrayList<String>();
		ArrayList<String> helpList			= new ArrayList<String>();
		ArrayList<String> paramList			= new ArrayList<String>();
		ArrayList<Task>   taskList 			= new ArrayList<Task>();
		
		Task   task1      					= null;
		Task   task2      					= null;
		Task   task3      					= null;
		Task   task4      					= null;
		Task   task5     					= null;
		Task   task6     					= null;
		
		Date   startTime 					= null;
		Date   endTime   					= null;
		// Test display
		userInput = "display";
		
		errorList = new ArrayList<String>();
		errorList.add(ERROR_EMPTY_TASKLIST);
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for misspelling
		userInput = "dsplay";
		
		errorList = new ArrayList<String>();
		errorList.add(ERROR_INVALID_COMMAND);
		
		helpList = new ArrayList<String>();
		helpList.add(HELP_ADD_TASK);
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for add floating task
		userInput = "add do \"sth1\"";
		task1      = new Task(1, "sth1",null, null);
		
		successList = new ArrayList<String>();
		successList.add(MESSAGE_ADD_TASK);
		taskList.add(task1);
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for add event task
		userInput = "add on 12/10/15 from 1200 to 1240 do \"sth2\"";
		try {
			startTime = df.parse("12/10/15 1200");
			endTime   = df.parse("12/10/15 1240");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task2     = new Task(2, "sth2", startTime, endTime, null);
		
		successList = new ArrayList<String>();
		successList.add(MESSAGE_ADD_TASK);
		taskList = new ArrayList<Task>();
		taskList.add(task2);
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for add event task with different start and end dates
		userInput = "add do \"sth3\" from 12/10/15 1200 to 14/10/15 1340";
		try {
			startTime = df.parse("12/10/15 1200");
			endTime   = df.parse("14/10/15 1340");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task3     = new Task(3, "sth3", startTime, endTime, null);
		
		successList = new ArrayList<String>();
		successList.add(MESSAGE_ADD_TASK);
		errorList = new ArrayList<String>();
		errorList.add(ERROR_START_BEFORE_END);
		taskList = new ArrayList<Task>();
		taskList.add(task3);
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		// Test for add event task with unspecified end time
		userInput = "add do \"sth4\" on 12/10/15 from 1200";
		try {
			startTime = df.parse("12/10/15 1200");
			endTime   = df.parse("12/10/15 1300");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task4     = new Task(4, "sth4", startTime, endTime, null);
		
		successList = new ArrayList<String>();
		successList.add(MESSAGE_ADD_TASK);
		taskList = new ArrayList<Task>();
		taskList.add(task4);
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		// Test for add event task with unspecified start time
		userInput = "add do \"sth5\" on 12/10/15 to 1240";
		try {
			startTime = df.parse("12/10/15 1100");
			endTime   = df.parse("12/10/15 1240");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5     = new Task(5, "sth5", startTime, endTime, null);
		
		successList = new ArrayList<String>();
		successList.add(MESSAGE_ADD_TASK);
		taskList = new ArrayList<Task>();
		taskList.add(task5);
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		// Test for add deadline task
		userInput = "add do \"sth6\" by 12/10/15 2359";
		try {
			df.parse("12/10/15 2359");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task6     = new Task(6, "sth6", startTime, endTime, null);
		
		successList = new ArrayList<String>();
		successList.add(MESSAGE_ADD_TASK);
		taskList = new ArrayList<Task>();
		taskList.add(task6);
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for edit desc only
		userInput = "edit 1 do \"nothing-5\"";
		
		task1      = new Task(1, "nothing-5",null, null);
		taskList.add(task1);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 1));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for edit floating to event (set startDate, startTime, endDate and endTime
		userInput = "edit 2 on 12/10/15 from 1200 to 1400";
		
		try {
			startTime = df.parse("12/10/15 1200");
			endTime   = df.parse("12/10/15 1400");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task2      = new Task(2,"sth2",startTime, endTime,null);
		taskList.add(task2);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 2));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for edit floating task startDate only
		userInput = "edit 5 from 10/15/15";
		try {
			startTime = df.parse("10/15/15 0000");
			endTime = task5.getEndDateTime();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5      = new Task(5,"sth5",startTime, endTime,null);
		taskList.add(task5);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 5));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for edit floating task startDate and startTime
		userInput = "edit 5 from 10/15/15 2115";
		try {
			startTime = df.parse("10/15/15 2115");
			endTime = task5.getEndDateTime();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5      = new Task(5,"sth5",startTime, endTime,null);
		taskList.add(task5);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 5));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for edit task startDate, startTime and endTime
		userInput = "edit 5 on 10/25/15 from 1130 to 1409";
		try {
			startTime = df.parse("10/25/15 1130");
			endTime = df.parse("10/25/15 1409");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5      = new Task(5,"sth5",startTime, endTime,null);
		taskList.add(task5);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 5));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		// Test for edit task endDate and endTime 
		userInput = "edit 5 to 10/27/15 2220";
		try {
			startTime = task5.getStartDateTime();
			endTime = df.parse("10/27/15 2220");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5      = new Task(5,"sth5",startTime, endTime,null);
		taskList.add(task5);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 5));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertFalse(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for edit task endDate only
		userInput = "edit 5 to 12/24/15";
		try {
			startTime = task5.getStartDateTime();
			endTime = df.parse("12/24/15 2220");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5      = new Task(5,"sth5",startTime, endTime,null);
		taskList.add(task5);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 5));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		

		// Test for edit task startDate and endDate only
		userInput = "edit 5 on 12/25/15";
		try {
			startTime = df.parse("10/25/15 1130");
			endTime = df.parse("12/24/15 2220");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5      = new Task(5,"sth5",startTime, endTime,null);
		taskList.add(task5);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 5));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);

		
		// Test for edit task startDate and startTime only
		userInput = "edit 5 on 09/25/15 from 1000";
		try {
			startTime = df.parse("09/25/15 1000");
			endTime = df.parse("12/24/15 2220");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5      = new Task(5,"sth5",startTime, endTime,null);
		taskList.add(task5);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 5));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for edit task endDate and endTime only
		userInput = "edit 5 on 10/25/15 to 1130";
		try {
			startTime = df.parse("10/25/15 1000");
			endTime = df.parse("10/25/15 1130");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		task5      = new Task(5,"sth5",startTime, endTime,null);
		taskList.add(task5);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 5));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for edit task venue
		userInput = "edit 5 at \"do from on to at by\"";
		
		startTime = task5.getStartDateTime();
		endTime = task5.getEndDateTime();
		
		task5      = new Task(5,"do from on to at by",startTime, endTime,null);
		taskList.add(task5);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_EDIT_TASK, 5));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
		
		// Test for delete task
		userInput = "delete 2";
		
		taskList.add(task2);
		successList = new ArrayList<String>();
		successList.add(String.format(MESSAGE_DELETE_TASK, 2));
		
		expected = buildExpectedHashmap(successList,warningList,helpList,paramList,errorList,taskList);
		
		TaskHandler.executeCommand(userInput);
		actual = stripJson(context.getDataModel());
		assertTrue(isSameObjHash(expected, actual));
		context.clearAllMessages();
		clearArrayLists(successList,warningList,helpList,paramList,errorList,taskList);
		
	}
	
	@SuppressWarnings("unchecked")
	private boolean isSameObjHash(HashMap<String, Object> expected, HashMap<String, Object> actual) {
		return
				expected.get("success_messages").equals(actual.get("success_messages")) &&
				expected.get("warning_messages").equals(actual.get("warning_messages")) &&
				expected.get("help_messages").equals(actual.get("help_messages")) &&
				expected.get("param_messages").equals(actual.get("param_messages")) &&
				expected.get("error_messages").equals(actual.get("error_messages")) &&
				isSameTaskList((ArrayList<Task>)expected.get("taskList"),(ArrayList<Task>)actual.get("taskList"));
	}

	private boolean isSameTaskList(ArrayList<Task> arrayList, ArrayList<Task> arrayList2) {
		if(arrayList.size() != arrayList2.size()){
			return false;
		}
		
		for(int i = 0; i < arrayList.size();i++){
			if(!arrayList.get(i).equals(arrayList.get(i))){
				return false;
			}
		}
		return true;
	}

	private void clearArrayLists(ArrayList<String> successList, ArrayList<String> warningList,
			ArrayList<String> helpList, ArrayList<String> paramList, ArrayList<String> errorList,
			ArrayList<Task> taskList) {
		errorList.clear();
		successList.clear();
		warningList.clear();
		helpList.clear();
		paramList.clear();
		taskList.clear();
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
