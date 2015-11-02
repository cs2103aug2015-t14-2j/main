<html>
	<head>
		<link href="../css/bootstrap.min.css" rel="stylesheet">
	</head>
	<body style="margin-top:30px">
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="col-md-12">
					<#if success_messages?has_content>
					<div class="alert alert-success">
						<#list success_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#if warning_messages?has_content>
					<div class="alert alert-warning">
						<#list warning_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#if error_messages?has_content>
					<div class="alert alert-danger">
						<#list error_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#if help_messages?has_content>
					<div class="alert alert-info">
						<#list help_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#if param_messages?has_content>
					<div class="alert alert-warning">
						<#list param_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					</#if>
					<#list taskList as task>
						<ul class="list-group">
							<li class="list-group-item active"><p>ID        : ${task.taskId}</p></li>
							<li class="list-group-item">Description: ${task.description}<#if task.isDone()>&nbsp;&nbsp;&nbsp;<span class="label label-success">Completed</span></#if></li>
							<li class="list-group-item">Venue      : ${task.venue!"Not specified"}</li>
							<li class="list-group-item">From       : ${(task.period.startTime)!"NULL"}</li>
							<li class="list-group-item">To         : ${(task.period.endTime)!"NULL"}<#if task.isHasEnded()>&nbsp;&nbsp;&nbsp;<span class="label label-info">Ended</span></#if></li>
							<li class="list-group-item">Deadline   : ${(task.deadline?datetime)!"NULL"}<#if task.isPastDeadline()>&nbsp;&nbsp;&nbsp;<span class="label label-danger">Past due</span></#if></li>
						</ul>
					</#list>
				</div>
			</div>
		</div>
	</body>
</html>