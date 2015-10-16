package Task;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Ellipse2D;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class Gui extends JFrame {
	
	private static final int FADE_DURATION_MS = 1000;
	
    public Gui() {
        super("ShapedWindow");
        setLayout(new GridBagLayout());

        // It is best practice to set the window's shape in
        // the componentResized method.  Then, if the window
        // changes size, the shape will be correctly recalculated.
        addComponentListener(new ComponentAdapter() {
            // Give the window an elliptical shape.
            // If the window is resized, the shape is recalculated here.
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new Ellipse2D.Double(0,0,getWidth(),getHeight()));
            }
        });

        setUndecorated(true);
        setSize(300,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JButton("I am a Button"));
    }
    
    private void showWindow(){
    	
    }

    public static void main(String[] args) {
        // Determine what the GraphicsDevice can support.
        GraphicsEnvironment ge = 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        final boolean isTranslucencySupported = 
            gd.isWindowTranslucencySupported(TRANSLUCENT);

        //If shaped windows aren't supported, exit.
        if (!gd.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT)) {
            System.err.println("Shaped windows are not supported");
            System.exit(0);
        }

        //If translucent windows aren't supported, 
        //create an opaque window.
        if (!isTranslucencySupported) {
            System.out.println(
                "Translucency is not supported, creating an opaque window");
        }

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Gui sw = new Gui();

                // Set the window to 70% translucency, if supported.
                if (isTranslucencySupported) {
                    sw.setOpacity(1.0f);
                }

                // Display the window.
                sw.setVisible(true);
                
            }
        });
    }
}