package test;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import Task.StringParser;
import Task.COMMAND_TYPE;
import Task.PARAMETER;

public class StringParserTest {

	private HashMap<PARAMETER, String> keywordHash = null;

	@Test
	public void testAddGetValuesFromInput() {
		
		
		//Empty Case
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"");
		assertEquals(0,
				keywordHash.size());
		
		//Basic case
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"by 12/11 1100 at \"hong kong\" do \"to be or not\" ");
		
		assertEquals("to be or not",
				keywordHash.get(PARAMETER.DESC));
		assertEquals("hong kong",
				keywordHash.get(PARAMETER.VENUE));
		assertEquals("12/11",
				keywordHash.get(PARAMETER.DEADLINE_DATE));
		assertEquals("1100",
				keywordHash.get(PARAMETER.DEADLINE_TIME));
		
		//Having same day scheduling
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"at \"hong kong\" do \"to be or not\" on 15/11 from 1100 to 1500");
		
		assertEquals("15/11",
				keywordHash.get(PARAMETER.START_DATE));
		assertEquals("15/11",
				keywordHash.get(PARAMETER.END_DATE));
		assertEquals("1100",
				keywordHash.get(PARAMETER.START_TIME));
		assertEquals("1500",
				keywordHash.get(PARAMETER.END_TIME));
		
		//Having same day scheduling
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"from 1100 at \"hong kong\" to 1500 do \"to be or not\" on 15/11");
		
		assertEquals("15/11",
				keywordHash.get(PARAMETER.START_DATE));
		assertEquals("15/11",
				keywordHash.get(PARAMETER.END_DATE));
		assertEquals("1100",
				keywordHash.get(PARAMETER.START_TIME));
		assertEquals("1500",
				keywordHash.get(PARAMETER.END_TIME));
		
		//Incorrect Format
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"from 1100 at \"hong kong\" to 12/11 1500 do \"to be or not\" on 1300"); //#miley #hola remind 30 60 90
		assertEquals(null,
				keywordHash.get(PARAMETER.START_DATE));
		assertEquals(null,
				keywordHash.get(PARAMETER.END_DATE));
		assertEquals("1100",
				keywordHash.get(PARAMETER.START_TIME));
		assertEquals(null,
				keywordHash.get(PARAMETER.END_TIME));
		
		//No param for keyword
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"by at do \"to be or not\" ");
		
		assertEquals("to be or not",
				keywordHash.get(PARAMETER.DESC));
		assertEquals(null,
				keywordHash.get(PARAMETER.VENUE));
		assertEquals(null,
				keywordHash.get(PARAMETER.DEADLINE_DATE));
		assertEquals(null,
				keywordHash.get(PARAMETER.DEADLINE_TIME));
		
		//No param for keyword
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"by \"fd\" at \"Huka\" do 43 \"sdfji\"");
		
		assertEquals("sdfji",
				keywordHash.get(PARAMETER.DESC));
		assertEquals("Huka",
				keywordHash.get(PARAMETER.VENUE));
		assertEquals(null,
				keywordHash.get(PARAMETER.DEADLINE_DATE));
		assertEquals(null,
				keywordHash.get(PARAMETER.DEADLINE_TIME));
				
		//giberrish
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"bjsdf sdfjla \"at party\" at \"hula hula\" fds8893 93 023 d0 do fs 4");
		
		assertEquals(null,
				keywordHash.get(PARAMETER.DESC));
		assertEquals("hula hula",
				keywordHash.get(PARAMETER.VENUE));
		assertEquals(null,
				keywordHash.get(PARAMETER.DEADLINE_DATE));
		assertEquals(null,
				keywordHash.get(PARAMETER.DEADLINE_TIME));
	}
	
	@Test
	public void testEditGetValuesFromInput() {
		
		//Basic case
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				"4 by 12/11 1100 at \"hong kong\" do \"to be or not\" ");
		assertEquals("4",
				keywordHash.get(PARAMETER.TASKID));
		assertEquals("to be or not",
				keywordHash.get(PARAMETER.DESC));
		assertEquals("hong kong",
				keywordHash.get(PARAMETER.VENUE));
		assertEquals("12/11",
				keywordHash.get(PARAMETER.DEADLINE_DATE));
		assertEquals("1100",
				keywordHash.get(PARAMETER.DEADLINE_TIME));
	}
	
	@Test
	public void testDisplayGetValuesFromInput() {
		//Empty Case
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				"");
		assertEquals(0,
				keywordHash.size());
		
		//Basic case
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.DISPLAY,
				" 4");
		assertEquals("4",
				keywordHash.get(PARAMETER.TASKID));
	}
	
	@Test
	public void testDeleteGetValuesFromInput() {
		//Empty Case
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.DELETE_TASK,
				"");
		assertEquals(0,
				keywordHash.size());
		
		//Basic case
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				"10");
		assertEquals("10",
				keywordHash.get(PARAMETER.TASKID));
		
		//Basic case space before
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				" 10");
		assertEquals("10",
				keywordHash.get(PARAMETER.TASKID));
		
		//Basic case space after
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				"10 ");
		assertEquals("10",
				keywordHash.get(PARAMETER.TASKID));
	}
	
	// NORMALLY PRIVATE METHOD TESTING //

	@Test
	public void testTransferQuoteToHashMap() {
		
		keywordHash = StringParser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,""); //Initialize
		
		assertEquals("at \"hong kong\" by 12/11 1100",
						StringParser.transferQuoteToHashMap(PARAMETER.DESC,"do",
								"do \"to be or not\" at \"hong kong\" by 12/11 1100", keywordHash));
		assertEquals(" by 12/11 1100",
						StringParser.transferQuoteToHashMap(PARAMETER.VENUE,"at",
								" at \"hong kong\" by 12/11 1100", keywordHash));
		assertEquals("to be or not",
						keywordHash.get(PARAMETER.DESC));
		assertEquals("hong kong",
						keywordHash.get(PARAMETER.VENUE));
	}
	
	@Test
	public void testContainsOnlyNumbers(){
		
		assertEquals(true,StringParser.containsOnlyNumbers("4"));
		assertEquals(true,StringParser.containsOnlyNumbers(" 4"));
		assertEquals(true,StringParser.containsOnlyNumbers("4 "));
		assertEquals(true,StringParser.containsOnlyNumbers("4 9"));
		assertEquals(false,StringParser.containsOnlyNumbers("4 j"));
		assertEquals(false,StringParser.containsOnlyNumbers(""));
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
		assertEquals(-1,StringParser.findKeywordIndexInput("   #hello","",0));
		assertEquals(-1,StringParser.findKeywordIndexInput(null,"",0));
		assertEquals(-1,StringParser.findKeywordIndexInput("   #hello",null,0));
		
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
		
		assertEquals("hello",StringParser.getKeywordnInString("hello",0,5));
		assertEquals("hello",StringParser.getKeywordnInString("hello",-1,7));
		assertEquals("",StringParser.getKeywordnInString("hello",7,9));
		assertEquals("ell",StringParser.getKeywordnInString("hello",1,3));
		assertEquals("",StringParser.getKeywordnInString("",1,3));
		assertEquals(null,StringParser.getKeywordnInString(null,1,3));
	}

	@Test
	public void testStringCompareToList() {
		
		assertEquals(-1,StringParser.stringCompareToList("hello",new String[]{"tiger","heelo","hell"}));
		assertEquals(1,StringParser.stringCompareToList("hello",new String[]{"tiger","hello","hell"}));
		assertEquals(-1,StringParser.stringCompareToList("hello",new String[]{"tiger","hello2","hell"}));
		assertEquals(-1,StringParser.stringCompareToList("",new String[]{"tiger","hello2","hell"}));
		assertEquals(-1,StringParser.stringCompareToList("hello",new String[]{}));
		assertEquals(-1,StringParser.stringCompareToList("hello",null));
		assertEquals(-1,StringParser.stringCompareToList(null,null));
		
	}

}
