package gui;
import java.awt.LayoutManager;
import java.awt.event.*;
import javax.swing.*;
import equipment.Board;
import io.Logging;
import model.Model;
/*
UP("Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)),
DOWN("Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)),
LEFT("Left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)),
RIGHT("Right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
 */
public class SouthPanels {
    public static enum MyEnums { scroll, up, down, left, right, reset; }
    public static class NewSouthPanel extends JPanel {
        class MyButtons extends ButtonsABC<MyEnums> {
            @Override public void enableAll(Mediator mediator) {
                Model model=mediator.model;
                if(model.board()==null) Logging.mainLogger.severe("board is null!");
                else {
                    boolean isTorus=model.board().topology().equals(Board.Topology.torus);
                    scroll.enableButton(isTorus); // uses instance variable
                    boolean isSelected=scroll.abstractButton.isSelected(); // uses instance variable
                    for(MyEnums e:MyEnums.values())
                        if(!e.equals(MyEnums.scroll)) get(e).abstractButton.setEnabled(isSelected&&isTorus);
                }
            }
            ButtonsABC<MyEnums>.ButtonWithEnum scroll=new ButtonWithEnum(MyEnums.scroll,new JToggleButton());
            ButtonsABC<MyEnums>.ButtonWithEnum up=new ButtonWithEnum(MyEnums.up);
            ButtonsABC<MyEnums>.ButtonWithEnum down=new ButtonWithEnum(MyEnums.down);
            ButtonsABC<MyEnums>.ButtonWithEnum right=new ButtonWithEnum(MyEnums.right);
            ButtonsABC<MyEnums>.ButtonWithEnum left=new ButtonWithEnum(MyEnums.left);
            ButtonsABC<MyEnums>.ButtonWithEnum reset=new ButtonWithEnum(MyEnums.reset);
        }
        public NewSouthPanel(Mediator mediator) {
            setName("south panel");
            this.mediator=mediator;
            LayoutManager oldLayoutManage=getLayout();
            // old layout is FlowLayout!
            Logging.mainLogger.info("old layout: "+oldLayoutManage);
            //LayoutManager layoutManager=new BoxLayout(this,BoxLayout.X_AXIS);
            //setLayout(layoutManager);
            for(MyEnums enums:MyEnums.values()) {
                // maybe just iterate over the buttons?
                ButtonsABC<MyEnums>.ButtonWithEnum button=buttons.get(enums);
                button.abstractButton.addActionListener(actionListener);
                add(button.abstractButton);
            }
        }
        ButtonsABC<MyEnums> buttons=new MyButtons();
        final Mediator mediator;
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    String name=abstractButton.getName();
                    MyEnums e2=buttons.valueOf(name);
                    ButtonsABC<MyEnums>.ButtonWithEnum b=buttons.get(e2);
                    if(name!=null&&e2!=null&&b!=null) {
                        //Logging.mainLogger.info(mediator.model.name+" "+"click: "+name);
                        if(e2 instanceof gui.SouthPanels.MyEnums) {
                            gui.SouthPanels.MyEnums e21=(SouthPanels.MyEnums)e2;
                            switch(e21) {
                                // top and bottom are probably not meaningful here.
                                // maybe use the fact that some of the enum names are the same
                                case scroll:
                                    buttons.enableAll(mediator);
                                    mediator.model.resetOffset();
                                    mediator.gamePanel.repaint();
                                    break;
                                case reset:
                                    mediator.model.resetOffset();
                                    mediator.gamePanel.repaint();
                                    break;
                                case up:
                                    mediator.model.offset(0,1);
                                    mediator.gamePanel.repaint();
                                    break;
                                case down:
                                    mediator.model.offset(0,-1);
                                    mediator.gamePanel.repaint();
                                    break;
                                case left:
                                    mediator.model.offset(-1,0);
                                    mediator.gamePanel.repaint();
                                    break;
                                case right:
                                    mediator.model.offset(1,0);
                                    mediator.gamePanel.repaint();
                                    break;
                                default:
                                    Logging.mainLogger.info(mediator.model.name+" "+name+" was not handled!");
                            }
                        }
                    } else Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
                } else Logging.mainLogger.info(mediator.model.name+" "+"what is action performed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1L;
    }
}
