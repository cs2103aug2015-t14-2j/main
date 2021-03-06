package Task;

/**
 * @@ author A0097689
 */

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import java.util.Date;

@SuppressWarnings({ "unused", "serial" })
public class TaskDeadlineEdit extends UndoableSignificantEdit implements UndoableEdit {
	protected Task   task;
	protected Date oldValue;
	protected Date newValue;
	
	public TaskDeadlineEdit(Task _task, Date _oldValue, Date _newValue) {
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
		task.setDeadline(oldValue);
		
	}
	
	@Override
	public void redo() {
		task.setDeadline(newValue);
	}
}
