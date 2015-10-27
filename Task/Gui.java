package Task;


import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import static java.awt.GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT;

public class Gui extends JFrame {
	
	private static final int 	FADE_DURATION_MS 	= 10;
	private static final float 	FADE_OUT_VAL		= .02f;
	private static final float 	FADE_IN_VAL 		= .08f;
	private static final float 	FADED_OUT 			= FADE_OUT_VAL;
	private static final float 	FADED_IN 			= .9f - FADE_IN_VAL;
	
	private static final int 	BOX_WIDTH			= 500;
	private static final int 	BOX_HEIGHT			= 400;
	private static final int 	BOX_ARC_WIDTH		= 15;
	private static final int 	BOX_ARC_HEIGHT		= BOX_ARC_WIDTH;
	
	private static final int 	TEXT_WIDTH			= 380;
	private static final int 	INPUT_FONT_SIZE		= 36;
	private static final int 	FEEDBACK_FONT_SIZE	= 14;
	private static final String FONT_NAME			= "SimSun";
	
	private static final String ERROR_NO_TRANSLUCENCY 		= "Translucency could not be enabled";
	private static final String ERROR_NO_SHAPED_WINDOWS  	= "Shaped windows are not supported";
	

	
	private static Gui 			instance 			= null;
	private static JPanel 		textInputFeedback 	= null;
	private static JTextField 	inputField 			= null;
	private static JTextArea 	feedbackField 		= null;
	
    public Gui(Controller c) {
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
        
        Font inputFont = new Font(FONT_NAME, Font.BOLD, INPUT_FONT_SIZE);
        inputField.setFont(inputFont);
        inputField.setForeground(Color.LIGHT_GRAY);
        
        inputField.setHorizontalAlignment(SwingConstants.CENTER);
        
        inputField.setBorder(null);
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
        
        feedbackField = new JTextArea(1,TEXT_WIDTH);
        
        feedbackField.setLineWrap( true );
        feedbackField.setWrapStyleWord( true );
        feedbackField.setEditable(false);
        
        feedbackField.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, Color.LIGHT_GRAY));
        
        Font feedbackFont = new Font(FONT_NAME, Font.BOLD, FEEDBACK_FONT_SIZE);
        feedbackField.setFont(feedbackFont);
        feedbackField.setForeground(Color.BLUE);
        
        JScrollPane scrollFeedbackField = new JScrollPane (feedbackField, 
        		   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollFeedbackField.setBorder(null);
        
        //POSITIONING
        
        textInputFeedback = new JPanel(new BorderLayout());

        textInputFeedback.add(inputField,BorderLayout.PAGE_START);
        textInputFeedback.add(scrollFeedbackField,BorderLayout.LINE_START);
        
        textInputFeedback.setPreferredSize(getSize());
        
        add(textInputFeedback);
        
    }
    
	protected static Gui getCurrentInstance() {
		if(instance == null){
			instance = new Gui(Controller.getInstance());
		}
		return instance;
	}
	
	public void setFeedbackText(String feedback){
		feedbackField.setText(feedback);
	}
    
    public static void switchViewWindow(Gui guiObject) throws InterruptedException{
    	if(guiObject.getOpacity() > FADED_OUT){
    		while(guiObject.getOpacity() > FADED_OUT){
    			guiObject.setOpacity(guiObject.getOpacity()-FADE_OUT_VAL);
    			Thread.sleep(FADE_DURATION_MS);
    		}
    		inputField.setText("");
    	} else {
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
    
}