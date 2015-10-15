package Task;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 *  Represents the control module that is in charge of initialization and GUI
 * 
 *  @author A0097689 Tan Si Kai
 *  @author A0009586 Jean Pierre Castillo
 *  @author A0118772 Audrey Tiah
 */

public class Controller implements NativeKeyListener {
	
	private static int[] myArray = new int[]{NativeKeyEvent.VC_SHIFT_L,			//Left shift
											 	NativeKeyEvent.VC_SHIFT_R		//Right shift
											 	};
	
	private List<Integer> keyPressedList = new ArrayList<>();
	private boolean isShortCutPressed = false;
	
	private final static Logger LOGGER = Logger.getLogger(StringParser.class.getName());
	
    public void nativeKeyPressed(NativeKeyEvent e) {

    	if(!keyPressedList.contains(e.getKeyCode())){
    		LOGGER.info("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    		keyPressedList.add(e.getKeyCode());
    	}
    	
    	
		if(isShortCut() && !isShortCutPressed){
    		isShortCutPressed = true;
    		LOGGER.info("ShortCut triggered");
    		//Do whatever
    	}
    	
        //TODO: shortcut for exit?
        /* if (e.getKeyCode() == NativeKeyEvent.VC_F10) {
          		LOGGER.info("Exit triggered");
                try {
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e1) {
					e1.printStackTrace();
				}
        }*/
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
    	
    	if(keyPressedList.contains(e.getKeyCode())){
    		LOGGER.info("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    		keyPressedList.remove(keyPressedList.indexOf(e.getKeyCode()));
    	}
    	if(!isShortCut() && isShortCutPressed){
    		LOGGER.info("ShortCut released");
    		isShortCutPressed = false;
    	}
    	
    }
    
    public void nativeKeyTyped(NativeKeyEvent e) {
    	
    }

    private boolean isShortCut() {
    	for(int key:myArray){
    		if(!keyPressedList.contains(key)){
    			return false;
    		}
    	}
    	return true;
	}

	public static void main(String[] args) {
		LOGGER.setLevel(Level.SEVERE);
        try {
                Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
                logger.setLevel(Level.OFF);
                GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
                System.err.println("There was a problem registering the native hook.");
                System.err.println(ex.getMessage());
                System.exit(1);
        }

        //Construct the example object and initialze native hook.
        GlobalScreen.addNativeKeyListener(new Controller());
    }
}