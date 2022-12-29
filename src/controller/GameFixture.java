package controller;
import static controller.GTPBackEnd.sleep2;
import static io.Init.first;
import static io.Logging.*;
import java.io.*;
import java.util.*;
import controller.GTPFrontEnd.PipeEofException;
import equipment.*;
import io.*;
import io.IO.*;
import io.IO.End.Holder;
import model.*;
import model.Move.MoveImpl;
import server.NamedThreadGroup;
import server.NamedThreadGroup.NamedThread;
import utilities.*;
public class GameFixture implements Runnable,Stopable {
    public enum States { starting, waitForBlackMove, waitForWhiteMove, ended, }
    public GameFixture(Model recorder) {
        recorder.setRole(Model.Role.anything);
        String recorderName="recorder-"+id;
        recorderFixture.setupBoth(Holder.duplex(),recorderName,recorder);
        // maybe have recorder just be a model?
    }
    public BothEnds opponent(BothEnds player) {
        if(player==blackFixture) return whiteFixture;
        else if(player==whiteFixture) return blackFixture;
        else return null;
    }
    public Stone color(BothEnds player) {
        if(player==blackFixture) return Stone.black;
        else if(player==whiteFixture) return Stone.white;
        else return null;
    }
    public boolean areBoardsEqual() {
        if(!blackFixture.backEnd.model.board().isEqual(whiteFixture.backEnd.model.board())) return false;
        if(!blackFixture.backEnd.model.board().isEqual(recorderFixture.backEnd.model.board())) return false;
        return true;
    }
    public void setupServerSide(End black,End white) {
        blackFixture.setupFrontEnd(black,blackName());
        whiteFixture.setupFrontEnd(white,whiteName());
        @SuppressWarnings("unused") Thread recorder=recorderFixture.backEnd.startGTP(id);
    }
    public void startGame() {
        System.out.println("start game");
        if(namedThread!=null) throw new RuntimeException("game thread alreasy exists!");
        Logging.mainLogger.info("starting game: "+id+" "+blackFixture+" "+whiteFixture);
        (namedThread=NamedThreadGroup.createNamedThread(id,this,"game")).start();
    }
    String generateMove() {
        // collapse or refactor this
        Response response;
        mainLogger.info("1 enter generate move for "+recorderFixture.backEnd.model.turn());
        // we need to set a timer here in case the other end never ends.
        mainLogger.fine(directory);
        switch(recorderFixture.backEnd.model.turn()) {
            case black:
                response=blackFixture.frontEnd.sendAndReceive(Command.genmove.name()+" black");
                if(response==null) { mainLogger.severe("genmove new behavior black"); return null; }
                if(response.isBad()) mainLogger.severe("black  bad oops");
                mainLogger.info("exit generate move at "+first.et);
                return response.response;
            case white:
                response=whiteFixture.frontEnd.sendAndReceive(Command.genmove.name()+" white");
                if(response==null) { mainLogger.severe("genmove new behavior white"); return null; }
                if(response.isBad()) { mainLogger.severe("white bad oops"); mainLogger.warning(response.toString()); }
                mainLogger.info("exit generate move"+first.et);
                return response.response;
            default:
                throw new RuntimeException("color oops");
        }
    }
    void tellObservers(String command) {
        // what happens if this fails?
        for(GTPFrontEnd frontEnd:observers) {
            Response response=frontEnd.sendAndReceive(command);
            if(!response.isOk()) throw new RuntimeException("tell oops");
        }
    }
    @Override public boolean isStopping() { return isStopping; }
    @Override public boolean setIsStopping() { boolean rc=isStopping; isStopping=true; return rc; }
    public void printHistograms(double dt) {
        if(dt!=0) {
            System.out.println("game took "+dt+" ms.");
            double speed=recorderFixture.backEnd.model.moves()/et.etms();
            System.out.println("speed: "+speed+" ,moves/ms.");
            System.out.println("average time: "+(1/speed)+" ms. for one move.");
        }
        System.out.println("histograms");
        System.out.println("recorder "+recorderFixture.backEnd.model.histogram);
        System.out.println("   black "+blackFixture.backEnd.model.histogram);
        if(whiteFixture.backEnd!=null) System.out.println("   white "+whiteFixture.backEnd.model.histogram);
        System.out.println("before: "+waitBefore);
        System.out.println("after: "+waitAfter);
    }
    @Override public void stop() { // change name to stop backends
        isStopping=true;
        if(recorderFixture!=null) { recorderFixture.stop(); GTPBackEnd.sleep2(longSleepTime); }
        if(blackFixture!=null) { blackFixture.stop(); GTPBackEnd.sleep2(longSleepTime); }
        if(whiteFixture!=null) { whiteFixture.stop(); GTPBackEnd.sleep2(longSleepTime); }
        // maybe: https://bugs.openjdk.java.net/browse/JDK-4859836
        // maybe this additional sync is not necessary or may hang?
        // https://www.reddit.com/r/javahelp/comments/7j1omm/buffer_reader_hangs_socket_communication/
        Logging.mainLogger.info("stop game threadr");
        String threadName=namedThread!=null?namedThread.getName():"null";
        IO.myClose(null,null,null,namedThread,threadName,this);
        GTPBackEnd.sleep2(GTPBackEnd.yield);
    }
    public void startPlayerBackends() {
        @SuppressWarnings("unused") Thread white=whiteFixture.backEnd.startGTP(id);
        @SuppressWarnings("unused") Thread black=blackFixture.backEnd.startGTP(id);
    }
    public static boolean initializeBoard(BothEnds both,int width) {
        ArrayList<String> strings=new ArrayList<>();
        strings.add(Command.boardsize.name()+" "+width);
        strings.add(Command.clear_board.name());
        return both.frontEnd.sendAndReceive(strings);
    }
    public Response initializeGame() {
        Response response=null;
        // turning on to test some server stuff.
        // maybe this init login should be outside the this run method?
        response=recorderFixture.frontEnd.sendAndReceive(Command.tgo_anything.name());
        // board is still null in recorder
        // but recorder may have a long game in it!
        if(recorderFixture.backEnd.model.board()!=null) {
            int width=recorderFixture.backEnd.model.board().width();
            if(!initializeBoard(recorderFixture,width)) throw new RuntimeException("init recorder oops");
            response=blackFixture.frontEnd.sendAndReceive(Command.tgo_black.name());
            if(!initializeBoard(blackFixture,width)) throw new RuntimeException("init black oops");
            response=whiteFixture.frontEnd.sendAndReceive(Command.tgo_white.name());
            if(!initializeBoard(whiteFixture,width)) throw new RuntimeException("init white oops");
        }
        return response;
    }
    @Override public void run() {
        try {
            Response response=null;
            Logging.mainLogger.info("enter game main loop");
            while(!isStopping) {
                if(namedThread!=null&&namedThread.isInterrupted()) {
                    Logging.mainLogger.severe(namedThread+" is interrupted, breaking out of game loop.");
                    break;
                }
                String command,move;
                gameLogger.info("get "+recorderFixture.backEnd.model.turn()+" move #"
                        +(recorderFixture.backEnd.model.moves()+1));
                move=generateMove();
                if(move!=null) while(move.endsWith("\n")) move=move.substring(0,move.length()-1);
                gameLogger.info(" 1 got move: #"+recorderFixture.backEnd.model.moves()+": "+move);
                if(move==null||move.equals("")) {
                    gameLogger.warning("move is null or empty, breaking out of game loop!");
                    break;
                }
                command=Command.play.name()+" "+recorderFixture.backEnd.model.turn()+" "+move;
                response=recorderFixture.frontEnd.sendAndReceive(command);
                if(response!=null) {
                    if(response.isBad()) {
                        gameLogger.warning(recorderFixture.backEnd.model.name+" bad response: "+response);
                        Logging.mainLogger.severe("response bad oops");
                        //throw new RuntimeException("response bad oops");
                        break; // lets try breaking out instead of throwing
                    }
                } else {
                    Logging.mainLogger.severe("response null oops");
                    break;
                }
                gameLogger.info("recorded move #"+recorderFixture.backEnd.model.moves()+"@"
                        +recorderFixture.backEnd.model.lastMoveGTP());
                if(saveGameAfterMove) {
                    System.out.println("saving game.");
                    try {
                        File file=new File(directory,"game"+fileId+".sgf");
                        System.out.println(file);
                        Writer writer=IO.toWriter(file);
                        boolean wasSaved=recorderFixture.backEnd.model.save(writer);
                        writer.close();
                        if(!wasSaved) {
                            gameLogger.warning("save game failed!");
                            System.out.println("game save failed!.");
                        } else System.out.println("game saved.");
                    } catch(Exception e) {
                        Logging.mainLogger.info("save game caught: "+e);
                    }
                }
                // maybe. if this were a correspondence game, we should be able to restore from this point.
                // yes, how to restore from here?
                // write a test for the above
                // shove will probably work.
                switch(recorderFixture.backEnd.model.turn()) { // send move to the other player
                    // these are different, they need to be the same!
                    case black:
                        command=Command.play.name()+" "+"white"+" "+move; // may get confuses if we allow somehow 2 moves in a row for one player?
                        response=blackFixture.frontEnd.sendAndReceive(command);
                        // check response?
                        break;
                    case white:
                        command=Command.play.name()+" "+"black"+" "+move;
                        response=whiteFixture.frontEnd.sendAndReceive(command);
                        // check response?
                        break;
                    default:
                        throw new RuntimeException("default color oops");
                }
                tellObservers(command); // send move to any observers
                if(move.contains(Move.gtpResignString)) { gameLogger.info("resign: "+move+" "+response); break; }
                if(recorderFixture.backEnd.model.moves()>maxMoves) {
                    gameLogger.info(
                            "breaking out of game run loop after "+recorderFixture.backEnd.model.moves()+" moves.");
                    break;
                }
            }
            Logging.mainLogger.info("after game main loop");
        } catch(PipeEofException e) {
            gameLogger.warning(this+" 0 caught: "+e);
            e.printStackTrace();
        } catch(NullPointerException e) {
            gameLogger.warning(this+" 1 caught: "+e);
            e.printStackTrace();
        } catch(Exception e) {
            gameLogger.warning(this+" 2 caught: "+e);
            e.printStackTrace();
        } finally {
            Logging.mainLogger.info("finally after game main loop");
            isStopping=true;
            if(Thread.currentThread().equals(namedThread)) gameLogger.info("game: "+id+" stopping itself!");
            else stop(); // no need to stop self?
        }
    }
    public static long maxId() { // just overwrite for now
        return 0;
    }
    public String blackName() { return "black-"+id; }
    public String whiteName() { return "white-"+id; }
    public String recorderName() { return "recorder-"+id; }
    public void printThreads() {
        System.out.println("2 game thread: "+namedThread);
        System.out.println("recorder: "+recorderFixture.backEnd.namedThread);
        if(blackFixture.backEnd!=null) System.out.println("   black: "+blackFixture.backEnd.namedThread);
        if(whiteFixture.backEnd!=null) System.out.println("   white: "+whiteFixture.backEnd.namedThread);
    }
    public void playOneMove(BothEnds player,Move move) { // does not wait for opponent's board.
        double t0=et.etms();
        player.backEnd.waitUntilItIsTmeToMove();
        waitBefore.add(et.etms()-t0);
        player.backEnd.model.playOneMove(move);
    }
    public int playOneMoveAndWait(BothEnds player,BothEnds opponent,Move move) {
        // needs a game to be running and we need both backends.
        // makes one move and waits for it to show up on opponents board.
        // this uses both ends.
        if(namedThread==null) throw new RuntimeException();
        if(player.frontEnd==null) throw new RuntimeException();
        // these nay not be necessary?
        if(opponent.frontEnd==null) throw new RuntimeException();
        Model playerModel=player.backEnd.model,opponentModel=opponent.backEnd.model;
        if(move.equals(Move.nullMove))
            throw new RuntimeException(move+" 4 "+playerModel.lastMoveGTP()+" "+playerModel.lastMove()+" move oops");
        Logging.mainLogger.info("wait until it's time to move");
        int moves=playerModel.moves();
        playOneMove(player,move);
        if(false) return moves;
        String playerName=playerModel.name;
        String lastMove=playerModel.lastMoveGTP();
        double t0=et.etms();
        //if(move instanceof Resign||move instanceof Pass) throw new RuntimeException(move+" before wait");
        opponentModel.waitForMoveCompleteOnBoard(moves); // on opponents board
        waitAfter.add(et.etms()-t0);
        if(playerModel.moves()!=opponentModel.moves()) {
            Logging.mainLogger.severe(playerName+" after wait for move  "+(moves+1)+" opponents board");
            throw new RuntimeException("moves oops");
        }
        if(!lastMove.equals(opponentModel.lastMoveGTP())) throw new RuntimeException("1 last move oops");
        if(!areBoardsEqual()) throw new RuntimeException("boards are not equal!");
        return moves; // off ny one. apparently not used.
    }
    public static void playSillyGame(GameFixture gameFixture,int m) throws InterruptedException {
        playSillyGame(gameFixture,m,false);
    }
    public static void playSillyGame(GameFixture gameFixture,int m,boolean verbose) throws InterruptedException {
        // maybe move this to game fixture?
        Et et=new Et();
        Logging.mainLogger.info("enter play silly game.");
        BothEnds black=gameFixture.blackFixture,white=gameFixture.whiteFixture;
        // this does not belong here. where should it be?
        // plays a game by making moves on the model.
        // assumes that both models are connected t0 the recorder.
        // check this!
        // seems like this is simulating two players playing omn their own boards.
        int n=black.backEnd.model.board().depth();
        boolean oddNumberOfMoves=false;
        for(int i=0;i<m;++i) {
            Logging.mainLogger.info("move "+i);
            // this should wait for a turn before moving
            Stone color=i%2==0?Stone.black:Stone.white;
            Move move=new MoveImpl(color,new Point(i/n,i%n));
            if(move.equals(Move.nullMove)) { throw new RuntimeException(move+" null move in play silly game."); }
            Stone turn=i%2==0?Stone.black:Stone.white;
            int moves=turn==Stone.black?gameFixture.playOneMoveAndWait(black,white,move)
                    :gameFixture.playOneMoveAndWait(white,black,move);
            //histogram.add(et.etms());
            sleep2(GTPBackEnd.yield);
            //if(i==m-1) { Logging.mainLogger.fine("odd number of moves"); oddNumberOfMoves=true; break; }
        }
        Model recorder=gameFixture.recorderFixture.backEnd.model;
        //sleep2(5);
        //while(recorder.moves()<m) sleep2(GTPBackEnd.yield);
        if(recorder.moves()!=m) {
            System.out.println(recorder.moves()+"!="+m);
            throw new RuntimeException(recorder.moves()+"!="+m);
        }
        double dt=et.etms();
        // this won't allow multiple moves in a row by a player.
        // check on this,maybe it will
        // gtp supposedly can/will do this.
        Logging.mainLogger.info("at end, play a resign at: "+first.et);
        if(m%2==0) // play a resign at the end.
            gameFixture.playOneMoveAndWait(black,white,Move.blackResign);
        else gameFixture.playOneMoveAndWait(white,black,Move.whiteResign);
        if(verbose) {
            Board board=gameFixture.recorderFixture.backEnd.model.board();
            Pair<List<Block>,List<Block>> blocks=Block.findBlocks(board);
            System.out.println("black: "+blocks.first.size()+" blocks (0).");
            System.out.println("white: "+blocks.second.size()+" blocks (180).");
            System.out.println("black has "+recorder.prisoners(Stone.white)+" white prisoners");
            System.out.println("white has "+recorder.prisoners(Stone.black)+" black prisoners");
            int width=board.width(),depth=board.depth();
            if(m==width*depth-1) {
                if(blocks.first.size()!=0) throw new RuntimeException();
                if(blocks.second.size()!=180) throw new RuntimeException();
                if(recorder.prisoners(Stone.white)!=0) throw new RuntimeException();
                if(recorder.prisoners(Stone.black)!=180) throw new RuntimeException();
            }
            gameFixture.printHistograms(dt);
        }
        Logging.mainLogger.info(black.backEnd.model.name+" "+"exit playGame()");
    }
    public static void printStuff(GameFixture game) {
        Model blackModel=game.blackFixture.backEnd.model;
        Model whiteModel=game.whiteFixture.backEnd.model;
        boolean isBlackWaitingForAMove=game.blackFixture.backEnd.isWaitingForMove();
        boolean isWhiteWaitingForAMove=game.whiteFixture.backEnd.isWaitingForMove();
        int blackMoves=blackModel.moves();
        int whiteMoves=whiteModel.moves();
        System.out.println("3 waitstates: "+blackMoves+" black moves "+isBlackWaitingForAMove+", "+whiteMoves
                +" white moves "+isWhiteWaitingForAMove);
    }
    public final BothEnds recorderFixture=new BothEnds();
    // maybe pass one of these around to start a game?
    public final BothEnds blackFixture=new BothEnds();
    public final BothEnds whiteFixture=new BothEnds();
    public final int maxMoves=Integer.MAX_VALUE-1;
    public final long fileId=++fileIds; // external id
    public final long id=++ids;
    public boolean isStopping;
    public boolean doInit=false; // was false.
    boolean saveGameAfterMove=true;
    Et et=new Et();
    Histogram waitBefore=new Histogram(5,0,5);
    Histogram waitAfter=new Histogram(5,0,5);
    public final List<GTPFrontEnd> observers=new ArrayList<>();
    public NamedThread namedThread;
    {
        NamedThreadGroup namedThreadGroup=new NamedThreadGroup(id);
        first.add(namedThreadGroup); //??
    }
    public static long fileIds=maxId(); // for saving
    public static long ids;
    public static final String directory="serverGames";
    public static final int longSleepTime=10;
}
