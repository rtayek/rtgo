package controller;
import java.io.StringReader;
import java.util.EnumSet;
import equipment.Stone;
import model.Model;
import sgf.Parser;
// maybe move all of the gtp stuff into it's own package?
public enum Command { // should implement some interface? (probably)
    // add sample arguments for testing?
    // other???
    // add up,down,right,left (already have undo/delete)
    // sep 2021 maybe these can do all we need?
    // not sure what all the tgo_'s are for.
    //looks like black, white, observe, anything are roles?
    tgo_stop,tgo_torus,tgo_black,tgo_white,tgo_observe, //
    tgo_anything,tgo_up,tgo_down,tgo_left,tgo_right, //
    tgo_top,tgo_bottom,tgo_delete, //
    protocol_version(true),name(true),version(true), //
    known_command(true,1,name.name()),list_commands(true),quit, //
    boardsize(1,Failure.set1,"19"),clear_board, //
    komi(1,Failure.set0,"6.5"),play(2,Failure.set3,"Black A1"), //
    genmove(true,1,"Black"),undo,showboard,tgo_send_sgf(true), //
    tgo_receive_sgf(1,"(;)"),tgo_goto_node(1,"frog");
    public String command() { return name(); }
    public String sample() { return name()+(sampleArguments==null?"":(" "+sampleArguments)); }
    private static String boardsizeCommand(int n) { return boardsize.name()+" "+n; }
    private static String play(Stone color,String move) { return genmove.name()+" "+color+" "+move; }
    private static String genmove(Stone color) { return genmove.name()+" "+color; }
    private Command() { this(false,0,EnumSet.noneOf(Failure.class),null); }
    private Command(boolean hasOutput) { this(hasOutput,0,EnumSet.noneOf(Failure.class),null); }
    private Command(int arguments,String sampleAguments) {
        this(false,arguments,EnumSet.noneOf(Failure.class),sampleAguments);
    }
    private Command(int arguments,EnumSet<Failure> failures,String sampleArguments) {
        this(false,arguments,failures,sampleArguments);
    }
    private Command(boolean hasOutput,int arguments,String sampleArguements) {
        this(hasOutput,arguments,EnumSet.noneOf(Failure.class),sampleArguements);
    }
    private Command(EnumSet<Failure> fails) { this(false,0,fails,null); }
    private Command(boolean hasOutput,int arguments,EnumSet<Failure> failures,String sampleArguments) {
        this.hasOutput=hasOutput;
        this.arguments=arguments;
        this.failures=failures;
        this.sampleArguments=sampleArguments;
    }
    static Command from(String string) {
        try {
            return Command.valueOf(string);
        } catch(IllegalArgumentException e) {
            return null;
        }
    }
    public static void doTGOSend(String key) {
        String expectedSgf=Parser.getSgfData(key);
        Model original=new Model();
        original.restore(new StringReader(expectedSgf));
        original.bottom();
        String command=Command.tgo_send_sgf.name();
        String response=null;
        GTPBackEnd backend=new GTPBackEnd(command,original);
        backend.runBackend(true);
        response=backend.out.toString();
        System.out.println("1 "+response);
        // why am i doing this twice?
        backend=new GTPBackEnd(command,original); // this is a new one?
        // yes, because unBackend() is a one-shot.
        String response2=backend.runCommands(true);
        System.out.println("2 "+response);
        if(!response.equals(response2)) System.out.println("fail!");
    }
    public final int arguments; // maybe have -1 mean many?
    private final String sampleArguments;
    final boolean hasOutput;
    final EnumSet<Failure> failures;
}