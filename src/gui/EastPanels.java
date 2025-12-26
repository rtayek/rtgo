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
            // rename to just buttons.
            // but do it later when the dust settles.
            @Override public void enableAll(Mediator mediator) {
                Model model=mediator.model;
                if(model.board()==null) Logging.mainLogger.config("board is null!");
                else {
                    boolean isTorus=model.board().topology().equals(Board.Topology.torus);
                    scroll.enableButton(isTorus); // uses instance variable
                    boolean isSelected=scroll.abstractButton.isSelected(); // uses instance variable
                    //boolean isSelected=get(MyEnums.scroll).abstractButton.isSelected(); // maybe use this instead?
                    for(MyEnums e:MyEnums.values())
                        if(!e.equals(MyEnums.scroll)) get(e).abstractButton.setEnabled(isSelected&&isTorus);
                }
            }
            // remove instance variables!
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
    public static class EastPanel extends JPanel {
        public enum Buttons { // use client property
            // instances have an abstract button
            // why is this one less complicated?
            scroll(new JToggleButton()),foo,bar;
            //scroll(new JToggleButton()),up,down,left,right,reset;
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
            }
            public static void enableAll(Mediator mediator) {
                // this code does not work!
                // but it looks just like the old south panel below.
                Model model=mediator.model;
                if(model.board()==null) Logging.mainLogger.severe("board is null!");
                else {
                    boolean isTorus=model.board().topology().equals(Board.Topology.torus);
                    scroll.abstractButton.setEnabled(isTorus);
                    boolean isSelected=scroll.abstractButton.isSelected();
                    for(Buttons button:values())
                        if(!button.equals(scroll)) button.abstractButton.setEnabled(isSelected&&isTorus);
                }
            }
            public static void oldSouthPanelenableAll(Mediator mediator) {
                // this code works in south panel
                Model model=mediator.model;
                if(model.board()==null) Logging.mainLogger.config("board is null!");
                else {
                    boolean isTorus=model.board().topology().equals(Board.Topology.torus);
                    scroll.abstractButton.setEnabled(isTorus);
                    boolean isSelected=scroll.abstractButton.isSelected();
                    for(Buttons button:values())
                        if(!button.equals(scroll)) button.abstractButton.setEnabled(isSelected&&isTorus);
                }
            }
            public final String tooltipText;
            public final AbstractButton abstractButton;
            public final KeyStroke keyStroke;
        }
        public EastPanel(Mediator mediator) {
            setName("east panel");
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
                            case foo:
                                mediator.gamePanel.repaint();
                                break;
                            case bar:
                                mediator.gamePanel.repaint();
                                break;
                            default:
                                Logging.mainLogger.info(mediator.model.name+" "+button+" was not handled!");
                        }
                    } else {
                        Logging.mainLogger.info(mediator.model.name+" "+"unknown action "+e);
                    }
                } else Logging.mainLogger.info(mediator.model.name+" "+"what is action performed in "+e.getSource());
            }
        };
        private static final long serialVersionUID=1L;
    }
}
