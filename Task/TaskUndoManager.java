package Task;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

@SuppressWarnings("serial")
public class TaskUndoManager extends UndoManager {
	TaskUndoManager() {
		super();
	}
	
	@Override
	public UndoableEdit editToBeUndone() {
		return super.editToBeUndone();
	}

	@Override
	public UndoableEdit editToBeRedone() {
		return super.editToBeRedone();
	}
	
}
