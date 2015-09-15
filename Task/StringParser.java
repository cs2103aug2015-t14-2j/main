package Task;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Represents the parser for strings
 * 
 *  @author A0097689 Tan Si Kai
 *  @author A0009586 Jean Pierre Castillo
 *  @author A0118772 Audrey Tiah
 */

public class StringParser {
	
	//Define String constants here
	private static final String SPACE_CHARACTER = "\\s+";
	
	//Define int constants here
	private static final int QUOTE_INTEGER = 34;
	private static final int WITHIN_KEYWORD = 0;
	private static final int SEPERATED_BY_SPACES = 1;
	
	//The hashmap contructed
	private HashMap<PARAMETER, ArrayList<String>> keywordHash = null;
	
	/**
	 * Gets the current HashMap
	 * @return The HashMap
	 */
	public HashMap<PARAMETER, ArrayList<String>> getKeywordHash() {
		return keywordHash;
	}

	/**
	 * Used to clear the HashMap in use
	 */
	public void clearHashmap(){
		keywordHash = new HashMap<PARAMETER, ArrayList<String>>(0);
	}

	/**
	 * Initiates the parser and parses the userInput based on the type of command
	 * @param command The type of command to be executed
	 * @param userInput
	 */
	public StringParser(){
		keywordHash = new HashMap<PARAMETER, ArrayList<String>>(0);
	}
	
	/**
	 * Used to get a HashMap from user input and a command type
	 * @param command The type of command used to treat the userInput differently
	 * @param query
	 * @return
	 */
	public HashMap<PARAMETER, ArrayList<String>> getValuesFromInput(COMMAND_TYPE command, String userInput) {
		
		switch (command) {
		case ADD_TASK:
			//Take the "" keyword out first
			userInput = transferQuoteToHashMap(PARAMETER.DESC,"do",userInput);
			userInput = transferQuoteToHashMap(PARAMETER.VENUE,"at",userInput);
			
			//Take the repeating param keywords out
			userInput = transferMultipleArgsToHashMap(PARAMETER.REMIND_TIMES,"remind",SEPERATED_BY_SPACES,userInput);
			userInput = transferMultipleArgsToHashMap(PARAMETER.HASHTAGS,"#",WITHIN_KEYWORD,userInput);
			
			String[] 	  keywordsInInput	={"from","to","by","remind","#"};
			PARAMETER[][] paramInInput		={{PARAMETER.START_DATE, PARAMETER.START_TIME},
												{PARAMETER.END_DATE, PARAMETER.END_TIME},
												{PARAMETER.DEADLINE_DATE, PARAMETER.DEADLINE_TIME}};
			
			addAttributesToHashTable(keywordsInInput, paramInInput, userInput.split(SPACE_CHARACTER));
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
	 * used to obtain the keywords with multiple parameters
	 * @param keyword The PARAMETER to be placed in the hashMap
	 * @param keywordString The string representation of the keyword
	 * @param typeOfArguments Used to distinguish the way arguments are read; 0 for #, 1 for remind times
	 * @param userInput The string to be parsed
	 * @return The parsed string without the keyword and its params
	 */
	private String transferMultipleArgsToHashMap(PARAMETER keyword, String keywordString, int typeOfArguments,
			String userInput) {
		int indexOfOccurance = findKeywordIndexInput(keywordString,userInput);
		int indexOfNextSpeace = userInput.indexOf(" ", indexOfOccurance+1);
		if(indexOfNextSpeace < 0){
			indexOfNextSpeace = userInput.length();
		}
		if (indexOfOccurance > 0){
			keywordHash.put(keyword, new ArrayList<String>());
		}
		
		//TODO: Finish # and reminders
		while(typeOfArguments == WITHIN_KEYWORD && indexOfOccurance > 0){
			keywordHash.get(keyword).add(getKeywordnInString(userInput,indexOfOccurance + 1,indexOfNextSpeace));
			userInput = trimStringPortionOut(userInput,indexOfOccurance,indexOfNextSpeace);
			indexOfOccurance = findKeywordIndexInput(keywordString,userInput);
		}
		while(typeOfArguments == SEPERATED_BY_SPACES && indexOfOccurance > 0){
			userInput = trimStringPortionOut(userInput,indexOfOccurance,indexOfNextSpeace);
			indexOfOccurance = findKeywordIndexInput(keywordString,userInput);
		}
		
		
		return userInput;
	}

	/**
	 * Searches, adds to hashmap and removes a keyword and its quote parameter
	 * @param keyword The parameter to be placed in the hashmap
	 * @param keywordString the keyword to be looked for
	 * @param userInput The full string that is being trimmed
	 * @return The trimmed string without the 
	 */
	public String transferQuoteToHashMap(PARAMETER keyword,String keywordString, String userInput) {
		int positionOfKeyword = findKeywordIndexInput(userInput, keywordString);
		int startOfQuote = userInput.indexOf(QUOTE_INTEGER, positionOfKeyword);
		int endOfQuote = userInput.indexOf(QUOTE_INTEGER, startOfQuote + 1);
		if(startOfQuote > 0 && endOfQuote > 0){
			keywordHash.put(keyword, new ArrayList<String>()); //Ignore the quote delimeters
			keywordHash.get(keyword).add((getKeywordnInString(userInput, startOfQuote + 1, endOfQuote - 1)));
			return trimStringPortionOut(userInput, positionOfKeyword, endOfQuote);
		} else{
			return userInput;
		}
		
	}

	/**
	 * Finds a keyword index that is not within quotes
	 * @param userInput The string that is searched for the keyword
	 * @param keywordString The keyword to be searched
	 * @return The index of the keyword found
	 */
	public int findKeywordIndexInput(String userInput, String keywordString) {
		boolean outsideOfQuotes = true;
		char[] keyword = keywordString.toCharArray();
		int indexInKeyword = 0;
		char[] userInputCharArr = userInput.toCharArray();
		for (int c = 0; c < userInputCharArr.length;c++) {
			if(userInputCharArr[c] == QUOTE_INTEGER){
				outsideOfQuotes = !outsideOfQuotes;
			}
	        if(outsideOfQuotes && keyword[indexInKeyword] == userInputCharArr[c]){
	        	
	        	if(indexInKeyword >= keyword.length - 1){
	        		return c - indexInKeyword;
	        	}
	        	indexInKeyword++;
	        } else {
	        	indexInKeyword = 0;
	        }
	    }
		return -1;
	}

	/**
	 * Used to trim a part inside of a String out
	 * @param userInput The string to be trimmed
	 * @param startOfDesc The start index of the portion to be trimmed out
	 * @param endOfDesc The end index of the portion to be trimmed out
	 * @return The trimmed out string result
	 */
	public String trimStringPortionOut(String userInput, int startOfDesc, int endOfDesc) {
		StringBuilder result = new StringBuilder();
		char[] userInputCharArr = userInput.toCharArray();
	    for (int c = 0; c < userInputCharArr.length; c++) {
	        if (c < startOfDesc || c > endOfDesc) {
	            result.append(userInputCharArr[c]);
	        }
	    }
	    return result.toString();
	}

	/**
	 * Used to obtain a string inside a String
	 * @param userInput The string to be trimmed
	 * @param startOfDesc The start index of the portion to be trimmed out
	 * @param endOfDesc The end index of the portion to be trimmed out
	 * @return The string inside the indexes of the userInput
	 */
	public String getKeywordnInString(String userInput, int startOfDesc, int endOfDesc) {
		StringBuilder result = new StringBuilder();
		char[] userInputCharArr = userInput.toCharArray();
	    for (int c = 0; c < userInputCharArr.length; c++) {
	        if (c >= startOfDesc && c <= endOfDesc) {
	            result.append(userInputCharArr[c]);
	        }
	    }
	    return result.toString();
	}

	/**
	 * return the index of a word without minding the case of the letters
	 * @param input The input string to be searched
	 * @param keywordsInInput The keyword list to compare to the input
	 * @return The index of the keyword input matches
	 */
	public int stringCompareToList(String input, String[] keywordsInInput) {
		for(int i = 0;i< keywordsInInput.length;i++){
		   if(keywordsInInput[i].equalsIgnoreCase(input)){
			   return i;
		   }
		}
		return -1;
	}

	/**
	 * Assuming each word is either a word or parameter this
	 * method places the keyword with the appropriate parameters
	 * @param keywordsInInput
	 * @param stringToParse
	 */
	private void addAttributesToHashTable(String[] keywordsInInput,PARAMETER[][] paramInInput, String[] stringToParse) {
		//Traverses the string word by word
		for(int i = 0; i < stringToParse.length; i++){
			i = keywordIndexForParams(keywordsInInput, paramInInput, stringToParse, i);
		}
	}
	
	/**
	 * Used to add parameters to each keyword in the the keywordsInInput that is found in the stringToParse to the hashMap
	 * @param keywordsInInput The string list of parameters being being read
	 * @param paramInInput The structure of parameters being being read
	 * @param stringToParse The string that is being parsed
	 * @param i The current word index from the userString
	 * @return The new word index after the params are extracted
	 */
	private int keywordIndexForParams(String[] keywordsInInput, PARAMETER[][] paramInInput, String[] stringToParse, int i) {
		int commandFromKeywordIndex = stringCompareToList(stringToParse[i], keywordsInInput);
		if(commandFromKeywordIndex > 0){
			//extracts the arguments for each keyword given they are not keywords
			for(int j = 0; j < paramInInput[commandFromKeywordIndex].length; j++){
				//TODO: fix for arguments that arent given for a keyword
				if(stringCompareToList(stringToParse[i], keywordsInInput) != 0 && i < stringToParse.length){
					i++;
					keywordHash.put(paramInInput[commandFromKeywordIndex][j], new ArrayList<String>()); //Ignore the quote delimeters
					keywordHash.get(paramInInput[commandFromKeywordIndex][j]).add(stringToParse[i]);
				}
			}
		}
		return i;
	}
	
}


