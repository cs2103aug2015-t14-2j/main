package test;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import Task.COMMAND_TYPE;
import Task.Validator;
import Task.*;

public class ValidatorTest {

	@Test
	public void testValidateUserInput() {
		//DESC, VENUE, START_DATE, END_DATE, START_TIME, END_TIME, DEADLINE, PRIORITY, REMIND_TIME ,STARTENDTIME
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.VENUE, "LoooL");
		testHashMap.put(PARAMETER.START_DATE, "21/05");
		testHashMap.put(PARAMETER.END_DATE, "22/05");
		testHashMap.put(PARAMETER.START_TIME, "1200");
		testHashMap.put(PARAMETER.END_TIME, "8pm");

		
		HashMap<PARAMETER, String> returnTest = Validator.validateUserInput(COMMAND_TYPE.ADD_TASK,testHashMap);
		assertTrue(returnTest.get(PARAMETER.DESC).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.VENUE).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.START_DATE).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.END_DATE).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.START_TIME).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.END_TIME).equals("VALID"));
	}
	
	@Test
	public void testValidateUserInput2() {
		//DESC, VENUE, START_DATE, END_DATE, START_TIME, END_TIME, DEADLINE, PRIORITY, REMIND_TIME ,STARTENDTIME
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.VENUE, "LoooL");
		testHashMap.put(PARAMETER.START_DATE, "21/05/2015");
		testHashMap.put(PARAMETER.END_DATE, "22/05/15");
		testHashMap.put(PARAMETER.START_TIME, "1200");
		testHashMap.put(PARAMETER.END_TIME, "8pm");

		
		HashMap<PARAMETER, String> returnTest = Validator.validateUserInput(COMMAND_TYPE.ADD_TASK,testHashMap);
		assertTrue(returnTest.get(PARAMETER.DESC).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.VENUE).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.START_DATE).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.END_DATE).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.START_TIME).equals("VALID"));
		assertTrue(returnTest.get(PARAMETER.END_TIME).equals("VALID"));
	}


}
