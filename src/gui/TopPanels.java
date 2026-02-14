package gui;
import static com.tayek.util.io.PropertiesIO.writePropertiesFile;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.util.Properties;
import javax.swing.*;
import javax.swing.event.*;
import gui.Spinners.NewSpinners.ParameterSpinners;
import gui.Spinners.NewSpinners.SpinnersABC.SpinnerWithAnEnum;
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
            if(mediator.useSpinnerOptions) {
                spinnerParameterOptions=new SpinnerParameterOptions();
                // add these in later?
                // looks like we are not doing that.
                // so lets try it.
                // are these the right ones?
                for(ButtonsABC<Navigate>.ButtonWithEnum button:buttons.buttons()) {
                    button.abstractButton.addActionListener(actionListener);
                    add(button.abstractButton);
                }
            } else {
                // are these the right ones?
                for(ButtonsABC<Navigate>.ButtonWithEnum button:buttons.buttons()) {
                    button.abstractButton.addActionListener(actionListener);
                    add(button.abstractButton);
                }
            }
        }
        //for(OldSpinners spinner:map.values())
        //    spinner.setValueInWidgetFromCurrentValue();
        // old spinners
        public void change(SpinnerWithAnEnum<?> button,Object value) {
            //Logging.mainLogger.info(parameter.name()+" changed from: "+parameter.currentValue()+"+ to: "+value);
            // move this inside spinners?
            // make thsi an instance method
            boolean ok=button.setValueInWisget(value);
            if(!ok) Logging.mainLogger.info(value+" is not ok!");
            Properties properties=new Properties();
            // looks like loadropertiesFromCurrentValues()?
            // yes. but this is for spinner with an enum?
            // and not the old parameter
            for(SpinnerWithAnEnum<?> b:spinners.buttons()) properties.put(b.t.name(),b.currentValue.toString());
            //Logging.mainLogger.config("writing new properties to: "+propertiesFilename+": "+properties);
            writePropertiesFile(properties,Parameters.propertiesFilename);
        }
        final Mediator mediator;
        ButtonsABC<Navigate> buttons=new MyButtons();
        // part of new spinner stuff
        // this is the single instance
        // maybe move this to mediator when the dust setles.
        //SpinnersABC spinners=new ParameterSpinners();
        // try to make the above work!
        ParameterSpinners spinners=new ParameterSpinners();
        //ParameterSpinners parameterSpinners=new ParameterSpinners();
        // the rest of the my buttons are in panels.
        // maybe this belongs in the top panel?
        // maybe all of the instances belong in the mediator
        SpinnerParameterOptions spinnerParameterOptions; // new stuff
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
                    Logging.mainLogger.info(mediator.model.name+" "+"click: "+name);
                    Enum<?> e2=spinners.valueOf(name);
                    SpinnerWithAnEnum<?> b=spinners.get(e2);
                    if(name!=null&&e2!=null&&b!=null) {
                        if(e2 instanceof Parameters) {
                            if(Parameters.valueOf(name)!=null) {
                                // maybe not be working for new spinner
                                // it does not, new spinners needs it's own version
                                change(b,jSpinner.getValue());
                            } else Logging.mainLogger.warning(jSpinner+" is not my spinner");
                        } else Logging.mainLogger.warning(e2+" is not a parameter");
                    }
                } else Logging.mainLogger.warning(name+" "+"what is state changed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1;
    }
    public static class TopPanel extends JPanel {
        // maybe don't make another enum here, just use the navigate enum;
        // instances have an abstract button
        static class Butttons_ { // replacement for enum buttons.
            Butttons_(Navigate navigate) {
                this.navigate=navigate;
                this.tooltipText=null;
                this.abstractButton=null;
                this.keyStroke=null;
            }
            final Navigate navigate;
            final String tooltipText;
            final AbstractButton abstractButton;
            final KeyStroke keyStroke;
        }
        public enum Buttons {
            top,bottom,up,down,left,right,delete("Delete this branch");
            Buttons(KeyStroke keyStroke) { this((String)null); }
            Buttons() { this((String)null); }
            Buttons(String tooltipText) { this(new JButton(),tooltipText); }
            Buttons(AbstractButton abstractButton) { this(abstractButton,null); }
            Buttons(AbstractButton abstractButton,String tooltipText) {
                this(abstractButton,tooltipText,(KeyStroke)null);
            }
            Buttons(AbstractButton abstractButton,String tooltipText,KeyStroke keyStroke) {
                this.abstractButton=abstractButton;
                abstractButton.setName(name());
                abstractButton.setText(name());
                this.tooltipText=tooltipText;
                if(tooltipText!=null) abstractButton.setToolTipText(tooltipText);
                this.keyStroke=keyStroke;
                // c.putClientProperty("tayek.go.name",name());
            }
            void enable(Model model) {
                // probably should have used mediator instead of model.
                try {
                    // looks like we are using the fact that some of the enum names are the same.
                    if(Navigate.valueOf(name())!=null) {
                        boolean canDo=Navigate.valueOf(name()).canDo(model);
                        abstractButton.setEnabled(Navigate.valueOf(name()).canDo(model));
                    } else Logging.mainLogger.info(model.name+" "+"enable can not find "+this);
                } catch(IllegalArgumentException e) {
                    Logging.mainLogger.info("caught: "+e);
                }
            }
            static Navigate find(KeyStroke keyStroke) {
                for(Buttons button:values()) if(button.keyStroke.equals(keyStroke))
                    if(Navigate.valueOf(button.name())!=null) return Navigate.valueOf(button.name());
                return null;
            }
            static void enableAll(Mediator mediator) { // call from node changed
                Model model=mediator.model;
                for(Buttons button:values()) button.enable(model);
                // for(Buttons button:values())
                // Model.mumble(button+" "+button.abstractButton.isEnabled());
            }
            final String tooltipText;
            final AbstractButton abstractButton;
            final KeyStroke keyStroke;
        }
        public TopPanel(Mediator mediator) {
            setName("top panel");
            this.mediator=mediator;
            for(Buttons button:Buttons.values()) {
                button.abstractButton.addActionListener(actionListener);
                add(button.abstractButton);
            }
        }
        final Mediator mediator;
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    String name=abstractButton.getName();
                    if(name!=null&&Buttons.valueOf(name)!=null) {
                        Buttons button=Buttons.valueOf(name);
                        Logging.mainLogger.info(mediator.model.name+" "+"click: "+button.name());
                        switch(button) {
                            // http://senseis.xmp.net/diagrams/33/9a1c19ffc34010cbda05a82dcc5a1788.sgf
                            // maybe use the fact that some of the enum_ names are the same.
                            case top:
                                mediator.model.top();
                                break;
                            case bottom:
                                mediator.model.bottom();
                                break;
                            case up:
                                mediator.model.up();
                                break;
                            case down:
                                mediator.model.down(0);
                                break;
                            case left:
                                mediator.model.left();
                                break;
                            case right:
                                mediator.model.right();
                                break;
                            case delete:
                                mediator.model.delete();
                                break;
                            default:
                                Logging.mainLogger.info(mediator.model.name+" "+button+" was not handled!");
                        }
                    } else Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
                } else Logging.mainLogger.info(mediator.model.name+" "+"what is action performed in "+e.getSource());
            }
        };
        private boolean ignoreChange;
        static ChangeListener changeListener=new ChangeListener() {
            @Override public void stateChanged(ChangeEvent e) {
                String name=null;
                if(e.getSource() instanceof JSpinner) {
                    JSpinner jSpinner=(JSpinner)e.getSource();
                    name=jSpinner.getName();
                    if(name!=null) if(Parameters.valueOf(name)!=null) {
                        // maybe not be working for new spinner
                        // it does not, new spinners needs it's own version
                        Parameters.change(Parameters.valueOf(name),jSpinner.getValue());
                    } else Logging.mainLogger.warning(jSpinner+" is not my spinner");
                } else Logging.mainLogger.warning(name+" "+"what is state changed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1L;
    }
}
