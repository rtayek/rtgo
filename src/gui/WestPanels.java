package gui;
import static io.IO.connect;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import controller.GTPBackEnd;
import gui.ButtonsABC.ButtonWithEnum;
import io.*;
import io.IO.End;
import model.Model.Role;
import server.NamedThreadGroup.NamedThread;
public class WestPanels {
    public static enum MyEnums { connect, disconnect }
    static public class NewWestPanel extends JPanel {
        class MyButtons extends ButtonsABC {
            // rename to just buttons.
            // but do it later when the dust settles.
            @Override public void enableAll(Mediator mediator) {
                boolean enable=mediator.gtp==null;
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
                ButtonWithEnum button=buttons.get(enums);
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
        ButtonsABC buttons=new MyButtons();
        final Mediator mediator;
        JLabel modeLabel;
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    String name=abstractButton.getName();
                    Enum e2=buttons.valueOf(name);
                    ButtonWithEnum b=buttons.get(e2);
                    if(name!=null&&e2!=null&&b!=null) if(e2 instanceof WestPanels.MyEnums) {
                        WestPanels.MyEnums e21=(WestPanels.MyEnums)e2;
                        switch(e21) {
                            case connect: // copy of left action so far
                                if(mediator.gtp==null) {
                                    Socket socket=new Socket();
                                    boolean ok=connect(IO.host,IO.defaultPort,1000,socket);
                                    if(ok) {
                                        End socketEnd=new End(socket);
                                        mediator.gtp=new GTPBackEnd(socketEnd,mediator.model);
                                        NamedThread thread=mediator.gtp.startGTP(0);
                                        if(thread==null) {
                                            Logging.mainLogger.severe("3 startGTP returns null!");
                                            throw new RuntimeException("3 can not run backend!");
                                        }
                                        mediator.model.strict=true;
                                        buttons.enableAll(mediator);
                                    } else Logging.mainLogger.warning(mediator.model.name+" "+"connection failed!");
                                    if(mediator.gtp==null) JOptionPane.showMessageDialog(null,"did not connect!");
                                } else Logging.mainLogger.warning(mediator.model.name+" "+"connection failed!");
                                break;
                            case disconnect:
                                if(mediator.gtp!=null) {
                                    mediator.gtp.stop();
                                    mediator.gtp=null;
                                    mediator.model.setRole(Role.anything);
                                    mediator.model.strict=false; // or deafult value
                                } else {
                                    Logging.mainLogger.severe(mediator.model.name+" "+"disconnect when not connected!");
                                }
                                buttons.enableAll(mediator);
                            default:
                                Logging.mainLogger.info(mediator.model.name+" button for"+e21+"  was not handled!");
                        }
                    } else throw new RuntimeException("oops");
                    else Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
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
                boolean enable=mediator.gtp==null;
                // make this one button.
                // maybe later.
                for(ConnectButtons button:values()) switch(button) {
                    case connect:
                        button.abstractButton.enable(enable);
                        break;
                    case disconnect:
                        button.abstractButton.enable(!enable);
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
                        switch(button) {
                            case connect:
                                if(mediator.gtp==null) {
                                    Socket socket=new Socket();
                                    boolean ok=connect(IO.host,IO.defaultPort,1000,socket);
                                    if(ok) {
                                        End socketEnd=new End(socket);
                                        mediator.gtp=new GTPBackEnd(socketEnd,mediator.model);
                                        NamedThread thread=mediator.gtp.startGTP(0);
                                        if(thread==null) {
                                            Logging.mainLogger.severe("3 startGTP returns null!");
                                            throw new RuntimeException("3 can not run backend!");
                                        }
                                        mediator.model.strict=true;
                                        WestPanel.ConnectButtons.enableAll(mediator);
                                        // add some more constants?
                                    } else Logging.mainLogger.warning(mediator.model.name+" "+"connection failed!");
                                    if(mediator.gtp==null) JOptionPane.showMessageDialog(null,"did not connect!");
                                } else Logging.mainLogger.warning(mediator.model.name+" "+"connection failed!");
                                break;
                            case disconnect:
                                if(mediator.gtp!=null) {
                                    mediator.gtp.stop();
                                    mediator.gtp=null;
                                    mediator.model.setRole(Role.anything);
                                    mediator.model.strict=false; // or deafult value
                                } else {
                                    Logging.mainLogger.severe(mediator.model.name+" "+"disconnect when not connected!");
                                }
                                WestPanel.ConnectButtons.enableAll(mediator);
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