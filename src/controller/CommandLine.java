package controller;
import java.io.*;
import java.util.Arrays;
import equipment.Board.*;
import equipment.Point;
import gui.*;
import io.IO;
import model.*;
import model.Model.MoveResult;
import model.Move.MoveImpl;
import view.CommandLIneView;
public class CommandLine {
    // we need the command line options
    // and we need the commands for the command line.
    private static void usage() {
        System.out.println("usage:");
        System.out.println("CommandLine.main() -role black, white, obseever, anything");
        System.out.println("m x y - move at (x,y)");
        System.out.println("M - unmove");
        System.out.println("u - up");
        System.out.println("d - down");
        System.out.println("r - right");
        System.out.println("l - left");
        System.out.println("o file - open file (no spaces allowed!");
        System.out.println("n - new game");
        System.out.println("g - new gui for model");
        System.out.println("p - print view");
        System.out.println("q - quit");
        System.out.println("t - treeview of sample sgf file.");
    }
    private String[] splitNext(String command,int i) {
        while(command.charAt(i)==' ') i++;
        String[] tokens=command.substring(i).split(" ");
        return tokens;
    }
    private void process(String command) {
        if(command.length()==0) return;
        System.out.println("got a: "+command.charAt(0));
        String[] tokens=null;
        switch(command.charAt(0)) {
            case 'h':
                usage();
                break;
            case 'o':
                tokens=splitNext(command,1);
                if(tokens!=null&&tokens.length>=1) { model.restore(IO.toReader(new File(tokens[0]))); }
                break;
            case 'u':
                model.up();
                break;
            case 'd':
                model.down(0);
                break;
            case 'r':
                model.right();
                break;
            case 'l':
                model.left();
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
                    Move move=new MoveImpl(model.turn(),point);
                    MoveResult wasLegal=model.move(move);
                    if(wasLegal!=MoveResult.legal) System.out.println("illegal move");
                } else System.out.println("no board, can not move!");
                break;
            case 'M':
                if(!model.hasABoard()) { System.out.println("start a game first."); usage(); break; }
                model.delete();
                break;
            case 'c':
                model.addObserver(new CommandLIneView(model));
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
                print();
                break;
            case 'q':
                print();
                break;
            case 't':
                TreeView2 treeView2=TreeView2.simple2();
                // gui code uses the old view?
                model.setRoot(treeView2.model.root());
                break;
            default:
                System.out.println("huh?");
                break;
        }
        System.out.println("exit process()");
    }
    private void print() { System.out.println(model); }
    void run() throws IOException {
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        String string=null;
        usage();
        print();
        System.out.println("type a command");
        while((string=bufferedReader.readLine())!=null) {
            if(string.equals("q")) break;
            try {
                process(string);
                print();
            } catch(Exception e) {
                System.out.println("run() "+this+" caught: "+e+" "+this);
            }
            System.out.println("type a command");
        }
        System.out.println("exiting run()");
    }
    public static void main(String[] arguments) throws IOException { new CommandLine().run(); }
    Model model=new Model("command line");
    static String lineSeparator=System.getProperty("line.separator");
}
