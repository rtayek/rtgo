package gui.demo;
import java.awt.BorderLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class KeyDiagnostics {
    private final JTextArea logArea=new JTextArea(20,100);
    private final JPanel keyPanel=new JPanel();
    private final SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss.SSS");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KeyDiagnostics().show());
    }

    private void show() {
        JFrame frame=new JFrame("Key Diagnostics");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setJMenuBar(createMenuBar());

        JLabel help=new JLabel(
                "Click the bordered panel, then press keys. Try Alt+O, Ctrl/Cmd+O, Delete, arrows.");
        help.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        frame.add(help,BorderLayout.NORTH);

        keyPanel.setBorder(BorderFactory.createTitledBorder("Focus Here"));
        keyPanel.setFocusable(true);
        keyPanel.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { log("KeyListener pressed  : "+describe(e)); }
            @Override public void keyReleased(KeyEvent e) { log("KeyListener released : "+describe(e)); }
            @Override public void keyTyped(KeyEvent e) { log("KeyListener typed    : "+describe(e)); }
        });
        frame.add(keyPanel,BorderLayout.CENTER);

        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea),BorderLayout.SOUTH);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if(e.getID()==KeyEvent.KEY_PRESSED) log("Dispatcher pressed   : "+describe(e));
            return false; // observe only; do not consume
        });

        wirePanelBindings();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> keyPanel.requestFocusInWindow());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar=new JMenuBar();
        JMenu menu=new JMenu("File");
        int menuMask=Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenuItem altOpen=new JMenuItem(new AbstractAction("Open (Alt+O)") {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { log("Menu action: Alt+O"); }
        });
        altOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.ALT_DOWN_MASK));
        menu.add(altOpen);

        JMenuItem menuOpen=new JMenuItem(new AbstractAction("Open (Ctrl/Cmd+O)") {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                log("Menu action: Ctrl/Cmd+O");
            }
        });
        menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,menuMask));
        menu.add(menuOpen);

        menuBar.add(menu);
        return menuBar;
    }

    private void wirePanelBindings() {
        InputMap im=keyPanel.getInputMap(JComponent.WHEN_FOCUSED);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0),"delete");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),"up");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),"down");

        keyPanel.getActionMap().put("delete",new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                log("KeyBinding action: DELETE");
            }
        });
        keyPanel.getActionMap().put("up",new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { log("KeyBinding action: UP"); }
        });
        keyPanel.getActionMap().put("down",new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { log("KeyBinding action: DOWN"); }
        });
    }

    private String describe(KeyEvent e) {
        String keyText=KeyEvent.getKeyText(e.getKeyCode());
        String modifiers=InputEvent.getModifiersExText(e.getModifiersEx());
        return keyText+" mods=["+modifiers+"] code="+e.getKeyCode();
    }

    private void log(String message) {
        logArea.append(sdf.format(new Date())+"  "+message+"\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
