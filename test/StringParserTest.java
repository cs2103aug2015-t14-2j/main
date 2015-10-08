package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Task.StringParser;
import Task.COMMAND_TYPE;
import Task.PARAMETER;

public class StringParserTest {

	private StringParser parser = null;
	
	//TODO: add do "get dog" by 02/05/15 at "hong kong"
	
	@Test
	public void testStringParser() {
		parser = new StringParser();
		assertEquals(0,parser.getKeywordHash().size());
	}

	@Test
	public void testAddGetValuesFromInput() {
		//Empty Case
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"");
		assertEquals(0,
				parser.getKeywordHash().size());
		
		//Basic case
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"by 12/11 1100 at \"hong kong\" do \"to be or not\" ");
		
		assertEquals("to be or not",
				parser.getKeywordHash().get(PARAMETER.DESC));
		assertEquals("hong kong",
				parser.getKeywordHash().get(PARAMETER.VENUE));
		assertEquals("12/11",
				parser.getKeywordHash().get(PARAMETER.DEADLINE_DATE));
		assertEquals("1100",
				parser.getKeywordHash().get(PARAMETER.DEADLINE_TIME));
		
		//Having same day scheduling
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"at \"hong kong\" do \"to be or not\" on 15/11 from 1100 to 1500");
		
		assertEquals("15/11",
				parser.getKeywordHash().get(PARAMETER.START_DATE));
		assertEquals("15/11",
				parser.getKeywordHash().get(PARAMETER.END_DATE));
		assertEquals("1100",
				parser.getKeywordHash().get(PARAMETER.START_TIME));
		assertEquals("1500",
				parser.getKeywordHash().get(PARAMETER.END_TIME));
		
		//Having same day scheduling
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"from 1100 at \"hong kong\" to 1500 do \"to be or not\" on 15/11");
		
		assertEquals("15/11",
				parser.getKeywordHash().get(PARAMETER.START_DATE));
		assertEquals("15/11",
				parser.getKeywordHash().get(PARAMETER.END_DATE));
		assertEquals("1100",
				parser.getKeywordHash().get(PARAMETER.START_TIME));
		assertEquals("1500",
				parser.getKeywordHash().get(PARAMETER.END_TIME));
		
		//Incorrect Format
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"from 1100 at \"hong kong\" to 12/11 1500 do \"to be or not\" on 1300"); //#miley #hola remind 30 60 90
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.START_DATE));
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.END_DATE));
		assertEquals("1100",
				parser.getKeywordHash().get(PARAMETER.START_TIME));
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.END_TIME));
		
		//No param for keyword
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"by at do \"to be or not\" ");
		
		assertEquals("to be or not",
				parser.getKeywordHash().get(PARAMETER.DESC));
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.VENUE));
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.DEADLINE_DATE));
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.DEADLINE_TIME));
		
		//No param for keyword
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"by \"fd\" at \"Huka\" do 43 \"sdfji\"");
		
		assertEquals("sdfji",
				parser.getKeywordHash().get(PARAMETER.DESC));
		assertEquals("Huka",
				parser.getKeywordHash().get(PARAMETER.VENUE));
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.DEADLINE_DATE));
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.DEADLINE_TIME));
				
		//giberrish
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"bjsdf sdfjla \"at party\" at \"hula hula\" fds8893 93 023 d0 do fs 4");
		
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.DESC));
		assertEquals("hula hula",
				parser.getKeywordHash().get(PARAMETER.VENUE));
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.DEADLINE_DATE));
		assertEquals(null,
				parser.getKeywordHash().get(PARAMETER.DEADLINE_TIME));
	}
	
	@Test
	public void testEditGetValuesFromInput() {
		//Empty Case
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				"");
		assertEquals(0,
				parser.getKeywordHash().size());
		
		//Basic case
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				"4 by 12/11 1100 at \"hong kong\" do \"to be or not\" ");
		assertEquals("4",
				parser.getKeywordHash().get(PARAMETER.TASKID));
		assertEquals("to be or not",
				parser.getKeywordHash().get(PARAMETER.DESC));
		assertEquals("hong kong",
				parser.getKeywordHash().get(PARAMETER.VENUE));
		assertEquals("12/11",
				parser.getKeywordHash().get(PARAMETER.DEADLINE_DATE));
		assertEquals("1100",
				parser.getKeywordHash().get(PARAMETER.DEADLINE_TIME));
	}
	
	@Test
	public void testDisplayGetValuesFromInput() {
		//Empty Case
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				"");
		assertEquals(0,
				parser.getKeywordHash().size());
		
		//Basic case
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.DISPLAY,
				" 4");
		assertEquals("4",
				parser.getKeywordHash().get(PARAMETER.TASKID));
	}
	
	@Test
	public void testDeleteGetValuesFromInput() {
		//Empty Case
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.DELETE_TASK,
				"");
		assertEquals(0,
				parser.getKeywordHash().size());
		
		//Basic case
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				" 10");
		assertEquals("10",
				parser.getKeywordHash().get(PARAMETER.TASKID));
		
		//Basic case
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.EDIT_TASK,
				"10 ");
		assertEquals("10",
				parser.getKeywordHash().get(PARAMETER.TASKID));
	}

	@Test
	public void testTransferQuoteToHashMap() {
		parser = new StringParser();
		assertEquals("at \"hong kong\" by 12/11 1100",
						parser.transferQuoteToHashMap(PARAMETER.DESC,"do",
								"do \"to be or not\" at \"hong kong\" by 12/11 1100"));
		assertEquals(" by 12/11 1100",
						parser.transferQuoteToHashMap(PARAMETER.VENUE,"at",
								" at \"hong kong\" by 12/11 1100"));
		assertEquals("to be or not",
						parser.getKeywordHash().get(PARAMETER.DESC));
		assertEquals("hong kong",
						parser.getKeywordHash().get(PARAMETER.VENUE));
	}
	
	@Test
	public void testContainsOnlyNumbers(){
		parser = new StringParser();
		assertEquals(true,parser.containsOnlyNumbers("4"));
		assertEquals(true,parser.containsOnlyNumbers(" 4"));
		assertEquals(true,parser.containsOnlyNumbers("4 "));
		assertEquals(true,parser.containsOnlyNumbers("4 9"));
		assertEquals(false,parser.containsOnlyNumbers("4 j"));
		assertEquals(false,parser.containsOnlyNumbers(""));
	}

	@Test
	public void testFindKeywordIndexInput() {
		parser = new StringParser();
		assertEquals(3,parser.findKeywordIndexInput("   hello","hello",-5));
		assertEquals(3,parser.findKeywordIndexInput("   hello","hello",0));
		assertEquals(-1,parser.findKeywordIndexInput("he\"llo\"","hello",0));
		assertEquals(-1,parser.findKeywordIndexInput("\"hello\"","hello",0));
		assertEquals(7,parser.findKeywordIndexInput("\"hello\"hello","hello",0));
		assertEquals(0,parser.findKeywordIndexInput("on 15/11 from 1100 to 1500","on",0));
		assertEquals(11,parser.findKeywordIndexInput("by 11/12   #hello","#",0));
		assertEquals(18,parser.findKeywordIndexInput("by 11/12   #hello #waffles","#",16));
		assertEquals(-1,parser.findKeywordIndexInput("   #hello","",0));
		assertEquals(-1,parser.findKeywordIndexInput(null,"",0));
		assertEquals(-1,parser.findKeywordIndexInput("   #hello",null,0));
		
	}

	@Test
	public void testTrimStringPortionOut() {
		parser = new StringParser();
		assertEquals("",parser.trimStringPortionOut("hello",0,5));
		assertEquals("",parser.trimStringPortionOut("hello",-1,7));
		assertEquals("hello",parser.trimStringPortionOut("hello",7,9));
		assertEquals("ho",parser.trimStringPortionOut("hello",1,3));
		assertEquals("",parser.trimStringPortionOut("",1,3));
		assertEquals(null,parser.trimStringPortionOut(null,1,3));
	}

	@Test
	public void testGetKeywordnInString() {
		parser = new StringParser();
		assertEquals("hello",parser.getKeywordnInString("hello",0,5));
		assertEquals("hello",parser.getKeywordnInString("hello",-1,7));
		assertEquals("",parser.getKeywordnInString("hello",7,9));
		assertEquals("ell",parser.getKeywordnInString("hello",1,3));
		assertEquals("",parser.getKeywordnInString("",1,3));
		assertEquals(null,parser.getKeywordnInString(null,1,3));
	}

	@Test
	public void testStringCompareToList() {
		parser = new StringParser();
		assertEquals(-1,parser.stringCompareToList("hello",new String[]{"tiger","heelo","hell"}));
		assertEquals(1,parser.stringCompareToList("hello",new String[]{"tiger","hello","hell"}));
		assertEquals(-1,parser.stringCompareToList("hello",new String[]{"tiger","hello2","hell"}));
		assertEquals(-1,parser.stringCompareToList("",new String[]{"tiger","hello2","hell"}));
		assertEquals(-1,parser.stringCompareToList("hello",new String[]{}));
		assertEquals(-1,parser.stringCompareToList("hello",null));
		assertEquals(-1,parser.stringCompareToList(null,null));
		
	}

	@Test
	public void testClearHashmap() {
		parser = new StringParser();
		parser.clearHashmap();
		assertEquals(0,parser.getKeywordHash().size());
	}
}
