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
		parser = new StringParser();
		parser.getValuesFromInput(COMMAND_TYPE.ADD_TASK,
				"do \"to be or not\" at \"hong kong\" by 12/11 1100"); //#miley #hola remind 30 60 90
		assertEquals("to be or not",
				parser.getKeywordHash().get(PARAMETER.DESC).get(0));
		assertEquals("hong kong",
				parser.getKeywordHash().get(PARAMETER.VENUE).get(0));
		assertEquals("12/11",
				parser.getKeywordHash().get(PARAMETER.DEADLINE_DATE).get(0));
		assertEquals("1100",
				parser.getKeywordHash().get(PARAMETER.DEADLINE_TIME).get(0));
		/*assertEquals("miley",
				parser.getKeywordHash().get(PARAMETER.HASHTAGS).get(0));
		assertEquals("hola",
				parser.getKeywordHash().get(PARAMETER.HASHTAGS).get(1));
		assertEquals("30",
				parser.getKeywordHash().get(PARAMETER.REMIND_TIMES).get(0));
		assertEquals("60",
				parser.getKeywordHash().get(PARAMETER.REMIND_TIMES).get(2));*/
	}

	@Test
	public void testTransferQuoteToHashMap() {
		parser = new StringParser();
		assertEquals(" at \"hong kong\" by 12/11 1100",
						parser.transferQuoteToHashMap(PARAMETER.DESC,"do",
								"do \"to be or not\" at \"hong kong\" by 12/11 1100"));
		assertEquals("  by 12/11 1100",
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
		assertEquals(3,parser.findKeywordIndexInput("   hello","hello"));
		assertEquals(-1,parser.findKeywordIndexInput("he\"llo\"","hello"));
		assertEquals(-1,parser.findKeywordIndexInput("\"hello\"","hello"));
		assertEquals(7,parser.findKeywordIndexInput("\"hello\"hello","hello"));
		assertEquals(3,parser.findKeywordIndexInput("   #hello","#"));
	}

	@Test
	public void testTrimStringPortionOut() {
		parser = new StringParser();
		assertEquals("",parser.trimStringPortionOut("hello",0,5));
		assertEquals("",parser.trimStringPortionOut("hello",0,7));
		assertEquals("hello",parser.trimStringPortionOut("hello",7,9));
		assertEquals("ho",parser.trimStringPortionOut("hello",1,3));
	}

	@Test
	public void testGetKeywordnInString() {
		parser = new StringParser();
		assertEquals("hello",parser.getKeywordnInString("hello",0,5));
		assertEquals("hello",parser.getKeywordnInString("hello",0,7));
		assertEquals("",parser.getKeywordnInString("hello",7,9));
		assertEquals("ell",parser.getKeywordnInString("hello",1,3));
	}

	@Test
	public void testStringCompareToList() {
		parser = new StringParser();
		assertEquals(-1,parser.stringCompareToList("hello",new String[]{"tiger","heelo","hell"}));
		assertEquals(1,parser.stringCompareToList("hello",new String[]{"tiger","hello","hell"}));
		assertEquals(-1,parser.stringCompareToList("hello",new String[]{"tiger","hello2","hell"}));
	}

	@Test
	public void testClearHashmap() {
		parser = new StringParser();
		parser.clearHashmap();
		assertEquals(0,parser.getKeywordHash().size());
	}
}
