package simplegui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class SessionHandlerExample implements ActionListener {
    public SessionHandlerExample() { initUI(); }
    private void initUI() {
        final JTextArea text=new JTextArea(20,80);
        frame.add(new JScrollPane(text));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        invalidationTimer.setRepeats(false);
        invalidationTimer.restart();
        // register listener to get all mouse/key events
        final AWTEventListener l=new AWTEventListener() {
            @Override public void eventDispatched(AWTEvent event) {
                // if any input event invoked - restart the timer to prolong the session
                invalidationTimer.restart();
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(l,AWTEvent.KEY_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK);
    }
    @Override public void actionPerformed(ActionEvent e) {
        // provide session invalidation here (show login dialog or do something else)
        JOptionPane.showMessageDialog(frame,"Your session is invalide");
        invalidationTimer.restart();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { @Override public void run() { new SessionHandlerExample(); } });
    }
    private static final int SESSION_TIMEOUT=3*1000; // 30 sec timeout for testing purposes
    private final Timer invalidationTimer=new Timer(SESSION_TIMEOUT,this);
    private final JFrame frame=new JFrame("Session test frame");
}