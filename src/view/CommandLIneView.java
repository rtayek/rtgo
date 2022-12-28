package view;
import java.util.*;
import controller.CommandLine;
import io.Logging;
import model.Model;
public class CommandLIneView implements Observer {
    @Override public void update(Observable model,Object hint) {
        if(model.equals(this.model)) {
            System.out.println("cl view <<<<<<<<<<<");
            CommandLine.print(this.model);
            System.out.println("cl view >>>>>>>>>>>");
        } else throw new RuntimeException("oops");
    }
    public CommandLIneView(Model model) { this.model=model; Logging.mainLogger.info(model.name+" "+"constructed."); }
    final Model model;
}
