package gui;
import java.awt.BorderLayout;
import model.Model;
public class Applet extends utilities.MyJApplet {
    @Override public void init() { super.init(); }
    @SuppressWarnings("deprecation") @Override public void start() { super.start(); }
    @SuppressWarnings("deprecation") @Override public void addContent() {
        getContentPane().add(new Main(this,new Model(),null),BorderLayout.CENTER);
    }
    private static final long serialVersionUID=1;
}