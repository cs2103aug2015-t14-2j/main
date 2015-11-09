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
    
    private static Controller controller 			= null;
    private static JavaFXGUI GUI         			= null;
    private static Stage stage           			= null;
    private static TextField tb          			= null;
    private static WebView browser       			= null;
    private static final int WIN_WIDTH 	 			= 1000;
    private static final int WIN_HEIGHT  			= 725;
	private static final double TAB_HEIGHT 			= 20;
	
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
        stage.setMinWidth(WIN_WIDTH);
        stage.setMinHeight(WIN_HEIGHT + TAB_HEIGHT);
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
        tb.setPrefSize(Integer.MAX_VALUE, TAB_HEIGHT);
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
}