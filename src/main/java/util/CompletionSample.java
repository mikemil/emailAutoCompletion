package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Document;


/**
 * An example on how to use the {@link EmailAddressAutoCompleteDocument}.
 * 
 * @see EmailAddressAutoCompleteDocument
 * @see CompletionService
 * 
 */
public class CompletionSample implements Runnable, KeyListener {

	public static final String REGEX_EMAIL_ADDRESS = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";
	
	private Pattern pattern;
	private JTextField input;
	
    /**
     * Create the GUI and display it to the world.
     */
    public void run() {
        // Create the completion service.
        EmailAddressCompletionService nameService = new EmailAddressCompletionService();

        // Create the input field.
        input = new JTextField();
        input.addKeyListener(this);

        // Create the auto completing document model with a reference to the
        // service and the input field.
        Document autoCompleteDocument = new EmailAddressAutoCompleteDocument(nameService, input);

        // Set the auto completing document as the document model on our input
        // field.
        input.setDocument(autoCompleteDocument);
        
        // Below we just create a frame and display the GUI.
        JFrame frame = new JFrame("Email Autocompletion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComponent contentPane = (JComponent) frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JLabel("Enter email address"), BorderLayout.NORTH);
        contentPane.add(input, BorderLayout.CENTER);
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.pack();
        frame.setVisible(true);
        
        pattern = Pattern.compile(REGEX_EMAIL_ADDRESS, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Application entry point.
     * 
     * @param args
     *            not in use
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new CompletionSample());
    }

    /**
     * A simple {@link CompletionService} providing completion for most popular
     * baby names 2008.
     */
    private static class EmailAddressCompletionService implements CompletionService<String> {

        /** Our name data. */
        private List<String> data;

        /**
         * Create a new <code>NameService</code> and populate it.
         * @throws IOException 
         */
        public EmailAddressCompletionService()  {
        	Properties props = null;
        	try {
        		URL urlBase = getClass().getResource("/emailaddress.properties");
    			props = new Properties();
    			props.load(urlBase.openStream());	
        	} catch(IOException ex) {
        		ex.printStackTrace();
        		props = new Properties();
        	}

			data = new ArrayList<String>();
			for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
				String propName = (String)e.nextElement();
				data.add(props.getProperty(propName));
			}
        }

        public String autoComplete(String startsWith) {
            String hit = null;
            for (String o : data) {
                if (o.startsWith(startsWith)) {
                    // CompletionService contract states that we only
                    // should return completion for unique hits.
                    if (hit == null) {
                        hit = o;
                    } else {
                        hit = null;
                        break;
                    }
                }
            }
            return hit;
        }

    }

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		Matcher m = pattern.matcher(input.getText());
		Color color = m.matches() ? Color.GREEN : Color.red;
		input.setForeground(color);
	}
}
