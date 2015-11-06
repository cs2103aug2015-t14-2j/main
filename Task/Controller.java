package Task;

import java.util.ArrayList;

import javafx.application.Application;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Locale;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

/**
 *  Represents the control module that is in charge of initialization and GUI
 *  Singleton structure
 * 
 *  @author A0097689 Tan Si Kai
 *  @author A0009586 Jean Pierre Castillo
 *  @author A0118772 Audrey Tiah
 */

public class Controller implements NativeKeyListener {
	private static Configuration cfg;
	private static Context context = Context.getInstance();
	private static Controller instance = null;
	private static int[] myArray = new int[]{NativeKeyEvent.VC_SHIFT_L,			//Left shift
											 	NativeKeyEvent.VC_SHIFT_R		//Right shift
											 	};
	
	private List<Integer> keyPressedList = new ArrayList<>();
	private boolean isShortCutPressed = false;
	
	private final static Logger LOGGER = Logger.getLogger(StringParser.class.getName());
	
	protected Controller() {
		// Configure Freemarker
		cfg = new Configuration(Configuration.VERSION_2_3_22);
		
		// Where do we load the templates from:
	    try {
			cfg.setDirectoryForTemplateLoading(new File("./templates/html"));
		} catch (IOException e) {
			e.printStackTrace();
			context.displayMessage("ERROR_HTML_TEMPLATE");
		}
	    
	    // Some other recommended settings:
	    cfg.setIncompatibleImprovements(new Version(2, 3, 20));
	    cfg.setDefaultEncoding("UTF-8");
	    cfg.setLocale(Locale.US);
	    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	public static Controller getInstance() {
      if(instance == null) {
         instance = new Controller();
      }
      return instance;
	}
	
    public void nativeKeyPressed(NativeKeyEvent e) {

    	if(!keyPressedList.contains(e.getKeyCode())){
    		LOGGER.info("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    		keyPressedList.add(e.getKeyCode());
    	}
    	
    	
		if(isShortCut() && !isShortCutPressed){
    		isShortCutPressed = true;
    		LOGGER.info("ShortCut triggered");
    		try {
				Gui.switchViewWindow(Gui.getCurrentInstance());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
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
            System.err.println("There was a problem enableing the shortcut functionality, ensure no instances are running");
            LOGGER.severe(ex.getMessage());
            System.exit(1);
        }

        // Construct the example object and initialze native hook.
        GlobalScreen.addNativeKeyListener(Controller.getInstance());
	    
        // Start the task handler before launching GUI so JavaFX application thread
        // still has TaskHandler info before forking
        TaskHandler.init(args);

        // Start the GUI
        Application.launch(JavaFXGUI.class);
	    
    }
	
    public void executeGUIInput(String text) {
        TaskHandler.inputFeedBack(text);
        renderView();
        context.clearAllMessages();
		JavaFXGUI.update();
    }

    public void prepareStartUpScreen() {
    	context.displayMessage("MESSAGE_WELCOME");
    	executeGUIInput("display");
    }

    private void renderView() {
        HashMap<String, Object> dataModel = context.getDataModel();
        Template template;  
		Writer fileWriter;
	    try {
	    	fileWriter = new FileWriter(new File("./templates/html/output.html"));
			template = cfg.getTemplate("index.ftl");
	        template.process(dataModel, fileWriter);
	        fileWriter.close();
	    } catch (TemplateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}