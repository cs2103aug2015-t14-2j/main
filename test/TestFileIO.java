package test;

import junit.framework.TestCase;
import Task.FileIO;
import Task.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TestFileIO extends TestCase {
	private static FileIO fileIO;
	private static SimpleDateFormat dateFormat       = new SimpleDateFormat("EEE, dd MMM, yyyy HHmm");
	
	protected void setUp() throws Exception {
		fileIO = new FileIO("./test/data/test1.json");
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testReadFromFile() {
		// Test 1
		ArrayList<Task> expectedTaskList = new ArrayList<Task>();
		int taskId = 1;
		Date createdTime;
		Date lastModifiedTime;
		Date startTime;
		Date endTime;
		Date deadline;
		String venue = "NUS";
		String description = "Buy lunch";
		boolean isDone = false;
		boolean isPastDeadline = false;
		boolean hasEnded = false;
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("#GE2015");
		tags.add("#Elections");
		tags.add("#homework");
		
		try {
			createdTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			lastModifiedTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			startTime = dateFormat.parse("Thu, 10 Sep, 2015 1200");
			endTime = dateFormat.parse("Fri, 11 Sep, 2015 1400");
			deadline = dateFormat.parse("Sat, 12 Sep, 2015 1200");
			Task expectedTask1 = new Task(createdTime, lastModifiedTime, taskId, description, startTime, endTime, deadline, venue, isDone, isPastDeadline, hasEnded, tags);
			expectedTaskList.add(expectedTask1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ArrayList<Task> actualTaskList = fileIO.readFromFile();
		
		assertEquals("Error", expectedTaskList, actualTaskList);
		
		// Test 2
		expectedTaskList = new ArrayList<Task>();
		fileIO = new FileIO("./test/data/test2.json");
		actualTaskList = fileIO.readFromFile();
		assertEquals(expectedTaskList, actualTaskList);
		
		// Test 3
		expectedTaskList = new ArrayList<Task>();
		venue = "Jurong West St.71 #07-10";
		description = "haircut";
		isDone = true;
		isPastDeadline = false;
		hasEnded = false;
	
		try {
			createdTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			lastModifiedTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			startTime = dateFormat.parse("Mon, 14 Sep, 2015 1800");
			endTime = dateFormat.parse("Mon, 14 Sep, 2015 2000");
			deadline = dateFormat.parse("Sat, 12 Sep, 2015 1200");
			Task expectedTask2 = new Task(createdTime, lastModifiedTime, taskId, description, startTime, endTime, deadline, venue, isDone, isPastDeadline, hasEnded, tags);
			expectedTaskList.add(expectedTask2);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
		
//		expectedTaskList.add(expectedTask1);
		fileIO = new FileIO("./test/data/test2.json");
		actualTaskList = fileIO.readFromFile();
		
		assertEquals(expectedTaskList, actualTaskList);
	}

	public void testWriteToFile() {
		fail("Not yet implemented");
	}

	public void testGetCurrentTaskId() {
		fail("Not yet implemented");
	}

}
