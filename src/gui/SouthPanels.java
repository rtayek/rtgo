package gui;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import equipment.Board;
import gui.ButtonsABC.ButtonWithEnum;
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
        class MyButtons extends ButtonsABC {
            // rename to just buttons.
            // but do it later when the dust settles.
            @Override public void enableAll(Mediator mediator) {
                Model model=mediator.model;
                if(model.board()==null) Logging.mainLogger.severe("board is null!");
                else {
                    boolean isTorus=model.board().topology().equals(Board.Topology.torus);
                    scroll.enableButton(isTorus); // uses instance variable
                    boolean isSelected=scroll.abstractButton.isSelected(); // uses instance variable
                    //boolean isSelected=get(MyEnums.scroll).abstractButton.isSelected(); // maybe use this instead?
                    for(MyEnums e:MyEnums.values())
                        if(!e.equals(MyEnums.scroll)) get(e).abstractButton.enable(isSelected&&isTorus);
                }
            }
            ButtonWithEnum<MyEnums> scroll=new ButtonWithEnum<MyEnums>(MyEnums.scroll,new JToggleButton());
            ButtonWithEnum<MyEnums> up=new ButtonWithEnum<MyEnums>(MyEnums.up);
            ButtonWithEnum<MyEnums> down=new ButtonWithEnum<MyEnums>(MyEnums.down);
            ButtonWithEnum<MyEnums> right=new ButtonWithEnum<MyEnums>(MyEnums.right);
            ButtonWithEnum<MyEnums> left=new ButtonWithEnum<MyEnums>(MyEnums.left);
            ButtonWithEnum<MyEnums> reset=new ButtonWithEnum<MyEnums>(MyEnums.reset);
        }
        public NewSouthPanel(Mediator mediator) {
            setName("south panel");
            this.mediator=mediator;
            LayoutManager oldLayoutManage=getLayout();
            // old layout is FlowLayout!
            System.out.println("old layout: "+oldLayoutManage);
            //LayoutManager layoutManager=new BoxLayout(this,BoxLayout.X_AXIS);
            //setLayout(layoutManager);
            for(MyEnums enums:MyEnums.values()) {
                // maybe just iterate over the buttons?
                ButtonWithEnum button=buttons.get(enums);
                button.abstractButton.addActionListener(actionListener);
                add(button.abstractButton);
            }
        }
        ButtonsABC buttons=new MyButtons();
        final Mediator mediator;
        static List<Board.Topology> types=Arrays.asList(Board.Topology.values());
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    String name=abstractButton.getName();
                    Enum e2=buttons.valueOf(name);
                    ButtonWithEnum b=buttons.get(e2);
                    if(name!=null&&e2!=null&&b!=null) {
                        //Logging.mainLogger.info(mediator.model.name+" "+"click: "+name);
                        if(e2 instanceof gui.SouthPanels.MyEnums) {
                            gui.SouthPanels.MyEnums e21=(SouthPanels.MyEnums)e2;
                            switch(e21) {
                                // top and bottom are probably not meaningful here.
                                // maybe use the fact that some of the enum names are the same
                                case scroll:
                                    SouthPanel.Buttons.enableAll(mediator);
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
    public static class SouthPanel extends JPanel {
        // 9/25/22 move is offser when scrolling in torus mode!
        public enum Buttons { // use client property
            // instances have an abstract button
            // why is this one so much more less complicated?
            scroll(new JToggleButton()),up,down,left,right,reset;
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
            public static void enableAll(Mediator mediator) {
                Model model=mediator.model;
                if(model.board()==null) Logging.mainLogger.severe("board is null!");
                else {
                    boolean isTorus=model.board().topology().equals(Board.Topology.torus);
                    scroll.abstractButton.enable(isTorus);
                    boolean isSelected=scroll.abstractButton.isSelected();
                    for(Buttons button:values()) if(!button.equals(scroll)) button.abstractButton.enable(isSelected&&isTorus);
                }
            }
            public final String tooltipText;
            public final AbstractButton abstractButton;
            public final KeyStroke keyStroke;
        }
        public SouthPanel(Mediator mediator) {
            setName("south panel");
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
                    if(name!=null&&gui.SouthPanels.SouthPanel.Buttons.valueOf(name)!=null) {
                        Buttons button=Buttons.valueOf(name);
                        Logging.mainLogger.info(mediator.model.name+" "+"click: "+button.name());
                        switch(button) {
                            // top and bottom are probably not meaningful here.
                            // maybe use the fact that some of the enum names are the same
                            case scroll:
                                SouthPanel.Buttons.enableAll(mediator);
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
                                Logging.mainLogger.info(mediator.model.name+" "+button+" was not handled!");
                        }
                    } else Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
                } else Logging.mainLogger.info(mediator.model.name+" "+"what is action performed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1L;
    }
}
