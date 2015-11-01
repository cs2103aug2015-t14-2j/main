package Task;

import java.text.ParseException;
import java.util.HashMap;

/**
 *  Represents the parser for strings
 * 
 *  @author A0097689 Tan Si Kai
 *  @author A0009586 Jean Pierre Castillo
 *  @author A0118772 Audrey Tiah
 */

public class StringParser {
	private static final Context context = Context.getInstance();
	
	//Define String constants here
	private static final String SPACE_CHARACTER = "\\s+";
	
	//Define int constants here
	private static final int QUOTE_INTEGER = 34;
	private static final int PARAM_NOT_FOUND = -1;
	private static final String ALL_TASKS = "-2";
	
	private static String[] 	keywordsInInput		={"on ","from ","to ","by "};
	private static String[] 	daysInInput			={"monday","tuesday","wednesday","thursday","friday","saturday","sunday",
														"today","tomorrow"};
	private static String[] 	ShorthandDaysInput	={"mon","tues","wed","thurs","fri","sat","sun","tmr"};
	
	private static PARAMETER[] 	paramInInput		={PARAMETER.DATE,
														PARAMETER.START_TIME,
														PARAMETER.END_TIME,
														PARAMETER.DEADLINE_TIME
														};
	

	
	/**
	 * Used to get a HashMap from user input and a command type
	 * @param command The type of command used to treat the userInput differently
	 * @param userInput The string from the user
	 * @return The hashmap with valid task inputs
	 * @throws ParseException Used to detect user errors in input
	 */
	public static HashMap<PARAMETER, Object> getValuesFromInput(COMMAND_TYPE command, String userInput) {
		
		HashMap<PARAMETER, String> keywordHash = new HashMap<PARAMETER, String>(0);
		
		getStringHashMap(command, userInput, keywordHash);
		
		return Validator.getObjectHashMap(keywordHash);
	}

	public static void getStringHashMap(COMMAND_TYPE command, String userInput, HashMap<PARAMETER, String> keywordHash) {
		
		switch (command) {
		case EDIT_TASK:
			
		case DISPLAY:
			userInput = getTaskID(userInput, keywordHash);
			
		case ADD_TASK:
			//Take the "" keyword out first
			userInput = transferQuoteToHashMap(PARAMETER.DESC,"do",userInput, keywordHash);
			userInput = transferQuoteToHashMap(PARAMETER.VENUE,"at",userInput, keywordHash);
			
			if(keywordHash.get(PARAMETER.DESC) == null){
				userInput = transferQuoteToHashMap(PARAMETER.DESC,"",userInput, keywordHash);
			}
			
			//Take the repeating param keywords out for remind
			//userInput = transferMultipleArgsToHashMap(PARAMETER.REMIND_TIMES,"remind",SEPERATED_BY_SPACES,userInput);
	
			addAttributesToHashTableWithoutKeyword(keywordsInInput, paramInInput, userInput.split(obtainOrFromStringList(keywordsInInput)), keywordHash);
			
			if(keywordHash.get(PARAMETER.START_TIME) == null && keywordHash.get(PARAMETER.END_TIME) == null && keywordHash.get(PARAMETER.DEADLINE_TIME) == null){
				addAttributesToHashTable(daysInInput, PARAMETER.DATE, userInput.split(obtainOrFromStringList(daysInInput)), keywordHash);
			}
			
			if(keywordHash.get(PARAMETER.START_TIME) == null && keywordHash.get(PARAMETER.END_TIME) == null && keywordHash.get(PARAMETER.DEADLINE_TIME) == null){
				addAttributesToHashTable(ShorthandDaysInput, PARAMETER.DATE, userInput.split(obtainOrFromStringList(ShorthandDaysInput)), keywordHash);
			}
			
			break;
			
		case DELETE_TASK:
			
		case DONE:
			
		case UNDONE:
			userInput = getTaskID(userInput, keywordHash);
			break;
		
		default:
			
		}
	}

	/**
	 * Obtains the delimiter string for an array of strings
	 * @param keywordsInInputAdd Array of strings to parse
	 * @return Delimiter string to use
	 */
	private static String obtainOrFromStringList(String[] keywordsInInputAdd) {
		StringBuilder result = new StringBuilder();
		for(String s:keywordsInInputAdd){
			result.append("(?= \\b"+ s + ")|");
		}
		return result.deleteCharAt(result.length() - 1).toString();
	}

	/**
	 * Used to obtain the taskID from a string and return the rest of the sentence
	 * This can be used with a single ID or a string following it
	 * @param userInput The user input
	 * @param keywordHash 
	 * @return The string after the ID has been taken out
	 */
	private static String getTaskID(String userInput, HashMap<PARAMETER, String> keywordHash) {
		String[] inputArray = userInput.trim().split(SPACE_CHARACTER,2);
		if(inputArray[0].toLowerCase().equals("all")){
			keywordHash.put(PARAMETER.TASKID, ALL_TASKS);
		} else if(inputArray[0] == null || !containsOnlyPositiveNumbers(inputArray[0])){
			// To prevent null exceptions in TaskHandler
			keywordHash.put(PARAMETER.TASKID, "-1");
			return userInput;
		} else {
			keywordHash.put(PARAMETER.TASKID, inputArray[0]);
		}
		
		if(inputArray.length > 1){
			return userInput.split(SPACE_CHARACTER,2)[1];
		}
		else return "";
	}
	
	/**
	 * Used to check if the contents of a string are numerical
	 * @param numString The string to be checked for all numbers
	 * @return A boolean representation of whether the string provided is all numbers
	 */
	public static boolean containsOnlyPositiveNumbers(String numString) {
		return numString.matches("^[0-9 ]+$");
	}

	/**
	 * Searches, adds to hashmap and removes a keyword and its quote parameter
	 * @param keyword The parameter to be placed in the hashmap
	 * @param keywordString the keyword to be looked for
	 * @param userInput The full string that is being trimmed
	 * @param keywordHash 
	 * @return The trimmed string without the 
	 */
	public static String transferQuoteToHashMap(PARAMETER keyword,String keywordString, String userInput, HashMap<PARAMETER, String> keywordHash) {
		int positionOfKeyword = findKeywordIndexInput(userInput, keywordString,0);
		if(positionOfKeyword == PARAM_NOT_FOUND){
			return userInput;
		}
		int startOfQuote = userInput.indexOf(QUOTE_INTEGER, positionOfKeyword);
		int endOfQuote = userInput.indexOf(QUOTE_INTEGER, startOfQuote + 1);
		if(startOfQuote >= 0 && endOfQuote > 0){
			keywordHash.put(keyword, (getKeywordInString(userInput, startOfQuote + 1, endOfQuote - 1))); //Ignore the quote delimeters
			return trimStringPortionOut(userInput, positionOfKeyword, endOfQuote).trim();
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
	public static int findKeywordIndexInput(String userInput, String keywordString, int StartIndex) {
		boolean outsideOfQuotes = true;
		if(userInput == null || userInput.length() == 0){
			return -1;
		} else if(keywordString == null || keywordString.length()==0){
			return 0;
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
	public static String trimStringPortionOut(String userInput, int startOfDesc, int endOfDesc) {
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
	public static String getKeywordInString(String userInput, int startOfDesc, int endOfDesc) {
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
	public static int indexKeywordInString(String input, String[] keywordsInInput) {
		if(input != null && keywordsInInput != null){
			for(int i = 0;i< keywordsInInput.length;i++){
			   if(input.toLowerCase().contains(keywordsInInput[i])){
				   return i;
			   }
			}
		}
		return PARAM_NOT_FOUND;
	}
	
	/**
	 * Wrapper for a single parameter to be inserted for multiple words
	 * @param keywordsInInput The keyword list corresponding to the parameters
	 * @param paramInInputs The parameter list corresponding to the keywords
	 * @param stringsToParse The strings that need to be placed in the appropriate parameters
	 * @param keywordHash The hashtable to be updated
	 */
	private static void addAttributesToHashTable(String[] keywordsInInput, PARAMETER paramInInput, String[] stringsToParse,
			HashMap<PARAMETER, String> keywordHash) {
		PARAMETER[] paramInInputs = new PARAMETER[keywordsInInput.length];
		for(int i =0; i < keywordsInInput.length;i++){
			paramInInputs[i] = paramInInput;
		}
		addAttributesToHashTableWithKeyword(keywordsInInput,paramInInputs,stringsToParse,keywordHash);
	}
	
	/**
	 * Takes every sentence and extracts the keyword, then takes the string and places
	 * it in the appropriate parameter
	 * @param keywordsInInput The keyword list corresponding to the parameters
	 * @param paramInInputs The parameter list corresponding to the keywords
	 * @param stringsToParse The strings that need to be placed in the appropriate parameters
	 * @param keywordHash The hashtable to be updated
	 */
	private static void addAttributesToHashTableWithKeyword(String[] keywordsInInput,PARAMETER[] paramInInputs, String[] stringsToParse, HashMap<PARAMETER, String> keywordHash) {
		//Traverses the string word by word
		for(int currentPhrase = 0; currentPhrase < stringsToParse.length; currentPhrase++){
			int commandFromKeywordIndex = indexKeywordInString(stringsToParse[currentPhrase], keywordsInInput);
			if(commandFromKeywordIndex != PARAM_NOT_FOUND && keywordHash.get(paramInInputs[commandFromKeywordIndex]) ==  null){
				keywordHash.put(paramInInputs[commandFromKeywordIndex], stringsToParse[currentPhrase].trim()); //Ignore the quote delimeters
			} else if(commandFromKeywordIndex != PARAM_NOT_FOUND && keywordHash.get(paramInInputs[commandFromKeywordIndex]) !=  null){
				//TODO: throw exception for double keyword
			}
		}
	}	

	/**
	 * Takes every sentence and extracts the keyword, then takes the string and places
	 * it in the appropriate parameter
	 * @param keywordsInInput The keyword list corresponding to the parameters
	 * @param paramInInputs The parameter list corresponding to the keywords
	 * @param stringsToParse The strings that need to be placed in the appropriate parameters
	 * @param keywordHash The hashtable to be updated
	 */
	private static void addAttributesToHashTableWithoutKeyword(String[] keywordsInInput,PARAMETER[] paramInInputs, String[] stringsToParse, HashMap<PARAMETER, String> keywordHash) {
		//Traverses the string word by word
		for(int currentPhrase = 0; currentPhrase < stringsToParse.length; currentPhrase++){
			int commandFromKeywordIndex = indexKeywordInString(stringsToParse[currentPhrase], keywordsInInput);
			if(commandFromKeywordIndex != PARAM_NOT_FOUND && keywordHash.get(paramInInputs[commandFromKeywordIndex]) ==  null){
				keywordHash.put(paramInInputs[commandFromKeywordIndex], stringsToParse[currentPhrase].split(keywordsInInput[commandFromKeywordIndex])[1].trim()); //Ignore the quote delimeters
			} else if(commandFromKeywordIndex != PARAM_NOT_FOUND && keywordHash.get(paramInInputs[commandFromKeywordIndex]) !=  null){
				//TODO: throw exception for double keyword
			}
		}
	}
}