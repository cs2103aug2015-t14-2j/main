package test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import Task.Validator;
import Task.*;

public class ValidatorTest {

	@Test
	// This is to test getObjectHashMap() and see whether it returns the correct
	// stuff.
	public void testgetObjectHashMap() throws ParseException {
		// DESC, VENUE, START_DATE, END_DATE, START_TIME, END_TIME,
		// DEADLINE,REMIND_TIME
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.START_DATE, "9-oCtObeR");
		testHashMap.put(PARAMETER.END_DATE, "22/auG/16");
		testHashMap.put(PARAMETER.START_TIME, "1200");
		testHashMap.put(PARAMETER.END_TIME, "8pm");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap);
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String returnedStartDate = dateFormat.format(returnedHashMap.get(PARAMETER.START_DATE));
		assertEquals(returnedStartDate, "09-10-2015 0000");
		String returnedEndDate = dateFormat.format(returnedHashMap.get(PARAMETER.END_DATE));
		assertEquals(returnedEndDate, "22-08-2016 0000");
		String returnedStartTime = dateFormat.format(returnedHashMap.get(PARAMETER.START_TIME));
		assertEquals(returnedStartTime, "09-10-2015 1200");
		String returnedEndTime = dateFormat.format(returnedHashMap.get(PARAMETER.END_TIME));
		assertEquals(returnedEndTime, "22-08-2016 2000");
	}

	@Test
	// This is to test getObjectHashMap() and see whether it returns the correct
	// stuff.
	public void testgetObjectHashMap1() throws ParseException {
		// DESC, VENUE, START_DATE, END_DATE, START_TIME, END_TIME,
		// DEADLINE,REMIND_TIME
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.START_DATE, "21,05");
		testHashMap.put(PARAMETER.END_DATE, "22 05");
		testHashMap.put(PARAMETER.START_TIME, "1200");
		testHashMap.put(PARAMETER.END_TIME, "8pm");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap);
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		String returnedStartDate = dateFormat.format(returnedHashMap.get(PARAMETER.START_DATE));
		assertEquals(returnedStartDate, "21-05-2015 0000");
		String returnedEndDate = dateFormat.format(returnedHashMap.get(PARAMETER.END_DATE));
		assertEquals(returnedEndDate, "22-05-2015 0000");
		String returnedStartTime = dateFormat.format(returnedHashMap.get(PARAMETER.START_TIME));
		assertEquals(returnedStartTime, "21-05-2015 1200");
		String returnedEndTime = dateFormat.format(returnedHashMap.get(PARAMETER.END_TIME));
		assertEquals(returnedEndTime, "22-05-2015 2000");
	}

	@Test
	public void testgetObjectHashMap2() throws ParseException {
		// DESC, VENUE, START_DATE, END_DATE, START_TIME, END_TIME, DEADLINE,
		// PRIORITY, REMIND_TIME ,STARTENDTIME
		HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
		testHashMap.put(PARAMETER.DESC, "Eat Apple");
		testHashMap.put(PARAMETER.START_DATE, "21/05");
		testHashMap.put(PARAMETER.DEADLINE_DATE, "25/05/2016");
		testHashMap.put(PARAMETER.DEADLINE_TIME, "10am");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");

		HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
		returnedHashMap = Validator.getObjectHashMap(testHashMap);
		assertEquals(returnedHashMap.get(PARAMETER.DESC), "Eat Apple");
		assertEquals(testHashMap.get(PARAMETER.VENUE), null);
		String returnedStartDate = dateFormat.format(returnedHashMap.get(PARAMETER.START_DATE));
		assertEquals(returnedStartDate, "21-05-2015 0000");
		String returnedDeadlineDate = dateFormat.format(returnedHashMap.get(PARAMETER.DEADLINE_DATE));
		assertEquals(returnedDeadlineDate, "25-05-2016 0000");
		String returnedDeadlineTime = dateFormat.format(returnedHashMap.get(PARAMETER.DEADLINE_TIME));
		assertEquals(returnedDeadlineTime, "25-05-2016 1000");
	}

	@Test
	// Test Correct dates in this format: DD/MMM(WORD)
	public void testWordMonthFormat() {
		String date = "21/APr";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.wordMonthFormat(date);
		String output = dateFormat.format(dateTest);
		assertEquals(output, "21-04-2015");
	}

	@Test
	// Test Correct dates in this format: DD/MMM(WORD)/YYYY
	public void testWordMonthFormat2() {
		String date = "21-APr-2016";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.wordMonthFormat(date);
		String output = dateFormat.format(dateTest);
		assertEquals(output, "21-04-2016");
	}

	@Test
	// Testing Illegal Dates
	public void testWordMonthFormat3() {
		String date = "35/APr/2016";
		Date dateTest = Validator.wordMonthFormat(date);
		assertEquals(dateTest, null);
	}

	@Test
	public void testWordMonthFormat4() {
		String date = "Apr/21";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.wordMonthFormat(date);
		String output = dateFormat.format(dateTest);
		assertEquals(output, "21-04-2015");
	}

	@Test
	// Test Correct dates in this format: DD/MMM(WORD)
	public void testWordMonthFormat5() {
		String date = "21 APrIL 15";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.wordMonthFormat(date);
		String output = dateFormat.format(dateTest);
		assertEquals(output, "21-04-2015");
	}

	@Test
	// Test Correct dates in this format: DD/MMM(WORD)
	public void numberDateFormat() {
		String date = "21-05";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.numberDateFormat(date);
		String output = dateFormat.format(dateTest);
		assertEquals(output, "21-05-2015");
	}
	
	public void numberDateFormat1() {
		String date = "2015-21-05";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.numberDateFormat(date);
		String output = dateFormat.format(dateTest);
		assertEquals(output, "21-05-2015");
	}

	@Test
	// Test proper date
	public void numberDateFormat2() {
		String date = "21/05/2016";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.numberDateFormat(date);
		String output = dateFormat.format(dateTest);
		assertEquals(output, "21-05-2016");
	}

	@Test
	// Test improper month
	public void numberDateFormat3() {
		String date = "21/13/2016";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.numberDateFormat(date);
		assertEquals(dateTest, null);
	}
	
	
	@Test
	// Test Word Date formats
	public void testWordFormat(){
		Date date = Validator.wordFormat("today");
		Calendar cal = Calendar.getInstance();
		assertEquals(date, cal.getTime());
		
		date = Validator.wordFormat("tomorrow");
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertEquals(date, cal.getTime());
		
		date = Validator.wordFormat("day afTer");
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertEquals(date, cal.getTime());
	}
	
	
	
	
	@Test
	// Test Improper day
	public void numberDateFormat4() {
		String date = "31/09/2016";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.numberDateFormat(date);
		assertEquals(dateTest, null);
	}
	
	@Test
	// Test invalid Leap year
	public void numberDateFormat5() {
		String date = "29/02/2015";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date dateTest = Validator.numberDateFormat(date);
		assertEquals(dateTest, null);
	}

	@Test
	// Test Time formats
	public void test12hrTimeFormat() throws ParseException {
		String time = "1230pm";
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
		Date timeTest = Validator.is12hrTimeFormat(time);
		String output = timeFormat.format(timeTest);
		assertEquals(output, "1230");
	}

	@Test
	// Test valid Time formats
	public void test12hrTimeFormat1() throws ParseException {
		String time = "930pm";
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		Date timeTest = Validator.is12hrTimeFormat(time);
		String output = timeFormat.format(timeTest);
		assertEquals(output, "21:30");
	}

	@Test
	// Test invalid
	public void test12hrTimeFormat2() throws ParseException {
		String time = "1330pm";
	//	SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
		Date timeTest = Validator.is12hrTimeFormat(time);
		// String output = timeFormat.format(timeTest);
		assertEquals(timeTest, null);
	}
	
	@Test
	// Test valid Time
	public void test12hrTimeFormat3() throws ParseException {
		String time = "1230pm";
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		Date timeTest = Validator.is12hrTimeFormat(time);
		 String output = timeFormat.format(timeTest);
		assertEquals(output, "12:30");
	}
	
	@Test
	// Test Invalid AMPM
	public void test12hrTimeFormat4() throws ParseException {
		String time = "1330zm";
		//SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
		Date timeTest = Validator.is12hrTimeFormat(time);
		// String output = timeFormat.format(timeTest);
		assertEquals(timeTest, null);
	}
	
	@Test
	// Test valid Time
	public void test24hrTimeFormat() throws ParseException {
		String time = "1330";
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		Date timeTest = Validator.is24hrTimeFormat(time);
		 String output = timeFormat.format(timeTest);
		assertEquals(output, "13:30");
	}

	@Test
	// Test 3 digit time		
	public void test24hrTimeFormat1() throws ParseException {
		String time = "230";
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
		Date timeTest = Validator.is24hrTimeFormat(time);
		 String output = timeFormat.format(timeTest);
		assertEquals(output, "02:30");
	}
	
	@Test
	// Test invalid time 		
	public void test24hrTimeFormat2() throws ParseException {
		String time = "2530";
		//SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
		Date timeTest = Validator.is24hrTimeFormat(time);
		// String output = timeFormat.format(timeTest);
		assertEquals(timeTest, null);
	}
	
}
