package Task;

/**
 * @@ author A0097689
 */

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

@SuppressWarnings({ "unused", "serial" })
public class TaskDescEdit extends UndoableSignificantEdit implements UndoableEdit {
	protected Task   task;
	protected String oldValue;
	protected String newValue;
	
	public TaskDescEdit(Task _task, String _oldValue, String _newValue) {
		task = _task;
		oldValue = _oldValue;
		newValue = _newValue;
		
	}

	@Override
	public String getUndoPresentationName() {
		return "Undo description change.";
	}
	
	@Override
	public String getRedoPresentationName() {
		return "Redo description change.";
	}
	
	@Override
	public void undo() {
		super.undo();
		task.setDescription(oldValue);
	}
	
	@Override
	public void redo() {
		super.redo();
		task.setDescription(newValue);
	}
}
