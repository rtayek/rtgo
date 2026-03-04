package utilities;
import static io.Init.first;
import java.awt.BorderLayout;
import javax.swing.JApplet;
@SuppressWarnings("removal") public class MyJApplet extends JApplet {
	@Override public void init() {
		getContentPane().setLayout(new BorderLayout());
		addContent();
	}
	public void addContent() {
		getContentPane().add(new MainGui(this),BorderLayout.CENTER);
	}
	public static void main(String[] args) {
		first.twice(); // do this first in all main programs!
		new MainGui(null);
	}
	private static final long serialVersionUID=1;
}
