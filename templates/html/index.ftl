<html>
	<head></head>
	<body>
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="col-md-12">
					<div class="alert alert-success">
						<#list success_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					<div class="alert alert-warning">
						<#list warning_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					<div class="alert alert-danger">
						<#list error_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					<div class="alert alert-info">
						<#list help_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					<div class="alert alert-warning">
						<#list param_messages as message>
							<p>${message}</p>
						</#list>
					</div>
					<#list taskList as task>
						<div>
							<div>
								<p>Task ID: ${task.taskId}</p>
							</div>
							<div>
								<p>Description: ${task.description}<#if task.isDone()><span class="label label-success">Completed</span></#if></p>
							</div>
							<div>
								<p>Venue: ${task.venue!"Not specified"}</p>
							</div>
							</div>
							<div>
								<p>From: ${(task.period.startTime)!"NULL"}</p>
							</div>
							<div>
								<p>To: ${(task.period.endTime)!"NULL"}<#if task.isHasEnded()><span class="label label-info">Ended</span></#if></p>
							</div>
							<div>
								<p>Deadline: ${(task.deadline?datetime)!"NULL"}<#if task.isPastDeadline()><span class="label label-danger">Past due</span></#if></p>
							</div>
						</div>
					</#list>
				</div>
			</div>
		</div>
	</body>
</html>