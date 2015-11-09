/**
 *  @@author A0118772
 *  
 *  Represents the test for the Validator class
 * 
 */

 package test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.junit.Test;

import Task.Validator;
import Task.*;

public class ValidatorTest {

	@Test
	public void testgetObjectHashMap(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.START_TIME, "1200");
		testHashMap.put(PARAMETER.END_TIME, "8pm");
		testHashMap.put(PARAMETER.DATE, "11/15");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.ADD_TASK );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String returnedStartTime = dateFormat.format(returnedHashMap.get(PARAMETER.START_TIME));
		assertEquals(returnedStartTime, "15-11-2015 1200");
		String returnedEndTime = dateFormat.format(returnedHashMap.get(PARAMETER.END_TIME));
		assertEquals(returnedEndTime, "15-11-2015 2000");
	}
	
	@Test
	public void testgetObjectHashMap1(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.DATE, "11/15/17");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.ADD_TASK );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String returnedStartTime = dateFormat.format(returnedHashMap.get(PARAMETER.START_TIME));
		assertEquals(returnedStartTime, "15-11-2017 0000");
		String returnedEndTime = dateFormat.format(returnedHashMap.get(PARAMETER.END_TIME));
		assertEquals(returnedEndTime, "15-11-2017 2359");
	}
	
	@Test
	public void testgetObjectHashMap2(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.DATE, "15/noV");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.ADD_TASK );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String returnedStartTime = dateFormat.format(returnedHashMap.get(PARAMETER.START_TIME));
		assertEquals(returnedStartTime, "15-11-2015 0000");
		String returnedEndTime = dateFormat.format(returnedHashMap.get(PARAMETER.END_TIME));
		assertEquals(returnedEndTime, "15-11-2015 2359");
	}
	
	@Test
	public void testgetObjectHashMap3(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.START_TIME, "11/02 5pm");
		testHashMap.put(PARAMETER.END_TIME, "11/5 3am");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.ADD_TASK );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String returnedStartTime = dateFormat.format(returnedHashMap.get(PARAMETER.START_TIME));
		assertEquals(returnedStartTime, "02-11-2015 1700");
		String returnedEndTime = dateFormat.format(returnedHashMap.get(PARAMETER.END_TIME));
		assertEquals(returnedEndTime, "05-11-2015 0300");
	}
	
	@Test
	public void testgetObjectHashMap4(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.DEADLINE_TIME, "11/15");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.ADD_TASK );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String deadlineTime = dateFormat.format(returnedHashMap.get(PARAMETER.DEADLINE_TIME));
		assertEquals(deadlineTime, "15-11-2015 2359");
	}
	
	@Test
	public void testgetObjectHashMap5(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.DEADLINE_TIME, "11/15 4pm");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.ADD_TASK );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String deadlineTime = dateFormat.format(returnedHashMap.get(PARAMETER.DEADLINE_TIME));
		assertEquals(deadlineTime, "15-11-2015 1600");
	}
	
	@Test
	public void testgetObjectHashMap6(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.DEADLINE_TIME, "wrwfwfw");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.ADD_TASK );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		assertEquals(returnedHashMap.get(PARAMETER.DEADLINE_TIME), null);
	}
	
	@Test
	public void testgetObjectHashMap7(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.DATE, "11/15");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.DISPLAY );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String returnedStartTime = dateFormat.format(returnedHashMap.get(PARAMETER.START_TIME));
		assertEquals(returnedStartTime, "15-11-2015 0000");
		String returnedEndTime = dateFormat.format(returnedHashMap.get(PARAMETER.END_TIME));
		assertEquals(returnedEndTime, "15-11-2015 2359");
	}
	
	@Test
	public void testgetObjectHashMap8(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.DEADLINE_TIME, "11/15");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.DISPLAY );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String deadlineTime = dateFormat.format(returnedHashMap.get(PARAMETER.DEADLINE_TIME));
		assertEquals(deadlineTime, "15-11-2015 2359");
	}
	
	@Test
	public void testgetObjectHashMap9(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.START_TIME, "2pm");
		testHashMap.put(PARAMETER.DATE, "11/15");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.EDIT_TASK );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String startTime = dateFormat.format(returnedHashMap.get(PARAMETER.START_TIME));
		assertEquals(startTime, "15-11-2015 1400");
		assertEquals(returnedHashMap.get(PARAMETER.END_TIME), null);
	}
	
	@Test
	public void testgetObjectHashMap10(){
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.DEADLINE_TIME, "11/15 3pm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.EDIT_TASK );
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String deadlineTime = dateFormat.format(returnedHashMap.get(PARAMETER.DEADLINE_TIME));
		assertEquals(deadlineTime, "15-11-2015 1500");
	}
	
	
}