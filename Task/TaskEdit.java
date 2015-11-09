package Task;

/**
 * @@ author A0097689
 */

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

@SuppressWarnings({ "unused", "serial" })
public class TaskEdit extends CompoundEdit implements UndoableEdit {
	protected Task   task;
	
	TaskEdit(Task _task) {
		super();
		task = _task;
		
	}

	@Override
	public String getUndoPresentationName() {
		return "Undo edit task.";
	}
	
	@Override
	public String getRedoPresentationName() {
		return "Redo edit task.";
	}
	
	@Override
	public void undo() {
		super.undo();
	}
	
	@Override
	public void redo() {
		super.redo();
	}
	
	@Override
	public UndoableEdit lastEdit() {
		return super.lastEdit();
	}
	
	public Task getTask() {
		return this.task;
	}
	
//	@Override
//	public boolean isSignificant() {
//		return true;
//	}
}
