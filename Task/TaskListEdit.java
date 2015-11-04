package Task;

import javax.swing.undo.UndoableEdit;
import java.util.ArrayList;
import java.util.Iterator;

public class TaskListEdit extends UndoableSignificantEdit implements UndoableEdit {
	protected int oldCurrentId;
	protected int newCurrentId;
	protected ArrayList<Task> taskList;
	protected ArrayList<Task> taskListContents;
	protected boolean isAdd;
	
	TaskListEdit(Task task, ArrayList<Task> _taskList, int _oldCurrentId, int _newCurrentId,boolean _isAdd) {
		super();
		taskList = _taskList;
		taskListContents = new ArrayList<Task>();
		taskListContents.addAll(taskList);
		taskListContents.add(task);
		newCurrentId = _newCurrentId;
		oldCurrentId = _oldCurrentId;
		isAdd = _isAdd;
	}
	
	TaskListEdit(ArrayList<Task> _taskList, int _oldCurrentId, int _newCurrentId,boolean _isAdd) {
		super();
		taskList = _taskList;
		taskListContents = new ArrayList<Task>();
		taskListContents.addAll(taskList);
		newCurrentId = _newCurrentId;
		oldCurrentId = _oldCurrentId;
		isAdd = _isAdd;
		
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
		ArrayList<Task> tempTasklist = new ArrayList<Task>(taskList);
		if(isAdd){
			for(Iterator<Task> iterator = taskList.iterator(); iterator.hasNext();){
				Task k = iterator.next();
				if(!taskListContents.contains(k)){
					iterator.remove();
				}
			}
		} else {
			for(Task k:taskListContents){
				if(!taskList.contains(k)){
					taskList.add(k);
				}
			}
			
		}
		
		taskListContents = new ArrayList<Task>(tempTasklist);
		TaskHandler.setCurrentTaskId(oldCurrentId);
	}
	
	@Override
	public void redo() {
		super.redo();
		ArrayList<Task> tempTasklist = new ArrayList<Task>(taskList);
		if(!isAdd){
			for(Iterator<Task> iterator = taskList.iterator(); iterator.hasNext();){
				Task k = iterator.next();
				if(!taskListContents.contains(k)){
					iterator.remove();
				}
			}
		} else {
			for(Task k:taskListContents){
				if(!taskList.contains(k)){
					taskList.add(k);
				}
			}
			
		}
		
		taskListContents = new ArrayList<Task>(tempTasklist);
		TaskHandler.setCurrentTaskId(oldCurrentId);
	}
}
