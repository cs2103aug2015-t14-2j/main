/**
 * @@author:A0009586
 */
package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import Task.Period;
import Task.Task;
import Task.TaskDeadlineEdit;
import Task.TaskDescEdit;
import Task.TaskDoneEdit;
import Task.TaskListEdit;
import Task.TaskPeriodEdit;
import Task.TaskVenueEdit;

public class EditTests {
	
	Task task = new Task(1,"Description","Venue");

	//DEADLINE EDIT//
	@Test
	public void taskDeadlineTest() {
		Date deadlineDate = new Date();
		task.setDeadline(deadlineDate);
		
		TaskDeadlineEdit edit = new TaskDeadlineEdit(task, null, deadlineDate);
		
		assertEquals(deadlineDate,
				task.getDeadline());
		
		//UNDO
		edit.undo();
		
		assertEquals(null,
				task.getDeadline());
		
		//REDO
		edit.redo();
		
		assertEquals(deadlineDate,
				task.getDeadline());		
		
	}
	
	//PERIOD EDIT//
	@Test
	public void taskPeriodEditTest() {
		Date startTime = new Date();
		Date endTime = new Date();
		Period period = new Period(startTime, endTime);
		
		task.setPeriod(period);
		
		TaskPeriodEdit edit = new TaskPeriodEdit(task, null, period);
		
		assertEquals(period,
				task.getPeriod());
		
		//UNDO
		edit.undo();
		
		assertEquals(null,
				task.getPeriod());
		
		//REDO
		edit.redo();
		
		assertEquals(period,
				task.getPeriod());
		
	}
	
	//DESC EDIT//
	@Test
	public void taskDescEditTest() {
		
		String description = "Sample Description";
		
		task.setDescription(description);
		
		TaskDescEdit edit = new TaskDescEdit(task, null,description);
		
		assertEquals(description,
				task.getDescription());
		
		//UNDO
		edit.undo();
		
		assertEquals(null,
				task.getDescription());
		
		//REDO
		edit.redo();
		
		assertEquals(description,
				task.getDescription());
		
	}
	
	//VENUE EDIT//
	@Test
	public void taskVenueEditTest() {
		
		String venue = "Sample Description";
		
		task.setVenue(venue);
		
		TaskVenueEdit edit = new TaskVenueEdit(task, null,venue);
		
		assertEquals(venue,
				task.getVenue());
		
		//UNDO
		edit.undo();
		
		assertEquals(null,
				task.getVenue());
		
		//REDO
		edit.redo();
		
		assertEquals(venue,
				task.getVenue());
		
	}
	
	//DONE EDIT//
	@Test
	public void taskDoneEditTest() {
		
		boolean done = true;
		
		task.setDone(done);
		
		TaskDoneEdit edit = new TaskDoneEdit(task, !done, done);
		
		assertEquals(done,
				task.isDone());
		
		//UNDO
		edit.undo();
		
		assertEquals(!done,
				task.isDone());
		
		//REDO
		edit.redo();
		
		assertEquals(done,
				task.isDone());
		
	}
	
	//TASKLIST EDIT//
	@Test
	public void taskListEditTest() {
		
		boolean isAnAdd = true;
		
		ArrayList<Task> taskList = new ArrayList<Task>();
		taskList.add(task);
		Task task2 = new Task(2,"Description2","Venue2");
		taskList.add(task2);
		Task task3 = new Task(3,"Description3","Venue3");
		taskList.add(task3);
		Task task4 = new Task(4,"Description4","Venue4");
		taskList.add(task4);
		Task task5 = new Task(5,"Description5","Venue5");
		
		TaskListEdit edit     = new TaskListEdit(task5, taskList, 5, 6,isAnAdd);
		
		taskList.add(task5);
		
		assertEquals(5,
				taskList.size());
		
		//UNDO ONE
		edit.undo();
		
		assertEquals(4,
				taskList.size());
		
		//REDO ONE
		edit.redo();
		
		assertEquals(5,
				taskList.size());
		
		//UNDO MULTIPLE
		edit     = new TaskListEdit(taskList, 6, 6,false);
		while(taskList.size() != 0){
			task = taskList.get(0);
			taskList.remove(task);
		}
		
		assertEquals(0,
				taskList.size());
		
		edit.undo();
		
		assertEquals(5,
				taskList.size());
		
		//REDO MULTIPLE
		edit.redo();
		
		assertEquals(0,
				taskList.size());
		
	}
	
	//COMPOUNDED EDIT//
	//TESTED THOUGH TASK HANDLER

}
