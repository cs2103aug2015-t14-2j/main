package Task;

import javafx.application.Application;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Locale;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

/**
 *  Represents the control module that is in charge of initialization and GUI
 *  Singleton structure
 * 
 *  @@author A0145472E
 */

public class Controller{
	private static Configuration cfg;
	private static Context context = Context.getInstance();
	private static Controller instance = null;
	
	private final static Logger LOGGER = Logger.getLogger(StringParser.class.getName());
	
	/**
	 *  @@author A0097689
	 */
	private Controller() {
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

	public static void main(String[] args) {
		LOGGER.setLevel(Level.SEVERE);
		
        TaskHandler.init(args);

        // Start the GUI
        Application.launch(JavaFXGUI.class);
	    
    }
	
	/**
	 *  @@author A0097689
	 */
    public void executeGUIInput(String text) {
        TaskHandler.inputFeedBack(text);
        renderView();
        context.clearAllMessages();
		JavaFXGUI.update();
    }

    /**
     *  @@author A0097689
     */
    public void prepareStartUpScreen() {
    	context.displayMessage("MESSAGE_WELCOME");
    	executeGUIInput("display");
    }

    /**
     *  @@author A0097689
     */
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
	    	context.displayMessage("ERROR_HTML_TEMPLATE");
			e.printStackTrace();
		}
    }
}