package gui;
import javax.swing.JFrame;
@SuppressWarnings("serial") public class Title extends JFrame {
    public Title() {
        this.setSize(200,100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Hello World!");
        this.setVisible(true);
    }
    public static void main(String[] args) { Title title=new Title(); title.setTitle("foo"); }
}
