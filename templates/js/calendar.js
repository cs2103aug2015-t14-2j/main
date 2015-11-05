$(document).ready(function() {
    // page is now ready, initialize the calendar...
 
	var data = JSON.parse(document.getElementById("jsonData").innerHTML);
	var events = [];
	for (var i=0; i < data.Tasks.length; i++) {
		eventObj = data.Tasks[i];
		if (eventObj.startTime!=null && eventObj.endTime!=null) {
			events.push({
				title  : eventObj.description,
				venue  : eventObj.venue,
				id     : eventObj.taskId,
				start  : eventObj.startTime,
				end    : eventObj.endTime,
				allDay : false,
				isDeadline: false
			});
		} else if (eventObj.deadline!=null) {
			events.push({
				title  : eventObj.description,
				start  : eventObj.deadline,
				id     : eventObj.taskId,
				venue  : eventObj.venue,
				allDay : false,
				isDeadline: true				
			})
		} else {
			// Do nothing	
		}
	}

    $('#calendar').fullCalendar({
        // put your options and callbacks here
    	displayEventEnd : true,
        eventSources : [

	        {
		        events : events,
		        backgroundColor  : 'rgb(59,145,173)',
		        borderColor      : 'black',
		        textColor: 'white'
	        	
	        }  
        ],
        
        eventRender: function(event, element) { 
            element.find('.fc-title').append("<p>" + "ID : " + event.id + "</p>");
        }  
        
    })

});