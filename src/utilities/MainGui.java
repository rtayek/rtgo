package utilities;
import java.awt.BorderLayout;
import javax.swing.*;
public class MainGui extends JPanel {
    public MainGui() { this(null); }
    /*@SuppressWarnings("deprecation")*/ public MainGui(MyJApplet applet) {
        this.applet=applet;
        if(!isApplet()) {
            frame=new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
            frame.setTitle(title());
        } else frame=null;
        // setLayout(new BorderLayout()); // let's see what happens
        // setPreferredSize(new Dimension(640, 480));
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                System.out.println("startng awt thread");
                MainGui.this.run();
                System.out.println("ending awt thread");
            }
        });
    }
    public String title() { return "Main Gui"; }
    public void addContent() { add(new JLabel("add content! top")); }
    void run() {
        System.out.println("enter main gui run().");
        if(isApplet()) addContent();
        else {
            frame.getContentPane().add(this,BorderLayout.CENTER);
            // center may not be right if no other layout?
            addContent();
            frame.pack();
            frame.setVisible(true);
        }
        System.out.println("exit main gui run().");
    }
    public boolean isApplet() { return applet!=null; }
    public JFrame frame() { return frame; }
    @SuppressWarnings("deprecation") public JApplet applet() { return applet; }
    public static void main(String[] args) { new MainGui(null); }
    public final JFrame frame;
    public final MyJApplet applet;
    private static final long serialVersionUID=1;
}
