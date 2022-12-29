package gui;
import java.awt.*;
import java.util.*;
import java.util.logging.Level;
import javax.swing.*;
import io.Logging;
@SuppressWarnings("serial") public class LogPanel extends JPanel {
    public LogPanel() { setLayout(new BorderLayout()); }
    public static void createAndShowGui(LogPanel logPanel) {
        JFrame frame=new JFrame("Logging");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(logPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        logPanel.frame=frame;
    }
    public static void main(String[] args) throws Exception {
        final LogPanel logPanel=new LogPanel();
        System.out.println(Logging.loggerNames);
        for(String name:Logging.loggerNames) {
            JPanel panel=new JPanel();
            Box box=Box.createVerticalBox();
            JLabel label=new JLabel(name);
            box.add(label);
            java.util.List<String> list=new ArrayList<>();
            for(Level level:Logging.levels) list.add(level+"");
            SpinnerListModel model=new SpinnerListModel(list);
            JSpinner spinner=new JSpinner(model);
            box.add(spinner);
            logPanel.add(box);
        }
        SwingUtilities.invokeLater(new Runnable() { @Override public void run() { createAndShowGui(logPanel); } });
        Thread.sleep(1500);
    }
    JFrame frame;
    {
        String[] x=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Logging.mainLogger.info(""+Arrays.asList(x));
        //textArea.setFont(new Font("Lucida Console",Font.PLAIN,16));
    }
}