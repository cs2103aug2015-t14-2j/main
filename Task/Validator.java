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

	public Validator() {
	}

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

		if (isValidString(hashmap.get(PARAMETER.DESC))) {
			objectHashMap.put(PARAMETER.DESC, hashmap.get(PARAMETER.DESC));
		}

		if (hashmap.get(PARAMETER.VENUE) != null) {
			if (isValidString(hashmap.get(PARAMETER.VENUE))) {
				objectHashMap.put(PARAMETER.VENUE, hashmap.get(PARAMETER.VENUE));
			}
		}
		Date start_Date = null;
		String endDate = hashmap.get(PARAMETER.END_DATE);
		Date end_Date = null;
		String startTime = hashmap.get(PARAMETER.START_TIME);
		String endTime = hashmap.get(PARAMETER.END_TIME);
		String deadlineTime = hashmap.get(PARAMETER.DEADLINE_TIME);
		String taskID = hashmap.get(PARAMETER.TASKID);
		String keyDate = hashmap.get(PARAMETER.DATE);

		// System.out.println("startDate: " + startDate);
		System.out.println("end date: " + endDate);
		System.out.println("start time: " + startTime);
		System.out.println("end time: " + endTime);
		// System.out.println("deadline date: " + deadlineDate);
		System.out.println("deadline time: " + deadlineTime);
		System.out.println("date: " + hashmap.get(PARAMETER.DATE));
		// Validate START_DATE, if valid, convert to DateTime and store in
		// hashMap
		if (command == COMMAND_TYPE.ADD_TASK) {
			if (deadlineTime != null) {
				updateDeadlineTimeHashMap(deadlineTime, objectHashMap, COMMAND_TYPE.ADD_TASK);
			}
			if (keyDate != null) {
				start_Date = parseNatty(keyDate);
				end_Date = parseNatty(keyDate);
				if (parseNatty(deadlineTime) == null && parseNatty(startTime) == null && parseNatty(endTime) == null) {
					context.displayMessage("WARNING_INVALID_DATEFORMAT");
				}
				if (parseNatty(keyDate) != null && deadlineTime == null) {
					keyWordUpdateHashMap(start_Date, end_Date, startTime, endTime, objectHashMap, command);
				}
				if (deadlineTime != null && parseNatty(keyDate) != null) {
					if (parseNatty(deadlineTime) != null) {
						Date DeadlineTime = parseNatty(deadlineTime);
						Calendar cal = Calendar.getInstance();
						cal.setTime(parseNatty(keyDate));
						Calendar cal2 = Calendar.getInstance();
						cal2.setTime(DeadlineTime);
						cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
						cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
						cal.set(Calendar.SECOND, 00);
						cal.set(Calendar.MILLISECOND, 0);
						DeadlineTime = cal.getTime();
						objectHashMap.put(PARAMETER.DEADLINE_TIME, DeadlineTime);
						objectHashMap.put(PARAMETER.DEADLINE_DATE, DeadlineTime);
					}
				}
			} else {
				// when there is no keyDate ( etc. direct dates: from ___ to
				// ___)
				if (startTime != null) {
					updateStartTimeHashMap(startTime, objectHashMap);
					Date updatedEndTime;
					if (endTime == null) {
						updatedEndTime = (Date) objectHashMap.get(PARAMETER.START_TIME);
						Date endingDate = (Date) objectHashMap.get(PARAMETER.START_DATE);
						if (updatedEndTime != null) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(updatedEndTime);
							cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
							updatedEndTime = cal.getTime();

							objectHashMap.put(PARAMETER.END_DATE, endingDate);

							SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
							try {
								Date endingTime = sdftime.parse(sdftime.format(updatedEndTime));
								objectHashMap.put(PARAMETER.END_TIME, endingTime);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				}
				// End time
				if (endTime != null) {
					updateEndTimeHashMap(endTime, endDate, objectHashMap);
					Date updatedStartTime;
					if (startTime == null) {
						updatedStartTime = (Date) objectHashMap.get(PARAMETER.END_TIME);
						Date startDate = (Date) objectHashMap.get(PARAMETER.END_DATE);
						if (updatedStartTime != null) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(updatedStartTime);
							cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 1);
							updatedStartTime = cal.getTime();
							objectHashMap.put(PARAMETER.START_DATE, startDate);

							SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
							try {
								Date startingTime = sdftime.parse(sdftime.format(updatedStartTime));
								objectHashMap.put(PARAMETER.START_TIME, startingTime);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				}
				// Deadline time ( etc. by _____)
				if (deadlineTime != null) {
					updateDeadlineTimeHashMap(deadlineTime, objectHashMap, command);
				}

			}
		}

		// EDIT//////////////////////////////////////////////////
		if (command == COMMAND_TYPE.EDIT_TASK) {
			if (startTime != null) {
				Calendar today = Calendar.getInstance();
				Date start = parseNatty(startTime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(start);
				// no time input
				if (cal.get(Calendar.HOUR_OF_DAY) == today.get(Calendar.HOUR_OF_DAY)
						&& cal.get(Calendar.MINUTE) == today.get(Calendar.MINUTE)) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
					try {
						Date startingDate = sdf.parse(sdf.format(start));
						objectHashMap.put(PARAMETER.START_DATE, startingDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					if (countOccurence(startTime, ' ') != 0 && startTime != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
						try {
							Date startingDate = sdf.parse(sdf.format(start));
							objectHashMap.put(PARAMETER.START_DATE, startingDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
					try {
						Date startingTime = sdftime.parse(sdftime.format(start));
						objectHashMap.put(PARAMETER.START_TIME, startingTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			if (endTime != null) {
				Calendar today = Calendar.getInstance();
				Date end = parseNatty(endTime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(end);
				// no time input
				if (cal.get(Calendar.HOUR_OF_DAY) == today.get(Calendar.HOUR_OF_DAY)
						&& cal.get(Calendar.MINUTE) == today.get(Calendar.MINUTE)) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
					try {
						Date endingDate = sdf.parse(sdf.format(end));
						objectHashMap.put(PARAMETER.END_DATE, endingDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					if (countOccurence(endTime, ' ') != 0 && endTime != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
						try {
							Date endingDate = sdf.parse(sdf.format(end));
							objectHashMap.put(PARAMETER.END_DATE, endingDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
					try {
						Date endingTime = sdftime.parse(sdftime.format(end));
						objectHashMap.put(PARAMETER.END_TIME, endingTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

			if (keyDate != null) {
				Date date = parseNatty(keyDate);
				SimpleDateFormat sdftime = new SimpleDateFormat("dd/M/yyyy");
				try {
					Date endingDate = sdftime.parse(sdftime.format(date));
					objectHashMap.put(PARAMETER.DATE, endingDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (deadlineTime != null){
				Calendar today = Calendar.getInstance();
				Date deadline = parseNatty(deadlineTime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(deadline);
				// no time input
				if (cal.get(Calendar.HOUR_OF_DAY) == today.get(Calendar.HOUR_OF_DAY)
						&& cal.get(Calendar.MINUTE) == today.get(Calendar.MINUTE)) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
					try {
						Date deadlineDate = sdf.parse(sdf.format(deadline));
						objectHashMap.put(PARAMETER.DEADLINE_DATE, deadlineDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					if (countOccurence(deadlineTime, ' ') != 0 && deadlineTime != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
						try {
							Date deadlineDate = sdf.parse(sdf.format(deadline));
							objectHashMap.put(PARAMETER.DEADLINE_DATE, deadlineDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
					try {
						Date deadline_Time = sdftime.parse(sdftime.format(deadline));
						objectHashMap.put(PARAMETER.DEADLINE_TIME, deadline_Time);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
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

			updateDeleteParamsHashMap(editString, numOfSpaces, objectHashMap);
		}

		if (command == COMMAND_TYPE.DISPLAY && keyDate != null && parseNatty(keyDate) != null) {
			Date showDate = parseNatty(keyDate);
			Calendar startcal = Calendar.getInstance();
			startcal.setTime(showDate);
			startcal = setStartTime(startcal);

			Calendar endcal = Calendar.getInstance();
			endcal.setTime(showDate);
			endcal = setEndTime(endcal);
			updateHashMapForDisplay(keyDate, startcal, endcal, objectHashMap);
		}

		if (command == COMMAND_TYPE.DISPLAY) {
			updateContextDisplay(deadlineTime, keyDate);

			// CHECK FOR FLAGS USED IN SEARCH
			setFlag(hashmap, PARAMETER.IS_DONE, objectHashMap);
			setFlag(hashmap, PARAMETER.HAS_ENDED, objectHashMap);
			setFlag(hashmap, PARAMETER.IS_PAST, objectHashMap);

			setFlag(hashmap, PARAMETER.SPECIAL, objectHashMap);
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
	 * @@author A0145472E
	 * 
	 *          Used to set the flag a Boolean flag in the hashmap
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

	/**
	 * @@author A0118772
	 */
	private static void keyWordUpdateHashMap(Date start_Date, Date end_Date, String startTime, String endTime,
			HashMap<PARAMETER, Object> objectHashMap, COMMAND_TYPE command) {
		Calendar cal = Calendar.getInstance();
		if (parseNatty(startTime) == null && parseNatty(endTime) == null) {
			cal.setTime(start_Date);
			cal = setStartTime(cal);
			start_Date = cal.getTime();
			cal.setTime(end_Date);
			cal = setEndTime(cal);
			end_Date = cal.getTime();
		}
		if (startTime != null && parseNatty(startTime) != null) {
			Date time = parseNatty(startTime);
			cal.setTime(start_Date);
			Calendar timePortion = Calendar.getInstance();
			timePortion.setTime(time);

			cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, 00);
			cal.set(Calendar.MILLISECOND, 0);
			start_Date = cal.getTime();

			if (endTime == null) {
				cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
				end_Date = cal.getTime();
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
			try {
				Date startDate = sdf.parse(sdf.format(start_Date));
				objectHashMap.put(PARAMETER.START_DATE, startDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
			try {
				Date startingTime = sdftime.parse(sdftime.format(start_Date));
				objectHashMap.put(PARAMETER.START_TIME, startingTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		if (endTime != null && parseNatty(endTime) != null) {
			Date time = parseNatty(endTime);
			cal.setTime(end_Date);
			Calendar timePortion = Calendar.getInstance();
			timePortion.setTime(time);

			cal.set(Calendar.HOUR_OF_DAY, timePortion.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, timePortion.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, 00);
			cal.set(Calendar.MILLISECOND, 0);
			end_Date = cal.getTime();

			if (startTime == null) {
				cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 1);
				start_Date = cal.getTime();
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
			try {
				Date endDate = sdf.parse(sdf.format(end_Date));
				objectHashMap.put(PARAMETER.END_DATE, endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
			try {
				Date endingTime = sdftime.parse(sdftime.format(end_Date));
				objectHashMap.put(PARAMETER.END_TIME, endingTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (command != COMMAND_TYPE.EDIT_TASK) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
			try {
				Date startDate = sdf.parse(sdf.format(start_Date));
				objectHashMap.put(PARAMETER.START_DATE, startDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
			try {
				Date startingTime = sdftime.parse(sdftime.format(start_Date));
				objectHashMap.put(PARAMETER.START_TIME, startingTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			try {
				Date endDate = sdf.parse(sdf.format(end_Date));
				objectHashMap.put(PARAMETER.END_DATE, endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			try {
				Date endingTime = sdftime.parse(sdftime.format(end_Date));
				objectHashMap.put(PARAMETER.END_TIME, endingTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
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
			Calendar today = Calendar.getInstance();
			if (cal.get(Calendar.HOUR_OF_DAY) == today.get(Calendar.HOUR_OF_DAY)
					&& cal.get(Calendar.MINUTE) == today.get(Calendar.MINUTE)) {
				cal = setStartTime(cal);
				start_Time = cal.getTime();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
			try {
				Date startDate = sdf.parse(sdf.format(start_Time));
				objectHashMap.put(PARAMETER.START_DATE, startDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
			try {
				Date startingTime = sdftime.parse(sdftime.format(start_Time));
				objectHashMap.put(PARAMETER.START_TIME, startingTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}

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
			Calendar today = Calendar.getInstance();
			if (today.get(Calendar.HOUR_OF_DAY) == cal.get(Calendar.HOUR_OF_DAY)
					&& today.get(Calendar.MINUTE) == cal.get(Calendar.MINUTE)) {
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

						SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
						try {
							Date date1 = sdf.parse(sdf.format(end_Time));
							objectHashMap.put(PARAMETER.END_DATE, date1);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
						try {
							Date time1 = sdftime.parse(sdftime.format(end_Time));
							objectHashMap.put(PARAMETER.END_TIME, time1);
						} catch (ParseException e) {
							e.printStackTrace();
						}

					} else {
						context.displayMessage("ERROR_START_BEFORE_END");
					}
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
					try {
						Date date1 = sdf.parse(sdf.format(end_Time));
						objectHashMap.put(PARAMETER.END_DATE, date1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
					try {
						Date time1 = sdftime.parse(sdftime.format(end_Time));
						objectHashMap.put(PARAMETER.END_TIME, time1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			} catch (NullPointerException n) {
				if (parseNatty(endDate + " " + endTime) != null) {
					end_Time = parseNatty(endDate + " " + endTime);
					SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
					try {
						Date date1 = sdf.parse(sdf.format(end_Time));
						objectHashMap.put(PARAMETER.END_DATE, date1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
					try {
						Date time1 = sdftime.parse(sdftime.format(end_Time));
						objectHashMap.put(PARAMETER.END_TIME, time1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

		} else {
			context.displayMessage("PARAM_SUBTITLE");
			context.displayMessage("PARAM_DEADLINE_TIME");
		}

	}

	private static void updateDeadlineTimeHashMap(String deadlineTime, HashMap<PARAMETER, Object> objectHashMap,
			COMMAND_TYPE command) {
		Date timeOfDeadline = null;
		if ((timeOfDeadline = parseNatty(deadlineTime)) == null) {
			timeOfDeadline = validTimeFormat(deadlineTime);
		}
		Calendar cal = Calendar.getInstance();
		if (timeOfDeadline != null) {
			cal.setTime(timeOfDeadline);
			Calendar today = Calendar.getInstance();
			if (cal.get(Calendar.HOUR_OF_DAY) == today.get(Calendar.HOUR_OF_DAY)
					&& cal.get(Calendar.MINUTE) == today.get(Calendar.MINUTE)) {
				cal = setEndTime(cal);
				timeOfDeadline = cal.getTime();
			}
			if (deadlineTime.contains("week") || deadlineTime.contains("wk")) {
				cal.setTime(timeOfDeadline);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				cal = setEndTime(cal);
				timeOfDeadline = cal.getTime();
			} else if (isMonthWord(deadlineTime) && command != COMMAND_TYPE.ADD_TASK) {
				cal.setTime(timeOfDeadline);
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
				cal = setEndTime(cal);
				timeOfDeadline = cal.getTime();
			} else if (deadlineTime.contains("yr") || deadlineTime.contains("year")) {
				cal.setTime(timeOfDeadline);
				cal.set(Calendar.MONTH, 11);
				cal.set(Calendar.DAY_OF_MONTH, 31);
				cal = setEndTime(cal);
				timeOfDeadline = cal.getTime();
			}

			Calendar current = Calendar.getInstance();
			if (cal.getTime().before(current.getTime())) {
				context.displayMessage("WARNING_DEADLINE_BEFORE_NOW");
			}

			objectHashMap.put(PARAMETER.DEADLINE_TIME, timeOfDeadline);

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

	private static void updateHashMapForDisplay(String keyDate, Calendar startcal, Calendar endcal,
			HashMap<PARAMETER, Object> objectHashMap) {
		if (keyDate.contains("week") || keyDate.contains("wk")) {
			// Calendar cal = Calendar.getInstance();
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

	}

	/**
	 * Given user query strings, determine the appropriate calendar view
	 * 
	 * @param deadline
	 * @param keyDate
	 */
	private static void updateContextDisplay(String deadline, String keyDate) {
		String query;
		if (keyDate != null) {
			query = keyDate;
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

	private static boolean isValidString(String string) {
		if (string == null || string.trim().equals("")) {
			return false;
		}
		return true;
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

	private static int countOccurence(String string, char character) {
		int counter = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == character) {
				counter++;
			}
		}
		return counter;
	}

	/*********************************************************************
	 * TIME HANDLING *
	 * 
	 * *
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

	@SuppressWarnings("unused")
	private static boolean tryToParse(String dateString, SimpleDateFormat df) {
		Date date;
		try {
			date = df.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

}
