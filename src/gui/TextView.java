package gui;
import static io.Logging.flushingStreamHandler;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.swing.*;
import io.Logging;
import io.Logging.MyFormatter;
import io.Tee;
@SuppressWarnings("serial") public class TextView extends JPanel {
    public TextView() {
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
    }
    public static void createAndShowGui(TextView textView) {
        JFrame frame=new JFrame("SysOut");
        // maybe have text view be a main gui.
        // so we can handle the titles in a consistent way.
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                textView.teardownTeest();
                System.out.println("closing");
                System.err.println("closing");
            }
        });
        frame.getContentPane().add(textView);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        textView.frame=frame;
    }
    void setupTees() {
        tee=new Tee(taOutputStream);
        tee.addOutputStream(System.out);
        tee.setOut();
        tee2=new Tee(taOutputStream);
        tee2.addOutputStream(System.err);
        tee2.setErr();
    }
    void teardownTeest() { tee.restoreOut(); tee2.restoreErr(); }
    private void printStuuff() {
        //System.out.println("System.out");
        tee.printStream.println("tee ps");
        //System.err.println("System.err");
        tee2.printStream.println("tee2 ps");
    }
    public static void main(String[] args) throws InterruptedException,IOException {
        PrintStream out=System.out;
        PrintStream err=System.err;
        final TextView textView=new TextView();
        SwingUtilities.invokeLater(new Runnable() { @Override public void run() { createAndShowGui(textView); } });
        Thread.sleep(1500); // yikes, make this smalller!
        textView.setupTees();
        Handler handler=flushingStreamHandler(textView.tee.printStream);
        handler.setFormatter(new MyFormatter());
        Logging.mainLogger.addHandler(handler);
        Logging.mainLogger.setLevel(Level.ALL);
        //Tee.printStuff(tee.printStream,out,err);
        for(int i=0;i<3;i++) {
            textView.printStuuff();
            Thread.sleep(10);
            Logging.mainLogger.info("log");
            Thread.sleep(10);
            out.println("---");
            Thread.sleep(1000);
        }
        Thread.sleep(10);
    }
    Tee tee,tee2;
    public JFrame frame;
    JTextArea textArea=new JTextArea(30,60);
    {
        String[] x=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        //Logging.mainLogger.info(""+Arrays.asList(x));
        textArea.setFont(new Font("Lucida Console",Font.PLAIN,16));
    }
    public TextAreaOutputStream taOutputStream=new TextAreaOutputStream(textArea,"Go");
    static java.util.List<TextView> textViews=new ArrayList<>(); // make non static!!
}