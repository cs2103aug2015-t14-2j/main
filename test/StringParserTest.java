package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Task.StringParser;
import Task.COMMAND_TYPE;
import Task.PARAMETER;

public class StringParserTest {

	private StringParser parser = null;
	
	@Test
	public void testStringParser() {
		parser = new StringParser();
		assertEquals(0,parser.getKeywordHash().size());
	}

	@Test
	public void testGetValuesFromInput() {
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
				parser.getKeywordHash().get(PARAMETER.DESC).get(0));
		assertEquals("hong kong",
				parser.getKeywordHash().get(PARAMETER.VENUE).get(0));
		assertEquals("12/11",
				parser.getKeywordHash().get(PARAMETER.DEADLINE_DATE).get(0));
		assertEquals("1100",
				parser.getKeywordHash().get(PARAMETER.DEADLINE_TIME).get(0));
		
		//Having same day scheduling
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"from 1100 at \"hong kong\" to 1500 do \"to be or not\" on 11/15");
		
		assertEquals("11/15",
				parser.getKeywordHash().get(PARAMETER.START_DATE).get(0));
		assertEquals("11/15",
				parser.getKeywordHash().get(PARAMETER.END_DATE).get(0));
		assertEquals("1100",
				parser.getKeywordHash().get(PARAMETER.START_TIME).get(0));
		assertEquals("1500",
				parser.getKeywordHash().get(PARAMETER.END_TIME).get(0));
		
		//Incorrect Format
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"from 1100 at \"hong kong\" to 12/11 1500 do \"to be or not\" on 1300"); //#miley #hola remind 30 60 90
		assertEquals("1300",
				parser.getKeywordHash().get(PARAMETER.START_DATE).get(0));
		assertEquals("1300",
				parser.getKeywordHash().get(PARAMETER.END_DATE).get(0));
		assertEquals("1100",
				parser.getKeywordHash().get(PARAMETER.START_TIME).get(0));
		assertEquals("12/11",
				parser.getKeywordHash().get(PARAMETER.END_TIME).get(0));
		
		//multiple # and reminders
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"from 1100 at \"hong kong\" to 1500 do \"to be or not\" on 11/15 #miley #hola remind 30 50 60");
		assertEquals("miley",
				parser.getKeywordHash().get(PARAMETER.HASHTAGS).get(0));
		assertEquals("hola",
				parser.getKeywordHash().get(PARAMETER.HASHTAGS).get(1));
		assertEquals("30",
				parser.getKeywordHash().get(PARAMETER.REMIND_TIMES).get(0));
		assertEquals("60",
				parser.getKeywordHash().get(PARAMETER.REMIND_TIMES).get(2));
		
		//multiple # and reminders with different order
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"from 1100 at \"hong kong\" to 1500 do \"to be or not\" on 11/15 #miley remind 30 50 60 #hola");
		assertEquals("miley",
				parser.getKeywordHash().get(PARAMETER.HASHTAGS).get(0));
		assertEquals("hola",
				parser.getKeywordHash().get(PARAMETER.HASHTAGS).get(1));
		assertEquals("30",
				parser.getKeywordHash().get(PARAMETER.REMIND_TIMES).get(0));
		assertEquals("60",
				parser.getKeywordHash().get(PARAMETER.REMIND_TIMES).get(2));
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
						parser.getKeywordHash().get(PARAMETER.DESC).get(0));
		assertEquals("hong kong",
						parser.getKeywordHash().get(PARAMETER.VENUE).get(0));
	}

	@Test
	public void testFindKeywordIndexInput() {
		parser = new StringParser();
		assertEquals(3,parser.findKeywordIndexInput("   hello","hello",-5));
		assertEquals(3,parser.findKeywordIndexInput("   hello","hello",0));
		assertEquals(-1,parser.findKeywordIndexInput("he\"llo\"","hello",0));
		assertEquals(-1,parser.findKeywordIndexInput("\"hello\"","hello",0));
		assertEquals(7,parser.findKeywordIndexInput("\"hello\"hello","hello",0));
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
