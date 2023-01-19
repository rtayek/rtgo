package gui;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
public class Toast extends JFrame {
    public Toast(final String message) {
        setUndecorated(true);
        setLayout(new GridBagLayout());
        setBackground(new Color(240,240,240,250));
        setLocationRelativeTo(null);
        setSize(300,50);
        add(new JLabel(message));
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),20,20));
            }
        });
    }
    public void display() {
        try {
            setOpacity(1);
            setVisible(true);
            Thread.sleep(time);
            //hide the toast message in slow motion
            for(double d=1.0;d>0.2;d-=0.1) {
                Thread.sleep(dt);
                setOpacity((float)d);
            }
            // set the visibility to false
            setVisible(false);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void toast(String message) {
        Toast toast=new Toast(message);
        toast.display();
    }
    public static void main(String[] args) {
        Toast message=new Toast("Welcome to TutorialsPoint.Com");
        message.display();
    }
    private static final long serialVersionUID=1L;
    public static int time=2000,dt=100;
}
