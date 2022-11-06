package server;
import static io.Logging.flushingStreamHandler;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Handler;
import javax.swing.*;
import gui.TextView;
import io.*;
import io.Logging.MyFormatter;
import model.Model;
import utilities.*;
public class ServerGui extends MainGui implements ActionListener { // this is the main ui.
    public ServerGui(MyJApplet applet,TextView textView) { super(applet); this.textView=textView; }
    public static class Black extends JPanel {
        // looks like i invented these
        // to control starting and stopping gui.Mains.
        // yes, we need both to have separate enums.
        // do i really need this ? yes, so enums are different
        // execinhg is necessary. we will neeed a role argument.
        public enum Buttons { // use client property
            // instances have an abstract button
            // why is this one so much more less complicated?
            create,delete,connect,disconnect,other,reset;
            // this would be create/delete in the server gui?
            Buttons(KeyStroke keyStroke) { this((String)null); }
            Buttons() { this((String)null); }
            Buttons(String tooltipText) { this(new JButton(),tooltipText); }
            Buttons(AbstractButton abstractButton) { this(abstractButton,null); }
            Buttons(AbstractButton abstractButton,String tooltipText) { this(abstractButton,tooltipText,null); }
            Buttons(AbstractButton abstractButton,String tooltipText,KeyStroke keyStroke) {
                this.abstractButton=abstractButton;
                abstractButton.setName(name());
                abstractButton.setText(name());
                abstractButton.setFont(new Font("Lucida Console",Font.PLAIN,32));
                this.tooltipText=tooltipText;
                if(tooltipText!=null) abstractButton.setToolTipText(tooltipText);
                this.keyStroke=keyStroke;
                // c.putClientProperty("tayek.go.name",name());
            }
            public static void enableAll(Model model) {
                for(Buttons button:values()) button.abstractButton.enable(true);
            }
            public final String tooltipText;
            public final AbstractButton abstractButton;
            public final KeyStroke keyStroke;
        }
        public Black(String playerName) {
            JLabel name=new JLabel(playerName);
            name.setFont(new Font("Lucida Console",Font.PLAIN,32));
            add(name);
            this.playerName=playerName;
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            for(Buttons button:Buttons.values()) {
                button.abstractButton.addActionListener(actionListener);
                add(button.abstractButton);
                button.abstractButton.enable(true);
            }
        }
        void action(Black.Buttons button) {
            // should this action code be in the panel or here in the meduator?
            Logging.mainLogger.info("go server "+"click: "+button.name());
            System.out.println(button.name());
            switch(button) {
                // top and bottom are probably not meaningful here.
                // maybe use the fact that some of the enum names are the same
                case create:
                    //Buttons.enableAll(model);
                    break;
                case reset:
                    break;
                case delete:
                    break;
                case connect:
                    break;
                case disconnect:
                    break;
                case other:
                    break;
                default:
                    Logging.mainLogger.info("go server  "+button+" was not handled!");
            }
        }
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    String name=abstractButton.getName();
                    if(name!=null&&Buttons.valueOf(name)!=null) action(Buttons.valueOf(name));
                    else Logging.mainLogger.info(name+" "+"unknown action "+e);
                } else Logging.mainLogger.info("what is action performed in "+e.getSource());
            }
        };
        String playerName;
        private static final long serialVersionUID=1L;
    }
    public static class White extends JPanel {
        public enum Buttons { // use client property
            // instances have an abstract button
            // why is this one so much more less complicated?
            create,delete,connect,disconnect,other,reset;
            Buttons(KeyStroke keyStroke) { this((String)null); }
            Buttons() { this((String)null); }
            Buttons(String tooltipText) { this(new JButton(),tooltipText); }
            Buttons(AbstractButton abstractButton) { this(abstractButton,null); }
            Buttons(AbstractButton abstractButton,String tooltipText) { this(abstractButton,tooltipText,null); }
            Buttons(AbstractButton abstractButton,String tooltipText,KeyStroke keyStroke) {
                this.abstractButton=abstractButton;
                abstractButton.setName(name());
                abstractButton.setText(name());
                abstractButton.setFont(new Font("Lucida Console",Font.PLAIN,32));
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
        public White(String playerName) {
            JLabel name=new JLabel(playerName);
            name.setFont(new Font("Lucida Console",Font.PLAIN,32));
            add(name);
            this.playerName=playerName;
            setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            for(Buttons button:Buttons.values()) {
                button.abstractButton.addActionListener(actionListener);
                add(button.abstractButton);
                button.abstractButton.enable(true);
            }
        }
        void action(White.Buttons button) {
            // should this action code be in the panel or here in the meduator?
            Logging.mainLogger.info("go server "+"click: "+button.name());
            System.out.println(button.name());
            switch(button) {
                // top and bottom are probably not meaningful here.
                // maybe use the fact that some of the enum names are the same
                case create:
                    //Buttons.enableAll(model);
                    break;
                case reset:
                    break;
                case delete:
                    break;
                case connect:
                    break;
                case disconnect:
                    break;
                case other:
                    break;
                default:
                    Logging.mainLogger.info("go server  "+button+" was not handled!");
            }
        }
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object object=e.getSource();
                if(object instanceof AbstractButton) {
                    AbstractButton abstractButton=((AbstractButton)object);
                    String name=abstractButton.getName();
                    if(name!=null&&Buttons.valueOf(name)!=null) action(Buttons.valueOf(name));
                    else Logging.mainLogger.info(name+" "+"unknown action "+e);
                } else Logging.mainLogger.info("what is action performed in "+e.getSource());
            }
        };
        String playerName;
        private static final long serialVersionUID=1L;
    }
    @Override public void actionPerformed(ActionEvent e) {
        //if(e.getSource().equals(timer)) { frame.dispose(); System.exit(0); }
    }
    void standard() { //my standard border layout
        setLayout(new BorderLayout());
        if(frame!=null) frame.setResizable(true);
        else;// ???
        JPanel north=new JPanel();
        north.setBackground(Color.green);
        north.setPreferredSize(new Dimension(width,depth));
        north.setMinimumSize(new Dimension(width,depth));
        add(north,BorderLayout.PAGE_START);
        JPanel east=new JPanel();
        east.setBackground(Color.pink);
        east.setPreferredSize(new Dimension(width,depth));
        east.setMinimumSize(new Dimension(width,depth));
        add(east,BorderLayout.LINE_END);
        JPanel west=new JPanel();
        west.setBackground(Color.yellow);
        west.setPreferredSize(new Dimension(width,depth));
        west.setMinimumSize(new Dimension(width,depth));
        add(west,BorderLayout.LINE_START);
        JPanel south=new JPanel();
        south.setPreferredSize(new Dimension(width,depth));
        south.setMinimumSize(new Dimension(width,depth));
        south.setBackground(Color.cyan);
        add(south,BorderLayout.PAGE_END);
    }
    @Override public String title() { return "TGO Server"; }
    @Override public void addContent() {
        standard();
        JPanel center=new JPanel();
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        Dimension d=new Dimension(640,480);
        center.setSize(d);
        center.setPreferredSize(d);
        center.setMaximumSize(d);
        center.setMinimumSize(d);
        center.setBorder(BorderFactory.createLineBorder(Color.black));
        //buttonPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        //for(PlayerButtonPanel.Buttons button:PlayerButtonPanel.Buttons.values())
        //    button.abstractButton.setBorder(BorderFactory.createLineBorder(Color.black));
        center.add(new JLabel("1"));
        center.add(black);
        center.add(new JLabel("2"));
        center.add(white);
        // won't wprk
        // need 2 copioes of the button panel!
        //        black.Buttons.enableAll(new )
        Black.Buttons.enableAll(new Model());
        add(center,BorderLayout.CENTER);
        if(!isApplet()) frame().pack();
        gui.Main.listComponentsIn(frame().getContentPane(),null,false);
    }
    public static void addTextView(TextView textView) {
        System.out.println("add text view");
        Logging.mainLogger.info("before tee");
        Tee tee=new Tee(System.out);
        tee.addOutputStream(textView.taOutputStream);
        tee.addOutputStream(System.err); // seems to do nothing?
        System.setOut(tee.printStream);
        System.setErr(tee.printStream);
        Logging.mainLogger.info("after tee");
    }
    public static Tee hookup(OutputStream outputStream) {
        // probably obsolete
        PrintStream out=System.out;
        PrintStream err=System.err;
        Tee tee=new Tee(outputStream); // tee main uses sysout
        //TextView.createAndShowGui(textView);
        synchronized(System.out) { // we need both in this case.
            tee.addOutputStream(new PrintStream(out)); // required
            tee.addOutputStream(new PrintStream(err));
            // old copies of sysout and syserr will show up.
            // since we are adding sysout here
            // why are we doing a setOut below?
        }
        tee.setOut();
        tee.setErr();
        // why not just add these like above??
        // oh, new stuff written to sysout and syserr will show up.
        Handler handler=flushingStreamHandler(tee.printStream);
        handler.setFormatter(new MyFormatter());
        Logging.mainLogger.addHandler(handler);
        Tee.printStuff(tee.printStream,out,err);
        return tee;
    }
    public static ServerGui runServerGui() {
        TextView textView=new TextView();
        boolean oneWay=true;
        if(oneWay) {
            TextView.createAndShowGui(textView);
            //addTextView(textView);
            hookup(textView.taOutputStream);
        } else {
            Tee tee=Tee.tee(new File("out.txt"));
            TextView.createAndShowGui(textView);
            tee.addOutputStream(textView.taOutputStream);
        }
        ServerGui serverGui=new ServerGui(null,textView);
        serverGui.frame().setTitle("Go Server");
        return serverGui;
    }
    public static void main(String[] args) {
        // add a panel somewhere to control logging
        runServerGui();
    }
    GoServer goServer;
    //gui.Main black=gui.Main.run();
    //black.frame.setTitle("Black player");
    //gui.Main white=gui.Main.run();
    //white.frame.setTitle("white player");
    //gui.Main black,white;
    Black black=new Black("blact");
    White white=new White("white");
    final gui.TextView textView;
    static int width=25,depth=25;
    private static final long serialVersionUID=1L;
}
