package Task;

/**
 * @@ author A0097689
 */

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

@SuppressWarnings("serial")
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
