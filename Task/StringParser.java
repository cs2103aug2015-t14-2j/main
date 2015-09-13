package Task;

import java.util.Date;

import java.util.HashMap;

/**
 *  Represents the handler for tasks
 * 
 *  @author A0097689 Tan Si Kai
 *  @author A0009586 Jean Pierre Castillo
 *  @author A0118772  Audrey Tiah
 */

public class StringParser {
	
	private static final String SPACE_CHARACTER = "\\s+";
	
	private static final int QUOTE_INTEGER = 34;
	
	private HashMap<PARAMETER, String> keywordHash = null;
	
	/**
	 * Initiates the parser and parses the userInput based on the type of command
	 * @param command The type of command to be executed
	 * @param userInput
	 */
	public StringParser(){
		keywordHash = new HashMap<PARAMETER, String>(0);
	}
	
	/**
	 * 
	 * @param command
	 * @param query
	 * @return
	 */
	public HashMap<PARAMETER, String> getValuesFromInput(COMMAND_TYPE command, String userInput) {
		
		switch (command) {
		case ADD_TASK:
			//Have "do" search for first to get rid of string
			userInput = transferQuoteToHashMap("do",userInput);
			userInput = transferQuoteToHashMap("at",userInput);
			
			String[] keywordsInInput={"on","from","to","by","on","at","with","remind","#"};
			
			addAttributesToHashTable(keywordsInInput, userInput.split(SPACE_CHARACTER));
			break;
		case GET_TASK:
			
		case DISPLAY:
			
		case SEARCH_TASK:
			
		case EDIT_TASK:
						
		default:
			
		}
		
		return keywordHash;
	}

	/**
	 * 
	 * @param userInput
	 * @return
	 */
	private String transferQuoteToHashMap(String keyword, String userInput) {
		//TODO ASSUMES "" are places correctly after keyword
		int positionOfKeyword = userInput.toLowerCase().indexOf(keyword, 0);
		int startOfQuote = userInput.indexOf(QUOTE_INTEGER, positionOfKeyword);
		int endOfQuote = userInput.indexOf(QUOTE_INTEGER, startOfQuote + 1);
		keywordHash.put(PARAMETER.DESC, getDescriptionInString(userInput, startOfQuote, endOfQuote));
		return trimStringPortionOut(userInput, positionOfKeyword, endOfQuote);
	}

	/**
	 * 
	 * @param userInput
	 * @param startOfDesc
	 * @param endOfDesc
	 * @return
	 */
	private String trimStringPortionOut(String userInput, int startOfDesc, int endOfDesc) {
		StringBuilder result = new StringBuilder();
	    for (char c : userInput.toCharArray()) {
	        if (userInput.indexOf(c) < startOfDesc || userInput.indexOf(c) > endOfDesc) {
	            result.append(c);
	        }
	    }
	    return result.toString();
	}

	/**
	 * 
	 * @param userInput
	 * @param startOfDesc
	 * @param endOfDesc
	 * @return
	 */
	private String getDescriptionInString(String userInput, int startOfDesc, int endOfDesc) {
		StringBuilder result = new StringBuilder();
	    for (char c : userInput.toCharArray()) {
	        if (userInput.indexOf(c) >= startOfDesc || userInput.indexOf(c) <= endOfDesc) {
	            result.append(c);
	        }
	    }
	    return result.toString();
	}

	/**
	 * 
	 * @param keywordsInInput
	 * @param stringToParse
	 */
	private void addAttributesToHashTable(String[] keywordsInInput, String[] stringToParse) {
		for(int i = 0; i < keywordsInInput.length; i++){
			
		}
	}
	
}


