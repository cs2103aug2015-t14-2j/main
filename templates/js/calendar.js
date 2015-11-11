//@@author A0097689
$(document).ready(function() {
    // page is now ready, initialize the calendar...
 
 	// Attach toast messages behaviour
 	window.setTimeout(removeMessages, 3000);

	var data = JSON.parse(document.getElementById("jsonData").innerHTML);
	// Determine view, default month
	if (document.getElementById("view")!=null){
		var view = document.getElementById("view").innerHTML;
	};
	
	if (view==null) {
		view = 'month';
	}
	var defaultDateString = document.getElementById("default_date").innerHTML;
	var lastTaskId        = document.getElementById("lastTaskId").innerHTML;	// Last Task Id for styling purposes
	var events            = [];
	
	for (var i=0; i < data.Tasks.length; i++) {
		eventObj = data.Tasks[i];
		var calendarEvent = {};
		
		var borderColor;
		var allDay        = isAllDay(eventObj);
		var isLastUpdated = false;
		if (eventObj.taskId == lastTaskId) {
			isLastUpdated = true;
		}

		if (eventObj.startTime!=null && eventObj.endTime!=null) {
			incrementEventCount(eventObj.startTime);

			calendarEvent["id"]              = eventObj.taskId;
			calendarEvent["title"]           = eventObj.description;
			calendarEvent["venue"]           = eventObj.venue;
			calendarEvent["start"]           = eventObj.startTime;
			calendarEvent["end"]             = eventObj.endTime;
			calendarEvent["deadline"]        = eventObj.deadline;
			calendarEvent["allDay"]          = allDay;
			calendarEvent["isDeadline"]      = false;
			calendarEvent["backgroundColor"] = 'rbg(59, 145, 173';
			calendarEvent["borderColor"]     = 'black';
			calendarEvent["textColor"]       = 'white';

			if (isLastUpdated) {
				calendarEvent["isLastUpdated"] = true;
			} else {
				calendarEvent["isLastUpdated"] = false;
			}

			events.push(calendarEvent);

		} else if (eventObj.deadline!=null) {
			incrementEventCount(eventObj.deadline);

			calendarEvent["id"]              = eventObj.taskId;
			calendarEvent["title"]           = eventObj.description;
			calendarEvent["venue"]           = eventObj.venue;
			calendarEvent["start"]           = eventObj.deadline;
			calendarEvent["allDay"]          = false;
			calendarEvent["isDeadline"]      = true;
			calendarEvent["backgroundColor"] = 'rgb(239, 131, 84)';
			calendarEvent["borderColor"]     = 'black';
			calendarEvent["textColor"]       = 'white';

			if (isLastUpdated) {
				calendarEvent["isLastUpdated"] = true;
			} else {
				calendarEvent["isLastUpdated"] = false;
			}

			events.push(calendarEvent);
		} else {
			// Floating tasks, do nothing, won't show up on calendar
		}
	}

    $('#calendar').fullCalendar({
        // put your options and callbacks here
    	displayEventEnd : true,
    	header: {
      		left: 'prev,next today',
        	center: 'title',
        	right: 'month,agendaWeek,agendaDay'
    	},
    	defaultView : view,
    	defaultDate : moment(defaultDateString),
    	views: {
        	month: { // name of view
            	titleFormat: 'MMMM YYYY',
            	eventLimit : 4,
        	},
        	agendaWeek: {
        		titleFormat: 'MMM D',
        		eventLimit : 10,
        		allDaySlot : true
        	},
        	agendaDay: {
        		titleFormat: 'MMMM D, YYYY',
        		eventLimit : 10,
        		allDaySlot : true
        	}
    	},
    	height : 640,
        eventSources : [

	        {
		        events : events,
		        borderColor      : 'black',
		        textColor        : 'white'
	        	
	        }  
        ],
        
        // Render expanded or collapsed view of events based on view
        // and number of events on a single day
        eventRender: function(event, element) {
			var start    = moment(event.start);
			var end      = moment(event.end);
			var duration = end.diff(start, 'hours');

			// Thicken border and set color for last updated event
			if (event.isLastUpdated) {
				$(element).css("border-width", "4px");
				$(element).css("border-color", "#5FAD41");
			}

        	if (view == 'month') {
        		// Collapsed view
        		if (numberOfEventsInDay(event.start) > 1) {
        			
        			element.find('.fc-title').html(
	            		"ID:" + event.id + "," + event.title 
	            	);
        		} else {
        			// Expanded event view for month
	            	element.find('.fc-title').html(
	            		"<p>ID   : " + event.id    + "</p>" +
	            		"<p>Desc : " + event.title + "</p>" 
	            	);
        		}
        	} else if (view == 'agendaDay') {
        		// Expanded view, determine horizontal or vertical layout based on duration
        		var horizontal = true;
        		if (event.end != undefined && (duration >= 1)) {
					horizontal = false;
				}
				if (horizontal) {
					element.find('.fc-title').html(
						"<div style='float:left;width:480px'>" +
							"<span style='float:left;width:20%'>ID          :" + event.id    + "</span>" +
							"<span style='float:left;width:40%'>Description :" + event.title + "</span>" +
							"<span style='float:left;width:40%'>Venue       :" + nullOrString(event.venue) + "</span>" +
						"</div>"
						);
				} else {
					element.find('.fc-title').html( 
		            	"<p>ID    : " + event.id + "</p>" +
		            	"<p>Desc  : " + event.title + "</p>" +
		            	"<p>Venue : " + nullOrString(event.venue) + "</p>");
				}
        	} else if (view == 'agendaWeek') {
        		// Expanded view, only vertical layout
        		element.find('.fc-title').html( 
	            	"<p>ID    : " + event.id    + "</p>" +
	            	"<p>Desc  : " + event.title + "</p>" +
	            	"<p>Venue : " + nullOrString(event.venue) + "</p>");
        	} else {
        		// Default
				element.find('.fc-title').html( 
	            	"<p>ID : " + event.id + "</p>");
        	}
        },
        titleRangeSeparator : '\u2014'
    });
});

// Hashtable of days followed by number of events in one day
var eventCount = {};
// Hashtable of days followed by number of events, called by eventRender
// function to retrieve the order of that event on that day
var eventOrder = {};

var incrementEventCount = function(dateString) {
	var dayTime = moment.utc(dateString).startOf('day');
	if (!eventCount.hasOwnProperty(dayTime.toISOString())) {
		eventCount[dayTime.toISOString()] = 0;
	}

	eventCount[dayTime.toISOString()]++;
};

var numberOfEventsInDay = function(dateString) {
	var dayTime = moment(dateString).startOf('day');
	var key = dayTime.toISOString();
	return eventCount[key];
};

// Fade out messages after 2 secs
var removeMessages = function() {
	$(".alert:not(.alert-info)").each(function() {
		$(this).fadeOut(2000, function() {
			$(this).remove();
		});
	});
};

// Given an event, return true if duration is greater or equal to 1 day
// else return false
var isAllDay = function(eventObj) {
	var start = moment(eventObj.startTime);
	var end   = moment(eventObj.endTime);
	var dayDuration   = (24 * 60 * 60 * 1000) - 2*60000;
	console.log(eventObj.taskId + " : " + (end-start) + " : " + dayDuration);

	if ((end - start) >= dayDuration) {

		return true;
	}
	return false;
};

// Returns empty string for null field, else return the field itself
var nullOrString = function(field) {
	if (field == null) {
		return "";
	} else {
		return field;
	}
};