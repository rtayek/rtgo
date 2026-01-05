package simplegui;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import io.Logging;
import utilities.*;
public class Main extends MainGui {
    // this is the panel that is the main gui.
    // maybe the gui should not subclass panel, but have a panel.
    public Main(MyJApplet applet,Model model) {
        super(applet);
        this.model=model;
        GraphicsDevice gd=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width=gd.getDisplayMode().getWidth();
        int height=gd.getDisplayMode().getHeight();
        Logging.mainLogger.info(width+"x"+height);
        Logging.mainLogger.info(String.valueOf(model.toString()));
    }
    Color oppositeColor(Color color) { return new Color(~color.getRGB()); }
    Border oppositeBorder(Color color) { return BorderFactory.createLineBorder(oppositeColor(color),3); }
    void addLabel(JPanel jPanel) { // should be just something to show layout in th panel
        Color color=jPanel.getBackground();
        LayoutManager layoutManager=jPanel.getLayout();
        Logging.mainLogger.info(String.valueOf(layoutManager));
        JLabel jLabel=new JLabel("s n");
        jPanel.add(jLabel);
        JPanel small=new JPanel();
        small.setBackground(oppositeColor(color));
        //small.setBorder(oppositeBorder(color));
        //small.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        small.setBorder(BorderFactory.createLineBorder(Color.black,3));
        JLabel jLabel2=new JLabel("l s");
        small.add(jLabel2);
        //jLabel2.setBackground(Color.yellow);
        JLabel jLabel3=new JLabel("2 l s");
        //jLabel2.`
        small.add(jLabel3);
        jLabel3.setBackground(color);
        jPanel.add(small);
    }
    public JPanel addContent(JPanel parent) {
        JPanel north=new JPanel();
        north.setBackground(Color.green);
        north.setPreferredSize(new Dimension(200,100));
        north.setMinimumSize(new Dimension(200,100));
        parent.add(north,BorderLayout.PAGE_START);
        JPanel east=new JPanel();
        east.setBackground(Color.cyan);
        east.setPreferredSize(new Dimension(200,100));
        east.setMinimumSize(new Dimension(200,100));
        parent.add(east,BorderLayout.LINE_END);
        JPanel west=new JPanel();
        west.setBackground(Color.cyan);
        west.setPreferredSize(new Dimension(200,100));
        west.setMinimumSize(new Dimension(200,100));
        parent.add(west,BorderLayout.LINE_START);
        JPanel south=new JPanel();
        south.setBackground(Color.green);
        south.setPreferredSize(new Dimension(200,100));
        south.setMinimumSize(new Dimension(200,100));
        parent.add(south,BorderLayout.PAGE_END);
        JPanel center=new JPanel();
        center.setBackground(Color.lightGray);
        center.setPreferredSize(new Dimension(200,100));
        center.setMinimumSize(new Dimension(200,100));
        parent.add(center,BorderLayout.CENTER);
        addLabel(north);
        addLabel(south);
        addLabel(east);
        addLabel(west);
        return center;
    }
    @Override public void addContent() { // runs on awt thread!
        // if this was a remote gui
        // how would we do this?
        //
        setLayout(new BorderLayout());
        if(frame!=null) frame.setResizable(true);
        else;// ???
        // add insets!
        JPanel center=addContent(this);
        /*Mediator mediator=*/new Mediator(model,this,center);
        if(isApplet()) frame().pack();
        else; // https://community.oracle.com/thread/1294964
    }
    public static void observe(MyJApplet applet,Model model) { new Main(applet,model); }
    public static void main(String[] args) { observe(null,new Model()); }
    final Model model;
    private static final long serialVersionUID=1L;
}
