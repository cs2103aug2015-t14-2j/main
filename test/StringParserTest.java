package test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.HashMap;

import org.junit.Test;

import Task.StringParser;
import Task.COMMAND_TYPE;
import Task.PARAMETER;

public class StringParserTest {

	private HashMap<PARAMETER, String> keywordHash =  new HashMap<PARAMETER, String>(0);
	
	@Test
	public void testObtainStringHashMap() throws ParseException {
		
		//Empty Case
		
		StringParser.getStringHashMap(COMMAND_TYPE.ADD_TASK,
				"",keywordHash);
		assertEquals(0,
				keywordHash.size());
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		//Basic case
		
		StringParser.getStringHashMap(COMMAND_TYPE.ADD_TASK,
				"by 12/11 1100 at \"hong kong\" do \"to be or not\" ",keywordHash);
		
		assertEquals("to be or not",
				keywordHash.get(PARAMETER.DESC));
		assertEquals("hong kong",
				keywordHash.get(PARAMETER.VENUE));
		assertEquals("12/11 1100",
				keywordHash.get(PARAMETER.DEADLINE_TIME));
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		//Having same day scheduling
		StringParser.getStringHashMap(COMMAND_TYPE.ADD_TASK,
				"at \"hong kong\" do \"to be or not\" on 15/11 from 1100 to 1500",keywordHash);
		
		assertEquals("15/11",
				keywordHash.get(PARAMETER.DATE));
		assertEquals("1500",
				keywordHash.get(PARAMETER.END_TIME));
		assertEquals("1100",
				keywordHash.get(PARAMETER.START_TIME));
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		//Having same day scheduling
		
		StringParser.getStringHashMap(COMMAND_TYPE.ADD_TASK,
				"from 1100 at \"hong kong\" to 1500 do \"to be or not\" on 15/11",keywordHash);
		
		assertEquals("15/11",
				keywordHash.get(PARAMETER.DATE));
		assertEquals("1500",
				keywordHash.get(PARAMETER.END_TIME));
		assertEquals("1100",
				keywordHash.get(PARAMETER.START_TIME));
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		//Using today keyword
		
		StringParser.getStringHashMap(COMMAND_TYPE.ADD_TASK,
				"at \"hong kong\" do \"to be or not\" today",keywordHash);
		
		assertEquals("today",
				keywordHash.get(PARAMETER.DATE));
		
		//Using tomorrow keyword
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		StringParser.getStringHashMap(COMMAND_TYPE.ADD_TASK,
				"at \"hong kong\" tomorrow 5pm do \"to be or not\"",keywordHash);
		
		assertEquals("tomorrow 5pm",
				keywordHash.get(PARAMETER.DATE));
		
		//Using tmr keyword
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		StringParser.getStringHashMap(COMMAND_TYPE.ADD_TASK,
				"at \"hong kong\" tmr 5pm do \"to be or not\"",keywordHash);
		
		assertEquals("tmr 5pm",
				keywordHash.get(PARAMETER.DATE));
	}
	
	@Test
	public void testEditobtainStringHashMap() throws ParseException {
		
		//Basic case
		StringParser.getStringHashMap(COMMAND_TYPE.EDIT_TASK,
				"4 by 12/11 1100 at \"hong kong\" do \"to be or not\" ",keywordHash);
		assertEquals("4",
				keywordHash.get(PARAMETER.TASKID));
		assertEquals("to be or not",
				keywordHash.get(PARAMETER.DESC));
		assertEquals("hong kong",
				keywordHash.get(PARAMETER.VENUE));
		assertEquals("12/11 1100",
				keywordHash.get(PARAMETER.DEADLINE_TIME));
	}
	
	@Test
	public void testDisplayobtainStringHashMap() throws ParseException {
		//Empty Case
		StringParser.getStringHashMap(COMMAND_TYPE.DISPLAY,
				"",keywordHash);
		assertEquals(1,
				keywordHash.size());
		
		//Basic case
		
		StringParser.getStringHashMap(COMMAND_TYPE.DISPLAY,
				" 4",keywordHash);
		assertEquals("4",
				keywordHash.get(PARAMETER.TASKID));
	}
	
	@Test
	public void testDeleteStringHashMap() throws ParseException {
		//Empty Case
		StringParser.getStringHashMap(COMMAND_TYPE.DELETE_TASK,
				"",keywordHash);
		assertEquals(1,
				keywordHash.size());
		
		//Basic case
		StringParser.getStringHashMap(COMMAND_TYPE.DELETE_TASK,
				"10",keywordHash);
		assertEquals("10",
				keywordHash.get(PARAMETER.TASKID));
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		//Basic case space before
		
		StringParser.getStringHashMap(COMMAND_TYPE.DELETE_TASK,
				" 10",keywordHash);
		assertEquals("10",
				keywordHash.get(PARAMETER.TASKID));
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		//Basic case space after
		
		StringParser.getStringHashMap(COMMAND_TYPE.DELETE_TASK,
				"10 ",keywordHash);
		assertEquals("10",
				keywordHash.get(PARAMETER.TASKID));
		
		//All case space after
		
		StringParser.getStringHashMap(COMMAND_TYPE.DELETE_TASK,
				" all ",keywordHash);
		assertEquals("-2",
				keywordHash.get(PARAMETER.TASKID));
	}
	
	// NORMALLY PRIVATE METHOD TESTING //

	@Test
	public void testTransferQuoteToHashMap() throws ParseException {
		
		StringParser.getStringHashMap(COMMAND_TYPE.ADD_TASK,"",keywordHash); //Initialize
		
		//Basic case do
		assertEquals("at \"hong kong\" by 12/11 1100",
						StringParser.transferQuoteToHashMap(PARAMETER.DESC,"do",
								"do \"to be or not\" at \"hong kong\" by 12/11 1100", keywordHash));
		
		assertEquals("to be or not",
				keywordHash.get(PARAMETER.DESC));
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		//Basic case at
		
		assertEquals("by 12/11 1100",
						StringParser.transferQuoteToHashMap(PARAMETER.VENUE,"at",
								" at \"hong kong\" by 12/11 1100", keywordHash));
		
		assertEquals("hong kong",
						keywordHash.get(PARAMETER.VENUE));
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		//No space in between quotes
		
		assertEquals("by 12/11 1100",
						StringParser.transferQuoteToHashMap(PARAMETER.VENUE,"at",
								"at\"hong kong\"by 12/11 1100", keywordHash));
		
		assertEquals("hong kong",
						keywordHash.get(PARAMETER.VENUE));
		
		keywordHash =  new HashMap<PARAMETER, String>(0);
		
		//at beginning
		assertEquals("by 12/11 1100",
						StringParser.transferQuoteToHashMap(PARAMETER.DESC,"",
								"\"do sth\" by 12/11 1100", keywordHash));
		assertEquals("do sth",
				keywordHash.get(PARAMETER.DESC));
	}

	@Test
	public void testFindKeywordIndexInput() {
		
		assertEquals(3,StringParser.findKeywordIndexInput("   hello","hello",-5));
		assertEquals(3,StringParser.findKeywordIndexInput("   hello","hello",0));
		assertEquals(-1,StringParser.findKeywordIndexInput("he\"llo\"","hello",0));
		assertEquals(-1,StringParser.findKeywordIndexInput("\"hello\"","hello",0));
		assertEquals(7,StringParser.findKeywordIndexInput("\"hello\"hello","hello",0));
		assertEquals(0,StringParser.findKeywordIndexInput("on 15/11 from 1100 to 1500","on",0));
		assertEquals(11,StringParser.findKeywordIndexInput("by 11/12   #hello","#",0));
		assertEquals(18,StringParser.findKeywordIndexInput("by 11/12   #hello #waffles","#",16));
		assertEquals(0,StringParser.findKeywordIndexInput("   #hello","",0));
		assertEquals(-1,StringParser.findKeywordIndexInput(null,"",0));
		assertEquals(0,StringParser.findKeywordIndexInput("   #hello",null,0));
		
	}

	@Test
	public void testTrimStringPortionOut() {
		
		assertEquals("",StringParser.trimStringPortionOut("hello",0,5));
		assertEquals("",StringParser.trimStringPortionOut("hello",-1,7));
		assertEquals("hello",StringParser.trimStringPortionOut("hello",7,9));
		assertEquals("ho",StringParser.trimStringPortionOut("hello",1,3));
		assertEquals("",StringParser.trimStringPortionOut("",1,3));
		assertEquals(null,StringParser.trimStringPortionOut(null,1,3));
	}

	@Test
	public void testGetKeywordnInString() {
		
		assertEquals("hello",StringParser.getKeywordInString("hello",0,5));
		assertEquals("hello",StringParser.getKeywordInString("hello",-1,7));
		assertEquals("",StringParser.getKeywordInString("hello",7,9));
		assertEquals("ell",StringParser.getKeywordInString("hello",1,3));
		assertEquals("",StringParser.getKeywordInString("",1,3));
		assertEquals(null,StringParser.getKeywordInString(null,1,3));
	}

	@Test
	public void testStringCompareToList() {
		
		assertEquals(2,StringParser.indexKeywordInString("hello",new String[]{"tiger","heelo","hell"}));
		assertEquals(1,StringParser.indexKeywordInString("hello",new String[]{"tiger","hello","hell"}));
		assertEquals(2,StringParser.indexKeywordInString("hello",new String[]{"tiger","hello2","hell"}));
		assertEquals(-1,StringParser.indexKeywordInString("",new String[]{"tiger","hello2","hell"}));
		assertEquals(-1,StringParser.indexKeywordInString("hello",new String[]{}));
		assertEquals(-1,StringParser.indexKeywordInString("hello",null));
		assertEquals(-1,StringParser.indexKeywordInString(null,null));
		
	}

}
