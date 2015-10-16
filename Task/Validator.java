package Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Audrey
 * 
 * This class takes the user input in parts(through a hashmap) and converts them
 * to their respective objects. It also throws exceptions if there are any invalid 
 * inputs. 
 * Exceptions are as follows:
 * 
 * ParseException : Invalid formats. E.g. User types in a date or time format that is not supported.
 * IllegalArgumentException: Dates are invalid. E.g. End date is before start date
 * IllegalStateException: String invalid. E.g. No input in venue or desc
 * 
 * Date Formats currently recognised:
 * 21/05/2015
 * 21/05
 * 21-05-2015
 * 21-05
 * 
 * Todo: Today, tday, tomorrow, tmr, mon tues etc,
 * 
 * Time Formats currently recognised:
 * 8pm
 * 0800pm
 * 1230
 * 2130
 * Todo: 8:30, 8:30pm
 * 
 */

public class Validator {
	public Validator() {
		
	}

	public static HashMap<PARAMETER, Object> getObjectHashMap(HashMap<PARAMETER, String> hashmap) throws ParseException {
		HashMap<PARAMETER, Object> objectHashMap = new HashMap<PARAMETER, Object>();
		
		if (isValidString(hashmap.get(PARAMETER.DESC))) {
			objectHashMap.put(PARAMETER.DESC, hashmap.get(PARAMETER.DESC));
		} else {
			throw new IllegalStateException("PARAMETER.DESC");
		}

		if (hashmap.get(PARAMETER.VENUE) != null) {
			if (isValidString(hashmap.get(PARAMETER.VENUE))) {
				objectHashMap.put(PARAMETER.VENUE, hashmap.get(PARAMETER.VENUE));
			} else {
				throw new IllegalStateException("PARAMETER.VENUE");
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
		String remindTimes = hashmap.get(PARAMETER.REMIND_TIMES);

		// Validate START_DATE, if valid, convert to DateTime and store in
		// hashMap
		if (startDate != null) {
			start_Date = validDateFormat(startDate);
			if (start_Date != null) {
				objectHashMap.put(PARAMETER.START_DATE, start_Date);
			} else {
				throw new ParseException("PARAMETER.END_DATE", 0);// No such
																	// format
			}
		}

		// end date
		if (endDate != null) {
			end_Date = validDateFormat(endDate);
			if (end_Date != null) {
				if (end_Date.before(start_Date)) {
					throw new IllegalArgumentException("END_DATE before START_DATE");
				} else {
					objectHashMap.put(PARAMETER.END_DATE, end_Date);
				}
			} else {
				throw new ParseException("PARAMETER.END_DATE", 0);// No such
																	// format
			}
		}
		// start TIME
		// time handling either (24hr(1235) or 12hr (845pm) format.
		if (startTime != null) {
			Date time = validTimeFormat(startTime);
			if (time != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(start_Date);
				Calendar timePortion = Calendar.getInstance();
				timePortion.setTime(time);

				cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));

				start_Date = cal.getTime();
				objectHashMap.put(PARAMETER.START_TIME, start_Date);
			} else {
				throw new ParseException("PARAMETER.START_TIME", 0);
			}
		}
		// End time
		if (endTime != null) {
			Date time = validTimeFormat(endTime);
			if (time != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(end_Date);
				Calendar timePortion = Calendar.getInstance();
				timePortion.setTime(time);

				cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));

				if (cal.getTime().before(start_Date)) {
					throw new IllegalArgumentException("END_DATE before START_DATE");
				} else {
					end_Date = cal.getTime();
					objectHashMap.put(PARAMETER.END_TIME, end_Date);
				}
			} else {
				throw new ParseException("PARAMETER.END_TIME", 0);
			}
		}
		// DEADLINE DATE
		Date dateOfDeadline = null;
		if (deadlineDate != null) {
			dateOfDeadline = validDateFormat(deadlineDate);
			if (dateOfDeadline != null) {
				Calendar cal = Calendar.getInstance();
				if (dateOfDeadline.before(cal.getTime())) {
					throw new IllegalArgumentException("DEADLINE_DATE before CURRENTDATE");
				} else {
					objectHashMap.put(PARAMETER.DEADLINE_DATE, dateOfDeadline);
				}
			} else {
				throw new ParseException("PARAMETER.DEADLINE_DATE", 0);
			}
		}

		// Deadline time
		if (deadlineTime != null) {
			Date timeOfDeadline = validTimeFormat(deadlineTime);
			if (timeOfDeadline != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateOfDeadline);

				Calendar timePortion = Calendar.getInstance();
				timePortion.setTime(timeOfDeadline);
				
				cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));

				Calendar current = Calendar.getInstance();
				dateOfDeadline = cal.getTime();
				if (cal.getTime().before(current.getTime())) {
					throw new IllegalArgumentException("DEADLINE_TIME before CURRENT");
				} else {
					objectHashMap.put(PARAMETER.DEADLINE_TIME, dateOfDeadline);
				}
			} else {
				throw new ParseException("PARAMETER.DEADLINE_TIME", 0);
			}
		}
		// REMIND TIMES????

		return objectHashMap;
	}

	/*
	 * private static boolean isValidPriority(String value) { value =
	 * value.trim(); // in case of front and back white spaces value =
	 * value.toLowerCase(); if (value.equals("low") || value.equals("medium") ||
	 * value.equals("high")) { return true; } return false; }
	 */

	private static boolean isValidString(String string) {
		string = string.trim();
		if (string == null || string.equals("")) {
			return false;
		}
		return true;
	}

	private static Date validDateFormat(String string) {
		if (numberDateFormat(string) != null) {
			return numberDateFormat(string);
		}
		if (wordMonthFormat(string) != null) {
			return wordMonthFormat(string);
		}
		return null;
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

	// Number date format for eg. 21/04 or 21/04/2015 ie DD/MM or DD/MM/YYYY
	public static Date numberDateFormat(String string) {
		Date date;
		SimpleDateFormat dateFormat;
		string = string.trim();
		if (string.contains("/")) {
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateFormat.setLenient(false);
			if (string.length() > 5) {
				try {
					date = dateFormat.parse(string);
					return date;
				} catch (ParseException e) {
					return null;
				} catch (IllegalArgumentException e) {
					return null;
				}
			} else {
				try {
					int year = Calendar.getInstance().get(Calendar.YEAR);
					string = string + "/" + year;
					date = dateFormat.parse(string);
					return date;
				} catch (ParseException e) {
					return null;
				} catch (IllegalArgumentException e) {
					return null;
				}
			}
		} else if (string.contains("-")) {
			dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			dateFormat.setLenient(false);
			if (string.length() > 5) {
				try {
					date = dateFormat.parse(string);
					return date;
				} catch (ParseException e) {
					return null;
				} catch (IllegalArgumentException e) {
					return null;
				}
			} else {
				try {
					int year = Calendar.getInstance().get(Calendar.YEAR);
					string = string + "-" + year;
					date = dateFormat.parse(string);
					return date;
				} catch (ParseException e) {
					return null;
				} catch (IllegalArgumentException e) {
					return null;
				}
			}
		}
		return null;
	}

	// Word Month date format. e.g. 21/Apr or 21/Apr/2015
	public static Date wordMonthFormat(String string) {
		Date date;
		string = string.trim();
		SimpleDateFormat dateFormat;
		// year inclusive
		if (string.contains("/")) {
			dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
			dateFormat.setLenient(false);
			if (string.length() > 6) {
				try {
					date = dateFormat.parse(string);
					return date;
				} catch (ParseException e) {
					return null;
				} catch (IllegalArgumentException e) {
					return null;
				}
			} else {
				try {
					int year = Calendar.getInstance().get(Calendar.YEAR);
					string = string + "/" + year;
					date = dateFormat.parse(string);
					return date;
				} catch (ParseException e) {
					return null;
				} catch (IllegalArgumentException e) {
					return null;
				}
			}
		} else if (string.contains("-")) {
			dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			dateFormat.setLenient(false);
			if (string.length() > 6) {
				try {
					date = dateFormat.parse(string);
					return date;
				} catch (ParseException e) {
					return null;
				} catch (IllegalArgumentException e) {
					return null;
				}
			} else {
				try {
					int year = Calendar.getInstance().get(Calendar.YEAR);
					string = string + "-" + year;
					date = dateFormat.parse(string);
					return date;
				} catch (ParseException e) {
					return null;
				} catch (IllegalArgumentException e) {
					return null;
				}
			}
		}
		return null;
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
		}

	}

	// Deals with 24 hrs format.
	// TODO: deal with inputs like 2500, 1270
	public static Date is24hrTimeFormat(String string) {
		string = string.trim();
		SimpleDateFormat timeFormat;
		Date time;
		try {
			timeFormat = new SimpleDateFormat("HHmm");
			timeFormat.setLenient(false);
			time = timeFormat.parse(string);
			return time;
		} catch (ParseException e) {
			return null;
		}

	}

}
