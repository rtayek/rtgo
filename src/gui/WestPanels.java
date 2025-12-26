package gui;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.net.InetAddress;
import javax.swing.*;
import io.Logging;
import model.Model;
import model.Model.Role;
public class WestPanels {
    public static enum MyEnums { connect, disconnect }
    static public class NewWestPanel extends JPanel {
        class MyButtons extends ButtonsABC<MyEnums> {
            // rename to just buttons.
            // but do it later when the dust settles.
            @Override public void enableAll(Mediator mediator) {
                boolean enable=mediator.model.gtp==null;
                // since we only one type of enum:
                for(MyEnums e:MyEnums.values()) switch(e) {
                    // this one is pretty simple.
                    case connect:
                        buttons.get(e).enableButton(enable);
                        break;
                    case disconnect:
                        buttons.get(e).enableButton(!enable);
                        break;
                    default:
                        Logging.mainLogger.info(e+" was not handled!");
                }
            }
            //ButtonWithEnum<MyEnums> connect=new ButtonWithEnum<MyEnums>(MyEnums.connect);
            //ButtonWithEnum<MyEnums> disconnect=new ButtonWithEnum<MyEnums>(MyEnums.disconnect);
            {
                add(MyEnums.class);
            }
        }
        public NewWestPanel(Mediator mediator) {
            // this may not work well.
            // the action listener - we can do value of name.
            // we need to call left action with the value of name.
            // looks good so far.
            setName("west panel");//
            this.mediator=mediator;
            LayoutManager layoutManager=new BoxLayout(this,BoxLayout.PAGE_AXIS);
            setLayout(layoutManager);
            for(MyEnums enums:MyEnums.values()) {
                // better to iterate over the buttons?
                ButtonsABC<MyEnums>.ButtonWithEnum button=buttons.get(enums);
                button.abstractButton.addActionListener(actionListener);
                add(button.abstractButton);
            }
            String localHost=null;
            try {
                localHost=InetAddress.getLocalHost().getHostAddress();
            } catch(java.net.UnknownHostException e) {
                Logging.mainLogger.info(mediator.model.name+" "+"caught: "+e);
            }
            JLabel label=new JLabel("localhost: "+localHost);
            add(label);
            Role mode=mediator.model.role();
            modeLabel=new JLabel("Mode: "+mode);
            add(modeLabel);
        }
        void setPlayerColor() { Role mode=mediator.model.role(); modeLabel.setText("Mode: "+mode); }
        ButtonsABC<MyEnums> buttons=new MyButtons();
        final Mediator mediator;
        JLabel modeLabel;
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    String name=abstractButton.getName();
                    MyEnums e2=buttons.valueOf(name);
                    ButtonsABC<MyEnums>.ButtonWithEnum b=buttons.get(e2);
                    if(name!=null&&e2!=null&&b!=null) {
                        WestPanels.MyEnums e21=e2;
                        Model model=mediator.model;
                        boolean ok=false;
                        switch(e21) {
                            case connect: // copy of left action so far
                                ok=Model.connectToServer(model);
                                if(model.gtp==null) Toast.toast("did not connect!");
                                buttons.enableAll(mediator);
                                break;
                            case disconnect:
                                ok=Model.disconnectFromServer(model);
                                buttons.enableAll(mediator);
                            default:
                                Logging.mainLogger.info(mediator.model.name+" button for"+e21+"  was not handled!");
                        }
                    } else Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
                } else Logging.mainLogger.info(mediator.model.name+" "+"what is action performed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1L;
    }
    public static class WestPanel extends JPanel {
        public enum ConnectButtons {
            connect,disconnect;
            ConnectButtons(KeyStroke keyStroke) { this((String)null); }
            ConnectButtons() { this((String)null); }
            ConnectButtons(String tooltipText) { this(new JButton(),tooltipText); }
            ConnectButtons(AbstractButton abstractButton) { this(abstractButton,null); }
            ConnectButtons(AbstractButton abstractButton,String tooltipText) { this(abstractButton,tooltipText,null); }
            ConnectButtons(AbstractButton abstractButton,String tooltipText,KeyStroke keyStroke) {
                this.abstractButton=abstractButton;
                abstractButton.setName(name());
                abstractButton.setText(name());
                this.tooltipText=tooltipText;
                if(tooltipText!=null) abstractButton.setToolTipText(tooltipText);
                this.keyStroke=keyStroke;
            }
            public static void enableAll(Mediator mediator) {
                // the new one will be different
                // the new one may not need to be static or may not wish to.
                boolean enable=mediator.model.gtp==null;
                // make this one button.
                // maybe later.
                for(ConnectButtons button:values()) switch(button) {
                    case connect:
                        button.abstractButton.setEnabled(enable);
                        break;
                    case disconnect:
                        button.abstractButton.setEnabled(!enable);
                        break;
                    default:
                        Logging.mainLogger.info(button+" was not handled!");
                }
            }
            public final String tooltipText;
            public final AbstractButton abstractButton;
            public final KeyStroke keyStroke;
        }
        public WestPanel(Mediator mediator) {
            setName("west panel");
            this.mediator=mediator;
            LayoutManager layoutManager=new BoxLayout(this,BoxLayout.PAGE_AXIS);
            setLayout(layoutManager);
            for(ConnectButtons button:ConnectButtons.values()) {
                button.abstractButton.addActionListener(actionListener);
                add(button.abstractButton);
            }
            String localHost=null;
            try {
                localHost=InetAddress.getLocalHost().getHostAddress();
            } catch(java.net.UnknownHostException e) {
                Logging.mainLogger.info(mediator.model.name+" "+"caught: "+e);
            }
            JLabel label=new JLabel("localhost: "+localHost);
            add(label);
            Role mode=mediator.model.role();
            modeLabel=new JLabel("Mode: "+mode);
            add(modeLabel);
        }
        void setPlayerColor() { Role mode=mediator.model.role(); modeLabel.setText("Mode: "+mode); }
        final Mediator mediator;
        JLabel modeLabel;
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    String name=abstractButton.getName();
                    if(name!=null&&ConnectButtons.valueOf(name)!=null) {
                        ConnectButtons button=ConnectButtons.valueOf(name);
                        Model model=mediator.model;
                        switch(button) {
                            case connect:
                                Model.connectToServer(model);
                                WestPanel.ConnectButtons.enableAll(mediator);
                                break;
                            case disconnect:
                                Model.disconnectFromServer(model);
                                WestPanel.ConnectButtons.enableAll(mediator);
                                break; // was not here?
                            default:
                                Logging.mainLogger.info(mediator.model.name+" "+button+" was not handled!");
                        }
                    } else Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
                } else Logging.mainLogger.info(mediator.model.name+" "+"what is action performed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1L;
    }
}
