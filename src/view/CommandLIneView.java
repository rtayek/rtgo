package view;
import io.Logging;
import java.util.Observable;
import java.util.Observer;
import model.Model;
public class CommandLIneView implements Observer {
    @Override public void update(Observable model,Object hint) {
        Model r=this.model;
        Logging.mainLogger.info(r.name+" "+"update()");
        if(model instanceof Model) {
            Model r1=this.model;
            Logging.mainLogger.info(r1.name+" "+"\n"+model);
        } else throw new RuntimeException("oops");
    }
    public CommandLIneView(Model model) { this.model=model; Logging.mainLogger.info(model.name+" "+"constructed."); }
    final Model model;
}
