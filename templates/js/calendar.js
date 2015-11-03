$(document).ready(function() {
    // page is now ready, initialize the calendar...
 
	var data = JSON.parse(document.getElementById("jsonData").innerHTML);
	var events = [];
	for (var i=0; i < data.Tasks.length; i++) {
		eventObj = data.Tasks[i];
		if (eventObj.startTime!=null && eventObj.endTime!=null) {
			events.push({
				title  : eventObj.description,
				start  : eventObj.startTime,
				end    : eventObj.endTime,
				allDay : false
			});
		}
	}
	// console.log(events);
	moment().format('ddd, DD MMM, YYYY HHmm');

    $('#calendar').fullCalendar({
        // put your options and callbacks here
    	displayEventEnd : true,
        eventSources : [
        	
	        {
		        events : [events],
		        color  : 'black',
		        textColor: 'yellow'
	        	
	        }

        ]
    })

});