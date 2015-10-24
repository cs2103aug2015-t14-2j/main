package Task;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Ellipse2D;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class Gui extends JFrame {
	
	private static final int 	FADE_DURATION_MS 	= 10;
	private static final float 	FADE_OUT_VAL		= .02f;
	private static final float 	FADE_IN_VAL 		= .08f;
	private static final float 	FADED_OUT 			= FADE_OUT_VAL;
	private static final float 	FADED_IN 			= .9f - FADE_IN_VAL;
	
	private static final int 	TEXTBOX_SIZE		= 20;
	
	private static final String ERROR_NO_TRANSLUCENCY 		= "Translucency could not be enabled";
	private static final String ERROR_NO_SHAPED_WINDOWS  	= "Shaped windows are not supported";
	

	
	static Gui instance = null;
	static JTextField tb = null;
	
    public Gui(Controller c) {
        super("ShapedWindow");
        setLayout(new GridBagLayout());
        setAlwaysOnTop (true);
        setType(javax.swing.JFrame.Type.UTILITY);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new Ellipse2D.Double(0,0,getWidth(),getHeight()));
            }
        });

        setUndecorated(true);
        setSize(300,100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        tb = new JTextField("", TEXTBOX_SIZE);
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            	String input = getUserInput();
                c.executeGUIInput(input);
            }
        };

        tb.addActionListener(action);
        add(tb);
    }
    
	protected static Gui getCurrentInstance() {
		if(instance == null){
			instance = new Gui(Controller.getInstance());
		}
		return instance;
	}
    
    public static void switchViewWindow(Gui guiObject) throws InterruptedException{
    	if(guiObject.getOpacity() > FADED_OUT){
    		while(guiObject.getOpacity() > FADED_OUT){
    			guiObject.setOpacity(guiObject.getOpacity()-FADE_OUT_VAL);
    			Thread.sleep(FADE_DURATION_MS);
    		}
    		tb.setText("");
    	} else {
    		while(guiObject.getOpacity() < FADED_IN){
    			guiObject.setOpacity(guiObject.getOpacity()+FADE_IN_VAL);
    			Thread.sleep(FADE_DURATION_MS);
    		}
    		tb.requestFocusInWindow();
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
                
                try {
					Thread.sleep(FADE_DURATION_MS * FADE_DURATION_MS);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
                
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
	    userInput = tb.getText();
	    tb.setText("");
	    return userInput;
	}
    
}