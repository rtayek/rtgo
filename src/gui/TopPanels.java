package gui;
import java.awt.LayoutManager;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import io.Logging;
import model.*;
/*
UP("Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)),
DOWN("Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)),
LEFT("Left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)),
RIGHT("Right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
 */
public class TopPanels {
    public static class NewTopPanel extends JPanel {
        class MyButtons extends ButtonsABC<Navigate> {
            // old top panel had enable and enable all!
            @Override public void enableAll(Mediator mediator) { // call from node changed
                Model model=mediator.model;
                for(ButtonsABC<Navigate>.ButtonWithEnum button:buttons.buttons()) {
                    Navigate e=button.t;
                    boolean canDo=e.canDoNoCheck(model);
                    button.abstractButton.setEnabled(canDo);
                }
            }
            // try to get rid of the instances
            ButtonsABC<Navigate>.ButtonWithEnum top=new ButtonWithEnum(Navigate.top);
            ButtonsABC<Navigate>.ButtonWithEnum bottoml=new ButtonWithEnum(Navigate.bottom);
            ButtonsABC<Navigate>.ButtonWithEnum up=new ButtonWithEnum(Navigate.up);
            ButtonsABC<Navigate>.ButtonWithEnum down=new ButtonWithEnum(Navigate.down);
            ButtonsABC<Navigate>.ButtonWithEnum right=new ButtonWithEnum(Navigate.right);
            ButtonsABC<Navigate>.ButtonWithEnum left=new ButtonWithEnum(Navigate.left);
            ButtonsABC<Navigate>.ButtonWithEnum deleye=new ButtonWithEnum(Navigate.delete,"Delete this branch");
        } // end of inner class
        public NewTopPanel(Mediator mediator) {
            setName("top  panel");
            this.mediator=mediator;
            // work here
            LayoutManager oldLayoutManage=getLayout();
            // old layout is FlowLayout!
            Logging.mainLogger.info(this+" old layout: "+oldLayoutManage);
            //LayoutManager layoutManager=new BoxLayout(this,BoxLayout.X_AXIS);
            //setLayout(layoutManager);
            spinnerParameterOptions=new SpinnerParameterOptions();
            for(ButtonsABC<Navigate>.ButtonWithEnum button:buttons.buttons()) {
                button.abstractButton.addActionListener(actionListener);
                add(button.abstractButton);
            }
        }
        final Mediator mediator;
        ButtonsABC<Navigate> buttons=new MyButtons();
        SpinnerParameterOptions spinnerParameterOptions;
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                String name=null;
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    name=abstractButton.getName();
                    Logging.mainLogger.info(mediator.model.name+" "+"click: "+name);
                    Navigate e2=buttons.valueOf(name);
                    ButtonsABC<Navigate>.ButtonWithEnum b=buttons.get(e2);
                    if(name!=null&&e2!=null&&b!=null) {
                        //Logging.mainLogger.info(mediator.model.name+" "+"click: "+name);
                        navigate(e2);
                    } else Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
                } else Logging.mainLogger.info(mediator.model.name+" "+"what is action performed in "+e.getSource());
            }
            void navigate(Navigate navigate) {
                Model.navigate(mediator.model,navigate);
            }
        };
        ChangeListener changeListener=new ChangeListener() {
            @Override public void stateChanged(ChangeEvent e) {
                String name=null;
                if(e.getSource() instanceof JSpinner) {
                    JSpinner jSpinner=(JSpinner)e.getSource();
                    name=jSpinner.getName();
                    if(name!=null) try {
                        Parameters parameter=Parameters.valueOf(name);
                        Parameters.change(parameter,jSpinner.getValue());
                    } catch(IllegalArgumentException ex) {
                        Logging.mainLogger.warning(jSpinner+" is not my spinner");
                    }
                } else Logging.mainLogger.warning(name+" "+"what is state changed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1;
    }
}
