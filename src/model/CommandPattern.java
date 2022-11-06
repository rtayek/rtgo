package model;
import java.util.ArrayList;
import java.util.List;
import sgf.SgfProperty;
abstract class Command {
    abstract void execute();
    void unexecute() { throw new RuntimeException(this+" is not undoable!"); }
    Command(List<SgfProperty> properties) { this.properties=properties; }
    final List<SgfProperty> properties;
    // maybe make each property a command and use a macro?
}
// looks like each node is a macro command
// since it has a list of properties.
// so each p2 will need a command?
// i will need to do and undo each command
// but i may not need much more than knowing the command?
// looks like our process command is really execute
// so maybe we just need a similar unprocess command?
//
// september 2021
// this is not used anywhere. might be something useful. not sure.
class Macro extends Command {
    Macro() { super(new ArrayList<SgfProperty>()); }
    @Override void execute() { for(Command command:commands) command.execute(); }
    @Override void unexecute() { for(int i=commands.size()-1;i>=0;i--) commands.get(i).unexecute(); }
    List<Command> commands=new ArrayList<>();
}
class Receiver {
    void action() {}
    // receiver will usually be a board or a most a model
    // so maybe we don't need this class.
}
abstract class SimpleModelCommand extends Command {
    SimpleModelCommand(Model model,List<SgfProperty> properties) { super(properties); this.model=model; }
    @Override void execute() {
        // model.action();
    }
    final Model model;
}
class Client {}
class Invoker {}
public class CommandPattern { public static void main(String[] args) {} }
