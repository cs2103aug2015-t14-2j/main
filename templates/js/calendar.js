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
            	titleFormat: 'MMMM YYYY'
            	// other view-specific options here
        	},
        	agendaWeek: {
        		titleFormat: 'MMM D',
        		allDaySlot : false
        	},
        	agendaDay: {
        		titleFormat: 'MMMM D, YYYY',
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
        
        eventRender: function(event, element) { 
            element.find('.fc-title').append("<p>" + "ID : " + event.id + "</p>");
        },
        titleRangeSeparator : '\u2014'
    })
});