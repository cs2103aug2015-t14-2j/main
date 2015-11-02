package Task;

import java.net.URL;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.stage.WindowEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.geometry.Insets;
import javafx.scene.web.WebView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.*;
import javafx.scene.control.TextField;

public class JavaFXGUI extends Application {
    
    private static Controller controller = null;
    private static JavaFXGUI GUI         = null;
    private static Stage stage           = null;
    private static TextField tb          = null;
    private static WebView browser       = null;
	
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
        BorderPane border = new BorderPane();
        stage = _stage;
        Scene scene = new Scene(border, 840, 640);
        HBox hbox = addHBox();

        border.setTop(hbox);
        border.setCenter(browser);

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
        tb.setPrefSize(800, 20);
        //Setting an action for the textbox
        tb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String userInput = tb.getText();
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