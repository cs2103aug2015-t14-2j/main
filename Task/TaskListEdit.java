package Task;

import javax.swing.undo.UndoableEdit;
import java.util.ArrayList;

public class TaskListEdit extends UndoableSignificantEdit implements UndoableEdit {
	protected int oldCurrentId;
	protected int newCurrentId;
	protected boolean isAdd;			// TRUE for add task, FALSE for delete task
	protected Task task;
	protected ArrayList<Task> taskList;

	TaskListEdit(Task _task, ArrayList<Task> _taskList, int _oldCurrentId, int _newCurrentId, boolean _isAdd) {
		super();
		task = _task;
		taskList = _taskList;
		newCurrentId = _newCurrentId;
		oldCurrentId = _oldCurrentId;
		isAdd       = _isAdd;
		
	}

	@Override
	public String getUndoPresentationName() {
		return "Undo add task.";
	}
	
	@Override
	public String getRedoPresentationName() {
		return "Redo add task.";
	}
	
	@Override
	public void undo() {
		super.undo();
		if (isAdd) {
			taskList.remove(task);
			System.out.println("Task removed:");

		} else {
			taskList.add(task);
			System.out.println("Task added:");
		}
		TaskHandler.setCurrentTaskId(oldCurrentId);
	}
	
	@Override
	public void redo() {
		super.redo();
		if (isAdd) {
			taskList.add(task);
			System.out.println("Task added:");
		} else {
			taskList.remove(task);
			System.out.println("Task removed:");
			
		}
		TaskHandler.setCurrentTaskId(newCurrentId);
	}
}
