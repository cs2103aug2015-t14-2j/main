$(document).ready(function() {
    // page is now ready, initialize the calendar...
 
	var data = JSON.parse(document.getElementById("jsonData").innerHTML);
	// Determine view, default month
	if (document.getElementById("view")!=null){
		var view = document.getElementById("view").innerHTML;
	};
	
	if (view==null) {
		view = 'month';
	}
	var defaultDateString = document.getElementById("default_date").innerHTML;

	var events = [];
	for (var i=0; i < data.Tasks.length; i++) {
		eventObj = data.Tasks[i];
		if (eventObj.startTime!=null && eventObj.endTime!=null) {
			incrementEventCount(eventObj.startTime);
			events.push({
				id     : eventObj.taskId,
				title  : eventObj.description,
				venue  : eventObj.venue,
				start  : eventObj.startTime,
				end    : eventObj.endTime,
				deadline : eventObj.deadline,
				allDay : false,
				isDeadline: false,
				backgroundColor : 'rgb(59,145,173)',
				borderColor : 'black',
				textColor : 'white'
			});
		} else if (eventObj.deadline!=null) {
			incrementEventCount(eventObj.deadline);
			events.push({
				id     : eventObj.taskId,
				title  : eventObj.description,
				start  : eventObj.deadline,
				venue  : eventObj.venue,
				allDay : false,
				isDeadline: true,
				backgroundColor : 'rgb(239,131,84)',
				borderColor: 'black',
				textColor : 'white'
			});
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
        		allDaySlot : false
        	},
        	agendaDay: {
        		titleFormat: 'MMMM D, YYYY',
        		eventLimit : 10,
        		allDaySlot : false
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

        	if (view == 'month') {
        		// Collapsed view
				var badgeClass = (event.isDeadline) ? 'badge-danger' : 'badge-primary'; 
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
							"<span style='float:left;width:40%'>Venue       :" + event.venue + "</span>" +
						"</div>"
						);
				} else {
					element.find('.fc-title').html( 
		            	"<p>ID    : " + event.id + "</p>" +
		            	"<p>Desc  : " + event.title + "</p>" +
		            	"<p>Venue : " + event.venue + "</p>");
				}
        	} else if (view == 'agendaWeek') {
        		// Expanded view, only vertical layout
        		element.find('.fc-title').html( 
	            	"<p>ID    : " + event.id    + "</p>" +
	            	"<p>Desc  : " + event.title + "</p>" +
	            	"<p>Venue : " + event.venue + "</p>");
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
}

var numberOfEventsInDay = function(dateString) {
	var dayTime = moment(dateString).startOf('day');
	var key = dayTime.toISOString();
	return eventCount[key];
}