package Task;

import java.util.Calendar;
import java.util.HashMap;

// This class deals with validation of all user input.
// It specifies which validation functions are required for which parameters.
// It runs through every validation function and sets the respective error message.
// Error messages should specify which parameter failed and why it failed.
public class Validator {
	// private and static variables here
	private static final String VALID_INPUT = "VALID";
	private static final String ERROR_INVALID_DESC = "";
	private static final String ERROR_INVALID_VENUE = "";
	private static final String ERROR_INVALID_DATE_FORMAT = "";
	private static final String ERROR_INVALID_TIME_FORMAT = "";
	private static final String ERROR_INSUFFICIENT_ARGUMENT = "";
	private static final String ERROR_START_AFTER_END = "";
	private static final String ERROR_INVALID_PRIORITY_FORMAT = "";
	private static final String NO_INPUT = "VALID";
	/* ...ADD IN MORE HERE */

	// This is optional, you can implement using other ways you like
	enum PARAMETER {
		DESC, VENUE, START_DATE, END_DATE, START_TIME, END_TIME, DEADLINE, PRIORITY, REMIND_TIME, STARTENDTIME
	};

	// Constructor
	public Validator() {

	}

	// Takes a HashMap returned from the Parser class and runs through each of
	// the key => value pairs
	// For each value, run through each of the relevant validation functions. If
	// fails, set the error message
	// for that particular validation function.
	// Returns a HashMap of PARAMETER => error_message_string
	public static HashMap<PARAMETER, String> validateUserInput(COMMAND_TYPE command,
			HashMap<PARAMETER, String> parsedUserInput) {

		HashMap<PARAMETER, String> errorHashMap = new HashMap<PARAMETER, String>();

		if (!isValidDesc(parsedUserInput.get(PARAMETER.DESC))) {
			errorHashMap.put(PARAMETER.DESC, ERROR_INVALID_DESC);
		} else {
			errorHashMap.put(PARAMETER.DESC, VALID_INPUT);
		}

		try {
			if (!isValidVenue(parsedUserInput.get(PARAMETER.VENUE))) {
				errorHashMap.put(PARAMETER.VENUE, ERROR_INVALID_VENUE);
			} else {
				errorHashMap.put(PARAMETER.VENUE, VALID_INPUT);
			}
		} catch (NullPointerException e) {
			errorHashMap.put(PARAMETER.VENUE, NO_INPUT);
		}

		try {
			if (!isValidStartDate(parsedUserInput.get(PARAMETER.START_DATE))) {
				errorHashMap.put(PARAMETER.START_DATE, ERROR_INVALID_DATE_FORMAT);
			} else {
				errorHashMap.put(PARAMETER.START_DATE, VALID_INPUT);
			}
		} catch (NullPointerException e) {
			errorHashMap.put(PARAMETER.START_DATE, NO_INPUT);
		}

		try {
			if (!isValidEndDate(parsedUserInput.get(PARAMETER.END_DATE))) {
				errorHashMap.put(PARAMETER.END_DATE, ERROR_INVALID_DATE_FORMAT);
			} else {
				errorHashMap.put(PARAMETER.END_DATE, VALID_INPUT);
			}
		} catch (NullPointerException e) {
			errorHashMap.put(PARAMETER.END_DATE, NO_INPUT);
		}

		try {
			if (isValidStartDate(parsedUserInput.get(PARAMETER.START_DATE))
					&& isValidEndDate(parsedUserInput.get(PARAMETER.END_DATE))) {
				if (!isValidDatePeriod(parsedUserInput.get(PARAMETER.START_DATE),
						parsedUserInput.get(PARAMETER.END_DATE))) {
					errorHashMap.put(PARAMETER.STARTENDTIME, ERROR_START_AFTER_END);
				} else if (!isValidTimePeriod(parsedUserInput.get(PARAMETER.START_TIME),
						parsedUserInput.get(PARAMETER.END_TIME))) {
					errorHashMap.put(PARAMETER.STARTENDTIME, ERROR_START_AFTER_END);
				} else {
					errorHashMap.put(PARAMETER.STARTENDTIME, VALID_INPUT);
				}
			}
		} catch (NullPointerException e) {
			errorHashMap.put(PARAMETER.START_DATE, NO_INPUT);
		}

		try {
			if (!isValidDeadline(parsedUserInput.get(PARAMETER.DEADLINE))) {
				errorHashMap.put(PARAMETER.DEADLINE, ERROR_INVALID_DATE_FORMAT);
			} else {
				errorHashMap.put(PARAMETER.DEADLINE, VALID_INPUT);
			}
		} catch (NullPointerException e) {
			errorHashMap.put(PARAMETER.DEADLINE, NO_INPUT);
		}

		try {
			if (!isValidStartTime(parsedUserInput.get(PARAMETER.START_TIME))) {
				errorHashMap.put(PARAMETER.START_TIME, ERROR_INVALID_TIME_FORMAT);
			} else {
				errorHashMap.put(PARAMETER.START_TIME, VALID_INPUT);
			}
		} catch (NullPointerException e) {
			errorHashMap.put(PARAMETER.START_TIME, NO_INPUT);
		}

		try {
			if (!isValidEndTime(parsedUserInput.get(PARAMETER.END_TIME))) {
				errorHashMap.put(PARAMETER.END_TIME, ERROR_INVALID_TIME_FORMAT);
			} else {
				errorHashMap.put(PARAMETER.END_TIME, VALID_INPUT);
			}
		} catch (NullPointerException e) {
			errorHashMap.put(PARAMETER.END_TIME, NO_INPUT);
		}

		try {
			if (!isValidRemindTime(parsedUserInput.get(PARAMETER.REMIND_TIME))) {
				errorHashMap.put(PARAMETER.REMIND_TIME, ERROR_INVALID_TIME_FORMAT);
			} else {
				errorHashMap.put(PARAMETER.REMIND_TIME, VALID_INPUT);
			}
		} catch (NullPointerException e) {
			errorHashMap.put(PARAMETER.REMIND_TIME, NO_INPUT);
		}

		try {
			if (!isValidPriority(parsedUserInput.get(PARAMETER.PRIORITY))) {
				errorHashMap.put(PARAMETER.PRIORITY, ERROR_INVALID_PRIORITY_FORMAT);
			} else {
				errorHashMap.put(PARAMETER.PRIORITY, VALID_INPUT);
			}
		} catch (NullPointerException e) {
			errorHashMap.put(PARAMETER.PRIORITY, NO_INPUT);
		}

		return errorHashMap;

	}

	// Takes command and hashmap of user input and runs through to see if all
	// the required parameters are present
	private boolean isValidMinimumArgumentForCommand(COMMAND_TYPE command, HashMap<PARAMETER, String> parsedUserInput) {
		switch (command) {
		case ADD_TASK: // Minimum input is description.
			if (!isValidDesc(parsedUserInput.get(PARAMETER.DESC))) {
				return false;
			} else {
				return true;
			}
		case GET_TASK:
			break;
		case DISPLAY:
			break;
		case EDIT_TASK:
			break;
		case INVALID_COMMAND:
			break;
		case EXIT:
			break;
		default:
			break;
		}

		return true;
	}

	/*
	 * Validate individual parameter values here
	 *
	 * TODO: when is a description invalid? Empty? Too many characters?
	 */
	private static boolean isValidDesc(String value) {

		// just spaces? " "
		if (isValidString(value)) {
			return true;
		}
		return false;
	}

	/* TODO: When is venue invalid? */
	private static boolean isValidVenue(String value) {

		value = value.trim();
		if (value.equals(null)) {
			return false;
		}
		return true;
	}

	/*
	 * Currently only: (21/05/2016 , 21/05, 21 May 2016, 21 May ) formats are
	 * accepted. TODO: Invalid dates like 66/38/2894? DONE
	 */
	private static boolean isValidStartDate(String value) {
		if (isValidDateFormat(value)) {
			return true;
		}
		return false;
	}

	/*
	 * Currently only validating formats. as above. TODO: EndDate < StartDate
	 * DONE error, EndDate < today's date error. DONE
	 */
	private static boolean isValidEndDate(String value) {
		if (isValidDateFormat(value)) {
			return true;
		}

		return false;
	}

	/*
	 * Similar to end date DONE
	 */
	private static boolean isValidDeadline(String value) {
		if (isValidDateFormat(value)) {
			return true;
		}

		return false;
	}

	/*
	 * Invalid time like 25pm, 5050 dealt with yet. DONE
	 */
	private static boolean isValidStartTime(String value) {
		if (isValidTimeFormat(value)) {
			return true;
		}
		return false;
	}

	private static boolean isValidEndTime(String value) {
		if (isValidTimeFormat(value)) {
			return true;
		}
		return false;
	}

	private static boolean isValidRemindTime(String value) {
		if (isValidTimeFormat(value)) {
			return true;
		}
		return false;
	}

	private static boolean isValidPriority(String value) {
		value = value.trim(); // in case of front and back white spaces
		value = value.toLowerCase();
		if (value.equals("low") || value.equals("medium") || value.equals("high")) {
			return true;
		}
		return false;
	}

	/*
	 * Lowest level functions here. Validate string, integer, Date fields here.
	 * Do not feel constrained by the function definitions. You can change them
	 * or add in more parameters, change types, or add new functions as you deem
	 * fit.
	 */

	private static boolean isValidDatePeriod(String startDate, String endDate) {
		int startYear = 0; // initialise year to this year
		int startMonth = 0, startDay = 0; // in case no specification of year
		int endYear = 0;
		int endMonth = 0, endDay = 0;

		if (isNumberDateFormat(startDate)) { // either 21/05 or 21/05/2015

			String splitStrSlash[] = startDate.split("\\/");
			try {
				startDay = Integer.parseInt(splitStrSlash[0]);
				startMonth = Integer.parseInt(splitStrSlash[1]);
				startYear = Integer.parseInt(splitStrSlash[2]);

			} catch (IndexOutOfBoundsException e) {
				startDay = Integer.parseInt(splitStrSlash[0]);
				startMonth = Integer.parseInt(splitStrSlash[1]);
				startYear = Calendar.getInstance().get(Calendar.YEAR);
			}
		}

		// is always Day Month year or day Month
		if (isWordMonthFormat(startDate)) {
			String splitStrSpace[] = startDate.split("\\s");
			startDay = Integer.parseInt(splitStrSpace[0]);
			switch (splitStrSpace[1].toLowerCase()) {

			case "jan":
				startMonth = 1;
				break;
			case "feb":
				startMonth = 2;
				break;
			case "march":
				startMonth = 3;
				break;
			case "apr":
				startMonth = 4;
				break;
			case "may":
				startMonth = 5;
				break;
			case "jun":
				startMonth = 6;
				break;
			case "jul":
				startMonth = 7;
				break;
			case "aug":
				startMonth = 8;
				break;
			case "sep":
				startMonth = 9;
				break;
			case "oct":
				startMonth = 10;
				break;
			case "nov":
				startMonth = 11;
				break;
			case "dec":
				startMonth = 12;
				break;
			default:
				break;
			}
			if (splitStrSpace.length == 3) {
				startYear = Integer.parseInt(splitStrSpace[2]);
			} else {
				startYear = Calendar.getInstance().get(Calendar.YEAR);
			}
		}

		if (isNumberDateFormat(endDate)) {
			String splitStrSlash[] = endDate.split("\\/");
			try {
				endDay = Integer.parseInt(splitStrSlash[0]);
				endMonth = Integer.parseInt(splitStrSlash[1]);
				endYear = Integer.parseInt(splitStrSlash[2]);

			} catch (IndexOutOfBoundsException e) {
				endDay = Integer.parseInt(splitStrSlash[0]);
				endMonth = Integer.parseInt(splitStrSlash[1]);
				endYear = Calendar.getInstance().get(Calendar.YEAR);
			}
		}

		if (isWordMonthFormat(endDate)) {
			String splitStrSpace[] = endDate.split("\\s");
			endDay = Integer.parseInt(splitStrSpace[0]);
			switch (splitStrSpace[1].toLowerCase()) {

			case "jan":
				endMonth = 1;
				break;
			case "feb":
				endMonth = 2;
				break;
			case "march":
				endMonth = 3;
				break;
			case "apr":
				endMonth = 4;
				break;
			case "may":
				endMonth = 5;
				break;
			case "jun":
				endMonth = 6;
				break;
			case "jul":
				endMonth = 7;
				break;
			case "aug":
				endMonth = 8;
				break;
			case "sep":
				endMonth = 9;
				break;
			case "oct":
				endMonth = 10;
				break;
			case "nov":
				endMonth = 11;
				break;
			case "dec":
				endMonth = 12;
				break;
			default:
				break;
			}
			if (splitStrSpace.length == 3) {
				endYear = Integer.parseInt(splitStrSpace[2]);
			} else {
				endYear = Calendar.getInstance().get(Calendar.YEAR);
			}
		}

		if (endYear < startYear) {
			return false;
		} else if (startYear == endYear && startMonth < endMonth) {
			return false;
		} else if (startYear == endYear && startMonth == endMonth && startDay < endDay) {
			return false;
		}
		return true;
	}

	// Should have a better way. This is purely String-parse-integer way. Maybe
	// java time conversion way? ONLY DEALS WITH TIME.
	private static boolean isValidTimePeriod(String startTime, String endTime) {
		int startHrs = 0;
		int startMins = 0;
		int endHrs = 0;
		int endMins = 0;

		if (is12hrTimeFormat(startTime)) {
			if (startTime.length() == 3) { // e.g. 5pm
				startHrs = Character.getNumericValue(startTime.charAt(0));
				startMins = 0;
			}
			if (startTime.length() == 5) { // e.g. 530pm
				if (startTime.substring(startTime.length() - 2, startTime.length()).equals("pm")) {
					startHrs = Character.getNumericValue(startTime.charAt(0)) + 12;
				} else {
					startHrs = Character.getNumericValue(startTime.charAt(0));
				}
				startMins = Integer.parseInt(startTime.substring(1, 3));
			}
		}

		if (is24hrTimeFormat(startTime)) { // e.g. 0024
			startHrs = Integer.parseInt(startTime.substring(2));
			startMins = Integer.parseInt(startTime.substring(2, startTime.length()));
		}

		if (is12hrTimeFormat(endTime)) {
			if (endTime.length() == 3) {
				endHrs = Character.getNumericValue(endTime.charAt(0));
				endMins = 0;
			}
			if (endTime.length() == 5) { // e.g. 530pm
				if (endTime.substring(endTime.length() - 2, endTime.length()).equals("pm")) {
					endHrs = Character.getNumericValue(endTime.charAt(0)) + 12;
				} else {
					endHrs = Character.getNumericValue(endTime.charAt(0));
				}
				endMins = Integer.parseInt(endTime.substring(1, 3));
			}
		}

		if (is24hrTimeFormat(endTime)) {
			endHrs = Integer.parseInt(endTime.substring(2));
			endMins = Integer.parseInt(endTime.substring(2, endTime.length()));
		}

		// Converted all to 24HRS.
		if (endHrs < startHrs) {
			return false;
		}
		if (endHrs == startHrs) {
			if (endMins < startMins) {
				return false;
			}
		}

		return true;
	}

	private static boolean isValidString(String string) {
		string = string.trim();
		if (string.equals(null)) {
			return false;
		}
		return true;
	}

	private static boolean isValidDateFormat(String string) {
		string = string.trim();
		if (isNumberDateFormat(string) || isWordMonthFormat(string)) {
			return true;
		}
		return false;
	}

	private static boolean isValidTimeFormat(String string) {
		string = string.trim();
		if (is12hrTimeFormat(string) || is24hrTimeFormat(string)) {
			return true;
		}
		return false;
	}

	/*********************************************************************
	 * DATE HANDLING * *
	 *********************************************************************/

	// Number date format for eg. 21/04 or 21/04/2015 ie DD/MM or DD/MM/YYYY
	public static Boolean isNumberDateFormat(String string) {
		if (string.contains("/")) {
			String splitStrSlash[] = string.split("\\/");
			try {
				if (isOnlyNumbers(splitStrSlash[0]) && isOnlyNumbers(splitStrSlash[1])
						&& isOnlyNumbers(splitStrSlash[2])) {

					if (Integer.parseInt(splitStrSlash[0]) <= 31 && Integer.parseInt(splitStrSlash[1]) <= 12
							&& Integer.parseInt(splitStrSlash[2]) <= 2030
							&& Integer.parseInt(splitStrSlash[2]) >= 2010) {
						return true;
					}

				}
			} catch (IndexOutOfBoundsException e) {
				if (isOnlyNumbers(splitStrSlash[0]) && isOnlyNumbers(splitStrSlash[1])) {
					if (Integer.parseInt(splitStrSlash[0]) <= 31 && Integer.parseInt(splitStrSlash[1]) <= 12) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// Word Month date format. e.g. 21 Apr or 21 Apr 2015
	public static Boolean isWordMonthFormat(String string) {
		if (string.contains(" ")) {
			String splitStrSpace[] = string.split("\\s");
			if (splitStrSpace.length == 2) {
				if (isOnlyNumbers(splitStrSpace[0]) && isWordMonth(splitStrSpace[1])) {
					if (Integer.parseInt(splitStrSpace[0]) <= 12) {
						return true;
					}
				}
			} else if (splitStrSpace.length == 3) {
				if (isOnlyNumbers(splitStrSpace[0]) && isWordMonth(splitStrSpace[1])
						&& isOnlyNumbers(splitStrSpace[2])) {
					if (Integer.parseInt(splitStrSpace[0]) <= 12 && Integer.parseInt(splitStrSpace[2]) <= 2030
							&& Integer.parseInt(splitStrSpace[2]) >= 2010) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// Checks if there is a month word in the string. Right now there is only
	// short forms etc. jan, feb
	// can add in more. e.g. january, febuary, etc.
	public static Boolean isWordMonth(String string) {
		if (string.equalsIgnoreCase("jan") || string.equalsIgnoreCase("feb") || string.equalsIgnoreCase("mar")
				|| string.equalsIgnoreCase("apr") || string.equalsIgnoreCase("may") || string.equalsIgnoreCase("jun")
				|| string.equalsIgnoreCase("jul") || string.equalsIgnoreCase("aug") || string.equalsIgnoreCase("sep")
				|| string.equalsIgnoreCase("oct") || string.equalsIgnoreCase("nov") || string.equals("dec")) {
			return true;
		}
		return false;
	}

	/*********************************************************************
	 * TIME HANDLING * *
	 *********************************************************************/

	// Deals with 12hr time formats. e.g 8pm, 5am.
	public static Boolean is12hrTimeFormat(String string) {
		string = string.trim();
		int length = string.length();
		String numString = string.substring(0, length - 2);
		String ampmstring = string.substring(length - 2, length);
		if (ampmstring.equals("am") || ampmstring.equals("pm")) {
			if (numString.length() <= 1 && Integer.parseInt(numString) <= 12) { // DEALS
																				// WITH
																				// 8PM
																				// 9PM
				return true; //
			}
			if (numString.length() == 3) { // e.g. 830pm
				if (Integer.parseInt(numString) / 100 <= 12 && Integer.parseInt(numString) % 100 <= 59) {
					return true;
				}
			}
		}
		return false;
	}

	// Deals with 24 hrs format.
	// TODO: deal with inputs like 2500, 1270
	public static Boolean is24hrTimeFormat(String string) {
		string = string.trim();
		if (string.length() == 4) {
			try {
				if (isOnlyNumbers(string)) {
					String hrsString = string.substring(string.length() - 2);
					String minsString = string.substring(string.length() - 2, string.length());
					if (Integer.parseInt(hrsString) <= 23 && Integer.parseInt(minsString) <= 59) {
						return true;
					}
				}
			} catch (NumberFormatException e) {
				return false;
			}

		}
		return false;
	}

	/*********************************************************************
	 * UTILITY * *
	 *********************************************************************/
	// check string only contains numbers
	public static Boolean isOnlyNumbers(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

}
