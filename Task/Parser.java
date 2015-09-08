package Task;

import java.util.HashMap;

import Task.TaskHandler.COMMAND_TYPE;

public class Parser {
	// Specify private and static variables here 
	
	// This is optional, you can implement using other ways you like
	enum PARAMETER {
		DESC,
		VENUE,
		START_DATE,
		END_DATE,
		START_TIME,
		END_TIME,
		DEADLINE,
		PRIORITY,
		REMIND_TIME
	};
	
	private String[] keywords = {"do", "at", "to", "from", "by" ,"with"};
	
	// Constructor
	public Parser () {
		
	}
	
/*	 Given a command e.g. ADD and a string user input, return a HashMap of key => values, where key is parameter as specified in our user manual. For example
 *	 Function call with COMMAND_TYPE = add, query = "Do attend CS2103 lecture at NUS LT3 on 23rd Dec 2015 from 1000 to 1200"
 *	 Returns : "description" : "attend CS2103 lecture"
 *	 		   "venue" : "NUS LT3"
 *	 		   "date"  : "23rd Dec 2015"
 *	           "startTime" : "1000"
 *	           "endTime" : "1200"
 */	
	public HashMap<PARAMETER, String> getValuesFromInput(COMMAND_TYPE command, String query) {
		return new HashMap<PARAMETER, String>();
	}
}
