package gui;
import java.awt.event.*;
import javax.swing.*;
import equipment.Board;
import io.Logging;
import model.Model;
public class EastPanels {
    public static enum MyEnums { scroll, foo, bar }
    public static class NewEastPanel extends JPanel {
        class MyButtons extends ButtonsABC<MyEnums> {
            @Override public void enableAll(Mediator mediator) {
                Model model=mediator.model;
                if(model.board()==null) Logging.mainLogger.config("board is null!");
                else {
                    boolean isTorus=model.board().topology().equals(Board.Topology.torus);
                    scroll.enableButton(isTorus); // uses instance variable
                    boolean isSelected=scroll.abstractButton.isSelected(); // uses instance variable
                    for(MyEnums e:MyEnums.values())
                        if(!e.equals(MyEnums.scroll)) get(e).abstractButton.setEnabled(isSelected&&isTorus);
                }
            }
            ButtonsABC<MyEnums>.ButtonWithEnum scroll=new ButtonWithEnum(MyEnums.scroll,new JToggleButton());
            ButtonsABC<MyEnums>.ButtonWithEnum foo=new ButtonWithEnum(MyEnums.foo);
            ButtonsABC<MyEnums>.ButtonWithEnum bar=new ButtonWithEnum(MyEnums.bar);
        }
        public NewEastPanel(Mediator mediator) {
            setName("east panel");
            this.mediator=mediator;
            for(ButtonsABC<MyEnums>.ButtonWithEnum button:buttons.buttons()) {
                button.abstractButton.addActionListener(actionListener);
                add(button.abstractButton);
            }
        }
        final Mediator mediator;
        ButtonsABC<MyEnums> buttons=new MyButtons();
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    String name=abstractButton.getName();
                    MyEnums e2=buttons.valueOf(name);
                    ButtonsABC<MyEnums>.ButtonWithEnum b=buttons.get(e2);
                    if(name!=null&&e2!=null&&b!=null) { //
                        Logging.mainLogger.info("click: "+name);
                        switch(e2) {
                            case foo:
                                mediator.gamePanel.repaint();
                                break;
                            case bar:
                                mediator.gamePanel.repaint();
                                break;
                            default:
                                Logging.mainLogger.info(name+" was not handled!");
                        }
                    } else Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
                } else Logging.mainLogger.info(mediator.model.name+" "+"what is action performed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1L;
    }
}
