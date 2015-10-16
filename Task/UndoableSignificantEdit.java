package Task;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

public class UndoableSignificantEdit extends AbstractUndoableEdit implements UndoableEdit {
	protected boolean isSignificant;
	
	UndoableSignificantEdit(){
		super();
	};
	
	public void setSignificant() {
		this.isSignificant = true;
	}
	
	@Override
	public boolean isSignificant() {
		return isSignificant;
	}
}
