/**
 *  @@author A0118772
 *  
 *  Represents the test for the Validator class
 * 
 */

package test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import Task.COMMAND_TYPE;
import Task.PARAMETER;
import Task.Validator;


public class ValidatorTest {

	private static SimpleDateFormat fullDateFormat      = new SimpleDateFormat("EEE, dd MMM, yyyy HHmm");
	private static SimpleDateFormat dateFormat 			= new SimpleDateFormat("dd-MM-yyyy");
	private static SimpleDateFormat timeFormat 			= new SimpleDateFormat("HHmm");
	
	private String differentInputCombinations[][] = {
	/*		Date		   Start Time   End Time	 	Deadline Time	*/
	/*1*/	{null,		   null,		 null,			null			},
	/*2*/	{null,		   null,		 null,			newSDate(30)	},
	/*3*/	{null,		   null,		 newSDate(20), 	null			},
	/*4*/	{null,		   null,		 newSDate(20), 	newSDate(30)	},
	/*5*/	{null,		   newSDate(10), null,			null			},
	/*6*/	{null,		   newSDate(10), null,			newSDate(30)	},
	/*7*/	{null,		   newSDate(10), newSDate(20), 	null			},
	/*8*/	{null,		   newSDate(10), newSDate(20), 	newSDate(30)	},
	/*9*/	{newSDate(0),  null,		 null,			null			},
	/*10*/	{newSDate(0),  null,		 null,			newSDate(30)	},
	/*11*/	{newSDate(0),  null,		 newSDate(20), 	null			},
	/*12*/	{newSDate(0),  null,		 newSDate(20), 	newSDate(30)	},
	/*13*/	{newSDate(0),  newSDate(10), null,			null			},
	/*14*/	{newSDate(0),  newSDate(10), null,			newSDate(30)	},
	/*15*/	{newSDate(0),  newSDate(10), newSDate(20), 	null			},
	/*16*/	{newSDate(0),  newSDate(10), newSDate(20), 	newSDate(30)	}
	};
	
	private Date differentOutputCombinationsAdd[][] = {
	/*		Start Date	  Start Time   End Date	    End Time	  Deadline Date	Deadline Time	*/
	/*1*/	{null,		  null,		   null,		null,		  null,			null			},
	/*2*/	{null,		  null,		   null,		null,		  newDate(30),	newDate(30)		},
	/*3*/	{newDate(19), newDate(19), newDate(20), newDate(20),  null,			null			},
	/*4*/	{null,		  null,		   null,		null,		  null,			null			},
	/*5*/	{newDate(10), newDate(10), newDate(11), newDate(11),  null,			null			},
	/*6*/	{null,		  null,		   null,		null,		  null,			null			},
	/*7*/	{newDate(10), newDate(10), newDate(20), newDate(20),  null,			null			},
	/*8*/	{newDate(10), newDate(10), newDate(20), newDate(20),  newDate(30),	newDate(30)		},
	/*9*/	{newDate(0),  newDate(1),  newDate(0),  newDate(2),   null,			null			},
	/*10*/	{null,		  null,		   null,		null,		  newDate(0),   newDate(30)		},
	/*11*/	{newDate(0),  newDate(19), newDate(0),  newDate(20),  null,			null			},
	/*12*/	{null,		  null,		   null,		null,		  null,			null			},
	/*13*/	{newDate(0),  newDate(10), newDate(0),  newDate(11),  null,			null			},
	/*14*/	{null,		  null,		   null,		null,		  null,			null			},
	/*15*/	{newDate(0),  newDate(10), newDate(0),  newDate(20),  null,			null			},
	/*16*/	{newDate(0),  newDate(10), newDate(0),  newDate(20),  newDate(30),	newDate(30)		},
	};
	
	//TODO:
	private Date differentOutputCombinationsEdit[][] = {
	/*		Start Date	  Start Time   End Date	    End Time	  Deadline Date	Deadline Time	*/
	/*1*/	{null,		  null,		   null,		null,		  null,			null			},
	/*2*/	{null,		  null,		   null,		null,		  newDate(30),	newDate(30)		},
	/*3*/	{newDate(19), newDate(19), newDate(20), newDate(20),  null,			null			},
	/*4*/	{null,		  null,		   null,		null,		  null,			null			},
	/*5*/	{newDate(10), newDate(10), newDate(11), newDate(11),  null,			null			},
	/*6*/	{null,		  null,		   null,		null,		  null,			null			},
	/*7*/	{newDate(10), newDate(10), newDate(20), newDate(20),  null,			null			},
	/*8*/	{newDate(10), newDate(10), newDate(20), newDate(20),  newDate(30),	newDate(30)		},
	/*9*/	{newDate(0),  newDate(1),  newDate(0),  newDate(2),   null,			null			},
	/*10*/	{null,		  null,		   null,		null,		  newDate(0),   newDate(30)		},
	/*11*/	{newDate(0),  newDate(19), newDate(0),  newDate(20),  null,			null			},
	/*12*/	{null,		  null,		   null,		null,		  null,			null			},
	/*13*/	{newDate(0),  newDate(10), newDate(0),  newDate(11),  null,			null			},
	/*14*/	{null,		  null,		   null,		null,		  null,			null			},
	/*15*/	{newDate(0),  newDate(10), newDate(0),  newDate(20),  null,			null			},
	/*16*/	{newDate(0),  newDate(10), newDate(0),  newDate(20),  newDate(30),	newDate(30)		},
	};
	
	private String newSDate(int feed) {
		switch (feed){
			case 0:  return "Mon, 7 Sep, 2015 0000";
			case 1:  return "Mon, 7 Sep, 2015 1200";
			case 2:  return "Mon, 7 Sep, 2015 2359";
			case 10: return "Wed, 9 Sep, 2015 0800";
			case 11: return "Wed, 9 Sep, 2015 0900";
			case 19: return "Fri, 11 Sep, 2015 1300";
			case 20: return "Fri, 11 Sep, 2015 1400";
			case 30: return "Sun, 13 Sep, 2015 2359";
			default:
				assert(false);
				return null;
		}
	}
	
	private Date newDate(int feed) {
		Date returnDate = null;
		try{
			returnDate = fullDateFormat.parse(newSDate(feed));
		} catch(ParseException e){
			assert(false);
		}
		return returnDate;
	}
	
	private String getTime(Date dateTime) {
		return dateTime == null ? null:timeFormat.format(dateTime);
	}

	private String getDate(Date dateTime) {
		return dateTime == null ? null:dateFormat.format(dateTime);
	}

	@Test
	public void testAllInputOutputAdd(){
		
		for(int i = 0; i < differentInputCombinations.length; i++){
			HashMap<PARAMETER, String> testHashMap = new HashMap<PARAMETER, String>();
			testHashMap.put(PARAMETER.DESC, "Test" + i);
			testHashMap.put(PARAMETER.DATE, 			differentInputCombinations[i][0]);
			testHashMap.put(PARAMETER.START_TIME, 		differentInputCombinations[i][1]);
			testHashMap.put(PARAMETER.END_TIME, 		differentInputCombinations[i][2]);
			testHashMap.put(PARAMETER.DEADLINE_TIME, 	differentInputCombinations[i][3]);
			
			HashMap<PARAMETER, Object> returnedHashMap = new HashMap<PARAMETER, Object>();
			returnedHashMap = Validator.getObjectHashMap(testHashMap,COMMAND_TYPE.ADD_TASK );
			
			assertEquals(returnedHashMap.get(PARAMETER.DESC), "Test" + i);
			assertEquals(getDate((Date)returnedHashMap.get(PARAMETER.START_DATE)), 	  getDate(differentOutputCombinationsAdd[i][0]));
			assertEquals(getTime((Date)returnedHashMap.get(PARAMETER.START_TIME)), 	  getTime(differentOutputCombinationsAdd[i][1]));
			assertEquals(getDate((Date)returnedHashMap.get(PARAMETER.END_DATE)), 	  getDate(differentOutputCombinationsAdd[i][2]));
			assertEquals(getTime((Date)returnedHashMap.get(PARAMETER.END_TIME)), 	  getTime(differentOutputCombinationsAdd[i][3]));
			assertEquals(getDate((Date)returnedHashMap.get(PARAMETER.DEADLINE_DATE)), getDate(differentOutputCombinationsAdd[i][4]));
			assertEquals(getTime((Date)returnedHashMap.get(PARAMETER.DEADLINE_TIME)), getTime(differentOutputCombinationsAdd[i][5]));
		}
	}
	
	@Test
	public void testUserSpecifiedTimeOnly(){
		assertEquals(true,Validator.userSpecifiedTimeOnly("2300"));
		assertEquals(true,Validator.userSpecifiedTimeOnly("12:06"));
		assertEquals(true,Validator.userSpecifiedTimeOnly("2800"));
		assertEquals(true,Validator.userSpecifiedTimeOnly("6pm"));
		assertEquals(true,Validator.userSpecifiedTimeOnly("12.6"));
	}
	
	@Test
	public void userSpecifiedDateAndTime(){
		assertEquals(true,Validator.userSpecifiedDateAndTime("wed 2300"));
		assertEquals(true,Validator.userSpecifiedDateAndTime("today 12:06"));
		assertEquals(false,Validator.userSpecifiedDateAndTime("12/2 2800"));
		assertEquals(true,Validator.userSpecifiedDateAndTime("this day 6pm"));
		assertEquals(false,Validator.userSpecifiedDateAndTime("12.6"));
		assertEquals(false,Validator.userSpecifiedDateAndTime("1600"));
	}
	
}