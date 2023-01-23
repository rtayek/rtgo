package gui;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.xml.stream.events.StartDocument;
import io.*;
import model.Model;
import utilities.*;
public class Main extends MainGui implements ActionListener,ComponentListener { // this is the main ui.
    public Main(MyJApplet applet,Model model,TextView textView) {
        super(applet);
        this.model=model;
        this.textView=textView;
        if(!isApplet()) {
            frame().addComponentListener(this);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    textView.teardownTees();
                    System.out.println("closing");
                    System.err.println("closing");
                }
            });
        }
        timer.start();
        setName("main game panel");
    }
    @Override public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(timer)) {
            frame.dispose();
            //System.exit(0);
        }
    }
    @Override public String title() { return "TGO Client"; }
    @Override public void addContent() { // runs on awt thread!
        // if this was a remote gui
        // how would we do this?
        //
        System.out.println("enter addContent().");
        setLayout(new BorderLayout());
        if(frame!=null) frame.setResizable(true);
        else;// ???
        // looks like these panels get overwritten?
        // by code in mediator?
        north=new JPanel();
        north.setName("initial north panel");
        Color color=Color.green.darker();
        north.setBackground(color);
        north.setPreferredSize(new Dimension(100,100));
        north.setMinimumSize(new Dimension(100,100));
        add(north,BorderLayout.PAGE_START);
        //BorderLayout x=(BorderLayout)getLayout();
        //Component old=x.getLayoutComponent(BorderLayout.PAGE_START);
        //if(old!=null) model.mumble("in main, old="+old);
        //else model.mumble("old is null");
        east=new JPanel();
        east.setName("initial east panel");
        color=Color.pink.darker();
        east.setBackground(color);
        east.setPreferredSize(new Dimension(100,100));
        east.setMinimumSize(new Dimension(100,100));
        add(east,BorderLayout.LINE_END);
        west=new JPanel();
        west.setName("initial west panel");
        color=Color.yellow.darker();
        west.setBackground(color);
        west.setPreferredSize(new Dimension(100,100));
        west.setMinimumSize(new Dimension(100,100));
        add(west,BorderLayout.LINE_START);
        south=new JPanel();
        south.setName("initial south panel");
        south.setPreferredSize(new Dimension(100,100));
        south.setMinimumSize(new Dimension(100,100));
        south.setBackground(Color.blue);
        add(south,BorderLayout.PAGE_END);
        Container container=frame().getContentPane();
        //System.out.println("main frame contains");
        //listComponentsIn(container,null,false);
        mediator=new Mediator(model,this,textView);
        System.out.println("exit addContent().");
    }
    public static void addTextViewOutputStreams(TextView textView) {
        System.out.println("add text view");
        Logging.mainLogger.info("before tee");
        Tee tee=new Tee(System.out); // keep around so we can untee?
        tee.addOutputStream(textView.taOutputStream);
        tee.addOutputStream(System.err); // seems to do nothing?
        // 10/25/22 maybe not now.
        tee.setOut();
        //System.setErr(tee.printStream);
        Logging.mainLogger.info("after tee");
    }
    public static Color darker(Color color) {
        return new Color(Math.max((int)(color.getRed()*darkFactor),0),Math.max((int)(color.getGreen()*darkFactor),0),
                Math.max((int)(color.getBlue()*darkFactor),0),color.getAlpha());
    }
    public static void darken(Component c) {
        if(c instanceof JLabel) {
            Color color=((JLabel)c).getBackground();
            //color=color.darker().darker().darker().darker();
            ((JLabel)c).setBackground(color);
        } else if(c instanceof JComponent) {
            if(!(c instanceof JButton)) {
                Color color=((JComponent)c).getBackground();
                color=darker(color);
                ((JComponent)c).setBackground(color);
                c.repaint();
            }
        } else if(c instanceof JFrame) {
            Color color=((JFrame)c).getBackground();
            //color=color.darker().darker().darker().darker();
            ((JComponent)c).setBackground(color);
        }
    }
    Consumer<Component> consumer=(c)->darken(c);
    public static void darken(Container parent) { // over complicated. clean up later.
        for(Component c:parent.getComponents()) {
            //System.out.println(c.getClass().getSimpleName());
            darken(c);
            if(c instanceof Container) darken((Container)c);
        }
    }
    public static void listComponentsIn(Component component,Indent indent,boolean all) {
        if(indent==null) indent=new Indent("    ");
        if(component!=null) {
            String name=component.getName();
            String className=component.getClass().getSimpleName();
            if(component instanceof Container||all) System.out.println(indent.indent()+" "+name+" "+className);
            if(component instanceof Container) {
                Container container=(Container)component;
                for(Component c:container.getComponents()) {
                    indent.in();
                    listComponentsIn(c,indent,all);
                    indent.out();
                }
            }
        }
    }
    public static Main run(String name) {
        Main main=null;
        TextView textView=new TextView();
        TextView.createAndShowGui(textView);
        textView.setupTees();
        System.out.println("out foo");
        textView.tee.printStream.println("printstream foo");
        System.err.println("err foo");
        textView.tee2.printStream.println("printstream 2 foo");
        Model model=new Model();
        if(startWithFile!=null) {
            System.out.println("restoring: "+startWithFile);
            model.restore(IO.toReader(startWithFile));
            }
        main=new Main(null,model,useTextView?textView:null);
        main.moveToLocationFor(name);
        String newTitle=main.title()+" "+name;
        main.frame.setTitle(newTitle);
        if(main.textView!=null) {
            Point point=main.frame().getLocation();
            point.x+=100;
            point.y+=100;
            main.textView.frame.setLocation(point);
            String title=main.textView.frame.getTitle();
            System.out.println("old tv: "+title);
            title+=" "+name;
            System.out.println("new tv: "+title);
            System.out.println(title);
            main.textView.frame.setTitle(main.textView.frame.getTitle()+" "+name);
        }
        System.out.println("main constructed.");
        mains.add(main);
        if(true) try {
            Thread.sleep(500);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        Consumer<String> c=(x)->System.out.println(x.toLowerCase());
        if(darkef) Main.darken(main.frame.getContentPane());
        Container container=main.frame().getContentPane();
        //listComponentsIn(container,null,false);
        return main;
    }
    public Point moveToLocationFor(String name) {
        Point point=name!=null?nameToPosition.get(name):null;
        if(point!=null) frame().setLocation(point);
        else frame().setLocation(new Point());
        return point;
    }
    private static void sysplaf() {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(UnsupportedLookAndFeelException e) {
            // handle exception
        } catch(ClassNotFoundException e) {
            // handle exception
        } catch(InstantiationException e) {
            // handle exception
        } catch(IllegalAccessException e) {
            // handle exception
        }
    }
    private static void xplaf() {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(UnsupportedLookAndFeelException e) {
            // handle exception
        } catch(ClassNotFoundException e) {
            // handle exception
        } catch(InstantiationException e) {
            // handle exception
        } catch(IllegalAccessException e) {
            // handle exception
        }
    }
    public static void setPlaf(LookAndFeelInfo plaf_) {
        System.out.println(plaf_);
        try {
            UIManager.setLookAndFeel(plaf_.getClassName());
        } catch(ClassNotFoundException|InstantiationException|IllegalAccessException
                |UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        System.out.println(Arrays.asList(args));
        LookAndFeelInfo[] plafs=UIManager.getInstalledLookAndFeels();
        System.out.println(Arrays.asList(plafs));
        setPlaf(plafs[2]);
        // add a panel somewhere to control logging
        // add a bunch of frames to control everything.
        Init.first.twice();
        System.out.println("1 Main.main()");
        Map<String,Object> map=MainGetOpt.processArguments(args);
        System.out.println(map);
        // use arguments from map instead of below.
        String name=(String)map.get("name");
        System.out.println("name: "+name);
        if(name!=null) { Main main=run(name); return; }
        Main maink=null;
        if(!run3) maink=run("one");
        else maink=run("black");
        System.out.println("black: "+maink.frame.getTitle());
        if(run3) {
            Main white=run("white");
            System.out.println("white: "+white.frame.getTitle());
            //Main observer=run("observer");
            //System.out.println("observer: "+observer.frame.getTitle());
        }
        for(Main main:mains) { System.out.println(main.frame.getTitle()); }
        System.out.println("exit Main.main() ---------------");
    }
    JPanel north,south,east,west;
    final Model model;
    Mediator mediator;
    int ttl=100*60*1000;
    final Timer timer=new Timer(ttl,this);
    public final TextView textView;
    String playerName;
    public static Boolean useTextView=true; // hack
    // useTextView reduces the copies from 4 to 2
    static boolean darkef=true;
    static boolean run3=false;
    //static File startWithFile=new File("fifteen.sgf");
    //static File startWithFile=new File("sgf/ff4_ex.sgf");
    static File startWithFile=null;
    private static final long serialVersionUID=1L;
    static final double darkFactor=.6;
    public static final Map<String,Point> nameToPosition=new TreeMap<>();
    static Set<Main> mains=new LinkedHashSet<>();
    static {
        nameToPosition.put("black",new Point(-1900,0));
        nameToPosition.put("white",new Point(-950,0));
        nameToPosition.put("observer",new Point(1978,-67));
    }
    @Override public void componentResized(ComponentEvent e) { //
    }
    @Override public void componentMoved(ComponentEvent e) { //
        Point p=frame().getLocation();
        if(frame().isVisible()) { Point pScteen=frame().getLocationOnScreen(); System.out.println(p+" "+pScteen); }
    }
    @Override public void componentShown(ComponentEvent e) { //
    }
    @Override public void componentHidden(ComponentEvent e) {}
}
