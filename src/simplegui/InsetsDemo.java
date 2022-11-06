package simplegui;
import java.applet.Applet;
import java.awt.*;
@SuppressWarnings("removal") public class InsetsDemo extends Applet {
    @Override public void init() {
        setBackground(Color.cyan);
        setLayout(new BorderLayout());
        add(new Button("RMI"),BorderLayout.NORTH);
        add(new Button("SERVLET"),BorderLayout.EAST);
        add(new Button("JDBC"),BorderLayout.SOUTH);
        add(new Button("BEANS"),BorderLayout.WEST);
        add(new Button("JAVA"),BorderLayout.CENTER);
    }
    @Override public Insets getInsets() { return new Insets(10,20,10,20); }
    private static final long serialVersionUID=1L;
}