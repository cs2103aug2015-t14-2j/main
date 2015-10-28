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
	private static final int WITHIN_KEYWORD = 0;
	private static final int SEPERATED_BY_SPACES = 1;
	private static final int KEYWORD = 2;
	private static final int HASHTAG_LENGTH = 1;
	
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
		
		boolean hasSamedate = false;
		
		switch (command) {
		case ADD_TASK:
			//Take the "" keyword out first
			userInput = transferQuoteToHashMap(PARAMETER.DESC,"do",userInput, keywordHash);
			userInput = transferQuoteToHashMap(PARAMETER.VENUE,"at",userInput, keywordHash);
			if(keywordHash.get(PARAMETER.DESC)==null){
				userInput = transferQuoteToHashMap(PARAMETER.DESC,"",userInput, keywordHash);
			}
			
			if(findKeywordIndexInput(userInput,"on",0) >= 0 ||
					findKeywordIndexInput(userInput,"today",0) >= 0 ||
					findKeywordIndexInput(userInput,"tomorrow",0) >= 0){
				hasSamedate = true;
			}
			
			//Take the repeating param keywords out
			//userInput = transferMultipleArgsToHashMap(PARAMETER.REMIND_TIMES,"remind",SEPERATED_BY_SPACES,userInput);
			//userInput = transferMultipleArgsToHashMap(PARAMETER.HASHTAGS,"#",WITHIN_KEYWORD,userInput);
			userInput = transferMultipleArgsToHashMap(PARAMETER.START_DATE,"today",KEYWORD,userInput,keywordHash);
			userInput = transferMultipleArgsToHashMap(PARAMETER.START_DATE,"tomorrow",KEYWORD,userInput,keywordHash);
			
			String[] 	  keywordsInInputAdd	={"on","from","to","by"};
			PARAMETER[][] paramInInputAdd		={{PARAMETER.START_DATE},
												{PARAMETER.START_DATE, PARAMETER.START_TIME},
												{PARAMETER.END_DATE, PARAMETER.END_TIME},
												{PARAMETER.DEADLINE_DATE, PARAMETER.DEADLINE_TIME}
												};
			if(hasSamedate){
				paramInInputAdd[1] = new PARAMETER[] {PARAMETER.START_TIME};
				paramInInputAdd[2] = new PARAMETER[] {PARAMETER.END_TIME};
			}
			
			addAttributesToHashTable(keywordsInInputAdd, paramInInputAdd, userInput.split(SPACE_CHARACTER), keywordHash);
			
			if(hasSamedate){
				keywordHash.put(PARAMETER.END_DATE, keywordHash.get(PARAMETER.START_DATE));
			}
			break;
			
		case EDIT_TASK:
			
			userInput = getTaskID(userInput, keywordHash);
			
			userInput = transferQuoteToHashMap(PARAMETER.DESC,"do",userInput, keywordHash);
			userInput = transferQuoteToHashMap(PARAMETER.VENUE,"at",userInput, keywordHash);
			if(keywordHash.get(PARAMETER.DESC)==null){
				userInput = transferQuoteToHashMap(PARAMETER.DESC,"",userInput, keywordHash);
			}
			
			if(findKeywordIndexInput(userInput,"on",0) >= 0 ||
					findKeywordIndexInput(userInput,"today",0) >= 0 ||
					findKeywordIndexInput(userInput,"tomorrow",0) >= 0){
				hasSamedate = true;
			}
			
			//Take the repeating param keywords out
			//userInput = transferMultipleArgsToHashMap(PARAMETER.REMIND_TIMES,"remind",SEPERATED_BY_SPACES,userInput);
			//userInput = transferMultipleArgsToHashMap(PARAMETER.HASHTAGS,"#",WITHIN_KEYWORD,userInput);
			
			String[] 	  keywordsInInputEd		={"on","from","to","by"};
			PARAMETER[][] paramInInputEd		={{PARAMETER.START_DATE},
												{PARAMETER.START_DATE, PARAMETER.START_TIME},
												{PARAMETER.END_DATE, PARAMETER.END_TIME},
												{PARAMETER.DEADLINE_DATE, PARAMETER.DEADLINE_TIME}};
			
			if(hasSamedate){
				paramInInputEd[1] = new PARAMETER[] {PARAMETER.START_TIME};
				paramInInputEd[2] = new PARAMETER[] {PARAMETER.END_TIME};
			}
			
			addAttributesToHashTable(keywordsInInputEd, paramInInputEd, userInput.split(SPACE_CHARACTER), keywordHash);
			
			if(hasSamedate){
				keywordHash.put(PARAMETER.END_DATE, keywordHash.get(PARAMETER.START_DATE));
			}
			break;
			
		case DELETE_TASK:
			userInput = getTaskID(userInput, keywordHash);
			break;

		case DONE:
			userInput = getTaskID(userInput, keywordHash);
			break;
			
		case UNDONE:
			userInput = getTaskID(userInput, keywordHash);
			break;
			
		case DISPLAY:
			userInput = getTaskID(userInput, keywordHash);
			//Take the "" keyword out first
			userInput = transferQuoteToHashMap(PARAMETER.DESC,"do",userInput, keywordHash);
			userInput = transferQuoteToHashMap(PARAMETER.VENUE,"at",userInput, keywordHash);
			if(keywordHash.get(PARAMETER.DESC)==null){
				userInput = transferQuoteToHashMap(PARAMETER.DESC,"",userInput, keywordHash);
			}
			
			if(findKeywordIndexInput(userInput,"on",0) >= 0 ||
					findKeywordIndexInput(userInput,"today",0) >= 0 ||
					findKeywordIndexInput(userInput,"tomorrow",0) >= 0){
				hasSamedate = true;
			}
			
			//Take the repeating param keywords out
			//userInput = transferMultipleArgsToHashMap(PARAMETER.REMIND_TIMES,"remind",SEPERATED_BY_SPACES,userInput);
			//userInput = transferMultipleArgsToHashMap(PARAMETER.HASHTAGS,"#",WITHIN_KEYWORD,userInput);
			userInput = transferMultipleArgsToHashMap(PARAMETER.START_DATE,"today",KEYWORD,userInput,keywordHash);
			userInput = transferMultipleArgsToHashMap(PARAMETER.START_DATE,"tomorrow",KEYWORD,userInput,keywordHash);
			
			String[] 	  keywordsInInputSearch	={"on","from","to","by"};
			PARAMETER[][] paramInInputSearch	={{PARAMETER.START_DATE},
												{PARAMETER.START_DATE, PARAMETER.START_TIME},
												{PARAMETER.END_DATE, PARAMETER.END_TIME},
												{PARAMETER.DEADLINE_DATE, PARAMETER.DEADLINE_TIME}
												};
			if(hasSamedate){
				paramInInputSearch[1] = new PARAMETER[] {PARAMETER.START_TIME};
				paramInInputSearch[2] = new PARAMETER[] {PARAMETER.END_TIME};
			}
			
			addAttributesToHashTable(keywordsInInputSearch, paramInInputSearch, userInput.split(SPACE_CHARACTER), keywordHash);
			
			if(hasSamedate){
				keywordHash.put(PARAMETER.END_DATE, keywordHash.get(PARAMETER.START_DATE));
			}
			break;

						
		default:
			
		}
	}
	
	/**
	 * used to obtain the keywords with multiple parameters
	 * @param keyword The PARAMETER to be placed in the hashMap
	 * @param keywordString The string representation of the keyword
	 * @param typeOfArguments Used to distinguish the way arguments are read; 0 for #, 1 for remind times
	 * @param userInput The string to be parsed
	 * @return The parsed string without the keyword and its params
	 */
	private static String transferMultipleArgsToHashMap(PARAMETER keyword, String keywordString, int typeOfArguments,
			String userInput, HashMap<PARAMETER,String> keywordHash) {
		
		int indexOfOccurance = findKeywordIndexInput(userInput,keywordString,0);
		int indexOfNextSpeace = userInput.indexOf(" ", indexOfOccurance);
		
		if(indexOfNextSpeace < 0){
			indexOfNextSpeace = userInput.length();
		}
		/*if (indexOfOccurance > 0){
			keywordHash.put(keyword, new ArrayList<String>());
		}
		
		while(typeOfArguments == WITHIN_KEYWORD && indexOfOccurance > 0){
			keywordHash.get(keyword).add(getKeywordnInString(userInput,indexOfOccurance + HASHTAG_LENGTH,indexOfNextSpeace - 1));
			userInput = trimStringPortionOut(userInput,indexOfOccurance,indexOfNextSpeace - 1);
			indexOfOccurance = findKeywordIndexInput(userInput,keywordString,indexOfOccurance);
		}
		
		if(typeOfArguments == SEPERATED_BY_SPACES && indexOfOccurance >= 0){
			userInput = trimStringPortionOut(userInput,indexOfOccurance,indexOfNextSpeace);
			indexOfNextSpeace = userInput.indexOf(" ", indexOfOccurance);
			
			//used to check for all numerical reminders after remind keyword
			while(containsOnlyNumbers(userInput.substring(indexOfOccurance,indexOfNextSpeace))){
				indexOfNextSpeace = userInput.indexOf(" ", indexOfOccurance);
				if(indexOfNextSpeace < 0){
					keywordHash.get(keyword).add(getKeywordnInString(userInput,indexOfOccurance,userInput.length()));
					userInput = trimStringPortionOut(userInput,indexOfOccurance,userInput.length());
					break;
				}
				keywordHash.get(keyword).add(getKeywordnInString(userInput,indexOfOccurance,indexOfNextSpeace - 1));
				userInput = trimStringPortionOut(userInput,indexOfOccurance,indexOfNextSpeace);
			}
		}*/
		
		if(typeOfArguments == KEYWORD && indexOfOccurance >= 0){
			keywordHash.put(keyword,getKeywordnInString(userInput,indexOfOccurance,indexOfNextSpeace - 1));
			userInput = trimStringPortionOut(userInput,indexOfOccurance,indexOfNextSpeace - 1);
			indexOfOccurance = findKeywordIndexInput(userInput,keywordString,indexOfOccurance);
		}
		
		
		return userInput;
	}

	/**
	 * Used to obtain the taskID from a string and return the rest of the sentence
	 * This can be used with a single ID or a string following it
	 * @param userInput The user input
	 * @param keywordHash 
	 * @return The string after the ID has been taken out
	 */
	private static String getTaskID(String userInput, HashMap<PARAMETER, String> keywordHash) {
		String[] inputArray = userInput.split(SPACE_CHARACTER,2);
		if(inputArray[0].equals("") && inputArray.length > 1){			//Check for variations in the number
			inputArray[0] = userInput.split(SPACE_CHARACTER,3)[1];
			if(userInput.split(SPACE_CHARACTER,3).length > 2){
				inputArray[1] = userInput.split(SPACE_CHARACTER,3)[2];
			} else {
				inputArray[1] = "";
			}
		} else if(inputArray[0].equals("") && inputArray.length == 1){
			return "";
		}
		if(inputArray[0] == null || !containsOnlyNumbers(inputArray[0])){
			// To prevent null exceptions in TaskHandler
			keywordHash.put(PARAMETER.TASKID, "-1");
			return userInput;
		}
		keywordHash.put(PARAMETER.TASKID, inputArray[0]);
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
	public static boolean containsOnlyNumbers(String numString) {
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
		int positionOfKeyword;
		if(keywordString.equals("")){
			positionOfKeyword = 0;
		} else {
			positionOfKeyword = findKeywordIndexInput(userInput, keywordString,0);
		}
		if(positionOfKeyword == -1){
			return userInput;
		}
		int startOfQuote = userInput.indexOf(QUOTE_INTEGER, positionOfKeyword);
		int endOfQuote = userInput.indexOf(QUOTE_INTEGER, startOfQuote + 1);
		if(startOfQuote >= 0 && endOfQuote > 0){
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
	public static int findKeywordIndexInput(String userInput, String keywordString, int StartIndex) {
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
	public static String getKeywordnInString(String userInput, int startOfDesc, int endOfDesc) {
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
	public static int stringCompareToList(String input, String[] keywordsInInput) {
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
	 * @param keywordHash 
	 */
	private static void addAttributesToHashTable(String[] keywordsInInput,PARAMETER[][] paramInInput, String[] stringToParse, HashMap<PARAMETER, String> keywordHash) {
		//Traverses the string word by word
		for(int currentWord = 0; currentWord < stringToParse.length;){
			currentWord = keywordIndexForParams(keywordsInInput, paramInInput, stringToParse, currentWord, keywordHash);
		}
	}
	
	/**
	 * Used to add parameters to each keyword in the the keywordsInInput that is found in the stringToParse to the hashMap
	 * @param keywordsInInput The string list of parameters being being read
	 * @param paramInInput The structure of parameters being being read
	 * @param stringToParse The string that is being parsed
	 * @param keywordHash 
	 * @param i The current word index from the userString
	 * @return The new word index after the params are extracted
	 */
	private static int keywordIndexForParams(String[] keywordsInInput, PARAMETER[][] paramInInput, String[] stringToParse, int currentWord, HashMap<PARAMETER, String> keywordHash) {
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


