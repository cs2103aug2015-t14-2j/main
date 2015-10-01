package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Task.FileIO;
import Task.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.io.FileUtils;


public class TestFileIO {
	private static FileIO fileIO;
	private static SimpleDateFormat dateFormat       = new SimpleDateFormat("EEE, dd MMM, yyyy HHmm");
	
	private static Task expectedTask1;
	private static Task expectedTask2;
	private static Task expectedTask3;
	private static Task expectedTask4;
	
	private static ArrayList<Task> expectedTaskList = new ArrayList<Task>();
	private static ArrayList<Task> actualTaskList = new ArrayList<Task>();
	private static ArrayList<String> tags = new ArrayList<String>();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileIO = new FileIO("./test/data/test1.json");
		
		// Task1 - has deadline, startTime and endTime and non-empty tags array
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
		try {
			createdTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			lastModifiedTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			startTime = dateFormat.parse("Thu, 10 Sep, 2015 1200");
			endTime = dateFormat.parse("Fri, 11 Sep, 2015 1400");
			deadline = dateFormat.parse("Sat, 12 Sep, 2015 1200");
			expectedTask1 = new Task(createdTime, lastModifiedTime, taskId, description, startTime, endTime, deadline, venue, isDone, isPastDeadline, hasEnded);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		// Task2 - no deadline, empty tags array 
		taskId = 2;
		venue = "Jurong West St.71 #07-10";
		description = "haircut";
		isDone = true;
		isPastDeadline = false;
		hasEnded = false;
		tags.add("#GE2015");
		
		try {
			createdTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			lastModifiedTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			startTime = dateFormat.parse("Mon, 14 Sep, 2015 1800");
			endTime = dateFormat.parse("Mon, 14 Sep, 2015 2000");
			deadline = null;
			expectedTask2 = new Task(createdTime, lastModifiedTime, taskId, description, startTime, endTime, deadline, venue, isDone, isPastDeadline, hasEnded);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
		
		// Task3 - has deadline, no startTime and endTime, empty tags array, null venue
		taskId = 3;
		venue = null;
		description = "Shop for groceries!";
		isDone = false;
		isPastDeadline = true;
		hasEnded = false;
		tags.add("#Elections");
		
		try {
			createdTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			lastModifiedTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			startTime = null;
			endTime = null;
			deadline = dateFormat.parse("Tue, 29 Sep, 2015 1200");
			expectedTask3 = new Task(createdTime, lastModifiedTime, taskId, description, startTime, endTime, deadline, venue, isDone, isPastDeadline, hasEnded);
		} catch (ParseException e3) {
			e3.printStackTrace();
		}
		
		// Task4 - no deadline, no startTime and endTime, empty tags array, null venue
		taskId = 4;
		venue = null;
		description = "CS2103 mid-terms";
		isDone = false;
		isPastDeadline = false;
		hasEnded = false;
		tags.add("#homework");
		
		try {
			createdTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			lastModifiedTime = dateFormat.parse("Thu, 10 Sep, 2015 1356");
			startTime = null;
			endTime = null;
			deadline = null;
			expectedTask4 = new Task(createdTime, lastModifiedTime, taskId, description, startTime, endTime, deadline, venue, isDone, isPastDeadline, hasEnded);
		} catch (ParseException e4) {
			e4.printStackTrace();
		}
	}

	@Before
	public void setUp() throws Exception {

				
	}
	
	@Test
	public void testReadFromFile() {
		// Test 1 - empty file
		actualTaskList = fileIO.readFromFile();
		assertEquals(expectedTaskList, actualTaskList);
		
		// Test 2
		expectedTaskList.add(expectedTask1);
		fileIO = new FileIO("./test/data/test2.json");
		actualTaskList = fileIO.readFromFile();
		assertEquals(expectedTaskList, actualTaskList);
		
		// Test 3
		expectedTaskList.add(expectedTask2);
		fileIO = new FileIO("./test/data/test3.json");
		actualTaskList = fileIO.readFromFile();
		assertEquals(expectedTaskList, actualTaskList);
		
		// Test 4
		expectedTaskList.add(expectedTask3);
		fileIO = new FileIO("./test/data/test4.json");
		actualTaskList = fileIO.readFromFile();
		assertEquals(expectedTaskList, actualTaskList);
		
		// Test 5
		expectedTaskList.add(expectedTask4);
		fileIO = new FileIO("./test/data/test5.json");
		actualTaskList = fileIO.readFromFile();
		assertEquals(expectedTaskList, actualTaskList);
	}

	@Test
	public void testWriteToFile() {
		expectedTaskList.clear();
		// Test 1
		fileIO = new FileIO("./test/data/write1.json");
		fileIO.writeToFile(expectedTaskList);
		File expectedFile = new File("./test/data/test1.json");
		File actualFile = new File("./test/data/write1.json");
		try {
			assertEquals(FileUtils.readFileToString(expectedFile, "utf-8"), FileUtils.readFileToString(actualFile, "utf-8"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Test 2
		expectedTaskList.add(expectedTask1);
		fileIO = new FileIO("./test/data/write2.json");
		fileIO.writeToFile(expectedTaskList);
		expectedFile = new File("./test/data/test2.json");
		actualFile = new File("./test/data/write2.json");
		try {
			assertEquals(FileUtils.readFileToString(expectedFile, "utf-8"), FileUtils.readFileToString(actualFile, "utf-8"));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		// Test 3
		expectedTaskList.add(expectedTask2);
		fileIO = new FileIO("./test/data/write3.json");
		fileIO.writeToFile(expectedTaskList);
		expectedFile = new File("./test/data/test3.json");
		actualFile = new File("./test/data/write3.json");
		try {
			assertEquals(FileUtils.readFileToString(expectedFile, "utf-8"), FileUtils.readFileToString(actualFile, "utf-8"));
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		
		// Test 4
		expectedTaskList.add(expectedTask3);
		fileIO = new FileIO("./test/data/write4.json");
		fileIO.writeToFile(expectedTaskList);
		expectedFile = new File("./test/data/test4.json");
		actualFile = new File("./test/data/write4.json");
		try {
			assertEquals(FileUtils.readFileToString(expectedFile, "utf-8"), FileUtils.readFileToString(actualFile, "utf-8"));
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		
		// Test 5
		expectedTaskList.add(expectedTask4);
		fileIO = new FileIO("./test/data/write5.json");
		fileIO.writeToFile(expectedTaskList);
		expectedFile = new File("./test/data/test5.json");
		actualFile = new File("./test/data/write5.json");
		try {
			assertEquals(FileUtils.readFileToString(expectedFile, "utf-8"), FileUtils.readFileToString(actualFile, "utf-8"));
		} catch (IOException e4) {
			e4.printStackTrace();
		}
		
	}

	public void testGetCurrentTaskId() {
		fail("Not yet implemented");
	}
	
	@After
	public void tearDown() throws Exception {
		// Clear data
		expectedTaskList.clear();
		actualTaskList.clear();
		tags.clear();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File file1 = new File("./test/data/write1.json");
		File file2 = new File("./test/data/write2.json");
		File file3 = new File("./test/data/write3.json");
		File file4 = new File("./test/data/write4.json");
		File file5 = new File("./test/data/write5.json");
		
		file1.delete();
		file2.delete();
		file3.delete();
		file4.delete();
		file5.delete();
		
	}
}
