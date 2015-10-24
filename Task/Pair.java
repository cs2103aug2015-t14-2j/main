package Task;

/**
 *  Simple implementation of Pair<String key, boolean value> with mutable value 
 *  This class is created for the very specific purpose of allowing code
 *  to set specific messages to be displayed to the user through the GUI.
 *  To be used in the Context object. 
 *  @author Jerry
 *
 */
public class Pair {
	private final String key; 		// Note that the message is immutable
	private boolean value;
	
	public Pair(String k) {
		this.key = k;
		this.value = false;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public boolean getValue() {
		return this.value;
	}
	
	public void displayMessage() {
		this.value = true;
	}
	
	public void clearMessage() {
		this.value = false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) {
			return false;
		}
		Pair pair_o = (Pair) o;
		return this.key.equals(pair_o.getKey()) 
				&& (this.value == pair_o.getValue());
	}
}
