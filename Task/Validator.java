package Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.joestelmach.natty.*;

/**
 * 
 * @@author A0118772
 * 
 *          This class takes the user input in parts(through a hashmap) and
 *          converts them to their respective objects.
 * 
 */

public class Validator {
	private static Context context = Context.getInstance();
	private static Parser parser = new Parser();

	private static String TRUE_STRING = "true";

	public Validator() {}

	/**
	 * Natty takes a phrase of user input and returns single date object
	 * 
	 * @param dateString
	 * @return Date object, null if Natty cannot parse into a date
	 */
	private static Date parseNatty(String dateString) {
		if (dateString == (null)) {
			return null;
		}
		List<DateGroup> dateGroup = parser.parse(dateString);
		if (dateGroup.size() == 1) {
			List<Date> dateList = dateGroup.get(0).getDates();
			if (dateList.size() == 1) {
				return dateList.get(0);
			} else {
				return null;
			}
		} else {
			context.displayMessage("ERROR_DATEFORMAT");
			return null;
		}
	}

	public static HashMap<PARAMETER, Object> getObjectHashMap(HashMap<PARAMETER, String> hashmap,
			COMMAND_TYPE command) {
		System.out.println(hashmap);
		
		HashMap<PARAMETER, Object> objectHashMap = new HashMap<PARAMETER, Object>();
 
 		String editString = hashmap.get(PARAMETER.DELETE_PARAMS);

		// Common behaviour regardles of COMMAND
		if (isValidString(hashmap.get(PARAMETER.DESC))) {
			objectHashMap.put(PARAMETER.DESC, hashmap.get(PARAMETER.DESC));
		}

		if (hashmap.get(PARAMETER.VENUE) != null) {
			if (isValidString(hashmap.get(PARAMETER.VENUE))) {
				objectHashMap.put(PARAMETER.VENUE, hashmap.get(PARAMETER.VENUE));
			}
		}
		updateTaskIdHashMap(hashmap, objectHashMap);
		updateDeleteParamsHashMap(editString, objectHashMap);
		
		// CHECK FOR FLAGS USED IN SEARCH
		setFlag(hashmap, PARAMETER.IS_DONE, objectHashMap);
		setFlag(hashmap, PARAMETER.HAS_ENDED, objectHashMap);
		setFlag(hashmap, PARAMETER.IS_PAST, objectHashMap);
		setFlag(hashmap, PARAMETER.SPECIAL, objectHashMap);

		switch (command) {
			case DISPLAY:
				updateHashMapForDisplay(hashmap, objectHashMap);
				updateHashMapForDisplay(hashmap, objectHashMap);
				break;
			case ADD:
				updateHashMapForAdd(hashmap, objectHashMap);
				break;
			case EDIT:
				updateHashMapForEdit(hashmap, objectHashMap);
				break;
			default:
				break;
		}

		System.out.println("Passed START_DATE: " + objectHashMap.get(PARAMETER.START_DATE));
		System.out.println("Passed START_TIME: " + objectHashMap.get(PARAMETER.START_TIME));
		System.out.println("Passed END_DATE: " + objectHashMap.get(PARAMETER.END_DATE));
		System.out.println("Passed END_TIME: " + objectHashMap.get(PARAMETER.END_TIME));
		System.out.println("Passed DEADLINE_DATE: " + objectHashMap.get(PARAMETER.DEADLINE_DATE));
		System.out.println("Passed DEADLINE_TIME: " + objectHashMap.get(PARAMETER.DEADLINE_TIME));
		System.out.println("Passed DATE: " + objectHashMap.get(PARAMETER.DATE));
		// System.out.println(hashmap.get(PARAMETER.DELETEPARAMS));

		return objectHashMap;
	}

	/**
	 * Checks and validates taskId
	 */
	private static void updateTaskIdHashMap(HashMap<PARAMETER, String> parsedMap, 
		HashMap<PARAMETER, String> objectHashMap) {	
		if (objectHashMap.get(PARAMETER.TASKID) != null) {
			if (containsOnlyNumbers(taskID)) {
				objectHashMap.put(PARAMETER.TASKID, Integer.parseInt(taskID));
			} else {
				context.displayMessage("PARAM_SUBTITLE");
				context.displayMessage("PARAM_TASKID_NUM");
				objectHashMap.put(PARAMETER.TASKID, 0);
			}
		} else {
			objectHashMap.put(PARAMETER.TASKID, -1);
		}
	}

	private static void updateHashMapForEdit(HashMap<PARAMETER, String> parsedMap,
		HashMap<PARAMETER, Object> objectHashMap) {

	}

	private static void updateHashMapForAdd(HashMap<PARAMETER, String> parsedMap, 
		HashMap<PARAMETER, Object> objectHashMap) {
		Date startDateObj;
		Date startTimeObj;
		Date endDateObj;
		Date endTimeObj;
		Date deadlineDateObj;
		Date deadlineTimeObj;

		if (parsedMap.containsKey((Object)PARAMETER.DATE) && 
			!parsedMap.get(PARAMETER.DATE).isEmpty()) {

			String dateString   = parsedMap.get(PARAMETER.DATE);

			if (parsedMap.containsKey((Object)PARAMETER.START_TIME) && 
				!parsedMap.get(PARAMETER.START_TIME).isEmpty()) {
				
				String startTime    = parsedMap.get(PARAMETER.START_TIME);
				startTime += " " + dateString;
				
				if (userSpecifiedTimeOnly(startTime) {
					Date nattyStart    = parseNatty(startTime);
					startTimeObj = getTimeOnly(nattyStart);
					
				} else if (userSpecifiedDateOnly(startTime)) {
					Date nattyStart = parseNatty(startTime);
					startDateObj = getDateOnly(nattyStart);

				} else if (userSpecifiedDateAndTime(startTime)) {
					// Both date and time 
					Date nattyStart = parseNatty(startTime);
					startTimeObj = getTimeOnly(nattyStart);
					startDateObj = getDateOnly(nattyEnd);

				} else {
					// cannot be parsed as date
					
				}				
			}
			if (parsedMap.containsKey((Object)PARAMETER.END_TIME) && 
				!parsedMap.get(PARAMETER.END_TIME).isEmpty()) {
				String endTime      = parsedMap.get(PARAMETER.END_TIME);
				endTime += " " + dateString;
				Date nattyEnd      = parseNatty(endTime);

			}
			if (parsedMap.containsKey((Object)PARAMETER.DEADLINE_TIME) && 
				!parsedMap.get(PARAMETER.DEADLINE_TIME).isEmpty()) {
				String deadlineTime = parsedMap.get(PARAMETER.DEADLINE_TIME);
				deadlineTime += " " + dateString;
				Date nattyDeadline = parseNatty(deadlineTime);

			}
		} else {
			if (parsedMap.containsKey((Object)PARAMETER.START_TIME) && 
				!parsedMap.get(PARAMETER.START_TIME).isEmpty()) {
				String startTime    = parsedMap.get(PARAMETER.START_TIME);
				
			}
			if (parsedMap.containsKey((Object)PARAMETER.END_TIME) && 
				!parsedMap.get(PARAMETER.END_TIME).isEmpty()) {
				String endTime      = parsedMap.get(PARAMETER.END_TIME);

			}
			if (parsedMap.containsKey((Object)PARAMETER.DEADLINE_TIME) && 
				!parsedMap.get(PARAMETER.DEADLINE_TIME).isEmpty()) {
				String deadlineTime = parsedMap.get(PARAMETER.DEADLINE_TIME);

			}			
		}

		// Check for null combinations

		// Default parameters if not specified by user


		// Put date objects into hashmap
		objectHashMap.put(PARAMETER.START_DATE, startDateObj);
		objectHashMap.put(PARAMETER.START_TIME, startTimeObj);
		objectHashMap.put(PARAMETER.END_DATE, endDateObj);
		objectHashMap.put(PARAMETER.END_TIME, endTimeObj);
		objectHashMap.put(PARAMETER.DEADLINE_DATE, deadlineDateObj);
		objectHashMap.put(PARAMETER.DEADLINE_TIME, deadlineTimeObj); 
	}

	private static void updateHashMapForDisplay(HashMap<PARAMETER, String> parsedMap,
		HashMap<PARAMETER, Object> objectHashMap, ) {
		Calendar startcal = Calendar.getInstance();
		Calendar endcal   = Calendar.getInstance();
		String keyDate;

		if (parsedMap.get(PARAMETER.DATE) != null) {
			keyDate = parsedMap.get(PARAMETER.DATE);
		} else if (parsedMap.get(PARAMETER.DEADLINE_TIME) != null) {
			keyDate = parsedMap.get(PARAMETER.DEADLINE_TIME);
		} else {
			// default behaviour e.g. display
			keyDate = "today";
		}

		if (keyDate.contains("week") || keyDate.contains("wk")) {
			startcal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			objectHashMap.put(PARAMETER.START_DATE, startcal.getTime());
			objectHashMap.put(PARAMETER.START_TIME, startcal.getTime());
			endcal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			endcal.add(Calendar.DAY_OF_WEEK, 7);
			objectHashMap.put(PARAMETER.END_DATE, endcal.getTime());
			objectHashMap.put(PARAMETER.END_TIME, endcal.getTime());
		} else if (isMonthWord(keyDate) && !isParseableToDate(keyDate)) {
			// Calendar cal = Calendar.getInstance();
			startcal.set(Calendar.DAY_OF_MONTH, startcal.getActualMinimum(Calendar.DATE));
			objectHashMap.put(PARAMETER.START_DATE, startcal.getTime());
			objectHashMap.put(PARAMETER.START_TIME, startcal.getTime());
			endcal.set(Calendar.DAY_OF_MONTH, endcal.getActualMaximum(Calendar.DATE));
			objectHashMap.put(PARAMETER.END_DATE, endcal.getTime());
			objectHashMap.put(PARAMETER.END_TIME, endcal.getTime());
		} else if (keyDate.contains("yr") || keyDate.contains("year")) {
			startcal.set(Calendar.MONTH, 0);
			startcal.set(Calendar.DAY_OF_YEAR, 1);
			objectHashMap.put(PARAMETER.START_DATE, startcal.getTime());
			objectHashMap.put(PARAMETER.START_TIME, startcal.getTime());
			endcal.set(Calendar.MONTH, 11);
			endcal.set(Calendar.DAY_OF_MONTH, 31);
			objectHashMap.put(PARAMETER.END_DATE, endcal.getTime());
			objectHashMap.put(PARAMETER.END_TIME, endcal.getTime());
		} else {
			objectHashMap.put(PARAMETER.START_DATE, startcal.getTime());
			objectHashMap.put(PARAMETER.START_TIME, startcal.getTime());
			objectHashMap.put(PARAMETER.END_DATE, endcal.getTime());
			objectHashMap.put(PARAMETER.END_TIME, endcal.getTime());
		}

		updateContextDisplay(deadlineTime, keyDate);
	}

	private static void updateDeleteParamsHashMap(String editString,
			HashMap<PARAMETER, Object> objectHashMap) {
		int n = 0;
		String[] splitString = editString.split("\\s+");
		PARAMETER[] parameterArray = new PARAMETER[20];
		for (int i = 0; i < splitString.length ; i++) {
			switch (splitString[i]) {
			case "from":
				parameterArray[n] = PARAMETER.START_TIME;
				n++;
				break;
			case "to":
				parameterArray[n] = PARAMETER.END_TIME;
				n++;
				break;
			case "by":
				parameterArray[n] = PARAMETER.DEADLINE_TIME;
				n++;
				break;
			case "on":
				parameterArray[n] = PARAMETER.START_TIME;
				n++;
				parameterArray[n] = PARAMETER.END_TIME;
				n++;
				break;
			case "at":
				parameterArray[n] = PARAMETER.VENUE;
				n++;
			default:
				break;

			}
		}
		objectHashMap.put(PARAMETER.DELETE_PARAMS, parameterArray);
	}

	/**
	 * Given user query strings, determine the appropriate calendar view
	 * 
	 * @param deadline
	 * @param date
	 */
	private static void updateContextDisplay(String deadline, String date) {
		String query;
		if (date != null) {
			query = date;
		} else if (deadline != null) {
			query = deadline;
		} else {
			context.displayMessage("VIEW_MONTH");
			return;
		}

		if (query.contains("week") || query.contains("wk")) {
			context.displayMessage("VIEW_WEEK");
		} else if (isMonthWord(query) && !isParseableToDate(query)) {
			context.displayMessage("VIEW_MONTH");
		} else if (query.contains("yr") || query.contains("year")) {
			context.displayMessage("VIEW_MONTH");
		} else {
			context.displayMessage("VIEW_DAY");
		}
	}

	/**
	 * Used to check if the contents of a string are numerical
	 * 
	 * @param numString
	 *            The string to be checked for all numbers
	 * @return A boolean representation of whether the string provided is all
	 *         numbers
	 */
	private static boolean containsOnlyNumbers(String numString) {
		return (numString.matches("[-+]?\\d*\\.?\\d+"));
	}

	private static Calendar setStartOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 00);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	private static Calendar setEndOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	private static boolean isValidString(String string) {
		if (string == null || string.trim().equals("")) {
			return false;
		}
		return true;
	}

	/**
	 * @@author A0145472E
	 * 
	 * Used to set the flag a Boolean flag in the hashmap
	 * @param hashmap
	 * @param isDone
	 * @param objectHashMap
	 */
	private static void setFlag(HashMap<PARAMETER, String> hashmap, PARAMETER param,
			HashMap<PARAMETER, Object> objectHashMap) {
		if (hashmap.get(param) != null) {
			objectHashMap.put(param, hashmap.get(param).equals(TRUE_STRING));
		}
	}

	/*********************************************************************
	 * TIME HANDLING *
	 * 
	 * *
	 *********************************************************************/

	private static boolean isDayWord(String string) {
		string = string.toLowerCase();
		if (string.contains("monday") || string.contains("mon") || string.contains("tue") || string.contains("tuesday")
				|| string.contains("tues") || string.contains("wed") || string.contains("wednes")
				|| string.contains("wednesday") || string.contains("thur") || string.contains("thurs")
				|| string.contains("thursday") || string.contains("fri") || string.contains("friday")
				|| string.contains("sat") || string.contains("saturday") || string.contains("sun")
				|| string.contains("sunday")) {
			return true;
		}
		return false;
	}

	private static boolean isMonthWord(String string) {
		string = string.toLowerCase();
		if (string.contains("month") || string.contains("mth") || string.contains("january") || string.contains("jan")
				|| string.contains("january") || string.contains("feb") || string.contains("february")
				|| string.contains("mar") || string.contains("march") || string.contains("apr")
				|| string.contains("april") || string.contains("may") || string.contains("jun")
				|| string.contains("june") || string.contains("jul") || string.contains("july")
				|| string.contains("aug") || string.contains("august") || string.contains("sep")
				|| string.contains("sept") || string.contains("september") || string.contains("oct")
				|| string.contains("october") || string.contains("nov") || string.contains("november")
				|| string.contains("dec") || string.contains("december")) {
			return true;
		}
		return false;
	}

	private static boolean isParseableToTime(String timeString) {
		SimpleDateFormat 12HourMin      = new SimpleDateFormat("hhmmaa");
		SimpleDateFormat 12HourColonMin = new SimpleDateFormat("hh:mmaa");
		SimpleDateFormat 24HourMin      = new SimpleDateFormat("HHmm");
		SimpleDateFormat 24HourColonMin = new SimpleDateFormat("HH:mm");

		if (tryToParse(timeString, 12HourMin) 		 ||
			tryToParse(timeString, 12HourColonMin)   || 
			tryToParse(timeString, 24HourMin)		 ||
			tryToParse(timeString, 24HourColonMin)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isParseableToDate(String dateString) {
		SimpleDateFormat dayMonthFullYear = new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat dayMonthYear     = new SimpleDateFormat("dd MMM yy");
		SimpleDateFormat dayMonth         = new SimpleDateFormat("dd MMM");
		SimpleDateFormat monthDay         = new SimpleDateFormat("MMM dd");

		if (tryToParse(dateString, dayMonthFullYear) ||
			tryToParse(dateString, dayMonthYear) 	 || 
			tryToParse(dateString, dayMonth)         ||
			tryToParse(dateString, monthDay)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tries to parse a string into a date object using the dateformat given, returns false if parseException
	 *
	 */
	private static boolean tryToParse(String dateString, SimpleDateFormat df) {
		Date date;
		try {
			date = df.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	private static Date getDateOnly(Date dateObj) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		String dateOnlyString       = dateFormat.format(dateObj);
		Date dateOnlyObj            = dateFormat.parse(dateOnlyString);

		return dateOnlyObj;
	}

	private static Date getTimeOnly(Date dateobj) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
		String timeOnlyString       = timeFormat.format(timeFormat);
		Date timeOnlyObj            = timeFormat.parse(timeOnlyString);

		return timeOnlyObj;
	}

	private static boolean isDateOnly(Date dateObj) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

	}

	/**
	 * Takes a date from Natty and checks the Date parsed from the Date PARAMETER to figure out
	 * whether user specified a date
	 */
	private static boolean userSpecifiedTimeOnly(String dateString, String dateParam) {
		Date date = parseNatty(dateString);
		Date dateParamObj = parseNatty(dateParam);

		if (date != null) {
			if (isToday(date)) {
				if (dateParamObj != null && isToday(dateParamObj)) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
		
		return false;
	}

	/**
	 * Checks if string from user is a date only
	 *
	 */
	private static boolean userSpecifiedDateOnly(String dateString) {
		Date date         = parseNatty(dateString);
		
		if (date == null) {
			return false;
		}

		Calendar now      = Calendar.getInstance();
		Calendar compared = Calendar.getInstance();

		compared.setTime(date);
		boolean isSameHour = (compared.get(Calendar.HOUR_OF_DAY) == now.get(Calendar.HOUR_OF_DAY));
		boolean isSameMin  = (compared.get(Calendar.MINUTE) == now.get(Calendar.MINUTE));
		boolean isSameSec  = (compared.get(Calendar.SECOND) == now.get(Calendar.SECOND));

		if (isSameHour && isSameMin && isSameSec) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean userSpecifiedDateAndTime(String dateString) {
		Date date = parseNatty(dateString);
		
		if (date == null) {
			return false;
		}

		return (!date.equals(new Date() && !userSpecifiedDateOnly(dateString) && !userSpecifiedTimeOnly(dateString));
	}

	private static boolean isToday(Date date) {
		Calendar cal = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		cal.setTime(date);
		if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
			return true;
		} else {
			return false;
		}
	}
}