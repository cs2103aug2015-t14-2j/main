package Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.joestelmach.natty.*;

/**
 * 
 * @author Audrey
 * 
 *         This class takes the user input in parts(through a hashmap) and
 *         converts them to their respective objects.
 * 
 * 
 */

public class Validator {
	private static Context context = Context.getInstance();
	private static Parser parser = new Parser();

	public Validator() {
	}
	
	/**
	 * Natty takes a phrase of user input and returns single date object
	 * @param  dateString
	 * @return Date object, null if Natty cannot parse into a date
	 */
	private static Date parseNatty(String dateString) {
		List<DateGroup> dateGroup = parser.parse(dateString);
		if (dateGroup.size() == 1) {
			List<Date> dateList = dateGroup.get(0).getDates();
			if (dateList.size() == 1) {
				// System.out.println(dateList.get(0).toString());
				return dateList.get(0);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static HashMap<PARAMETER, Object> getObjectHashMap(HashMap<PARAMETER, String> hashmap,
			COMMAND_TYPE command) {

		HashMap<PARAMETER, Object> objectHashMap = new HashMap<PARAMETER, Object>();

		if (isValidString(hashmap.get(PARAMETER.DESC))) {
			objectHashMap.put(PARAMETER.DESC, hashmap.get(PARAMETER.DESC));
		}

		if (hashmap.get(PARAMETER.VENUE) != null) {
			if (isValidString(hashmap.get(PARAMETER.VENUE))) {
				objectHashMap.put(PARAMETER.VENUE, hashmap.get(PARAMETER.VENUE));
			}
		}
		// DO DATE
		// START_DATE, END_DATE, START_TIME, END_TIME, DEADLINE_DATE,
		// DEADLINE_TIME, REMIND_TIMES
		String startDate = hashmap.get(PARAMETER.START_DATE);
		Date start_Date = null;
		String endDate = hashmap.get(PARAMETER.END_DATE);
		Date end_Date = null;
		String startTime = hashmap.get(PARAMETER.START_TIME);
		String endTime = hashmap.get(PARAMETER.END_TIME);
		String deadlineDate = hashmap.get(PARAMETER.DEADLINE_DATE);
		String deadlineTime = hashmap.get(PARAMETER.DEADLINE_TIME);
		String taskID = hashmap.get(PARAMETER.TASKID);
		String keyDate = hashmap.get(PARAMETER.DATE);
		// Validate START_DATE, if valid, convert to DateTime and store in
		// hashMap
		/*
		System.out.println("startDate: " + startDate);
		System.out.println("end date: " + endDate);
		System.out.println("start time: " + startTime);
		System.out.println("end time: " + endTime);
		System.out.println("deadline date: " + deadlineDate);
		System.out.println("deadline time: " + deadlineTime);
		System.out.println("date: " + hashmap.get(PARAMETER.DATE));
		*/
		//used when there is a parsed KeyDate (etc. no deliminator or on _____) 
		if (keyDate != null) {
			start_Date = parseNatty(keyDate);
			end_Date = parseNatty(keyDate);
			if (parseNatty(keyDate) != null) {
				keyWordUpdateHashMap(start_Date,end_Date,startTime,endTime,objectHashMap);
			}
		} else {
			//when there is no keyDate ( etc. direct dates: from ___ to ___)
			if (startTime != null) {
				updateStartTimeHashMap(startTime,objectHashMap);
			}
			// End time
			if (endTime != null) {
				updateEndTimeHashMap(endTime,endDate, objectHashMap);				
			}
			// Deadline time ( etc. by _____) 
			if (deadlineTime != null) {
				updateDeadlineTimeHashMap(deadlineTime, objectHashMap);
			}
		}
		
		if (taskID != null) {
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
		
		String editString;
		if ((editString = hashmap.get(PARAMETER.DELETE_PARAMS)) != null) {
			int numOfSpaces = countOccurence(editString, ' ');
			
			updateDeleteParamsHashMap(editString,numOfSpaces,objectHashMap);
		}
		
		if (command == COMMAND_TYPE.DISPLAY && keyDate != null){
			Date showDate = parseNatty(keyDate);
			Calendar startcal = Calendar.getInstance();
			startcal.setTime(showDate);
			startcal = setStartTime(startcal);
			
			
			Calendar endcal = Calendar.getInstance();
			endcal.setTime(showDate);
			endcal = setEndTime(endcal);
			updateHashMapForDisplay(keyDate, startcal, endcal, objectHashMap);
		}
		System.out.println("Passed START_DATE: " + objectHashMap.get(PARAMETER.START_DATE));
		System.out.println("Passed START_TIME: " + objectHashMap.get(PARAMETER.START_TIME));
		System.out.println("Passed END_DATE: " + objectHashMap.get(PARAMETER.END_DATE));
		System.out.println("Passed END_TIME: " + objectHashMap.get(PARAMETER.END_TIME));
		System.out.println("Passed DEADLINE_DATE: " + objectHashMap.get(PARAMETER.DEADLINE_DATE));
		System.out.println("Passed DEADLINE_TIME: " + objectHashMap.get(PARAMETER.DEADLINE_TIME));
		// System.out.println(hashmap.get(PARAMETER.DELETEPARAMS));
		
		return objectHashMap;
	}

	
	
	private static void keyWordUpdateHashMap(Date start_Date, Date end_Date, String startTime, String endTime,
			HashMap<PARAMETER, Object> objectHashMap) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(start_Date);

		cal = setStartTime(cal);
		start_Date = cal.getTime();
		cal.setTime(end_Date);
		cal = setEndTime(cal);
		end_Date = cal.getTime();

		if (startTime != null) {
			Date time = parseNatty(startTime);
			cal.setTime(start_Date);
			Calendar timePortion = Calendar.getInstance();
			timePortion.setTime(time);

			cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, 00);
			cal.set(Calendar.MILLISECOND, 0);
			start_Date = cal.getTime();
		}
		if (endTime != null) {
			Date time = parseNatty(endTime);
			cal.setTime(end_Date);
			Calendar timePortion = Calendar.getInstance();
			timePortion.setTime(time);

			cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, 00);
			cal.set(Calendar.MILLISECOND, 0);
			end_Date = cal.getTime();
		}
		objectHashMap.put(PARAMETER.START_DATE, start_Date);
		objectHashMap.put(PARAMETER.START_TIME, start_Date);
		objectHashMap.put(PARAMETER.END_DATE, end_Date);
		objectHashMap.put(PARAMETER.END_TIME, end_Date);	
	}

	private static void updateStartTimeHashMap(String startTime, HashMap<PARAMETER, Object> objectHashMap) {
		Date start_Time = null;
		if ((start_Time = parseNatty(startTime)) == null) {
			start_Time = validTimeFormat(startTime);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 0);
		if (start_Time != null) {
			cal.setTime(start_Time);
			if (countOccurence(startTime, ' ') != 1) {
				cal = setStartTime(cal);
				start_Time = cal.getTime();
			}

			objectHashMap.put(PARAMETER.START_TIME, start_Time);
			objectHashMap.put(PARAMETER.START_DATE, start_Time);

		} else {
			context.displayMessage("PARAM_SUBTITLE");
			context.displayMessage("PARAM_DEADLINE_TIME");
		}
		
	}

	private static void updateEndTimeHashMap(String endTime, String endDate, HashMap<PARAMETER, Object> objectHashMap) {
		Date end_Time;
		if ((end_Time = parseNatty(endTime)) == null) {
			end_Time = validTimeFormat(endTime);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 0);
		if (end_Time != null) {
			cal.setTime(end_Time);
			if (countOccurence(endTime, ' ') != 1) {
				cal = setEndTime(cal);
				end_Time = cal.getTime();
			}
			try {
				Date start_Time = (Date) objectHashMap.get(PARAMETER.START_DATE);
				if (end_Time.before(start_Time)) {
					if (isDayWord(endTime)) {
						cal.setTime(end_Time);
						cal.add(Calendar.DAY_OF_MONTH, 7);
						end_Time = cal.getTime();
						objectHashMap.put(PARAMETER.END_TIME, end_Time);
						objectHashMap.put(PARAMETER.END_DATE, end_Time);
					} else {
						context.displayMessage("ERROR_START_BEFORE_END");
					}
				} else {
					objectHashMap.put(PARAMETER.END_TIME, end_Time);
					objectHashMap.put(PARAMETER.END_DATE, end_Time);
				}
			} catch (NullPointerException n) {
				end_Time = parseNatty(endDate + " " + endTime);
				cal.setTime(end_Time);
				cal = setEndTime(cal);
				end_Time = cal.getTime();
				objectHashMap.put(PARAMETER.END_TIME, end_Time);
				objectHashMap.put(PARAMETER.END_DATE, end_Time);
			}

		} else {
			context.displayMessage("PARAM_SUBTITLE");
			context.displayMessage("PARAM_DEADLINE_TIME");
		}
		
	}

	private static void updateDeadlineTimeHashMap(String deadlineTime, HashMap<PARAMETER, Object> objectHashMap) {
		Date timeOfDeadline = null;
		if ((timeOfDeadline = parseNatty(deadlineTime)) == null) {
			timeOfDeadline = validTimeFormat(deadlineTime);
		}
		Calendar cal = Calendar.getInstance();
		if (timeOfDeadline != null) {
			cal.setTime(timeOfDeadline);
			if (countOccurence(deadlineTime, ' ') != 1) {
				cal = setEndTime(cal);
				timeOfDeadline = cal.getTime();
			}
			if (deadlineTime.contains("week") || deadlineTime.contains("wk")) {
				cal.setTime(timeOfDeadline);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				cal = setEndTime(cal);
				cal.add(Calendar.DAY_OF_WEEK, 7);
				timeOfDeadline = cal.getTime();
			} else if (isMonthWord(deadlineTime)) {
				cal.setTime(timeOfDeadline);
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
				cal = setEndTime(cal);
				timeOfDeadline = cal.getTime();
			} else if (deadlineTime.contains("yr")|| deadlineTime.contains("year")){
				cal.setTime(timeOfDeadline);
				cal.set(Calendar.MONTH,11);
				cal.set(Calendar.DAY_OF_MONTH, 31);
				cal = setEndTime(cal);
				timeOfDeadline = cal.getTime();
			}

			Calendar current = Calendar.getInstance();
			if (cal.getTime().before(current.getTime())) {
				context.displayMessage("WARNING_DEADLINE_BEFORE_NOW");
			}
			objectHashMap.put(PARAMETER.DEADLINE_TIME, timeOfDeadline);
			objectHashMap.put(PARAMETER.DEADLINE_DATE, timeOfDeadline);

		} else {
			context.displayMessage("PARAM_SUBTITLE");
			context.displayMessage("PARAM_DEADLINE_TIME");

		}
		
	}

	private static void updateDeleteParamsHashMap(String editString, int numOfSpaces,
		HashMap<PARAMETER, Object> objectHashMap) {
		int n = 0;
		String[] splitString = editString.split("\\s+");
		PARAMETER[] parameterArray = new PARAMETER[20];
		for (int i = 0; i < numOfSpaces + 1; i++) {
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

	private static void updateHashMapForDisplay(String keyDate, Calendar startcal, Calendar endcal, HashMap<PARAMETER, Object> objectHashMap) {
		if (keyDate.contains("week") || keyDate.contains("wk")) {
			// Calendar cal = Calendar.getInstance();
			startcal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			objectHashMap.put(PARAMETER.START_DATE, startcal.getTime());
			objectHashMap.put(PARAMETER.START_TIME, startcal.getTime());
			endcal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			endcal.add(Calendar.DAY_OF_WEEK, 7);
			objectHashMap.put(PARAMETER.END_DATE, endcal.getTime());
			objectHashMap.put(PARAMETER.END_TIME, endcal.getTime());
			context.displayMessage("VIEW_WEEK");
		} else if (isMonthWord(keyDate)) {
			// Calendar cal = Calendar.getInstance();
			startcal.set(Calendar.DAY_OF_MONTH, startcal.getActualMinimum(Calendar.DATE));
			objectHashMap.put(PARAMETER.START_DATE, startcal.getTime());
			objectHashMap.put(PARAMETER.START_TIME, startcal.getTime());
			endcal.set(Calendar.DAY_OF_MONTH, endcal.getActualMaximum(Calendar.DATE));
			objectHashMap.put(PARAMETER.END_DATE, endcal.getTime());
			objectHashMap.put(PARAMETER.END_TIME, endcal.getTime());
			context.displayMessage("VIEW_MONTH");
		} else if (keyDate.contains("yr") || keyDate.contains("year")) {
			startcal.set(Calendar.MONTH,0);
			startcal.set(Calendar.DAY_OF_YEAR, 1);
			objectHashMap.put(PARAMETER.START_DATE, startcal.getTime());
			objectHashMap.put(PARAMETER.START_TIME, startcal.getTime());
			endcal.set(Calendar.MONTH,11);
			endcal.set(Calendar.DAY_OF_MONTH, 31);
			objectHashMap.put(PARAMETER.END_DATE, endcal.getTime());
			objectHashMap.put(PARAMETER.END_TIME, endcal.getTime());
			context.displayMessage("VIEW_MONTH");
		} else {
			objectHashMap.put(PARAMETER.START_DATE, startcal.getTime());
			objectHashMap.put(PARAMETER.START_TIME, startcal.getTime());
			objectHashMap.put(PARAMETER.END_DATE, endcal.getTime());
			objectHashMap.put(PARAMETER.END_TIME, endcal.getTime());
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
	private static Calendar setStartTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 00);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	private static Calendar setEndTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	private static boolean containsOnlyNumbers(String numString) {
		return (numString.matches("[-+]?\\d*\\.?\\d+"));
	}

	private static boolean isValidString(String string) {
		if (string == null || string.trim().equals("")) {
			return false;
		}
		return true;
	}

	private static Date validDateFormat(String string) {
		if (wordFormat(string) != null) {
			return wordFormat(string);
		}

		if (numberDateFormat(string) != null) {
			return numberDateFormat(string);
		}
		if (wordMonthFormat(string) != null) {
			return wordMonthFormat(string);
		}
		return null;
	}

	private static Date wordFormat(String string) {
		string = string.trim();
		string = string.toLowerCase();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 00);
		cal.set(Calendar.MINUTE, 00);
		Date date;
		switch (string) {
		case "today":
		case "tdy":
			date = cal.getTime();
			break;
		case "tomorrow":
		case "tmr":
			cal.add(Calendar.DAY_OF_YEAR, 1);
			date = cal.getTime();
			break;
		case "the day after":
		case "day after":
			cal.add(Calendar.DAY_OF_YEAR, 2);
			date = cal.getTime();
			break;
		case "monday":
		case "mon":
			cal.add(Calendar.DATE, 1);
			while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				cal.add(Calendar.DATE, 1);
			}
			date = cal.getTime();
			break;
		case "tuesday":
		case "tues":
			cal.add(Calendar.DATE, 1);
			while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY) {
				cal.add(Calendar.DATE, 1);
			}
			date = cal.getTime();
			break;
		case "wednesday":
		case "wed":
		case "wednes":
			cal.add(Calendar.DATE, 1);
			while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
				cal.add(Calendar.DATE, 1);
			}
			date = cal.getTime();
			break;
		case "thursday":
		case "thurs":
			cal.add(Calendar.DATE, 1);
			while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
				cal.add(Calendar.DATE, 1);
			}
			date = cal.getTime();
			break;
		case "friday":
		case "fri":
			cal.add(Calendar.DATE, 1);
			while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
				cal.add(Calendar.DATE, 1);
			}
			date = cal.getTime();
			break;
		case "saturday":
		case "sat":
			cal.add(Calendar.DATE, 1);
			while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				cal.add(Calendar.DATE, 1);
			}
			date = cal.getTime();
			break;
		case "sunday":
		case "sun":
			cal.add(Calendar.DATE, 1);
			while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				cal.add(Calendar.DATE, 1);
			}
			date = cal.getTime();
			break;
		default:
			date = null;
		}

		return date;
	}

	private static Date validTimeFormat(String string) {
		if (is12hrTimeFormat(string) != null) {
			return is12hrTimeFormat(string);
		}
		if (is24hrTimeFormat(string) != null) {
			return is24hrTimeFormat(string);
		}
		return null;
	}

	/*********************************************************************
	 * DATE HANDLING * *
	 *********************************************************************/

	private static Date numberDateFormat(String string) {
		Date date;
		SimpleDateFormat dateFormat = null;
		string = string.trim();

		// dd/MM and dd/MM/yyyy
		if (!Character.isDigit(string.charAt(2))) {
			if (string.contains("/")) {
				if (string.length() == 8) {
					dateFormat = new SimpleDateFormat("dd/MM/yy");
					dateFormat.setLenient(false);
				} else {
					dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					dateFormat.setLenient(false);
					if (string.length() <= 5) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "/" + year;
					}
				}
			} else if (string.contains("-")) {
				if (string.length() == 8) {
					dateFormat = new SimpleDateFormat("dd/MM/yy");
					dateFormat.setLenient(false);
				} else {
					dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					dateFormat.setLenient(false);
					if (string.length() <= 5) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "-" + year;
					}
				}
			} else if (string.contains(" ")) {
				if (string.length() == 8) {
					dateFormat = new SimpleDateFormat("dd/MM/yy");
					dateFormat.setLenient(false);
				} else {
					dateFormat = new SimpleDateFormat("dd MM yyyy");
					dateFormat.setLenient(false);
					if (string.length() <= 5) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + " " + year;
					}
				}
			} else if (string.contains(",")) {
				if (string.length() == 8) {
					dateFormat = new SimpleDateFormat("dd/MM/yy");
					dateFormat.setLenient(false);
				} else {
					dateFormat = new SimpleDateFormat("dd,MM,yyyy");
					dateFormat.setLenient(false);
					if (string.length() <= 5) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "," + year;
					}
				}
			}
		}
		// yyyy mm dd
		else if (Character.isDigit(string.charAt(2))) {
			if (string.contains("/")) {
				dateFormat = new SimpleDateFormat("yyyy/MM/dd");
				dateFormat.setLenient(false);
			} else if (string.contains("-")) {
				dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				dateFormat.setLenient(false);
			} else if (string.contains(" ")) {
				dateFormat = new SimpleDateFormat("yyyy MM dd");
				dateFormat.setLenient(false);
			} else if (string.contains(",")) {
				dateFormat = new SimpleDateFormat("yyyy,MM,dd");
				dateFormat.setLenient(false);
			}
		}
		try {
			date = dateFormat.parse(string);
			return date;
		} catch (ParseException e) {
			return null;
		} catch (NullPointerException p) {
			return null;
		}
	}

	// Word Month date format. e.g. 21/Apr or 21/Apr/2015
	private static Date wordMonthFormat(String string) {
		Date date;
		string = string.trim();
		SimpleDateFormat dateFormat = null;
		// deals with single digit dates 9-august
		if (!Character.isDigit(string.charAt(1)) && Character.isDigit(string.charAt(0))) {
			string = "0" + string;
		}

		// dd/MMM---------------------------------------------------------------
		if (!Character.isLetter(string.charAt(2))) {
			if (string.contains("/")) {
				// Deals with Fully typed month
				if (containMonthWord(string.toLowerCase())) {
					if (countOccurence(string, '/') == 1) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "/" + year;
						dateFormat = new SimpleDateFormat("dd/MMMM/yyyy", Locale.ENGLISH);
					} else if (!Character.isDigit(string.charAt(string.length() - 3))
							&& countOccurence(string, '/') == 2) {
						dateFormat = new SimpleDateFormat("dd/MMMM/yy", Locale.ENGLISH);
					} else {
						dateFormat = new SimpleDateFormat("dd/MMMM/yyyy", Locale.ENGLISH);
					}
				}
				// Deals with 3 letters word month
				else {
					if (string.length() == 9) {
						dateFormat = new SimpleDateFormat("dd/MMM/yy");
					} else {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "/" + year;
						dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
					}
				}

			} else if (string.contains("-")) {

				// Deals with Fully typed months
				if (containMonthWord(string.toLowerCase())) {
					if (countOccurence(string, '-') == 1) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "-" + year;
						dateFormat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
					} else if (!Character.isDigit(string.charAt(string.length() - 3))
							&& countOccurence(string, '-') == 2) {
						dateFormat = new SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH);
					} else {
						dateFormat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
					}
				}
				// Deals with 3 letters word month
				else {
					if (string.length() == 9) {
						dateFormat = new SimpleDateFormat("dd-MMM-yy");
					} else {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "-" + year;
						dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
					}
				}
			} else if (string.contains(" ")) {

				// Deals with Fully typed months
				if (containMonthWord(string.toLowerCase())) {
					if (countOccurence(string, ' ') == 1) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + " " + year;
						dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
					} else if (!Character.isDigit(string.charAt(string.length() - 3))
							&& countOccurence(string, ' ') == 2) {
						dateFormat = new SimpleDateFormat("dd MMMM yy", Locale.ENGLISH);
					} else {
						dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
					}
				}
				// Deals with 3 letters word month
				else {
					if (string.length() == 9) {
						dateFormat = new SimpleDateFormat("dd MMM yy");
					} else {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + " " + year;
						dateFormat = new SimpleDateFormat("dd MMM yyyy");
					}
				}
			} else if (string.contains(",")) {

				// Deals with Fully typed months
				if (containMonthWord(string.toLowerCase())) {
					if (countOccurence(string, ',') == 1) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "," + year;
						dateFormat = new SimpleDateFormat("dd,MMMM,yyyy", Locale.ENGLISH);
					} else if (!Character.isDigit(string.charAt(string.length() - 3))
							&& countOccurence(string, ',') == 2) {
						dateFormat = new SimpleDateFormat("dd,MMMM,yy", Locale.ENGLISH);
					} else {
						dateFormat = new SimpleDateFormat("dd,MMMM,yyyy", Locale.ENGLISH);
					}
				}
				// Deals with 3 letters word month
				else {
					if (string.length() == 9) {
						dateFormat = new SimpleDateFormat("dd,MMM,yy");
					} else {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "," + year;
						dateFormat = new SimpleDateFormat("dd,MMM,yyyy");
					}
				}
			}
		}
		// MMM dd
		// ------------------------------------------------------------------------------------
		else if (Character.isLetter(string.charAt(2))) {
			if (string.contains("/")) {
				if (containMonthWord(string)) {
					if (countOccurence(string, '/') == 1) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "/" + year;
					}
					dateFormat = new SimpleDateFormat("MMMM/dd/yyyy", Locale.ENGLISH);
				}
				// Deals with 3 letters word month
				else {
					if (string.length() == 9) {
						dateFormat = new SimpleDateFormat("MMM/dd/yy");
					} else {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "/" + year;
						dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
					}
				}

			} else if (string.contains("-")) {

				// Deals with Fully typed months
				if (containMonthWord(string)) {
					if (countOccurence(string, '-') == 1) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "-" + year;
					}
					dateFormat = new SimpleDateFormat("MMMM-dd-yyyy", Locale.ENGLISH);
				}
				// Deals with 3 letters word month
				else {
					if (string.length() == 9) {
						dateFormat = new SimpleDateFormat("MMM-dd-yy");
					} else {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "-" + year;
						dateFormat = new SimpleDateFormat("MMM-dd-yyyy");
					}
				}
			} else if (string.contains(" ")) {

				// Deals with Fully typed months
				if (containMonthWord(string)) {
					if (countOccurence(string, ' ') == 1) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + " " + year;
					}
					dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
				}
				// Deals with 3 letters word month
				else {
					if (string.length() == 9) {
						dateFormat = new SimpleDateFormat("MMM dd yy");
					} else {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + " " + year;
						dateFormat = new SimpleDateFormat("MMM dd yyyy");
					}
				}
			} else if (string.contains(",")) {

				// Deals with Fully typed months
				if (containMonthWord(string)) {
					if (countOccurence(string, ',') == 1) {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + "," + year;
					}
					dateFormat = new SimpleDateFormat("MMMM,dd,yyyy", Locale.ENGLISH);
				}
				// Deals with 3 letters word month
				else {
					if (string.length() == 9) {
						dateFormat = new SimpleDateFormat("MMM,dd,yy");
					} else {
						int year = Calendar.getInstance().get(Calendar.YEAR);
						string = string + " " + year;
						dateFormat = new SimpleDateFormat("MMM,dd,yyyy");
					}
				}
			}
		}
		try {
			dateFormat.setLenient(false);
			date = dateFormat.parse(string);
			return date;
		} catch (ParseException e) {
			return null;
		} catch (NullPointerException p) {
			return null;
		}

	}

	private static int countOccurence(String string, char character) {
		int counter = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == character) {
				counter++;
			}
		}
		return counter;
	}

	private static boolean containMonthWord(String string) {
		// TODO Auto-generated method stub
		String months[] = { "january", "february", "march", "april", "may", "june", "july", "august", "september",
				"october", "november", "december" };
		for (int i = 0; i < 12; i++) {
			if (string.contains(months[i])) {
				return true;
			}
		}

		return false;
	}

	/*********************************************************************
	 * TIME HANDLING *
	 * 
	 * @throws ParseException
	 *             *
	 *********************************************************************/

	// Deals with 12hr time formats. e.g 8pm, 5am, 1230am, 1230pm
	private static Date is12hrTimeFormat(String string) {
		SimpleDateFormat timeFormat;
		Date time;
		try {
			if (string.length() == 3) {
				timeFormat = new SimpleDateFormat("haa");
				timeFormat.setLenient(false);
				time = timeFormat.parse(string);
				return time;
			} else if (string.length() == 4) {
				timeFormat = new SimpleDateFormat("hhaa");
				timeFormat.setLenient(false);
				time = timeFormat.parse(string);
				return time;
			} else if (string.length() == 5) {
				timeFormat = new SimpleDateFormat("hmmaa");
				timeFormat.setLenient(false);
				time = timeFormat.parse(string);
				return time;
			} else {
				timeFormat = new SimpleDateFormat("hhmmaa");
				timeFormat.setLenient(false);
				time = timeFormat.parse(string);
				return time;
			}
		} catch (ParseException e) {
			return null;
		} catch (NullPointerException p) {
			return null;
		}

	}

	// Deals with 24 hrs format.
	// TODO: deal with inputs like 2500, 1270
	private static Date is24hrTimeFormat(String string) {
		string = string.trim();
		SimpleDateFormat timeFormat = null;
		Date time;
		try {
			if (string.length() == 4) {
				timeFormat = new SimpleDateFormat("HHmm");
				timeFormat.setLenient(false);
			} else if (string.length() == 3) {
				timeFormat = new SimpleDateFormat("Hmm");
				timeFormat.setLenient(false);
			}
			time = timeFormat.parse(string);
			return time;
		} catch (ParseException e) {
			return null;
		} catch (NullPointerException p) {
			return null;
		}

	}

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

}
