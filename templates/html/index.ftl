<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<link href="../css/bootstrap.min.css" rel="stylesheet">
		<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.4.0/fullcalendar.min.css' />
		<script src="../js/jQuery_v1.11.2.js"></script>
		<script src="../js/bootstrap.js"></script>
		<script src='https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.6/moment.min.js'></script>
		<script src='https://cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.4.0/fullcalendar.min.js'></script>
		<script src="../js/calendar.js?"></script>
	</head>
	<body style="margin-top:10px">
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="col-xs-12">
					<#if success_messages?has_content>
					<div class="alert alert-success">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<#list success_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#if warning_messages?has_content>
					<div class="alert alert-warning">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<#list warning_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#if error_messages?has_content>
					<div class="alert alert-danger">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<#list error_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#if help_messages?has_content>
					<div class="alert alert-info">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<#list help_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#if param_messages?has_content>
					<div class="alert alert-warning">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<#list param_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#if view_messages?has_content>
					<div style="display:none">
						<#list view_messages as message>
							<p id="view">${message}</p>
						</#list>
					</div>
					</#if>
					<div style="display:none" id="default_date">${default_date!.now?date}</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="col-xs-8">
					<div style="display:none" id="jsonData">${jsonData}</div>
					<div id="calendar"></div>
				</div>
				<div class="col-xs-4" style="height:660px;overflow:scroll">
					<#list taskList as task>
						<div id="task" class="panel panel-primary">
							<div class="panel panel-heading">
								<p>${task.description}<#if task.isDone()>&nbsp;&nbsp;&nbsp;<span class="label label-success">Completed</span><#elseif task.isPastDeadline()>&nbsp;&nbsp;&nbsp;<span class="label label-danger">Past due</span><#else></#if><span class="badge pull-right">ID: ${task.taskId}</span></p>
							</div>
							<table class="table table-hover table-condensed">
								<tbody>
									<tr>
										<td style="width:20%">Venue: </td>
										<td><#if task.venue??>${task.venue}<#else><span class="label label-default">Empty</span></#if></td>
									</tr>
									<tr>
										<td style="width:20%">Period:</td>
										<td><#if task.period??>${(task.period.startDateTime?datetime)} - ${(task.period.endDateTime?datetime)}<#else><span class="label label-default">Empty</#if></td>
									</tr>
									<tr>
										<td style="width:20%">Deadline: </td>
										<td><#if task.deadline??>${(task.deadline?datetime)}<#else><span class="label label-default">Empty</span></#if></td>
									</tr>
								</tbody>
							</table>
						</div>
					</#list>
				</div>
			</div>
		</div>
	</body>
</html>