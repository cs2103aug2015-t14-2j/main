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
 *         converts them to their respective objects. It also throws exceptions
 *         if there are any invalid inputs. Exceptions are as follows:
 * 
 *         1) ParseException : Invalid formats. E.g. User types in a date or
 *         time format that is not supported. 2) IllegalArgumentException: Dates
 *         are invalid. E.g. End date is before start date 3)
 *         IllegalStateException: String invalid. E.g. No input in venue or desc
 * 
 *         Date Formats currently recognised:
 * 
 *         1) All numbers. etc 21,05,2015 dd/MM/yy , dd/MM/yyyy , dd/MM (for
 *         comma, space and hyphens too)
 * 
 *         2) Also done yyyy/MM/dd
 * 
 *         3) Word Month Format 3 Letter Months accepted Jan, Feb etc. Fully
 *         spelt months accepted. Format same as above.
 * 
 *         4) Word Month Format with Month first accepted. Etc: August 8
 * 
 *         5) Words : Today, tomorrow, tdy , tmr
 * 
 *         6) Monday to SUNDAY also supported HOWEVER : ONLY WORK ON :
 *         START_DATES, DEADLINE_DEADS. Does not support "from monday to friday"
 *         , only "on monday", "by monday" etc.
 * 
 *         Todo: Today, tday, tomorrow, tmr, mon tues etc,
 * 
 *         Time Formats currently recognised: 8pm 0800pm 1230 2130 Todo: 8:30,
 *         8:30pm
 * 
 */

public class Validator {
	private static Context context = Context.getInstance();
	private static Parser parser = new Parser();

	public Validator() {
	}

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

	public static HashMap<PARAMETER, Object> getObjectHashMap(HashMap<PARAMETER, String> hashmap) {

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
		String keyWord = hashmap.get(PARAMETER.DATE);
		// Validate START_DATE, if valid, convert to DateTime and store in
		// hashMap

		if (keyWord != null) {
			start_Date = parseNatty(keyWord);
			end_Date = parseNatty(keyWord);

			Calendar cal = Calendar.getInstance();
			cal.setTime(start_Date);
			cal.set(Calendar.HOUR_OF_DAY, 00);
			cal.set(Calendar.MINUTE, 00);
			cal.set(Calendar.SECOND, 00);
			cal.set(Calendar.MILLISECOND, 0);
			start_Date = cal.getTime();
			cal.setTime(end_Date);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 00);
			cal.set(Calendar.MILLISECOND, 0);
			end_Date = cal.getTime();

			objectHashMap.put(PARAMETER.START_DATE, start_Date);
			objectHashMap.put(PARAMETER.START_TIME, start_Date);
			objectHashMap.put(PARAMETER.END_DATE, end_Date);
			objectHashMap.put(PARAMETER.END_TIME, start_Date);
		} else {
			if (startDate != null) {

				if ((start_Date = numberDateFormat(startDate)) == null) {
					start_Date = parseNatty(startDate);
				}

				Calendar cal = Calendar.getInstance();
				cal.setTime(start_Date);
				Calendar timePortion = Calendar.getInstance();
				timePortion.set(Calendar.HOUR_OF_DAY, 00);
				timePortion.set(Calendar.MINUTE, 00);

				cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));
				cal.set(Calendar.SECOND, 00);
				cal.set(Calendar.MILLISECOND, 0);
				start_Date = cal.getTime();

				if (start_Date != null) {
					objectHashMap.put(PARAMETER.START_DATE, start_Date);

				} else {
					context.displayMessage("PARAM_SUBTITLE");
					context.displayMessage("PARAM_START_DATE");
					// throw new ParseException("PARAMETER.START_DATE", 0);// No
					// such
					// format
				}
			}
			// end date
			if (endDate != null) {
				if ((end_Date = numberDateFormat(endDate)) == null) {
					end_Date = parseNatty(endDate);
				}

				Calendar cal = Calendar.getInstance();
				cal.setTime(end_Date);
				Calendar timePortion = Calendar.getInstance();
				timePortion.set(Calendar.HOUR_OF_DAY, 23);
				timePortion.set(Calendar.MINUTE, 59);
				cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));
				cal.set(Calendar.SECOND, 00);
				cal.set(Calendar.MILLISECOND, 0);
				end_Date = cal.getTime();

				if (end_Date != null) {
					objectHashMap.put(PARAMETER.END_DATE, end_Date);
				} else {
					context.displayMessage("PARAM_SUBTITLE");
					context.displayMessage("PARAM_END_DATE");
					// throw new ParseException("PARAMETER.END_DATE", 0);// No
					// such
					// format
				}
			}
			// start TIME
			// time handling either (24hr(1235) or 12hr (845pm) format.
			if (startTime != null) {
				Date time;
				if (validDateFormat(startTime) != null) {
					time = validDateFormat(startTime);
					objectHashMap.put(PARAMETER.START_TIME, time);
					objectHashMap.put(PARAMETER.START_DATE, time);
				} else {
					time = validTimeFormat(startTime);

					if (time != null) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(start_Date);
						Calendar timePortion = Calendar.getInstance();
						timePortion.setTime(time);

						cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
						cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));
						cal.set(Calendar.SECOND, 00);
						cal.set(Calendar.MILLISECOND, 0);

						if ((start_Date = parseNatty(startDate + " " + startTime)) == null) {
							start_Date = cal.getTime();
						}
						start_Date = cal.getTime();
						objectHashMap.put(PARAMETER.START_TIME, start_Date);
						objectHashMap.put(PARAMETER.START_DATE, objectHashMap.get(PARAMETER.START_TIME));
					} else {
						context.displayMessage("PARAM_SUBTITLE");
						context.displayMessage("PARAM_START_TIME");
						// throw new ParseException("PARAMETER.START_TIME", 0);
					}
				}
			} else {
				objectHashMap.put(PARAMETER.START_TIME, objectHashMap.get(PARAMETER.START_DATE));
			}

			// End time
			Date end_Time;
			if(endTime != null){
				if ((end_Time = parseNatty(endDate + " " + endTime)) == null){
					end_Time = validTimeFormat(endTime);
				}
				Calendar cal = Calendar.getInstance();
				if(end_Time != null){
					cal.setTime(end_Time);
				if(!endTime.contains(" ")){
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 59);
					cal.set(Calendar.SECOND, 00);
					cal.set(Calendar.MILLISECOND, 0);
					end_Time = cal.getTime();
				}
				
				objectHashMap.put(PARAMETER.DEADLINE_TIME, end_Time);
				objectHashMap.put(PARAMETER.DEADLINE_DATE, end_Time);
				
				
				}else {
					context.displayMessage("PARAM_SUBTITLE");
					context.displayMessage("PARAM_DEADLINE_TIME");
					// throw new ParseException("PARAMETER.DEADLINE_TIME",
					// 0);
				}	
			}

			// DEADLINE DATE
			Date dateOfDeadline = null;
			if (deadlineDate != null) {
				if ((dateOfDeadline = numberDateFormat(deadlineDate)) == null) {
					dateOfDeadline = parseNatty(deadlineDate);
				}

				if (dateOfDeadline != null) {
					Calendar cal = Calendar.getInstance();
					if (dateOfDeadline.before(cal.getTime())) {
						context.displayMessage("WARNING_DEADLINE_BEFORE_NOW");
						objectHashMap.put(PARAMETER.DEADLINE_DATE, dateOfDeadline);
						// throw new IllegalArgumentException("DEADLINE_DATE
						// before
						// CURRENTDATE");
					} else {
						Calendar cal2 = Calendar.getInstance();
						cal2.setTime(dateOfDeadline);
						cal2.set(Calendar.HOUR_OF_DAY, 23);
						cal2.set(Calendar.MINUTE, 59);
						cal2.set(Calendar.SECOND, 00);
						cal2.set(Calendar.MILLISECOND, 0);
						dateOfDeadline = cal2.getTime();
						objectHashMap.put(PARAMETER.DEADLINE_DATE, dateOfDeadline);
					}
				} else {
					context.displayMessage("PARAM_SUBTITLE");
					context.displayMessage("PARAM_DEADLINE_DATE");
					// throw new ParseException("PARAMETER.DEADLINE_DATE", 0);
				}
			}

			// Deadline time
			Date timeOfDeadline = null;
			if(deadlineTime != null){
				if ((timeOfDeadline = parseNatty(deadlineDate + " " + deadlineTime)) == null){
					timeOfDeadline = validTimeFormat(deadlineTime);
				}
				Calendar cal = Calendar.getInstance();
				if(timeOfDeadline != null){
					cal.setTime(timeOfDeadline);
				if(!deadlineTime.contains(" ")){
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 59);
					cal.set(Calendar.SECOND, 00);
					cal.set(Calendar.MILLISECOND, 0);
					timeOfDeadline = cal.getTime();
				}
				
				Calendar current = Calendar.getInstance();
				if (cal.getTime().before(current.getTime())) {
					context.displayMessage("WARNING_DEADLINE_BEFORE_NOW");
					// throw new IllegalArgumentException("DEADLINE_TIME
					// before
					// CURRENT");
				}
				objectHashMap.put(PARAMETER.DEADLINE_TIME, timeOfDeadline);
				objectHashMap.put(PARAMETER.DEADLINE_DATE, timeOfDeadline);
				
				
				}else {
					context.displayMessage("PARAM_SUBTITLE");
					context.displayMessage("PARAM_DEADLINE_TIME");
					// throw new ParseException("PARAMETER.DEADLINE_TIME",
					// 0);
				}	
			}


			if (taskID != null) {
				if (containsOnlyNumbers(taskID)) {
					objectHashMap.put(PARAMETER.TASKID, Integer.parseInt(taskID));
				} else {
					context.displayMessage("PARAM_SUBTITLE");
					context.displayMessage("PARAM_TASKID_NUM");
					objectHashMap.put(PARAMETER.TASKID, 0);
					// throw new ParseException("PARAMETER.TASKID", 0);
				}
			} else {
				objectHashMap.put(PARAMETER.TASKID, -1);
			}
		}
		System.out.println("startDate: " + startDate);
		System.out.println("end date: " + endDate);
		System.out.println("start time: " +startTime);
		System.out.println("end time: " + endTime);
		System.out.println("deadline date: " + deadlineDate);
		System.out.println("deadline time: " + deadlineTime);

		System.out.println(objectHashMap.get(PARAMETER.START_DATE));
		System.out.println(objectHashMap.get(PARAMETER.START_TIME));
		System.out.println(objectHashMap.get(PARAMETER.END_DATE));
		System.out.println(objectHashMap.get(PARAMETER.END_TIME));
		System.out.println(objectHashMap.get(PARAMETER.DEADLINE_DATE));
		System.out.println(objectHashMap.get(PARAMETER.DEADLINE_TIME));
		return objectHashMap;
	}

	/**
	 * Used to check if the contents of a string are numerical
	 * 
	 * @param numString
	 *            The string to be checked for all numbers
	 * @return A boolean representation of whether the string provided is all
	 *         numbers
	 */
	public static boolean containsOnlyNumbers(String numString) {
		return (numString.matches("[-+]?\\d*\\.?\\d+"));
	}

	/*
	 * private static boolean isValidPriority(String value) { value =
	 * value.trim(); // in case of front and back white spaces value =
	 * value.toLowerCase(); if (value.equals("low") || value.equals("medium") ||
	 * value.equals("high")) { return true; } return false; }
	 */

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

	public static Date wordFormat(String string) {
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

	public static Date numberDateFormat(String string) {
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
	public static Date wordMonthFormat(String string) {
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
	public static Date is12hrTimeFormat(String string) {
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
	public static Date is24hrTimeFormat(String string) {
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

}
