package simplegui;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import equipment.Board;
import io.Logging;
/*
UP("Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)),
DOWN("Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)),
LEFT("Left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)),
RIGHT("Right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
 */
public class ControlPanel extends JPanel {
    public enum Buttons { // use client property
        // instances have an abstract button
        // why is this one so much less complicated?
        humidity,temperature,reset;
        Buttons(KeyStroke keyStroke) { this((String)null); }
        Buttons() { this((String)null); }
        Buttons(String tooltipText) { this(new JButton(),tooltipText); }
        Buttons(AbstractButton abstractButton) { this(abstractButton,null); }
        Buttons(AbstractButton abstractButton,String tooltipText) { this(abstractButton,tooltipText,null); }
        Buttons(AbstractButton abstractButton,String tooltipText,KeyStroke keyStroke) {
            this.abstractButton=abstractButton;
            abstractButton.setName(name());
            abstractButton.setText(name());
            this.tooltipText=tooltipText;
            if(tooltipText!=null) abstractButton.setToolTipText(tooltipText);
            this.keyStroke=keyStroke;
            // c.putClientProperty("tayek.go.name",name());
        }
        private void enable(boolean enable) { abstractButton.setEnabled(enable); }
        public static void enableAll(Model model) { for(Buttons button:values()) button.enable(true); }
        public final String tooltipText;
        public final AbstractButton abstractButton;
        public final KeyStroke keyStroke;
    }
    public ControlPanel(Mediator mediator) {
        this.mediator=mediator;
        for(Buttons button:Buttons.values()) {
            button.abstractButton.addActionListener(actionListener);
            add(button.abstractButton);
        }
    }
    final Mediator mediator;
    static List<Board.Topology> types=Arrays.asList(Board.Topology.values());
    ActionListener actionListener=new ActionListener() {
        @Override public void actionPerformed(ActionEvent e) {
            Object object=e.getSource();
            if(object instanceof AbstractButton) {
                AbstractButton abstractButton=((AbstractButton)object);
                String name=abstractButton.getName();
                if(name!=null&&Buttons.valueOf(name)!=null) mediator.action(Buttons.valueOf(name));
                else Logging.mainLogger.info(mediator.model+" "+"unknown action "+e);
            } else Logging.mainLogger.info(mediator.model+" "+"what is action performed in "+e.getSource());
        }
    };
    private static final long serialVersionUID=1L;
}
