package Task;


import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;
import static java.awt.GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.text.html.*;
import javax.swing.text.*;

import java.net.MalformedURLException;
import java.net.URL;

public class Gui extends JFrame {
	
	private static final int 	FADE_DURATION_MS 	= 10;
	private static final float 	FADE_OUT_VAL		= .02f;

	private static final float 	FADE_IN_VAL 		= .04f;
	private static final float 	FADED_OUT 			= FADE_OUT_VAL;
	private static final float 	FADED_IN 			= .96f - FADE_IN_VAL;
	
	private static final int 	BOX_WIDTH			= (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
	private static final int 	BOX_HEIGHT			= (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 3 / 4;;
	private static final int 	BOX_ARC_WIDTH		= 15;
	private static final int 	BOX_ARC_HEIGHT		= BOX_ARC_WIDTH;
	
	private static final int 	TEXT_WIDTH			= 380;
	private static final int 	INPUT_FONT_SIZE		= 28;
	private static final int 	FEEDBACK_FONT_SIZE	= 14;
	private static final String FONT_NAME			= "HelveticaNeue";
	
	private static final String ERROR_NO_TRANSLUCENCY 		= "Translucency could not be enabled";
	private static final String ERROR_NO_SHAPED_WINDOWS  	= "Shaped windows are not supported";
	
	private static Gui 			instance 			= null;
	private static JPanel 		textInputFeedback 	= null;
	private static JPanel 		textTasks		 	= null;
	private static JTextField 	inputField 			= null;
	private static JEditorPane 	feedbackField 		= null;
	private static JTextArea 	taskField 			= null;
	
    public Gui(final Controller c) {
        super("ShapedWindow");
        setLayout(new GridBagLayout());
        setAlwaysOnTop (true);
        setType(javax.swing.JFrame.Type.UTILITY);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),BOX_ARC_WIDTH,BOX_ARC_HEIGHT));
            }
        });

        setUndecorated(true);
        setSize(BOX_WIDTH,BOX_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //INPUT
        
        inputField = new JTextField(TEXT_WIDTH);
        
        Font inputFont = new Font(FONT_NAME, Font.PLAIN, INPUT_FONT_SIZE);
        inputField.setFont(inputFont);
        inputField.setForeground(Color.GRAY);
        
        inputField.setHorizontalAlignment(SwingConstants.LEFT);
        
        inputField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        inputField.setBackground(getBackground());
        
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            	String input = getUserInput();
                c.executeGUIInput(input);
            }
        };
        inputField.addActionListener(action);
        
        //FEEDBACK

        feedbackField = new JEditorPane();
        feedbackField.setContentType("text/html");
        HTMLEditorKit kit = (HTMLEditorKit) feedbackField.getEditorKit();
        StyleSheet ss = new StyleSheet();
        URL ss_url;
        try {
            ss_url = new URL(new URL("file:"), "./templates/css/bootstrap.min.css");
            ss.importStyleSheet(ss_url);
        } catch (MalformedURLException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        kit.setStyleSheet(ss);
        
        Font feedbackFont = new Font(FONT_NAME, Font.BOLD, FEEDBACK_FONT_SIZE);
        feedbackField.setFont(feedbackFont);
        
        feedbackField.setEditable(false);
        feedbackField.setBorder(null);
        // feedbackField.setBackground(getBackground());
        
        //TASKS
        
        taskField = new JTextArea(1,TEXT_WIDTH);
        
        taskField.setLineWrap( true );
        taskField.setWrapStyleWord( true );
        taskField.setEditable(false);
        
        taskField.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, Color.GRAY));
        
        Font taskFont = new Font(FONT_NAME, Font.BOLD, FEEDBACK_FONT_SIZE);
        taskField.setFont(taskFont);
        // taskField.setForeground(Color.GREEN);
        
        JScrollPane scrollFeedbackField = new JScrollPane (taskField, 
        		   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollFeedbackField.setBorder(null);
        
        // //POSITIONING
        
        textInputFeedback = new JPanel(new BorderLayout());
        textTasks = new JPanel(new BorderLayout());

        textTasks.add(inputField,BorderLayout.PAGE_START);
        
        JPanel containerPanel = new JPanel();
        containerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        containerPanel.setLayout(new BorderLayout());
        containerPanel.add(feedbackField);
        
        textTasks.add(containerPanel,BorderLayout.PAGE_END);
        textInputFeedback.add(textTasks,BorderLayout.PAGE_START);
        textInputFeedback.add(scrollFeedbackField,BorderLayout.CENTER);
        
        textInputFeedback.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 4, Color.GRAY));
        
        textInputFeedback.setPreferredSize(getSize());
        
        add(textInputFeedback);
        
    }
    
	protected static Gui getCurrentInstance() {
		if(instance == null){
			instance = new Gui(Controller.getInstance());
		}
		return instance;
	}
	
	 public static int getContentHeight(String content) {
        JEditorPane dummyEditorPane=new JEditorPane();
        dummyEditorPane.setSize(BOX_WIDTH,Short.MAX_VALUE);
        feedbackField.setContentType("text/html");
        dummyEditorPane.setText(content);
        
        return dummyEditorPane.getPreferredSize().height;
    }

	
	public void setFeedbackText(String feedback){
		feedback = "<html>" + feedback + "</html>";
		feedbackField.setText(feedback);
		
		textTasks.setSize(new Dimension(BOX_WIDTH,getContentHeight(feedback) + inputField.getSize().height));
		
		feedbackField.setSize(new Dimension(BOX_WIDTH,getContentHeight(feedback)));
	}
	
	public void setTaskText(String feedback){
		taskField.setText(feedback);
	}
    
    public static void switchViewWindow(Gui guiObject) throws InterruptedException{
    	if(guiObject.getOpacity() > FADED_OUT){
    		while(guiObject.getOpacity() > FADED_OUT){
    			guiObject.setOpacity(guiObject.getOpacity()-FADE_OUT_VAL);
    			Thread.sleep(FADE_DURATION_MS);
    		}
    		guiObject.setVisible(false);
    		
    	} else {
    		guiObject.setFeedbackText("<font color=\"green\">Welcome to TextBuddy! Input a command above to get started!</font>");
    		inputField.setText("");
    		taskField.setText("");
    		
    		guiObject.setVisible(true);
    		
    		while(guiObject.getOpacity() < FADED_IN){
    			guiObject.setOpacity(guiObject.getOpacity()+FADE_IN_VAL);
    			Thread.sleep(FADE_DURATION_MS);
    		}
    		inputField.requestFocusInWindow();
    	}
    }

    public static void initGUI() {

        GraphicsEnvironment  _GraphicsEnvironment= 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice _GraphicsDevice = _GraphicsEnvironment.getDefaultScreenDevice();
        final boolean isTranslucencySupported = 
            _GraphicsDevice.isWindowTranslucencySupported(TRANSLUCENT);

        if (!_GraphicsDevice.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT)) {
            System.err.println(ERROR_NO_SHAPED_WINDOWS);
            System.exit(0);
        }

        if (!isTranslucencySupported) {
            System.out.println(ERROR_NO_TRANSLUCENCY);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Gui sw = Gui.getCurrentInstance();

                if (isTranslucencySupported) {
                    sw.setOpacity(FADED_OUT);
                }

                // Display the window.
                sw.setVisible(true);
                
                // Switch the view of the window
                try {
					switchViewWindow(sw);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }

	public String getUserInput() {
		String userInput = "";
	    userInput = inputField.getText();
	    inputField.setText("");
	    return userInput;
	}

    public static void update() {
        try {
            Document doc = feedbackField.getDocument();
            doc.putProperty(Document.StreamDescriptionProperty, null);
            URL url = new URL(new URL("file:"), "./templates/html/output.html");
            feedbackField.setPage("http://www.google.com");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}