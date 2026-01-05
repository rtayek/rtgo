package view;
import java.util.*;
import controller.CommandLine;
import io.Logging;
import model.Model;
public class CommandLIneView implements Observer {
    @Override public void update(Observable model,Object hint) {
        if(model.equals(this.model)) {
            Logging.mainLogger.info("cl view <<<<<<<<<<<");
            CommandLine.print(this.model);
            Logging.mainLogger.info("cl view >>>>>>>>>>>");
        } else throw new RuntimeException("oops");
    }
    public CommandLIneView(Model model) { this.model=model; Logging.mainLogger.info(model.name+" "+"constructed."); }
    final Model model;
}
