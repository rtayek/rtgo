package controller;
import static io.Init.first;
import static model.MNodeAcceptor.MNodeFinder.labelPredicate;
import static sgf.HexAscii.*;
//import static sgf.Parser.getSgfData; // get rid of this!
import static utilities.Utilities.cat;
import java.io.*;
import java.net.*;
import java.util.*;
import equipment.*;
import gui.*;
import io.*;
import io.IO.*;
import model.*;
import model.MNodeAcceptor.MNodeFinder;
import model.Model.*;
import server.NamedThreadGroup;
import server.NamedThreadGroup.NamedThread;
import sgf.MNode;
// https://www.gnu.org/software/gnugo/gnugo_19.html
//how to stick into gogui:
//"C:\Program Files\Java\jdk1.8.0_40\bin\java.exe" -cp bin controller.GTP
//d:/ray/dev/goapps/go2/
//main program for gogui is net.sf.gogui.gogui.MainWrapper
//"C:\Program Files\Java\jdk1.8.0_40\bin\java.exe" -cp ./go.jar controller.GTP
//the current gtp class works as a client
//it reads commands and waits for a move after a genmove
//the above sorta works (or at least it used to).
//enum Argument { int_, float_, string, vertex, color, move, boolean_ }
public class GTPBackEnd implements Runnable,Stopable {
    // 8/6/22 maybe we can isolate the 2 line feeds thing.
    // maybe put it in a wrapper around message/response send/receve?
    public GTPBackEnd(End end,Model model) {
        this.socket=end.socket();
        this.in=end.in();
        this.out=end.out();
        model=model!=null?model:new Model("model");
        this.model=model;
    }
    public GTPBackEnd(String command,Model model) { // for testing
        // makes a back end that will read the command.
        this(new End(new BufferedReader(new StringReader(command)),new StringWriter()),model);
    }
    public NamedThread startGTP(long id) { // starts with a new thread each time.
        if(IO.currentThreadIsTimeLimited())
            Logging.mainLogger.severe("not main! "+id+" "+model.name+" "+Thread.currentThread().getName());
        boolean failIfOnTimeLimitedThread=false;
        if(failIfOnTimeLimitedThread&&IO.currentThreadIsTimeLimited()) {
            if(false) System.exit(0);
            Logging.mainLogger.severe("not starting gtp thread!");
            return null;
        } else {
            (namedThread=NamedThreadGroup.createNamedThread(id,this,model.name)).start();
            System.out.println("start gtp thread: "+namedThread);
            Logging.mainLogger.info("start gtp thread: "+namedThread);
            return namedThread;
        }
    }
    public void writeLinefeed() {
        try {
            out.write('\n');
            out.flush();
        } catch(IOException e) {
            Logging.mainLogger.finer("write line feed: "+e);
        }
    }
    private boolean sendWithoutLineFeeds(Character character,Integer id,String string) {
        // maybe make a send that just takes a string?
        // construct the the string here.
        // should be identical to back end.
        // 7/3/22 confusing name. sends a reply and adds a line feed.
        // this seems to insure that all of the responses
        // sent back to the front end, do end in line feeds.
        //
        try {
            out.write(character);
            if(id!=-1) out.write(id.toString());
            out.write(' ');
            if(string!=null) {
                Logging.mainLogger.fine("writing: "+" "+character+string+" on "+out);
                out.write(string);
            }
            out.write('\n');
            out.flush();
            return true;
        } catch(SocketException e) {
            throw new RuntimeException(e);
        } catch(IOException e) {
            Logging.mainLogger.info("send w/o lfs caught: "+e+" "+this);
        }
        return false;
    }
    public boolean send(Character character,int id,String string) {
        boolean isOk=sendWithoutLineFeeds(character,id,string);
        writeLinefeed();
        return isOk;
    }
    boolean send(Character character,int id,Failure failure) {
        return send(character,id,failure!=null?failure.toString2():null);
    }
    public boolean isWaitingForMove() { return isWaitingForMove; }
    public void waitUntilItIsTmeToMove() {
        // return boolean instead of throwing?
        boolean once=false;
        while(!isWaitingForMove()) {
            if(!once) System.out.println("waiting for a move.");
            if(Thread.currentThread().isInterrupted()) throw new RuntimeException();
            GTPBackEnd.sleep2(GTPBackEnd.yield);
            once=true;
        }
        System.out.println("end of waiting for a move.");
    }
    private void waitForAMove(Message message) { // generate a move
        // this only gets called from genmove
        // with generate move false
        // so it needs to wait for someone to make a move!
        // this sets isWaitinfFoeMove=true
        // then waits until a move (or pass or resign) appears on the board.
        // then it sets isWaitinfFoeMove=false
        Logging.mainLogger.info(model.name+" enter genmove() at "+first.et);
        int old=model.moves();
        Logging.mainLogger.fine(model.name+" before set "+" true "+(model.moves()+1));
        boolean previous=isWaitingForMove;
        isWaitingForMove=true;
        // is this the same as isWaitingForMoveCompleteOnBoard in model?
        // maybe not
        // allow model to move
        Logging.mainLogger.fine(model.name+" after set waiting for move to true, old value was: "+previous);
        Logging.mainLogger
        .fine(model.name+" "+"gtp: waiting for move to complete on board "+(model.moves()+1)+" to complete.");
        model.waitForMoveCompleteOnBoard(old);
        Logging.mainLogger
        .fine(model.name+" "+first.et+" before set waiting for move to false "+model.moves()+" to false.");
        previous=isWaitingForMove;
        // prevent model from moving
        isWaitingForMove=false;
        // maybe move this down more?
        Logging.mainLogger.fine(model.name+" after set waiting for move to false, old value was: "+previous);
        // get rid of the the sleep!
        if(true) GTPBackEnd.sleep2(1);
        if(model.lastMoveGTP()==null) Logging.mainLogger.warning("last move is null!");
        else Logging.mainLogger.fine(model.name+" last move is not null "+model.lastMoveGTP());
        String move=model.lastMoveGTP();
        Logging.mainLogger.info(model.name+" done waiting, got a move: "+move);
        Logging.mainLogger.info(model.name+" move "+move+" at "+first.et);
        send(okCharacter,message.id,move);
        Logging.mainLogger.info(model.name+" exit genmove() at "+first.et);
    }
    private boolean processCommand(String string) {
        Logging.mainLogger.severe("1: "+string);
        if(isWaitingForMove()) Logging.mainLogger.severe(model.name+" waiting for move!.");
        String stripped=Message.strip(string);
        if(stripped==null||stripped.isEmpty()) return true;
        Message message=new Message(stripped);
        // spaces break below. 5 arhuments insead of 1.
        if(message.command!=null) if(message.arguments.length-1!=message.command.arguments) {
            Logging.mainLogger.warning(model.name+" "+"gtp: wrong number of arguments!");
            send(badCharacter,message.id,Failure.syntax_error);
            // check for quit command just in case?
            return true;
        }
        boolean ok;
        if(message.command!=null) switch(message.command) {
            case protocol_version:
                send(okCharacter,message.id,protocolVersionString);
                break;
            case tgo_torus:
                model.setBoardTopology(Board.Topology.torus);
                send(okCharacter,message.id,"");
                break;
                // maybe these next 4 should be tgo_role with a role argument?
                // maybe.
            case tgo_black:
                System.out.println("got: "+message.command);
                model.setRole(Model.Role.playBlack);
                send(okCharacter,message.id,"");
                break;
            case tgo_white:
                model.setRole(Model.Role.playWhite);
                send(okCharacter,message.id,"");
                break;
            case tgo_observe:
                model.setRole(Model.Role.observer);
                send(okCharacter,message.id,"");
                break;
            case tgo_anything:
                model.setRole(Model.Role.anything);
                send(okCharacter,message.id,"");
                break;
            case tgo_up:
                if(Navigate.up.canDo(model)) {
                    model.up();
                    send(okCharacter,message.id,"true");
                } else send(okCharacter,message.id,"false");
                break;
            case tgo_down:
                if(Navigate.down.canDo(model)) {
                    model.down(0);
                    send(okCharacter,message.id,"true");
                } else send(okCharacter,message.id,"false");
                break;
            case tgo_right:
                if(Navigate.right.canDo(model)) {
                    model.right();
                    send(okCharacter,message.id,"true");
                } else send(okCharacter,message.id,"false");
                break;
            case tgo_left:
                if(Navigate.left.canDo(model)) {
                    model.left();
                    send(okCharacter,message.id,"true");
                } else send(okCharacter,message.id,"false");
                break;
            case tgo_top:
                if(Navigate.up.canDo(model)) {
                    model.top();
                    send(okCharacter,message.id,"true");
                } else send(okCharacter,message.id,"false");
                break;
            case tgo_bottom:
                if(Navigate.down.canDo(model)) {
                    model.bottom();
                    send(okCharacter,message.id,"true");
                } else send(okCharacter,message.id,"false");
                break;
            case tgo_delete:
                if(Model.canDelete(model)) {
                    model.delete();
                    send(okCharacter,message.id,"true");
                } else send(okCharacter,message.id,"false");
                break;
                // these two are very similar.
                // are they the same?
            case undo:
                if(Model.canDelete(model)) {
                    model.delete();
                    send(okCharacter,message.id,"true");
                } else send(okCharacter,message.id,"false");
                break;
            case tgo_stop: // stop everything
                // interrupt thread here?
                // do we need to wait?
                Logging.mainLogger.severe("got tgo_stop, what should we do?");
                // stop(); // maybe call stop?
                send(okCharacter,message.id,"true");
                done=true;
                break;
            case version:
                send(okCharacter,message.id,Model.version);
                break;
            case name:
                send(okCharacter,message.id,Model.sgfApplicationName);
                break;
            case known_command:
                if(message.arguments.length>1) {
                    Command c=Command.from(message.arguments[1]);
                    if(c!=null) send(okCharacter,message.id,"true");
                    else send(okCharacter,message.id,"false");
                } else send(okCharacter,message.id,"false");
                break;
            case list_commands:
                try {
                    sendWithoutLineFeeds(okCharacter,message.id,"");
                    for(Command c:Command.values()) out.write(""+c+'\n');
                    out.flush();
                    writeLinefeed();
                } catch(IOException e) {
                    e.printStackTrace();
                    send(badCharacter,message.id,"throws "+e);
                }
                break;
            case quit:
                send(okCharacter,message.id,"");
                // how to wait for this response to be sent?
                return false;
            case boardsize:
                System.out.println(model.name+" boardsize command.");
                if(message.arguments.length>1) {
                    int n=-1;
                    try {
                        n=Integer.parseInt(message.arguments[1]);
                    } catch(NumberFormatException e) {
                        send(badCharacter,message.id,Failure.syntax_error);
                    }
                    if(1<=n&&n<=Model.LargestBoardSize) {
                        // maybe model should not be doing a set root here?
                        // maybe just a set board?
                        if(true) {
                            Board board=Board.factory.create(n,n,model.boardTopology(),model.boardShape());
                            model.setBoard(board);
                        } else model.setRoot(n,n,model.boardTopology(),model.boardShape());
                        // shape and type above come from parameters through model?
                        send(okCharacter,message.id,"");
                    } else send(badCharacter,message.id,Failure.unacceptable_size);
                } else send(badCharacter,message.id,"missing argumant for board size");
                // replace above with syntax error or unacceptable size?
                // probably not.
                break;
            case clear_board:
                if(model.board()==null) {
                    System.out.println("board is null in clear board");
                    int width=model.state().widthFromSgf;
                    int depth=model.state().depthFromSgf;
                    Board board=Board.factory.create(width,depth,model.boardTopology(),model.boardShape());
                    model.setBoard(board);
                }
                //model.setRoot();
                model.board().setAll(Stone.vacant);
                send(okCharacter,message.id,"");
                break;
            case komi:
                if(message.arguments.length>1) {
                    float n=-1;
                    try {
                        n=Float.parseFloat(message.arguments[1]);
                        model.setKomi(n);
                        send(okCharacter,message.id,"");
                    } catch(NumberFormatException e) {
                        send(badCharacter,message.id,Failure.syntax_error);
                    }
                } else send(badCharacter,message.id,"missing argumant for komi");
                // replace above with syntax error?
                break;
            case play:
                // Consecutive moves of the same color are not considered
                // illegal from the protocol point of view.
                if(message.arguments.length>2) {
                    if(model.board()==null) model.setRoot();
                    String color=message.arguments[1];
                    Stone who=null;
                    if(color.equalsIgnoreCase(Model.black)||color.equalsIgnoreCase("b")) who=Stone.black;
                    if(color.equalsIgnoreCase(Model.white)||color.equalsIgnoreCase("w")) who=Stone.white;
                    if(who!=null) {
                        if(/*!model.strict||*/who.equals(model.turn())) { // may not be needed?
                            String moveString=message.arguments[2];
                            Move move=Move.fromGTP(who,moveString,model.board().width(),model.board().depth());
                            Role old=model.role();
                            model.setRole(Role.anything);
                            MoveResult moveResult=model.move(move);
                            model.setRole(old);
                            switch(moveResult) {
                                case legal:
                                    send(okCharacter,message.id,"");
                                    break;
                                case notYourTurn:
                                    if(false/*!model.strict*/) {
                                        send(okCharacter,message.id,"");
                                    } else {
                                        send(badCharacter,message.id,Failure.illegal_move);
                                    }
                                    break;
                                default:
                                    send(badCharacter,message.id,Failure.illegal_move);
                                    break;
                            }
                        } else {
                            Logging.mainLogger.severe(model.name+" "+"gtp: it is not "+who+"'s turn to play!");
                            send(badCharacter,message.id,Failure.illegal_move);
                        }
                    } else {
                        Logging.mainLogger.severe(model.name+" "+"gtp: color is not black or white: "+color);
                        send(badCharacter,message.id,"color is not black or white"); // syntax
                        // error?
                    }
                } else {
                    Logging.mainLogger.warning(model.name+" "+"missing arguments for play!");
                    send(badCharacter,message.id,"missing argumant(s) for play");
                }
                // replace above with syntax error?
                break;
            case genmove:
                Logging.mainLogger.warning(model.name+" got genmove for move #"+(model.moves()+1)+" at "+first.et);
                // maybe use this just to keep track of whose move it is?
                // maybe not.
                if(message.arguments.length>1) {
                    if(model.board()==null) {
                        Logging.mainLogger.fine("no board, calling setRoot().");
                        model.setRoot();
                    }
                    String color=message.arguments[1];
                    Stone who=null;
                    if(color.equalsIgnoreCase("black")||color.equalsIgnoreCase("b")) who=Stone.black;
                    else if(color.equalsIgnoreCase("white")||color.equalsIgnoreCase("w")) who=Stone.white;
                    if(who!=null) {
                        // maybe a wait is needed after sending the genmove
                        // maybe we need two stricts, one for the model and one for gtp?
                        if(generateMove) { // generate move. can return "PASS" "Resign"
                            String noICoordinates=null;
                            Move move=model.generateSillyMove(model.turn());
                            if(move!=null) {
                                noICoordinates=move.toGTPCoordinates(model.board().width(),model.board().depth());
                                Logging.mainLogger.info(model.name+" returning move: "+noICoordinates);
                                send(okCharacter,message.id,noICoordinates);
                            } else {
                                Logging.mainLogger.warning(model.name+" failed to generate a move! returning pass.");
                                Move pass=model.turn().equals(Stone.black)?Move.blackPass:Move.whitePass;
                                send(okCharacter,message.id,pass.name());
                                return false;
                            }
                        } else waitForAMove(message);
                        Logging.mainLogger.info(model.name+" done with genmove.");
                    } else send(badCharacter,message.id,Failure.syntax_error);
                } else send(badCharacter,message.id,"missing argumant(s) for genmove");
                // replace above with syntax error?
                break;
            case showboard:
                send(okCharacter,message.id,/* get past the '='*/'\n'+model.toString());
                break;
            case tgo_goto_node:
                if(message.arguments.length>2) {
                    Long label=null;
                    String argument=message.arguments[1];
                    try {
                        label=Long.valueOf(argument);
                    } catch(NumberFormatException e) {
                        System.out.println(argument+" threw "+e);
                    }
                    if(label!=null) {
                        // go to the specified node
                        MNode remote=new MNode(null);
                        remote.label=label;
                        MNode root=model.root();
                        MNodeFinder finder=MNodeFinder.find(remote,root,labelPredicate);
                        System.out.println(finder.ancestors);
                        System.out.println(finder.found);
                        if(finder.found!=null) {
                            model.top(); // go to the root
                            //List<MNode> x=finder.ancestors;
                            //x.add(finder.found);
                            //convert to a list of moves
                            // may not haave all of the properties
                            ok=model.goToMNode(finder.found);
                            if(ok) {
                                System.out.println("it worked!");
                                send(okCharacter,message.id,"went to label: "+label);
                            } else send(badCharacter,message.id,"go to move failed wth label: "+label);
                        } else send(badCharacter,message.id,"can not find node with label: "+label);
                    } else send(badCharacter,message.id,Failure.syntax_error);
                } else send(badCharacter,message.id,"missing argumant(s) for: "+message.command);
                Logging.mainLogger.severe(message.command+" is not implemented!");
                send(okCharacter,message.id,"");
                break;
            case tgo_receive_sgf:
                String sgfString=message.arguments[1];
                Logging.mainLogger.warning("received encoded sgf: "+sgfString);
                if(useHexAscii) sgfString=decodeToString(sgfString);
                Logging.mainLogger.warning("decoded sgf: "+sgfString);
                model.restore(new StringReader(sgfString));
                ok=send(okCharacter,message.id,"");
                break;
            case tgo_send_sgf:
                sgfString=model.save();
                if(useHexAscii) { sgfString=encode(sgfString); }
                send(okCharacter,message.id,sgfString);
                break;
            default:
                send(badCharacter,message.id,"unimplemented command "+message.command); // change
                // failure
                break;
        }
        else {
            send(badCharacter,message.id,"unknown command "+Arrays.asList(message.arguments));
        }
        return true;
    }
    //while((len=inputStream.read(buffer))!=-1&&!Thread.isInterrupted()){}
    @Override public void stop() {
        isStopping=true;
        Logging.mainLogger.fine("enter back end stop");
        boolean printActiveThreads=false;
        if(printActiveThreads) IO.printThreads(IO.activeThreads(),"active",false);
        IO.myClose(in,out,socket,namedThread,model!=null?model.name:null,this);
        Logging.mainLogger.fine("exit back end stop");
    }
    @Override public boolean isStopping() { return isStopping; }
    @Override public boolean setIsStopping() { boolean rc=isStopping; isStopping=true; return rc; }
    public boolean getGenerateMove() { return generateMove; }
    public boolean setGenerateMove(boolean generateMove) {
        boolean old=this.generateMove;
        this.generateMove=generateMove;
        return old;
    }
    @Override public void run() {
        try { // this reads one line and processes it.
            Logging.mainLogger.info(namedThread+" trying first read.");
            // maybe try looping on in.ready()?
            loop:for(String string=in.readLine();string!=null;string=in.readLine()) {
                if(namedThread!=null&&namedThread.isInterrupted()) {
                    Logging.mainLogger.severe(namedThread+" is interrupted, breaking out of run loop.");
                    break;
                }
                if(isStopping) { // lies! - fix this!
                    Logging.mainLogger.severe("stopping: break out of run loop.");
                }
                Logging.mainLogger.fine("read: '"+string+"'");
                if(done) { Logging.mainLogger.fine(model.name+"done in run loop."); break loop; }
                if(namedThread!=null&&namedThread.isInterrupted()) {
                    Logging.mainLogger.info(model.name+"thread was interrupted.");
                    break loop;
                }
                if(isWaitingForMove()) {
                    throw new RuntimeException("move did not complete!");
                    //while(!isWaitingForMove());
                }
                try {
                    if(!processCommand(string)) {
                        Logging.mainLogger.info(model.name+" "+"exiting run loop.");
                        break loop;
                    }
                } catch(Exception e) {
                    if(isStopping) Logging.mainLogger.info("stopping - process command caught: "+e);
                    else {
                        Logging.mainLogger.severe("not stopping - process command caught: "+e);
                        Logging.mainLogger.severe("stopping: "+isStopping()+" "+IO.toString(namedThread));
                        // break for now, but maybe try to continue/recover later
                    }
                    break loop;
                }
                //Logging.mainLogger.info(model.name+" after process command: "+string+" "+isWaitingForMove());
                // Model.sleep(10); // omitting makes a test work!
                // yes it does help a lot.
                // why? it should be ok for a sleep here.
                //}
                Logging.mainLogger.fine("bottom of read loop.");
                Logging.mainLogger.fine("sleep");
                GTPBackEnd.sleep2(GTPBackEnd.yield);
                Logging.mainLogger.fine("trying subsequent read.");
            }
            Logging.mainLogger.fine(model.name+" end of file or quit or done.");
            if(!done) done=true;
        } catch(InterruptedIOException e) {
            if(isStopping) Logging.mainLogger.info(model.name+" "+"stopping  caught: "+e);
            else Logging.mainLogger.severe(model.name+" "+"0 caught: "+e);
            try {
                GTPBackEnd.sleep2(yield);
                in.close();
            } catch(IOException e1) {
                Logging.mainLogger.severe("close in: "+model.name+" "+"caught: "+e1);
            }
        } catch(IOException e) {
            if(e.getMessage().equals(pipeBrokenMessage)) //
                Logging.mainLogger.info(model.name+" "+"0.5 caught: "+e);
            else {
                Logging.mainLogger.severe(model.name+" "+"1 caught: "+e);
            }
        } catch(Exception e) {
            Logging.mainLogger.severe(model.name+" "+"2 caught: "+e+" "+this);
        } finally { // maybe let it exit and stop later;
            //stop(); // we used to do this
        }
        Logging.mainLogger.info(model.name+" stopping - exit run() "+namedThread);
        Logging.mainLogger.info(model.name+" exit run() "+namedThread);
    }
    private static class GTPRunner {
        // may not be needed now.
        // but it might be a bit cleaner than send and receive static?
        // maybe
        private Writer sendAndReceive(String string,Model model,boolean justRun) throws IOException {
            GTPBackEnd backEnd=new GTPBackEnd(string,model);
            if(justRun) backEnd.run(); // just run, do not start the thread.
            else { // fails right now
                @SuppressWarnings("unused") NamedThread back=backEnd.startGTP(0);
                if(back==null) {
                    Logging.mainLogger.severe("1 startGTP returns null!");
                    if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("1 can not run backend!");
                }
            }
            backEnd.in.close(); // since we just call run?
            backEnd.out.close();
            return backEnd.out;
        }
    }
    public void waitForExitRunLoop() { while(!done) GTPBackEnd.sleep2(GTPBackEnd.yield); }
    public static void sleep2(int n) {
        if(n>=0) try {
            Thread.sleep(n);
        } catch(InterruptedException e) {
            Thread current=Thread.currentThread();
            NamedThread namedThread=current instanceof NamedThread?(NamedThread)current:null;
            if(namedThread!=null) { // this is way too complicated!!!
                Stopable stopable=namedThread.runnable instanceof Stopable?(Stopable)namedThread.runnable:null;
                if(stopable!=null) System.out.println("is stopable stopping: "+stopable.isStopping());
                else Logging.mainLogger.severe("not a stopable!");
            } else Logging.mainLogger.severe(" not a named thread "+Thread.currentThread()+"sleep2 was interrupted!");
        }
        else if(n==yield) Thread.yield();
    }
    public void runBackend(boolean justRun) {
        if(justRun) run(); // just run, do not start the thread.
        else {
            @SuppressWarnings("unused") NamedThread back=startGTP(0);
            if(back==null) {
                Logging.mainLogger.severe("2 startGTP returns null!");
                if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("2 can not run backend!");
            }
        }
        // maybe pass in a lambda to be done here
        // might make testing commands like genmove easier?
        if(!justRun) {
            waitForExitRunLoop(); // let's see if this always works
            // is this needed?
        }
    }
    public String runCommands(boolean justRun) {
        runBackend(justRun);
        String string=out.toString(); // hackish, in this case out is a string writer!
        boolean ok=Response.checkForTwoLineFeeds(string); // move this outside?
        if(!ok) Logging.mainLogger.severe("checkForTwoLineFeeds fails: "+string);
        return string;
    }
    public static Response[] runCommands(List<String> strings,Model model,boolean justRun) {
        String string=new GTPBackEnd(cat(strings),model).runCommands(justRun);
        return Response.responses(string);
        // these all run from the same gtp backend instance
    }
    public static boolean checkMoveCommandsDirect(Model model,List<String> gtpMoves,boolean oneAtATime) {
        boolean rc=true;
        if(oneAtATime) for(String gtpMove:gtpMoves) {
            String string=new GTPBackEnd(gtpMove,model).runCommands(true);
            Response response=Response.response(string);
            if(!response.isOk()) { rc=false; Logging.mainLogger.severe(response+" is not ok!"); }
        }
        else {
            Response[] responses=runCommands(gtpMoves,model,true);
            for(Response response:responses) if(!response.isOk()) {
                rc=false;
                Logging.mainLogger.severe(response+" is not ok!");
            }
        }
        return rc;
    }
    private static void typeGTPCommandsAtTheConsole() throws FileNotFoundException {
        InputStreamReader r=new InputStreamReader(System.in);
        Writer w=new OutputStreamWriter(System.out);
        PrintStream printStream=new PrintStream("out"+System.currentTimeMillis()+".txt.tmp");
        System.setOut(printStream); // redirect output stream!
        // maybe a bas idea as text view and tree view will?
        // Tee tee=Tee.tee(new File("gtpSession.txt"));
        TextView textView=new TextView();
        TextView.createAndShowGui(textView);
        Main.addTextViewOutputStreams(textView);
        Model model=new Model();
        new Main(null,model,textView);
        GTPBackEnd.sleep2(1); // was 10
        // make a gtp with the same model
        End end=new End(new BufferedReader(r),w);
        GTPBackEnd gtp=new GTPBackEnd(end,model);
        // run the gtp
        gtp.run();
        // so it looks like this can play a game
        // by typing at the console
        // the gui has no idea that the model is in a gtp
        // undo the tee?
        // close resources?
        // 1/9/23 maybe use runBackend() here?
    }
    public static void main(String[] args) throws FileNotFoundException { typeGTPCommandsAtTheConsole(); }
    public boolean done;
    // maybe we need more dones
    // done with read loop because we got a tgo_stop
    // done with read loop because eof
    // ...
    // done with read loop for any other reason
    public boolean isStopping;
    public final BufferedReader in;
    public final Writer out;
    public final Model model; // may be null
    // yes, will be null if this is never started.
    public Socket socket;
    public transient long gameId;
    public transient NamedThread namedThread; // may be null
    private volatile transient boolean generateMove=false; // or wait for a move
    protected volatile transient boolean isWaitingForMove;
    // no, it's a good name.
    // this is set by genmove which is a request to generate a move.
    // there are two cases:
    // 1) generateMove is true -> generate a (silly in my case) move.
    // 2) generateMove is false -> wait for someone else to make a move.
    boolean useHexAscii=true; // needs to be always on!
    public static final boolean throwOnstartGTPFailure=true;
    // 1 works for controller tests.
    // not any more, 20 is failing a lot now.
    // 0 works for controller tests now, but game tests hang.
    public static final int noTimeoutTime0=0;
    public static final int shortTimeoutTime=10;
    public static final int timeoutTime=100;
    public static final int longTimeoutTime=400;
    public static int sleepTime=1;
    public static final int yield=-1;
    // move the above constants to IO class.
    public static final Character okCharacter='=',badCharacter='?';
    public static final String okString=""+okCharacter+" ",badString=""+badCharacter+" ";
    public static final String protocolVersionString="2";
    public static final String unknownCommandMessage="unknown command";
    public static final Character tab='\t';
    public static final Character carriageReturn='\r';
    public static final String twoLineFeeds="\n\n";
    static final String pipeBrokenMessage="Pipe broken";
}
