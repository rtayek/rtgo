package gui;
import java.awt.BorderLayout;
import model.Model;
public class Applet extends utilities.MyJApplet {
    @Override public void init() { super.init(); }
    @Override public void start() { super.start(); }
    @Override public void addContent() {
        getContentPane().add(new Main(this,new Model(),null),BorderLayout.CENTER);
    }
    private static final long serialVersionUID=1;
}
