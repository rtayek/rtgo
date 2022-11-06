package controller;
import static controller.GTPBackEnd.*;
import java.io.IOException;
import java.util.List;
import equipment.Board;
import io.*;
import io.IO.End;
import io.IO.End.Holder;
import model.Model;
import server.NamedThreadGroup;
import server.NamedThreadGroup.NamedThread;
import utilities.Pair;
public class BothEnds { // has both a front end and a back end.
    public BothEnds() {}
    public void setupBoth(Holder holder,String frontEndName,Model model) {
        // maybe get rid of this?
        if(holder.front!=null) setupFrontEnd(holder.front,frontEndName);
        if(holder.back!=null) setupBackEnd(holder.back,model);
    }
    public void setupFrontEnd(End end,String name) { frontEnd=new GTPFrontEnd(end); frontEnd.name=name; }
    public void setupBackEnd(End end,Model model) { backEnd=new GTPBackEnd(end,model); }
    public void setupBackEnd(End end,String name,long id) {
        Model model=new Model(name); // model probably already has id in the name.
        model.gameId=id;
        setupBackEnd(end,model);
        backEnd.gameId=id;
    }
    public void stopFrontEnd() throws IOException {
        if(frontEnd!=null) {
            Logging.mainLogger.fine("stop front end: "+frontEnd.name+" "+frontEnd.thread);
            frontEnd.stop();
            Logging.mainLogger.fine("front end stopped: "+frontEnd.name+" "+frontEnd.thread);
        } else Logging.mainLogger.fine("front end is null!");
    }
    public void stopBackEnd() throws IOException {
        if(backEnd!=null) {
            Logging.mainLogger.fine("stop back end "+backEnd.model.name);
            backEnd.stop();
            Logging.mainLogger.fine("back end stopped: "+backEnd.model.name+" "+backEnd.namedThread);
        }
    }
    public void stop() {
        // order seems to matter here.
        // duplex hangs when we stop front first (game test case)
        boolean stopBackEndFirst=false;
        // false hangs!
        // true gets a lot of timeouts
        if(stopBackEndFirst) {
            try {
                stopBackEnd();
            } catch(IOException e) {
                e.printStackTrace();
            }
            try {
                stopFrontEnd();
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                stopFrontEnd();
            } catch(IOException e) {
                e.printStackTrace();
            }
            try {
                stopBackEnd();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean checkBoth() { // call this!
        boolean rc=true;
        if(frontEnd==null) {
            Logging.mainLogger.warning("front end is null!");
            rc=false;
        } else {
            if(frontEnd.in==null) { Logging.mainLogger.warning("front end input is null!"); rc=false; }
            if(frontEnd.out==null) { Logging.mainLogger.warning("front end output is null!"); rc=false; }
        }
        if(backEnd==null) {
            Logging.mainLogger.warning("back end is null!");
            rc=false;
        } else {
            if(backEnd.in==null) { Logging.mainLogger.warning("back end input is null!"); rc=false; }
            if(backEnd.out==null) { Logging.mainLogger.warning("back end output is null!"); rc=false; }
            if(backEnd.model==null) { Logging.mainLogger.warning("back end model is null!"); rc=false; }
            if(backEnd.socket==null) { Logging.mainLogger.warning("back end socket is null!"); rc=false; }
        }
        if(!rc) Logging.mainLogger.warning("check fails!");
        return rc;
    }
    @Override public String toString() { return "BothEnds [frontEnd="+frontEnd+", backEnd="+backEnd+"]"; }
    private static void run() throws IOException,InterruptedException {
        // this just runs a thread that sends and receives some commands.
        // maybe this belongs in a test or in gtp?
        Holder holder=Holder.duplex();
        Model model=new Model("model");
        model.setBoard(Board.factory.create(7));
        BothEnds both=new BothEnds();
        both.setupBoth(holder,"model",model);
        @SuppressWarnings("unused") NamedThread back=both.backEnd.startGTP(0);
        if(back==null) { Logging.mainLogger.severe("startGTP returns a null thread!"); return; }
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.groupZero,"main",true);
        Thread thread=null;
        Pair<Boolean,Boolean> done=new Pair<>(false,false);
        Runnable runnable=new Runnable() {
            static int messages=10;
            @Override public void run() {
                for(int i=0;i<messages;i++) {
                    //send and receive does a start gtp!
                    Response response=both.frontEnd.sendAndReceive(Command.name.name());
                    if(!response.isOk()) Logging.mainLogger.warning("name fails!");
                }
                done.first=true;
            }
        };
        // confused? too many threads here
        if(thread==null)
            (thread=NamedThreadGroup.createNamedThread(NamedThreadGroup.groupZero,runnable,model.name)).start();
        else Logging.mainLogger.warning("thread is not null in start gtp!");
        while(!done.first) sleep2(yield);
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.groupZero,"main",true);
    }
    static void runBoth(Model expected,boolean useThread) throws Exception {
        Model actual=new Model();
        actual.setRoot(expected.board().width(),expected.board().depth());
        List<String> gtpMoves=expected.gtpMovesToCurrentState();
        boolean ok=checkMoveCommandsDirect(actual,gtpMoves,false);
        if(!ok) Logging.mainLogger.severe("run both fails on: "+gtpMoves);
    }
    private static void runBoth0(Holder holder) throws Exception {
        Model model=new Model("model");
        model.setBoard(Board.factory.create());
        BothEnds both=new BothEnds();
        both.setupBoth(holder,model.name,model);
        @SuppressWarnings("unused") NamedThread back=both.backEnd.startGTP(0);
        Logging.mainLogger.info("started gtp");
        // all of the other direct stuff works when we do not start a thread.
        // figure out why THIS does not work when we do not start a thread!
        // not clear why these are failing
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.groupZero,"main",true);
        Response response=both.frontEnd.sendAndReceive(Command.name.name());
        Logging.mainLogger.fine("response: "+response);
        response=both.frontEnd.sendAndReceive(Command.boardsize.name()+" 9");
        Logging.mainLogger.fine("response: "+response);
        both.backEnd.setGenerateMove(true);
        // falsehangs
        // true works
        Logging.mainLogger.fine("is waiting for a move: "+both.backEnd.isWaitingForMove()+" at: "+Init.first.et);
        response=both.frontEnd.sendAndReceive(Command.genmove.name()+" black");
        Logging.mainLogger.fine("response: "+response);
        Logging.mainLogger.fine("wait "); // hangs here
        boolean waitUntilItIsTmeToMove=false; // may not matter?
        // it does matter here. it does not matter in both duplex and both socket test cases.
        Logging.mainLogger.fine("is waiting for a move: "+both.backEnd.isWaitingForMove());
        if(waitUntilItIsTmeToMove) both.backEnd.waitUntilItIsTmeToMove(); // removing this makes this work!
        Logging.mainLogger.fine("end of wait");
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.groupZero,"main",true);
        GTPBackEnd.sleep2(1); // was 10
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.groupZero,"main",true);
        NamedThreadGroup.printThraedsAtEnd();
    }
    public static void main(String[] args) throws Exception {
        //run();
        //Model model=new Model();
        //model.setBoard(Board.factory.create(7));
        //runBoth(new Model(),false);
        Holder holder=Holder.duplex();
        BothEnds.runBoth0(holder); // just one model! (was true)
    }
    public transient GTPFrontEnd frontEnd;
    public transient GTPBackEnd backEnd;
}
