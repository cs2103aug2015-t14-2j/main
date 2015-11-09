/**
 *  @@author A0009586
 *  @author Jean Castillo
 *  
 *  Represents the parser for strings
 * 
 */

package Task;

import java.text.ParseException;
import java.util.HashMap;


public class StringParser {
	//Define String constants here
	private static final String SPACE_CHARACTER = "\\s+";
	
	//Define int constants here
	private static final int QUOTE_INTEGER = 34;
	private static final int PARAM_NOT_FOUND = -1;
	private static final String ALL_TASKS = "-2";
	
	private static String[] 	AllKeywordsInInput	={"on","from","to","by","do","at"};
	private static String[] 	daysInInput			={"monday","tuesday","wednesday","thursday","friday","saturday","sunday",
														"today","tomorrow","tmr"};
	private static String[] 	taskTypes			={"deadline","floating","event"};
	private static String[]		taskTypeDel			={"from to","from to by","by"};
	
	private static String[]		searchTypeDone		={"done"};
	private static String[]		searchTypeEnded		={"ended"};
	private static String[]		searchTypePast		={"past"};
	private static String[]		searchTypeAll		={"all"};
	

	private static String[] 	keywordsInInput		={"on ","from ","to ","by "};
	private static PARAMETER[] 	paramInInput		={PARAMETER.DATE,
														PARAMETER.START_TIME,
														PARAMETER.END_TIME,
														PARAMETER.DEADLINE_TIME,
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
		
		return Validator.getObjectHashMap(keywordHash, command);
	}

	public static void getStringHashMap(COMMAND_TYPE command, String userInput, HashMap<PARAMETER, String> keywordHash) {
		boolean isDeleteParams = false;
		
		userInput = userInput.trim();
		
		switch (command) {
		
		case DELETE_TASK:
			
		case DONE:
			
		case UNDONE:
			getTaskID(userInput, keywordHash);
			break;
			
		case DISPLAY:
			getTaskID(userInput, keywordHash);
			//Take the "" keywords out
			userInput = transferQuoteToHashMap(PARAMETER.DESC,"do",userInput, keywordHash);
			userInput = transferQuoteToHashMap(PARAMETER.VENUE,"at",userInput, keywordHash);
			
			if(keywordHash.get(PARAMETER.DESC) == null){
				userInput = transferQuoteToHashMap(PARAMETER.DESC,"",userInput, keywordHash);
			}
			
			addAttributesWithoutKeyword(keywordsInInput, paramInInput, userInput.split(obtainDelimiterStringList(keywordsInInput)), keywordHash);
			
			if(keywordHash.get(PARAMETER.START_TIME) == null && keywordHash.get(PARAMETER.END_TIME) == null && keywordHash.get(PARAMETER.DEADLINE_TIME) == null){
				addAttributesWithOneParamKeyword(daysInInput, PARAMETER.DATE, userInput.split(obtainDelimiterStringList(daysInInput)), keywordHash);
			}
			
			addAttributesFromList(searchTypeDone,PARAMETER.IS_DONE, userInput, keywordHash);
			addAttributesFromList(searchTypeEnded,PARAMETER.HAS_ENDED, userInput, keywordHash);
			addAttributesFromList(searchTypePast,PARAMETER.IS_PAST, userInput, keywordHash);
			addAttributesFromList(searchTypeAll,PARAMETER.SPECIAL, userInput, keywordHash);
			
			break;
			
		case EDIT_TASK:
			getTaskID(userInput, keywordHash);
			isDeleteParams = findKeywordIndexInput(userInput.trim(),"no", 0) != PARAM_NOT_FOUND;
			//Take the no keywords out
			if(isDeleteParams){
				userInput = obtainTrailingKeywordsToHashmap(userInput, findKeywordIndexInput(userInput.trim(),"no ", 0),
								"no ".length(),AllKeywordsInInput,PARAMETER.DELETE_PARAMS,keywordHash);
			}
			
			
			addStringToParamFromList(userInput,taskTypes,taskTypeDel,PARAMETER.DELETE_PARAMS,keywordHash);
			removeRepeatedWordsParam(PARAMETER.DELETE_PARAMS,keywordHash);			
			
		case ADD_TASK:
		
		default:
			//Take the "" keywords out
			userInput = transferQuoteToHashMap(PARAMETER.DESC,"do",userInput, keywordHash);
			userInput = transferQuoteToHashMap(PARAMETER.VENUE,"at",userInput, keywordHash);
			
			if(keywordHash.get(PARAMETER.DESC) == null){
				userInput = transferQuoteToHashMap(PARAMETER.DESC,"",userInput, keywordHash);
			}
			
			addAttributesWithoutKeyword(keywordsInInput, paramInInput, userInput.split(obtainDelimiterStringList(keywordsInInput)), keywordHash);
			
			if(keywordHash.get(PARAMETER.START_TIME) == null && keywordHash.get(PARAMETER.END_TIME) == null && keywordHash.get(PARAMETER.DEADLINE_TIME) == null){
				addAttributesWithOneParamKeyword(daysInInput, PARAMETER.DATE, userInput.split(obtainDelimiterStringList(daysInInput)), keywordHash);
			}
			
			break;
			
		}
	}

	private static void addAttributesFromList(String[] wordList, PARAMETER param, String userInput,
			HashMap<PARAMETER, String> keywordHash) {
		for (int i = 0; i < wordList.length;i++){
			if(userInput.toLowerCase().contains(wordList[i].toLowerCase())){
				keywordHash.put(param, "true");
			}
		}
	}

	/**
	 * Used to remove repeated words inside a string obtained from a hashmap
	 * @param param The parameter to be used in obtaining the words from hashmap
	 * @param keywordHash The hashmap to obtain the string from
	 */
	private static void removeRepeatedWordsParam(PARAMETER param, HashMap<PARAMETER, String> keywordHash) {
		String wordList = keywordHash.get(param);
		if(wordList != null && wordList.length() > 0){
			wordList.replaceAll("(\\b\\w+\\b) (?=.*\\b\\1\\b)", "");
			keywordHash.put(param, wordList);
		}
	}

	/**
	 * Places a matching string in the PARAMTER of keywordHash for a String in the keywords
	 * @param userInput The string to be parsed for keywords
	 * @param keywords The keywords each word in the userInput will be compared to
	 * @param paramEqv The corresponding string to be used for a found match
	 * @param paramToUse The parameter to store the paramEqv to 
	 * @param keywordHash The hashmap to obtain the paramToUse
	 */
	private static void addStringToParamFromList(String userInput, String[] keywords, String[] paramEqv,
			PARAMETER paramToUse, HashMap<PARAMETER, String> keywordHash) {
		for(int i = 0;i < keywords.length;i++){
			if(userInput.contains(keywords[i])){
				if(keywordHash.get(paramToUse) == null){
					keywordHash.put(paramToUse, paramEqv[i]);
				}
				else {
					keywordHash.put(paramToUse, keywordHash.get(paramToUse) + 
										" " + 
										paramEqv[i]);
				}
			}
		}
	}

	/**
	 * Obtains the words followed by a keyword that match a list of Strings
	 * @param inputString The String to parse
	 * @param startingPosition The starting position of the search
	 * @param offset The offset of the keyword used to delimit the string
	 * @param keywords the list of keywords to compare each trailing word to
	 * @param param The Parameter which each matching word will be added to
	 * @param keywordHash The hashmap containing the list of interest
	 * @return The original string with all the trailing words of the keyword that
	 * matched the keywords and the keyword itself
	 */
	private static String obtainTrailingKeywordsToHashmap(String inputString, int startingPosition, int offset, String[] keywords,
			PARAMETER param, HashMap<PARAMETER, String> keywordHash) {
		int endPosition = startingPosition + offset;
		String[] trimedKeywords = trimStringList(keywords);
		while(indexKeywordInString(nextWordFromIndex(inputString,endPosition),trimedKeywords) != PARAM_NOT_FOUND){
			if(keywordHash.get(param) == null){
				keywordHash.put(param, nextWordFromIndex(inputString,endPosition));
			}
			else {
				keywordHash.put(param, keywordHash.get(param) + 
									" " + 
									nextWordFromIndex(inputString,endPosition));
			}
			endPosition = endPosition +	nextWordFromIndex(inputString,endPosition).length() + 1;
		}
		if(endPosition == startingPosition + offset){
			return trimStringPortionOut(inputString, startingPosition, endPosition - 1);
		} else {
			return trimStringPortionOut(inputString, startingPosition, endPosition);
		}
		
	}

	/**
	 * Trims each word inside a list
	 * @param keywords The list to be trimmed
	 * @return keywords with all trimmed Strings
	 */
	private static String[] trimStringList(String[] keywords) {
		String[] trimedKeywords = new String[keywords.length];
		for(int i =0;i < keywords.length;i++){
			trimedKeywords[i] = keywords[i].trim();
		}
		return trimedKeywords;
	}

	/**
	 * used to obtain the index of the next word from a starting position
	 * @param inputString The string to traverse
	 * @param startingPostion The position to start from
	 * @return The index of the next word
	 */
	private static String nextWordFromIndex(String inputString, int startingPostion) {
		if(inputString.length()>startingPostion){
			String remainingString = inputString.substring(startingPostion);
			return remainingString.trim().split(" ")[0];
		}
		return null;
	}

	/**
	 * Obtains the delimiter string for an array of strings
	 * @param keywordsInInputAdd Array of strings to parse
	 * @return Delimiter string to use
	 */
	private static String obtainDelimiterStringList(String[] keywordsInInputAdd) {
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
	private static void getTaskID(String userInput, HashMap<PARAMETER, String> keywordHash) {
		String[] inputArray = userInput.trim().split(SPACE_CHARACTER,2);
		if(inputArray[0].toLowerCase().equals("all")){
			keywordHash.put(PARAMETER.TASKID, ALL_TASKS);
		} else if(inputArray[0] == null || !containsOnlyPositiveNumbers(inputArray[0])){
			// To prevent null exceptions in TaskHandler
			keywordHash.put(PARAMETER.TASKID, "-1");
		} else {
			keywordHash.put(PARAMETER.TASKID, inputArray[0]);
		}
	}
	
	/**
	 * Used to check if the contents of a string are numerical
	 * @param numString The string to be checked for all numbers
	 * @return A boolean representation of whether the string provided is all numbers
	 */
	private static boolean containsOnlyPositiveNumbers(String numString) {
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
	private static String transferQuoteToHashMap(PARAMETER keyword,String keywordString, String userInput, HashMap<PARAMETER, String> keywordHash) {
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
	private static int findKeywordIndexInput(String userInput, String keywordString, int StartIndex) {
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
	private static String trimStringPortionOut(String userInput, int startOfDesc, int endOfDesc) {
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
	private static String getKeywordInString(String userInput, int startOfDesc, int endOfDesc) {
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
	private static int indexKeywordInString(String input, String[] keywordsInInput) {
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
	private static void addAttributesWithOneParamKeyword(String[] keywordsInInput, PARAMETER paramInInput, String[] stringsToParse,
			HashMap<PARAMETER, String> keywordHash) {
		PARAMETER[] paramInInputs = new PARAMETER[keywordsInInput.length];
		for(int i =0; i < keywordsInInput.length;i++){
			paramInInputs[i] = paramInInput;
		}
		addAttributesWithKeyword(keywordsInInput,paramInInputs,stringsToParse,keywordHash);
	}
	
	/**
	 * Takes every sentence and extracts the keyword, then takes the string and places
	 * it in the appropriate parameter
	 * @param keywordsInInput The keyword list corresponding to the parameters
	 * @param paramInInputs The parameter list corresponding to the keywords
	 * @param stringsToParse The strings that need to be placed in the appropriate parameters
	 * @param keywordHash The hashtable to be updated
	 */
	private static void addAttributesWithKeyword(String[] keywordsInInput,PARAMETER[] paramInInputs, String[] stringsToParse, HashMap<PARAMETER, String> keywordHash) {
		//Traverses the string word by word
		for(int currentPhrase = 0; currentPhrase < stringsToParse.length; currentPhrase++){
			int commandFromKeywordIndex = indexKeywordInString(stringsToParse[currentPhrase], keywordsInInput);
			if(commandFromKeywordIndex != PARAM_NOT_FOUND && keywordHash.get(paramInInputs[commandFromKeywordIndex]) ==  null){
				keywordHash.put(paramInInputs[commandFromKeywordIndex], stringsToParse[currentPhrase].trim());
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
	private static void addAttributesWithoutKeyword(String[] keywordsInInput,PARAMETER[] paramInInputs, String[] stringsToParse, HashMap<PARAMETER, String> keywordHash) {
		//Traverses the string word by word
		for(int currentPhrase = 0; currentPhrase < stringsToParse.length; currentPhrase++){
			int commandFromKeywordIndex = indexKeywordInString(stringsToParse[currentPhrase], keywordsInInput);
			if(commandFromKeywordIndex != PARAM_NOT_FOUND && keywordHash.get(paramInInputs[commandFromKeywordIndex]) ==  null){
				keywordHash.put(paramInInputs[commandFromKeywordIndex], stringsToParse[currentPhrase].split(keywordsInInput[commandFromKeywordIndex])[1].trim()); //Ignore the quote delimeters
			}
		}
	}
}