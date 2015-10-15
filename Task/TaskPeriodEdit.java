package Task;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import Task.Period;

public class TaskPeriodEdit extends AbstractUndoableEdit implements UndoableEdit {
	protected Task   task;
	protected Period oldValue;
	protected Period newValue;
	
	TaskPeriodEdit(Task _task, Period _oldValue, Period _newValue) {
		task = _task;
		oldValue = _oldValue;
		newValue = _newValue;
		
	}

	@Override
	public String getUndoPresentationName() {
		return "Undo period change.";
	}
	
	@Override
	public String getRedoPresentationName() {
		return "Redo period change.";
	}
	
	@Override
	public void undo() {
		super.undo();
		task.setPeriod(oldValue);
		
	}
	
	@Override
	public void redo() {
		super.redo();
		task.setPeriod(newValue);
	}
	
	@Override
	public boolean isSignificant() {
		return false;
	}
}
