package gui;
import io.Logging;
import model.Model;
public class X {
    public static void main(String[] args) throws Exception {
        Model model=new Model();
        TextView textView=new TextView();
        TextView.createAndShowGui(textView);
        Main.addTextViewOutputStreams(textView);
        Main main=new Main(null,model,textView);
        //model.mumble("waiting for frame to be displayable");
        long t0=System.nanoTime();
        //	while(!main.frame.isDisplayable())
        //		Model.sleep(1);
        long dt=System.nanoTime()-t0;
        Logging.mainLogger.info("waited "+dt/1_000_000+" ms. for frame to be displayable");
        //model.mumble();
        main.frame.setTitle("local");
        main.frame.repaint();
    }
}
