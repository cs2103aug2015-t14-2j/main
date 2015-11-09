package Task;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.stage.WindowEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.web.WebView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

public class JavaFXGUI extends Application {
    
    private static Controller controller = null;
    private static JavaFXGUI GUI         = null;
    private static Stage stage           = null;
    private static TextField tb          = null;
    private static WebView browser       = null;
    private static final int WIN_WIDTH 	 = 1000;
    private static final int WIN_HEIGHT  = 740;
    
    // @@author A0009586
    private static final int 	FADE_DURATION_MS 	= 2;
	private static final float 	FADE_OUT_VAL		= .01f;
	private static final float 	FADE_IN_VAL 		= .05f;
	private static final float 	FADED_OUT 			= FADE_OUT_VAL;
	private static final float 	FADED_IN 			= 1f - FADE_IN_VAL;
	
	public JavaFXGUI() {
		controller = Controller.getInstance();
	}
	
	public static JavaFXGUI getInstance() {
		if (GUI==null) {
			return GUI = new JavaFXGUI();
		} else {
			return GUI;
		}
	}
	
    @Override 
    public void start(Stage _stage) throws Exception {  
    	String template = getClass().getResource("/templates/html/output.html").toExternalForm();
        browser = new WebView(); 
        browser.getEngine().load(template);
        stage = _stage;
        HBox hbox = addHBox();

        // Set grid layout
        BorderPane border = new BorderPane();
        border.setTop(hbox);
        border.setCenter(browser);

        Scene scene = new Scene(border, WIN_WIDTH, WIN_HEIGHT);
        // Handle close button
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
              Platform.exit();
              System.exit(0);
           }
        });
        controller.prepareStartUpScreen();
        stage.setScene(scene);  
        stage.show();
    } 
    
    /*
    * Creates an HBox with textbox and exit button for the top region
    */   
    private HBox addHBox() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 12, 5, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");
        
        tb = new TextField();
        tb.setPrefSize(WIN_WIDTH, 20);
        //Setting an action for the textbox
        tb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                final String userInput = tb.getText();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.executeGUIInput(userInput);
                    }
                });
                tb.setText("");
             }
         });
        
        hbox.getChildren().addAll(tb);
        
        return hbox;
    } 

    public static void update() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String template = getClass().getResource("/templates/html/output.html").toExternalForm(); 
                browser.getEngine().load(template);
            }
        });
    }

    // @@author A0009586
	public static void switchViewWindow() throws InterruptedException {
		if(stage.getOpacity() > FADED_OUT){
    		while(stage.getOpacity() > FADED_OUT){
    			stage.setOpacity(stage.getOpacity()-FADE_OUT_VAL);
    			Thread.sleep(FADE_DURATION_MS);
    		}
    		stage.setOpacity(0f);
    	} else {    	
    		while(stage.getOpacity() < FADED_IN){
    			stage.setOpacity(stage.getOpacity()+FADE_IN_VAL);
    			Thread.sleep(FADE_DURATION_MS);
    		}
    		stage.setOpacity(FADED_IN + FADE_IN_VAL);
    		tb.requestFocus();
    	}
		
	}
}