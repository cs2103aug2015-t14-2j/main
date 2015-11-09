package Task;

/**
 * These are the possible command types for tasks
 * 
 *  @@author A0097689
 */

public enum COMMAND_TYPE {
	PATH,
	FILEOPEN,
	FILESAVE,
	ADD_TASK, 
	GET_TASK,
	DISPLAY,
	EDIT_TASK, 
	DELETE_TASK,
	UNDO,
	REDO,
	INVALID_COMMAND, 
	DONE, 
	UNDONE,
	HELP,
	EXIT
}
