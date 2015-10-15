package Task;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

public class TaskDoneEdit extends AbstractUndoableEdit implements UndoableEdit {
	protected Task   task;
	protected boolean oldValue;
	protected boolean newValue;
	
	TaskDoneEdit(Task _task, boolean _oldValue, boolean _newValue) {
		task = _task;
		oldValue = _oldValue;
		newValue = _newValue;
		
	}

	@Override
	public String getUndoPresentationName() {
		return "Undo isDone change.";
	}
	
	@Override
	public String getRedoPresentationName() {
		return "Redo isDone change.";
	}
	
	@Override
	public void undo() {
		super.undo();
		task.setDone(oldValue);
		
	}
	
	@Override
	public void redo() {
		super.redo();
		task.setDone(newValue);
	}
	
	@Override
	public boolean isSignificant() {
		return true;
	}
}
