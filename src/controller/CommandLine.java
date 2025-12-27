package controller;
import java.io.*;
import java.util.*;
import equipment.Board.*;
import equipment.Point;
import gui.*;
import io.*;
import model.*;
import model.Model.MoveResult;
import model.LegacyMove.MoveImpl;
import model.Move2.MoveType;
import view.CommandLIneView;
public class CommandLine {
    // we need the command line options
    // and we need the commands for the command line.
    private static void usage() {
        System.out.println("usage:");
        //System.out.println("CommandLine.main() -role black, white, obseever, anything.");
        System.out.println("h - help.");
        System.out.println("c - add a new command line view.");
        System.out.println("m x y - move at (x,y).");
        System.out.println("M - unmove.");
        System.out.println("u - up.");
        System.out.println("d - down.");
        System.out.println("r - right.");
        System.out.println("l - left.");
        System.out.println("t - top.");
        System.out.println("b - bottom.");
        System.out.println("D - delete.");
        System.out.println("o file - open file (no spaces allowed!");
        System.out.println("n - new game.");
        System.out.println("g - new gui for model.");
        System.out.println("p - print view.");
        System.out.println("q - quit.");
        System.out.println("s - connect to server");
        System.out.println("S - disconnect from server");
        System.out.println("T - toggle treeview.");
    }
    private String[] splitNext(String command,int i) {
        while(command.charAt(i)==' ') i++;
        String[] tokens=command.substring(i).split(" ");
        return tokens;
    }
    private void process(String command) {
        if(command.length()==0) return;
        System.out.println("process got a command: "+command.charAt(0));
        String[] tokens=null;
        boolean ok=false;
        switch(command.charAt(0)) {
            case 'h':
                usage();
                break;
            case 'c':
                model.addObserver(new CommandLIneView(model));
                break;
            case 'u':
                model.up();
                break;
            case 'd':
                model.down(0);
                break;
            case 'D':
                model.delete();
                break;
            case 'r':
                model.right();
                break;
            case 'l':
                model.left();
                break;
            case 't':
                model.top();
                break;
            case 'b':
                model.bottom();
                break;
            case 'm':
                if(model.hasABoard()) {
                    tokens=splitNext(command,1);
                    System.out.println(Arrays.asList(tokens));
                    int x=Integer.parseInt(tokens[0]);
                    int y=Integer.parseInt(tokens[1]);
                    Point point=new Point(x,y);
                    // should be the same as other places?
                    //String move=Coordinates.toGtpCoordinateSystem(point,
                    //        model.board().depth());
                    Move2 move=new Move2(MoveType.move,model.turn(),point);
                    MoveResult wasLegal=model.move(move);
                    if(wasLegal!=MoveResult.legal) System.out.println("illegal move");
                } else System.out.println("no board, can not move!");
                break;
            case 'M':
                if(!model.hasABoard()) { System.out.println("start a game first."); usage(); break; }
                model.delete();
                break;
            case 'o':
                tokens=splitNext(command,1);
                System.out.println(Arrays.asList(tokens));
                if(tokens!=null&&tokens.length>=1) { model.restore(IOs.toReader(new File(tokens[0]))); }
                break;
            case 'g':
                new Main(null,model,null);
                // need a way to make sure this guy is just an observer.
                // haha - but we would like to let him add variations.
                //
                break;
            case 'n':
                model.setRoot(9,9,Topology.normal,Shape.normal);
                break;
            case 'p':
                print(model);
                break;
            case 'q':
                if(true) throw new RuntimeException("got a q!");
                break;
            case 's':
                if(model.gtp==null) {
                    ok=Model.connectToServer(model);
                    if(ok) System.out.println("conected.");
                    else System.out.println("connect fails!");
                } else System.out.println("already conected!");
                break;
            case 'S':
                if(model.gtp!=null) {
                    ok=Model.disconnectFromServer(model);
                    if(ok) System.out.println("disconected.");
                    else System.out.println("disconnect fails!");
                } else System.out.println("already disconected!");
                break;
            case 'T':
                if(myTreeView==null) {
                    myTreeView=new TreeView(null,model);
                    model.addObserver(myTreeView);
                } else {
                    model.deleteObserver(myTreeView);
                    myTreeView.frame.dispose();
                    myTreeView=null;
                }
                // try to fix unselected root
                model.setChangedAndNotify(Event.newTree);
                //TreeView2 treeView2=TreeView2.simple2();
                // gui code uses the old view?
                //model.setRoot(treeView2.model.root());
                break;
            default:
                System.out.println("huh?");
                break;
        }
        System.out.println("exit process()");
    }
    public static void print(Model model) { System.out.println("model: "+model.name+"\n"+model); }
    void run() throws IOException {
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        String string=null;
        usage();
        print(model);
        System.out.println("type a command");
        while((string=bufferedReader.readLine())!=null) {
            if(string.equals("q")) break;
            try {
                process(string);
                System.out.println("((((((");
                print(model);
                System.out.println("))))))");
            } catch(Exception e) {
                System.out.println("run() "+this+" caught: "+e+" "+this);
            }
            System.out.println("type a command");
        }
        System.out.println("exiting run()");
    }
    void startup() { for(String command:startup) process(command); }
    public static void main(String[] arguments) throws IOException {
        System.out.println(Init.first);
        CommandLine commandLine=new CommandLine();
        commandLine.startup();
        commandLine.run();
    }
    List<String> startup=Arrays.asList(new String[] { //
            //"o sgf/ff4_ex.sgf", //
            //"t,"
            "c","s",});
    Model model=new Model("cl model");
    TreeView myTreeView;
}
