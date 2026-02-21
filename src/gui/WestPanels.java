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
            @Override public void enableAll(Mediator mediator) {
                boolean enable=mediator.model.gtp==null;
                for(MyEnums e:MyEnums.values()) switch(e) {
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
            {
                add(MyEnums.class);
            }
        }
        public NewWestPanel(Mediator mediator) {
            setName("west panel");//
            this.mediator=mediator;
            LayoutManager layoutManager=new BoxLayout(this,BoxLayout.PAGE_AXIS);
            setLayout(layoutManager);
            for(MyEnums enums:MyEnums.values()) {
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
                        switch(e21) {
                            case connect:
                                Model.connectToServer(model);
                                if(model.gtp==null) Toast.toast("did not connect!");
                                buttons.enableAll(mediator);
                                break;
                            case disconnect:
                                Model.disconnectFromServer(model);
                                buttons.enableAll(mediator);
                                break;
                            default:
                                Logging.mainLogger.info(mediator.model.name+" button for"+e21+"  was not handled!");
                        }
                    } else Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
                } else Logging.mainLogger.info(mediator.model.name+" "+"what is action performed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1L;
    }
}
