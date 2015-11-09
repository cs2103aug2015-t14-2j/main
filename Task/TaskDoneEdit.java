package Task;

/**
 * @@ author A0097689
 */

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

@SuppressWarnings({ "unused", "serial" })
public class TaskDoneEdit extends UndoableSignificantEdit implements UndoableEdit {
	protected Task   task;
	protected Boolean oldValue;
	protected Boolean newValue;
	
	public TaskDoneEdit(Task _task, Boolean _oldValue, Boolean _newValue) {
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
}
