package Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import java.util.logging.Level;
import java.util.logging.Logger;

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
	private final static Logger LOGGER = Logger.getLogger(StringParser.class.getName()); 
	
	//Define int constants here
	private static final int QUOTE_INTEGER = 34;
	private static final int PARAM_NOT_FOUND = -1;
	//private static final int WITHIN_KEYWORD = 0;
	//private static final int SEPERATED_BY_SPACES = 1;
	//private static final int HASHTAG_LENGTH = 1;
	
	//The hashmap contructed
	private HashMap<PARAMETER, String> keywordHash = null;
	
	/**
	 * Gets the current HashMap
	 * @return The HashMap
	 */
	public HashMap<PARAMETER, String> getKeywordHash() {
		return keywordHash;
	}

	/**
	 * Used to clear the HashMap in use
	 */
	public void clearHashmap(){
		keywordHash = new HashMap<PARAMETER, String>(0);
	}

	/**
	 * Initiates the parser and parses the userInput based on the type of command
	 * @param command The type of command to be executed
	 * @param userInput
	 */
	public StringParser(){
		keywordHash = new HashMap<PARAMETER, String>(0);
	}
	
	/**
	 * Used to get a HashMap from user input and a command type
	 * @param command The type of command used to treat the userInput differently
	 * @param userInput The string from the user
	 * @return The hashmap with valid task inputs
	 */
	public HashMap<PARAMETER, String> getValuesFromInput(COMMAND_TYPE command, String userInput) {
		
		LOGGER.setLevel(Level.SEVERE);
		
		switch (command) {
		case ADD_TASK:
			//Take the "" keyword out first
			userInput = transferQuoteToHashMap(PARAMETER.DESC,"do",userInput);
			userInput = transferQuoteToHashMap(PARAMETER.VENUE,"at",userInput);
			
			//Take the repeating param keywords out
			//userInput = transferMultipleArgsToHashMap(PARAMETER.REMIND_TIMES,"remind",SEPERATED_BY_SPACES,userInput);
			//userInput = transferMultipleArgsToHashMap(PARAMETER.HASHTAGS,"#",WITHIN_KEYWORD,userInput);
			
			String[] 	  keywordsInInputAdd	={"on","from","to","by"};
			PARAMETER[][] paramInInputAdd		={{PARAMETER.START_DATE},
												{PARAMETER.START_DATE, PARAMETER.START_TIME},
												{PARAMETER.END_DATE, PARAMETER.END_TIME},
												{PARAMETER.DEADLINE_DATE, PARAMETER.DEADLINE_TIME}};
			if(findKeywordIndexInput(userInput,"on",0) >= 0){
				LOGGER.info("On keyword used! Madifying both start time and end time");
				paramInInputAdd[1] = new PARAMETER[] {PARAMETER.START_TIME};
				paramInInputAdd[2] = new PARAMETER[] {PARAMETER.END_TIME};
			}
			
			addAttributesToHashTable(keywordsInInputAdd, paramInInputAdd, userInput.split(SPACE_CHARACTER));
			
			if(findKeywordIndexInput(userInput,"on",0) >= 0){
				keywordHash.put(PARAMETER.END_DATE, keywordHash.get(PARAMETER.START_DATE));
			}
			break;
			
		case EDIT_TASK:
			
			userInput = getTaskID(userInput);
			
			//Take the "" keyword out first
			userInput = transferQuoteToHashMap(PARAMETER.DESC,"do",userInput);
			userInput = transferQuoteToHashMap(PARAMETER.VENUE,"at",userInput);
			
			//Take the repeating param keywords out
			//userInput = transferMultipleArgsToHashMap(PARAMETER.REMIND_TIMES,"remind",SEPERATED_BY_SPACES,userInput);
			//userInput = transferMultipleArgsToHashMap(PARAMETER.HASHTAGS,"#",WITHIN_KEYWORD,userInput);
			
			String[] 	  keywordsInInputEd		={"on","from","to","by"};
			PARAMETER[][] paramInInputEd		={{PARAMETER.START_DATE},
												{PARAMETER.START_DATE, PARAMETER.START_TIME},
												{PARAMETER.END_DATE, PARAMETER.END_TIME},
												{PARAMETER.DEADLINE_DATE, PARAMETER.DEADLINE_TIME}};
			
			if(findKeywordIndexInput(userInput,"on",0) >= 0){
				LOGGER.info("On keyword used! Madifying both start time and end time");
				paramInInputEd[1] = new PARAMETER[] {PARAMETER.START_TIME};
				paramInInputEd[2] = new PARAMETER[] {PARAMETER.END_TIME};
			}
			
			addAttributesToHashTable(keywordsInInputEd, paramInInputEd, userInput.split(SPACE_CHARACTER));
			
			if(findKeywordIndexInput(userInput,"on",0) >= 0){
				keywordHash.put(PARAMETER.END_DATE, keywordHash.get(PARAMETER.START_DATE));
			}
			break;
			
		case DISPLAY:
			userInput = getTaskID(userInput);
			break;
			
		case DELETE_TASK:
			userInput = getTaskID(userInput);
			break;
			
		case SEARCH_TASK:
			break;
		default:
			assert(false);//Should never be reached
			break;
		}
		removeInvalidInputs(Validator.validateUserInput(command, keywordHash), keywordHash);
		
		return keywordHash;
	}

	/**
	 * Used to obtain the taskID from a string and return the rest of the sentence
	 * This can be used with a single ID or a string following it
	 * @param userInput The user input
	 * @return The string after the ID has been taken out
	 */
	private String getTaskID(String userInput) {
		LOGGER.info("Obtained task ID for "+ userInput);
		String[] inputArray = userInput.split(SPACE_CHARACTER,2);
		if(inputArray[0].equals("") && inputArray.length > 1){			//Check for variations in the number
			LOGGER.info("Assuming extra spaces were used");
			inputArray[0] = userInput.split(SPACE_CHARACTER,3)[1];
			if(userInput.split(SPACE_CHARACTER,3).length > 2){
				inputArray[1] = userInput.split(SPACE_CHARACTER,3)[2];
			} else {
				LOGGER.warning("String has too few arguments");
				inputArray[1] = "";
			}
		} else if(inputArray[0].equals("") && inputArray.length == 1){
			return "";
		}
		
		if(containsOnlyNumbers(inputArray[0])){
			keywordHash.put(PARAMETER.TASKID, inputArray[0]);
			if(inputArray.length > 1){
				return userInput.split(SPACE_CHARACTER,2)[1];
			}
			else return "";
		}
		return userInput;
	}

	/**
	 * removes invalid inputs as dictated by the validator
	 * @param validKeywordHash Hashmap of the valid entries in the original Hashmap
	 * @param keywordHash The original Hashmap to be cleaned for valid entries
	 * @return The original Hashmap minus any invalid entries as dictated by the validKeywordHash
	 */
	private HashMap<PARAMETER, String> removeInvalidInputs(HashMap<PARAMETER, String> validKeywordHash,
			HashMap<PARAMETER, String> keywordHash) {
		LOGGER.info("Removed invalid inputs for hashMap");
		ArrayList<PARAMETER> toRemove = new ArrayList<PARAMETER>();
		for(Entry<PARAMETER, String> entry : validKeywordHash.entrySet()) {
			if(validKeywordHash.get(entry.getKey()) != "VALID"){
				 LOGGER.warning("Found invalid user input from validator at " + entry.toString());
				 toRemove.add(entry.getKey());
			}
		}
		for(int i = 0; i < toRemove.size(); i++){
			keywordHash.remove(toRemove.get(i));
		}
		
		return keywordHash;
		
	}

	/**
	 * Used to check if the contents of a string are numerical
	 * @param numString The string to be checked for all numbers
	 * @return A boolean representation of wheather the string provided is all numbers
	 */
	public boolean containsOnlyNumbers(String numString) {
		return numString.matches("^[0-9 ]+$");
	}

	/**
	 * Searches, adds to hashmap and removes a keyword and its quote parameter
	 * @param keyword The parameter to be placed in the hashmap
	 * @param keywordString the keyword to be looked for
	 * @param userInput The full string that is being trimmed
	 * @return The trimmed string without the ""
	 */
	public String transferQuoteToHashMap(PARAMETER keyword,String keywordString, String userInput) {
		LOGGER.info("Obtained quotes from string: "+ userInput);
		int positionOfKeyword = findKeywordIndexInput(userInput, keywordString,0);
		if(positionOfKeyword == -1){
			return userInput;
		}
		int startOfQuote = userInput.indexOf(QUOTE_INTEGER, positionOfKeyword);
		int endOfQuote = userInput.indexOf(QUOTE_INTEGER, startOfQuote + 1);
		if(startOfQuote > 0 && endOfQuote > 0){
			keywordHash.put(keyword, (getKeywordnInString(userInput, startOfQuote + 1, endOfQuote - 1))); //Ignore the quote delimeters
			return trimStringPortionOut(userInput, positionOfKeyword, endOfQuote + 1);
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
	public int findKeywordIndexInput(String userInput, String keywordString, int StartIndex) {
		LOGGER.info("Obtained keyword index for input: "+ userInput +" and keyword " + keywordString);
		boolean outsideOfQuotes = true;
		if(keywordString == null || keywordString.length()==0 || userInput == null || userInput.length() == 0){
			return -1;
		}
		char[] keyword = keywordString.toCharArray();
		int indexInKeyword = 0;
		char[] userInputCharArr = userInput.toCharArray();
		if(StartIndex < 0){
			StartIndex = 0;
		}
		for (int c = StartIndex; c < userInputCharArr.length;c++) {
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
		LOGGER.info("Trimmed "+ userInput + " from " + startOfDesc + "to" + endOfDesc);
		StringBuilder result = new StringBuilder();
		if(userInput == null){
			return null;
		}
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
		LOGGER.info("Obtained substring of "+ userInput + " from " + startOfDesc + "to" + endOfDesc);
		StringBuilder result = new StringBuilder();
		if(userInput == null){
			return null;
		}
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
		LOGGER.info("Comparing all keywords in userInput to "+ input);
		if(input != null && keywordsInInput != null){
			for(int i = 0;i< keywordsInInput.length;i++){
			   if(keywordsInInput[i].equalsIgnoreCase(input)){
				   return i;
			   }
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
		LOGGER.info("Placing paramaters for keywords in user input");
		for(int currentWord = 0; currentWord < stringToParse.length;){
			currentWord = keywordIndexForParams(keywordsInInput, paramInInput, stringToParse, currentWord);
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
	private int keywordIndexForParams(String[] keywordsInInput, PARAMETER[][] paramInInput, String[] stringToParse, int currentWord) {
		int commandFromKeywordIndex = stringCompareToList(stringToParse[currentWord], keywordsInInput);
		//Start from the first parameter
		currentWord++;
		if(commandFromKeywordIndex != PARAM_NOT_FOUND){
			//extracts the arguments for each keyword given they are not keywords
			for(int j = 0; j < paramInInput[commandFromKeywordIndex].length; j++){
				if(currentWord < stringToParse.length && 
						stringCompareToList(stringToParse[currentWord], keywordsInInput) == PARAM_NOT_FOUND && 
						currentWord < stringToParse.length){
					keywordHash.put(paramInInput[commandFromKeywordIndex][j], stringToParse[currentWord]); //Ignore the quote delimeters
					currentWord++;
				}
			}
		}
		return currentWord;
	}
	
}


