package gui;
import java.awt.*;
import io.Logging;
import javax.swing.*;
public class WaitForFrameToBeVisible {
    JFrame frame=new JFrame("FrameDemo");
    private void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel emptyLabel=new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(175,100));
        frame.getContentPane().add(emptyLabel,BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
    void run() throws InterruptedException {
        Logging.mainLogger.info("waiting for frame to be displayable");
        long t0=System.nanoTime();
        javax.swing.SwingUtilities.invokeLater(new Runnable() { @Override public void run() { createAndShowGUI(); } });
        while(!frame.isDisplayable()) Thread.sleep(1);
        long dt=System.nanoTime()-t0;
        Logging.mainLogger.info("waited "+dt/1_000_000+" ms. for frame to be displayable");
    }
    public static void main(String[] args) throws Exception { new WaitForFrameToBeVisible().run(); }
}
