package Task;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

public class TaskVenueEdit extends UndoableSignificantEdit implements UndoableEdit {
	protected Task   task;
	protected String oldValue;
	protected String newValue;
	protected boolean isSignificant = false;
	
	TaskVenueEdit(Task _task, String _oldValue, String _newValue) {
		task = _task;
		oldValue = _oldValue;
		newValue = _newValue;
		
	}

	@Override
	public String getUndoPresentationName() {
		return "Undo venue change.";
	}
	
	@Override
	public String getRedoPresentationName() {
		return "Redo venue change.";
	}
	
	@Override
	public void undo() {
		super.undo();
		task.setVenue(oldValue);
		
	}
	
	@Override
	public void redo() {
		super.redo();
		task.setVenue(newValue);
	}
}