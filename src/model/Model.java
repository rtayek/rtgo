package model;
import static io.IO.*;
import static io.Logging.parserLogger;
import static sgf.Parser.restoreSgf;
import java.awt.geom.Point2D;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import audio.Audio;
import controller.Command;
import controller.GTPBackEnd;
import equipment.*;
import equipment.Board.*;
import io.*;
import io.IO.*;
import model.Move.*;
import server.NamedThreadGroup.NamedThread;
import sgf.*;
import utilities.*;
public class Model extends Observable { // model of a go game or problem forrest
    // more like game tree or forest of such.
    public enum Who { commandLine, gui, gtp }
    public enum What { move, navigate, delete }
    // observer can only navigate.
    // black/white can play if it's his turn.
    // anything can do anything. maybe should be review?
    // gtp?
    // if anything moves, navigate or deletes, how to tell observers?
    public enum Role { // both, neither, one, other; // client only
        anything,observer,playBlack(Stone.black),playWhite(Stone.white);
        Role(Stone stone) { this.stone=stone; }
        Role() { this(Stone.vacant); }
        /*
        public static Role fromCommand(Command command) {
            Role rc=null;
            switch(command) {
                case tgo_anything:
                    rc=anything;
                    break;
                case tgo_black:
                    rc=playBlack;
                    break;
                case tgo_white:
                    rc=playWhite;
                    break;
                case tgo_observe:
                    rc=observer;
                    break;
                default:
                    break;
            }
            return rc;
        }
         */
        public final Stone stone;
    }
    public enum Where { // generalize to not turn or not playing this color?
        onVacant,occupied,hole,notCloseEnough,notInBand,onBoard,offBoard;
        // should this have a value for ko or duplicate board position?
        public static boolean inABand(Board board,int band,int x,int y) {
            if(board.isOnBoard(x,y)) return false;
            if(x<-band||board.width()+band<=x) return false;
            if(y<-band||board.depth()+band<=y) return false;
            return true;
        }
        public static Where isPoint(Board board,int band,Point2D.Float point,Point closest) {
            if(point!=null) { double distance=point.distance(closest); if(distance>=epsilon) return notCloseEnough; }
            // so either point is null or it's close enough
            Where where=offBoard;
            boolean isPointOnBoard=board.isOnBoard(closest);
            if(!isPointOnBoard) {
                if(board.topology().equals(Topology.torus)) {
                    if(band>0) if(inABand(board,band,closest.x,closest.y)) {
                        closest.x=board.moduloWidth(closest.x);
                        closest.y=board.moduloDepth(closest.y);
                        if(board.isOnBoard(closest)) where=onBoard;
                    }
                }
            } else where=onBoard;
            if(where.equals(onBoard)) {
                if(board.at(closest.x,closest.y)==Stone.edge) where=hole;
                else {
                    if(board.at(closest.x,closest.y)!=Stone.vacant) where=occupied;
                    else where=onVacant;
                }
            }
            return where;
        }
    }
    public enum MoveResult {
        legal,occupied,notYourTurn,duplicateHash,badRole,threw,unknown;
        boolean wasLegal() { return this.equals(legal); }
    }
    public enum Reason { ok, notYourTurn, notYourColor; }
    public Model() { this(""); }
    public Model(String name) {
        //if(name.contains("19")) Logging.mainLogger.severe("frog model has 19 in name.");
        this.name=name;
        setRoot();
    }
    public boolean save(Writer writer) {
        MNode root=root();
        boolean found=hasRT(root);
        // add new root if we don't already have one.
        if(!found) { root=new MNode(null); root.children.add(root()); }
        boolean ok=MNode.save(writer,root,new Indent(SgfNode.options.indent));
        return ok;
    }
    public static boolean hasRT(MNode root) {
        boolean found=false;
        for(SgfProperty p:root.sgfProperties) { if(p.p().equals(P.RT)) { found=true; break; } }
        return found;
    }
    public String save() {
        StringWriter stringWriter=new StringWriter();
        save(stringWriter);
        return stringWriter.toString();
    }
    public void restore(Reader reader) {
        if(reader==null) {
            System.out.println("restoring model from null reader!");
            Logging.mainLogger.config("restoring model from null reader!");
        }
        MNode games=MNode.restore(reader);
        setRoot(games);
    }
    public Model(Model model,String name) { // copy constructor
        this.name=name;
        String string=model.save();
        restore(new StringReader(string));
    }
    public static boolean canDelete(Model model) { return model.currentNode().parent!=null; }
    public static File insureExtension(File file,String desiredExtension) {
        String extension=getExtension(file);
        if(extension==null||!extension.equalsIgnoreCase("sgf"))
            file=new File(file.getParent(),file.getName()+"."+desiredExtension);
        return file;
    }
    void checkForLittleGolem(MNode node) {
        boolean isTorus=false;
        for(SgfProperty property:node.sgfProperties) for(String string:property.list())
            if(string.contains("Toroidal")||(state.isFromLittleGolem=string.contains("littlegolem.com"))) {
                isTorus=true;
                break;
            }
        if(false&&isTorus) { // don't want this to be the default
            Logging.mainLogger.config(name+" "+"looks like a toroidal game!");
            setBoardTopology(Topology.torus);
        }
        // not sure why i needed this.
        // maybe just a way to automagically use torus topology/
    }
    public MNode root() {
        if(state.root==null) Logging.mainLogger.warning("root() is returning null!");
        return state.root;
    }
    public void setRoot(MNode root) { // this always gets called.
        setBoard(null);
        if(root==null) {
            // maybe leave it at null
            // and adjust display?
            Logging.mainLogger.config("root is null!: ");
            setRoot();
            return;
        }
        stack.clear();
        state=new State();
        state.root=root;
        // this is where we probably need to insure that there us a board.
        // or make sure some other stuff is setup if this is the root.
        //
        if(root.children.size()>1) Logging.mainLogger.info("more than one game!");
        if(root!=null) checkForLittleGolem(root);
        Logging.mainLogger.config(name+" "+"doing root: "+root);
        do_(root);
        if(board()==null) System.out.println("board is null after do root.");
        else System.out.println("board is not null after do root.");
        // maybe do a second do_() in some cases?
        //if(root.children.size()>0)
        //    do_(root.children.get(0));
        long t0=System.nanoTime();
        Logging.mainLogger.fine(name+" "+"doing notify @"+t0+" , "+root);
        // we are doing a notify here!
        setChangedAndNotify(new Event.Hint(Event.newTree,""+t0)); // ??
        // node change will select
        setChangedAndNotify(new Event.Hint(Event.nodeChanged,""+t0)); // ??
    }
    public boolean isFromLittleGolem() { return state.isFromLittleGolem; }
    public static void addProperty(MNode node,P p,String string) { addProperty(node,p,new String[] {string}); }
    static void addProperty(MNode node,P p,String[] strings) {
        List<String> list=new ArrayList<>(Arrays.asList(strings));
        addProperty(node,p,list);
    }
    static void addProperty(MNode node,P p,List<String> strings) {
        SgfProperty property=new SgfProperty(p,strings);
        node.sgfProperties.add(property);
    }
    private static void addSgfRegion_(int depth,MNode newRoot,List<Point> points) {
        if(points.size()>0) {
            List<String> strings=new ArrayList<>();
            for(Point point:points) {
                String string=Coordinates.toSgfCoordinates(point,depth);
                strings.add(string);
            }
            Logging.mainLogger.warning("region string: "+strings);
            addProperty(newRoot,P.RG,strings);
        }
    }
    private static void addRegion(int width,int depth,Topology topology,Shape shape,MNode newRoot) {
        List<Point> points=null;
        if(topology==Topology.diamond) points=Board.getPointsForDiamondRegion(width,depth);
        else points=Shape.getPointsForRegion(width,depth,shape);
        addSgfRegion_(depth,newRoot,points);
    }
    public void setRootFromParameters() {
        System.out.println("set root from parameters");
        int width=(int)Parameters.width.currentValue();
        int depth=(int)Parameters.depth.currentValue();
        Board.Topology topology=(Board.Topology)Parameters.topology.currentValue();
        Board.Shape shape=(Board.Shape)Parameters.shape.currentValue();
        setRoot(width,depth,topology,shape);
    }
    public void setRoot() { setRoot(Board.standard,Board.standard); }
    public void setRoot(int width,int depth) { setRoot(width,depth,Board.Topology.normal,Shape.normal); }
    public void setRoot(int width,int depth,Board.Topology topology,Board.Shape shape) {
        // where is the board constructed?
        Logging.mainLogger.config("setRoot: "+name+" "+"board type is: "+topology+", shape is: "+shape);
        //IO.stackTrace(10);
        MNode newRoot=new MNode(null);
        //MNode main=new MNode(newRoot); // add extra root
        MNode main=new MNode(null); // no extra root
        addProperty(main,P.FF,"4");
        addProperty(main,P.GM,"1");
        addProperty(main,P.AP,sgfApplicationName);
        addProperty(main,P.C,"root");
        if(!topology.equals(Topology.normal)) addProperty(main,P.C,sgfBoardTopology+topology);
        if(!shape.equals(Shape.normal)) addProperty(main,P.C,sgfBoardShape+shape);
        String string=Integer.valueOf(width).toString()+":"+Integer.valueOf(depth).toString();
        if(width==depth) string=Integer.valueOf(width).toString();
        boolean alwaysSetBoardSize=true;// was true
        // false breaks a lot of tests
        if(alwaysSetBoardSize) addProperty(main,P.SZ,string);
        // work needed here!
        if(topology.equals(Topology.torus)) addProperty(main,P.KM,"4.5");
        addRegion(width,depth,topology,shape,main);
        Logging.mainLogger.fine(name+" "+"new root is: "+main);
        setRoot(main);
        Logging.mainLogger.config("exit setRoot: "+name+" "+"board type is: "+topology+", shape is: "+shape);
    }
    public MNode currentNode() { return state.node; }
    public Board board() { return state.board; }
    public void setBoard(Board board) { // this is wierd! - do a notify or something!!!
        this.state.board=board;
        if(board==null) Logging.mainLogger.config("board is null!");
        else Logging.mainLogger.fine("setting board");
    }
    public Topology boardTopology() { return state.topology; }
    public void setBoardTopology(Topology type) { state.topology=type; }
    public Shape boardShape() { return state.shape; }
    public void setBoardShape(Shape shape) {
        Logging.mainLogger.config("set shape to: "+shape);
        state.shape=shape;
        // maybe have to adjust with and depth if shape is unusual
        // i.e. diamond would need width=depth
    }
    public boolean hasABoard() { return board()!=null; }
    public double komi() { return state.komi; }
    public void setKomi(double komi) { state.komi=komi; }
    public void setBand(int band) { Logging.mainLogger.warning("set band to: "+band); state.band=band; }
    public int band() { return state.band; }
    public synchronized int moves() { return state.moves; }
    public Collection<Move> mainLineFromState() { while(Navigate.down.do_(this)); return movesToCurrentState(); }
    public List<String> gtpMovesToCurrentState() {
        // not necessarily the main line!
        List<Move> moves=movesToCurrentState();
        List<String> gtpMoves=Move.toGTPMoves(moves,board().width(),board().depth());
        return gtpMoves;
    }
    public List<Move> movesToCurrentState() {
        // current line
        Stack<State> stack=this.stack;
        List<Move> moves=new ArrayList<>();
        Board board=null;
        String lastMoveGTP=null;
        boolean first=true;
        for(State state:stack) {
            // how do we tell if a move was made?
            if(state.board!=null) board=state.board;
            if(first) {
                first=false; // skip first? why? setup?
                // take a look a this skip later!
            } else {
                if(state.board==null) board=state.board=Board.factory.create();
                Move move=Move.fromGTP(state.lastColorGTP,state.lastMoveGTP,state.board.width(),state.board.depth());
                moves.add(move);
            }
            if(state.lastMoveGTP!=null) lastMoveGTP=state.lastMoveGTP;
        }
        Move lastMove=this.lastMove(); // was breaking edge test case at top
        if(!lastMove.equals(Move.nullMove)) if(this.board()!=null) moves.add(this.lastMove());
        //Move ve=Move.fromGTP(veGTP,board.width(),board.depth());
        //moves.add(ve);
        return moves;
    }
    public void makeMoves(List<Move> moves) { for(Move move:moves) move(move); }
    public Point generateRandomMove() {
        Point point=new Point(random.nextInt(board().width()),random.nextInt(board().depth()));
        int i=0,limit=10*board().width()*board().depth();
        for(;board().at(point).equals(
                Stone.vacant);point=new Point(random.nextInt(board().width()),random.nextInt(board().depth())),++i) {
            Move move=new MoveImpl(turn(),point);
            if(move(move)==MoveResult.legal) break;
            else if(i>limit) { Logging.mainLogger.warning("break out "+i+" "+limit); point=null; break; }
        }
        return point;
    }
    public int movesToGenerate() { return board().width()*board().depth()*7/10; }
    public static void generateSillyMoves(Model model,int moves) { // like silly
        Stone turn=Stone.black;
        int n=0;
        for(Move move=model.generateSillyMove(model.turn());move!=null;move=model
                .generateSillyMove(model.turn()),turn=turn.otherColor(),++n) {
            if(n>=moves) break;
            if(n>=model.board().width()*model.board().depth()) break;
            model.move(move);
        }
    }
    public static void generateAndMakeMoves(Model model,int moves) { // like silly (almost)
        int n=0;
        String move=null;
        while(true) {
            move=model.generateAndMakeMove();
            ++n;
            if(n>=moves) { break; }
            if(move==null) { break; }
        }
    }
    public static void generateRandomMoves(Model model,int n) {
        Stone turn=Stone.black;
        for(Point move=model.generateRandomMove();move!=null;move=model.generateRandomMove(),turn=turn.otherColor()) {
            if(model.moves()>=n) break;
            model.move(new MoveImpl(turn,move));
        }
    }
    public Pass passMove() { // make this static?
        return turn().equals(Stone.black)?Move.blackPass:Move.whitePass;
    }
    public static List<Point> generateRandomMovesInList(Model model,int n) {
        Stone turn=Stone.black;
        List<Point> points=new ArrayList<>();
        for(Point point=model.generateRandomMove();point!=null;points
                .add(point),point=model.generateRandomMove(),turn=turn.otherColor()) {
            if(model.moves()>=n) break;
            model.move(new MoveImpl(turn,point));
        }
        return points;
    }
    public Move generateSillyMove(Stone who) {
        // always returns a black move
        int width=board().width();
        int depth=board().depth();
        if(width!=depth) width=depth=Math.min(width,depth);
        int moves=moves();
        Move move=new MoveImpl(who,new Point(moves/width,moves%width));
        return move;
    }
    public Move lastMove() {
        if(board()==null) { System.out.println("board is null, returning null mode."); return Move.nullMove; }
        return Move.fromGTP(lastColorGTP(),lastMoveGTP(),board().width(),board().depth());
    }
    public String lastMoveGTP() { // what about pass?
        return state.lastMoveGTP;
    }
    public Stone lastColorGTP() { return state.lastColorGTP; }
    void sgfMakeMove(Stone stone,Point point) { // only called by tests
        state.sgfMakeMove(stone,point);
    }
    void sgfUnmakeMove(Point at) { // only called by tests
        state.sgfUnmakeMove(at);
    }
    public void setChangedAndNotify(Object object) { setChanged(); notifyObservers(object); }
    void addChildWithOneProperty(MNode child,P p,String string) {
        SgfProperty property=new SgfProperty(p,Arrays.asList(new String[] {string}));
        child.sgfProperties.add(property);
        currentNode().children.add(child);
    }
    public Stone turn() { return state.turn; }
    public MoveResult moveAndPlaySound(Stone color,String GtpCoordinates,int width) {
        // maybe this should use code in the move class?
        // only called in 2 plces in the color bug test case!
        Point point=Coordinates.fromGtpCoordinateSystem(GtpCoordinates,width);
        return moveAndPlaySound(color,point);
    }
    public MoveResult moveAndPlaySound(Stone color,Point point) {
        // maybe this should use code in the move class also?
        // 8/22/22 investigate this
        boolean ok=checkAction(role(),What.move);
        if(ok) {
            Move move=new MoveImpl(color,point);
            MoveResult wasLegal=move(move);
            if(wasLegal==MoveResult.legal) {
                Audio.play(Audio.Sound.stone); // all the same just for now
                if(state.capturedBlocks!=null&&state.capturedBlocks.size()>0
                        ||state.selfCaptured!=null&&!state.selfCaptured.points().isEmpty())
                    Audio.play(Audio.Sound.capture);
            }
            return wasLegal;
        } else {
            // this needs work.
            Logging.mainLogger.warning(color+" "+point+" "+role()+" is bad role!");
            return MoveResult.badRole; // not really illegal, it's a role violation
            // maybe have a move result for a role violation
        }
    }
    public int playOneMove(Move move) {
        Logging.mainLogger.info("play one move: "+move);
        if(move.equals(Move.nullMove)) throw new RuntimeException(move+" 1 "+lastMoveGTP()+" "+lastMove()+" move oops");
        int moves=moves();
        Model.MoveResult ok=move(move);
        // maybe use was legal here?
        switch(ok) {
            case legal:
                break;
            case notYourTurn:
                break;
            default:
                throw new RuntimeException(ok+" !ok play one move move oops "+this);
        }
        if(!move.equals(lastMove())) {
            // let's try to get resign into last move!
            if(true&&move.isResign()) System.out.println(move);
            else {
                Logging.mainLogger.info(move+"!="+lastMoveGTP()+" "+lastMove()+" 3 move oops");
                throw new RuntimeException(move+"!="+lastMoveGTP()+" "+lastMove()+" 5 move oops");
            }
        }
        return moves;
    }
    public int playOneMove(Stone color,String gtpMoveString) {
        Logging.mainLogger.info("play one move from gtp: "+gtpMoveString);
        Move move=Move.fromGTP(color,gtpMoveString,board().width(),board().depth());
        if(move.equals(Move.nullMove)) {
            throw new RuntimeException(move+" "+lastMoveGTP()+" "+lastMove()+" 6 move oops");
        }
        // what is gtp move string for resign?
        return playOneMove(move);
    }
    private void pass() {
        if(currentNode()!=null) {
            MNode child=new MNode(currentNode());
            P p=turn()==Stone.black?P.B:P.W;
            String sgfCoordinates=""; // or tt?
            addChildWithOneProperty(child,p,sgfCoordinates);
            down_(currentNode().children.size()-1);
            // maybe still needs to move
        } else Logging.mainLogger.severe(name+" "+"no current node for pass!");
    }
    private void resign() {
        if(currentNode()!=null) {
            MNode child=new MNode(currentNode());
            Stone stone=turn();
            P p=stone.equals(Stone.black)?P.ZB:P.ZW;
            addChildWithOneProperty(child,p,stone+" Resign");
            Stone other=turn().otherColor();
            Character winner=other.name().toUpperCase().charAt(0);
            SgfProperty property=new SgfProperty(P.RE,Arrays.asList(new String[] {winner+" Resign"}));
            child.sgfProperties.add(property);
            down_(currentNode().children.size()-1);
        } else Logging.mainLogger.severe(name+" "+"no current node for resign!");
    }
    public boolean checkAction(Role role,What action) {
        // does this need a "who is asking" boolean?
        if(role.equals(Role.anything)) return true;
        if(role.equals(Role.observer)&&action.equals(What.navigate)) return true;
        if(action.equals(What.move)&&turn().equals(role.stone)) return true;
        return false;
    }
    public MoveResult move(Move move) {
        if(!checkAction(role(),What.move)) return MoveResult.badRole;
        Logging.mainLogger.info(name+" "+turn()+" move #"+(moves()+1)+" is: "+move);
        MoveResult rc=MoveResult.legal;
        if(move instanceof Pass) //
            pass();
        else if(move instanceof Resign) //
            resign(); // generates RE and Z[BW]
        else /*if(move instanceof MoveImpl)*/ {
            Point point=move.point();
            if(point==null) { Logging.mainLogger.severe("point is null: "+move); throw new RuntimeException(); }
            try {
                MoveResult wasLegal;
                //Point point=Coordinates.fromGtpCoordinateSystem(string,board().depth());
                wasLegal=addMoveNodeAndExecute(move);
                if(wasLegal!=MoveResult.legal)
                    Logging.mainLogger.info(name+" not legal because: "+wasLegal+" "+turn()+" at move #"+moves()
                            +" illegal move: can not move "+move.color()+" at point: "+point+", "+isPoint(point));
                // bad message above fix!
                rc=wasLegal;
            } catch(Exception e) {
                Logging.mainLogger.warning(this+" caught: "+e);
                rc=MoveResult.threw;
            }
        }
        return rc;
    }
    public MoveResult move(Stone color,Point point) { Move move=new MoveImpl(color,point); return move(move); }
    public MoveResult move(Stone color,String GtpCoordinates,int width) {
        Point point=Coordinates.fromGtpCoordinateSystem(GtpCoordinates,width);
        return move(color,point);
    }
    public MoveResult isLegalMove(Move move) { // change to accept
        // string
        // also, maybe check for duplicate code.
        MoveResult wasLegal=addMoveNodeAndExecute(move);
        if(wasLegal==MoveResult.legal) delete();
        return wasLegal;
    }
    public Where isPoint(Point point) { return Where.isPoint(board(),band(),null,point); }
    MoveResult addMoveNodeAndExecute(Move move) {
        // may not be a move node. see haskall version of sgf code.
        if(move instanceof Pass||move instanceof Resign) throw new RuntimeException();
        else {
            Stone color=move.color();
            Point point=move.point();
            if(point==null) { Logging.mainLogger.fine("point is null!"); }
            if(currentNode()!=null) {
                Where where=Where.isPoint(board(),band(),null,point);
                if(where.equals(Model.Where.onVacant)) {
                    if(color.equals(turn())||role.equals(Role.anything)) {
                        MNode child=new MNode(currentNode());
                        P p=color==Stone.black?P.B:P.W;
                        String sgfCoordinates=Coordinates.toSgfCoordinates(point,board().depth());
                        addChildWithOneProperty(child,p,sgfCoordinates);
                        down_(currentNode().children.size()-1); // might be a variation
                        if(!isduplicateHash()) {
                            return MoveResult.legal;
                        } else {
                            Logging.mainLogger.info(name+" "+"duplicate hash!");
                            delete();
                            if(!checkingForLegalMove)
                                setChangedAndNotify(new Event.Hint(Event.illegalMove,"duplicate hash"));
                            return MoveResult.duplicateHash;
                        }
                    } else {
                        Logging.mainLogger.severe(name+" "+MoveResult.notYourTurn);
                        return MoveResult.notYourTurn;
                    }
                } else {
                    Logging.mainLogger.severe(name+" "+where+" "+MoveResult.occupied);
                    return MoveResult.occupied;
                }
            } else Logging.mainLogger.severe(name+" "+"no current node!");
            return MoveResult.unknown;
        }
    }
    public String generateAndMakeMove() { // finds first legal move
        Stone who=turn();
        for(int x=0;x<board().width();x++) {
            for(int y=0;y<board().depth();y++) {
                Point point=new Point(x,y);
                if(board().at(point).equals(Stone.vacant)) {
                    Move move=new MoveImpl(who,point);
                    MoveResult wasLegal=move(move);
                    if(wasLegal==MoveResult.legal) {
                        Logging.mainLogger.info(name+"model made "+who+" move at "+point);
                        return Coordinates.toGtpCoordinateSystem(point,board().width(),board().depth());
                    }
                }
            }
        }
        return null;
    }
    void do_(MNode node) { // set node and execute the sgf
        state.node=node;
        if(node!=null) {//
            for(int i=0;i<node.sgfProperties.size();i++) {
                try {
                    processProperty(node.sgfProperties.get(i));
                } catch(Exception e) {
                    Logging.mainLogger.severe(name+"1 caught: "+e);
                    IO.stackTrace(10);
                    setChangedAndNotify(new Event.Hint(Event.exception,"do"));
                }
            }
        } else Logging.mainLogger.warning(name+" "+"do with null!");
        setChangedAndNotify(new Event.Hint(Event.nodeChanged,"do"));
    }
    void push() { // see if we can eliminate copying the board
        Board copy=board()!=null?board().copy():null;
        State clone=null;
        try {
            clone=state.clone();
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        stack.push(state);
        // maybe synchronize this?
        //maybe set veGTP to null or something?
        // it looks like last move should be set to null or something
        state=clone;
        //state.ve=null; // is this the right thing to do?
        // maybe not. i wonder what it was set to before?
        setBoard(copy);
    }
    void pop() { state=stack.pop(); }
    public void undo() {
        throw new RuntimeException("undo is not implemented");
        // like delete, but on a terminal node
    }
    public void delete() { // like up, but this will delete the node!
        if(Navigate.up.canDo(this)) {
            MNode node=currentNode();
            up();
            if(!currentNode().children.remove(node)) throw new RuntimeException("oops");
            // need to enable the navigation controls
            setChangedAndNotify(new Event.Hint(Event.nodeChanged,"delete"));
        } else Logging.mainLogger.warning(name+" "+"delete - can not do this!");
    }
    public void top() { while(Navigate.up.do_(this)); }
    public void bottom() { while(Navigate.down.do_(this)); }
    public void up() {
        if(Navigate.up.canDo(this)) {
            if(stack.empty()) throw new RuntimeException("stack is empty!");
            pop();
            setChangedAndNotify(new Event.Hint(Event.nodeChanged,"up"));
        } else Logging.mainLogger.warning(name+" "+"up - can not do this!");
    }
    public void down_(Integer indexOfChild) {
        if(Navigate.down.canDoNoCheck(this)) {
            push();
            do_(currentNode().children.get(indexOfChild));
        } else Logging.mainLogger.warning(name+" "+"down - can not do this!");
    }
    public void down(Integer indexOfChild) {
        if(Navigate.down.canDo(this)) {
            push();
            do_(currentNode().children.get(indexOfChild));
        } else Logging.mainLogger.warning(name+" "+"down - can not do this!");
    }
    public void right() { // will become a pop(); do();
        if(Navigate.right.canDo(this)) {
            MNode parent=currentNode().parent;
            List<MNode> children=parent.children;
            int me=children.indexOf(currentNode());
            if(stack.empty()) throw new RuntimeException("stack is empty!");
            pop();
            push();
            do_(children.get(me+1));
        } else Logging.mainLogger.warning(name+" "+"right - can not do this!");
    }
    public void left() {
        if(Navigate.left.canDo(this)) {
            MNode parent=currentNode().parent;
            List<MNode> children=parent.children;
            int me=children.indexOf(currentNode());
            if(stack.empty()) throw new RuntimeException("stack is empty!");
            pop();
            push();
            do_(children.get(me-1));
        } else Logging.mainLogger.warning(name+" "+"left - can not do this!");
    }
    public static void navigate(Model model,Navigate navigate) {
        switch(navigate) {
            // http://senseis.xmp.net/diagrams/33/9a1c19ffc34010cbda05a82dcc5a1788.sgf
            // maybe use the fact that some of the enum_ names are the same.
            case top:
                model.top();
                break;
            case bottom:
                model.bottom();
                break;
            case up:
                model.up();
                break;
            case down:
                model.down(0);
                break;
            case left:
                model.left();
                break;
            case right:
                model.right();
                break;
            case delete:
                model.delete();
                break;
            default:
                throw new RuntimeException("default navigate!");
        }
    }
    private void processProperty(SgfProperty property) {
        // need a way to convert this/these to gtp?
        Logging.mainLogger.config("property: "+property);
        String string=null;
        P p=property.p();
        P2 p2=P2.which(p.id);
        if(p2!=null) {
            switch(p2) {
                // AB[dl][ld] list of stones
                // vs list of points
                // ;FF[4]GM[1]AP[RTGO] SZ[19:17]KM[4.5]RG[ij][jj][kj][ii][ji][ki][ih][jh][kh]
                case AB:
                case AW:
                    for(String s:property.list()) {
                        Point point=Coordinates.fromSgfCoordinates(s,board().depth());
                        board().setAt(point,p2.equals(P2.AB)?Stone.black:Stone.white);
                    }
                    break;
                case AP:
                    state.application=property.list().get(0);
                    if(state.application.startsWith(sgfApplicationName)); // mumble("it's
                    // one of ours");
                    break;
                case FF:
                    state.sgfVersion=property.list().get(0);
                    break;
                case GM:
                    string=property.list().get(0);
                    state.gameType=Integer.valueOf(string);
                    if(state.gameType!=sgfGoGame) Logging.mainLogger.config(name+" "+"not a go game!");
                    break;
                case HA:
                    string=property.list().get(0);
                    state.handicap=Double.valueOf(string);
                    break;
                case KM:
                    string=property.list().get(0);
                    state.komi=Double.valueOf(string);
                    break;
                case BL:
                case WL:
                    // time left
                    break;
                case B:
                case W:
                    Stone color=null;
                    if(p2.equals(P2.B)) color=Stone.black;
                    else if(p2.equals(P2.W)) color=Stone.white;
                    else throw new RuntimeException("oops");
                    string=property.list().get(0);
                    boolean isPass=string.equals("");
                    if(board()==null) { // had to put this back in!
                        Board board=Board.factory.create(Board.standard,Board.standard,Topology.normal);
                        setBoard(board);
                        // maybe we need to do this earlier?
                    }
                    if(board().width()<=Board.standard&&board().depth()<=Board.standard&&string.equals("tt"))
                        isPass=true; // hack for some sgf wierdness/
                    if(isPass) {
                        Logging.mainLogger.config(name+" "+"passing");
                        state.sgfPass();
                    } else {
                        Point point=Coordinates.fromSgfCoordinates(string,board().depth());
                        state.sgfMakeMove(color,point);
                        // if(!checkingForLegalMove) Audio.playStoneSound();
                        // if(areStonesInAtari()) Sound.play("goatari.wav");
                    }
                    break;
                case C:
                    // mumble("comment: "+property.list());
                    String comment=property.list().get(0);
                    if(comment.startsWith(sgfApplicationName)) { // get type and shape
                        if(comment.startsWith(sgfBoardTopology)) {
                            String typeString=comment.substring(sgfBoardTopology.length());
                            Board.Topology type=Board.Topology.valueOf(typeString);
                            Logging.mainLogger.fine(name+" "+"setting board type to "+type);
                            setBoardTopology(type);
                        } else if(comment.startsWith(sgfBoardShape)) {
                            String shapeString=comment.substring(sgfBoardShape.length());
                            Shape shape=Shape.valueOf(shapeString);
                            setBoardShape(shape);
                        } else Logging.mainLogger.warning(name+" "+"what is "+comment);
                    }
                    break;
                case RG:
                    if(state.application.equals(sgfApplicationName)) Logging.mainLogger
                            .warning(state.board.topology()+", "+state.shape+", region: "+property.list());
                    // above we have diamond, hole1, region [jj]
                    // using state.board above does not seem quite right.
                    // yes, diamond needs another region or a bigger region.
                    // but diamond is a topology, maybe it should be a shape?
                    // if it's a shape, we must have a normal board.
                    // what about other shapes, do these require a normal board?
                    //
                    //if(!shape.equals(Shape.normal))
                    // let's let any board have regions
                    // maybe restrict diamond to normal for now and add some holes later?
                    for(String s:property.list()) {
                        Logging.mainLogger.config(name+" "+"hole at: "+s);
                        Point point=Coordinates.fromSgfCoordinates(s,board().depth());
                        board().setAt(point,Stone.edge);
                    }
                    break;
                case SZ: // create the board
                    // maybe delay the board creation?
                    // we need to convert this to gtp. how?
                    // what others do we need to convert?
                    string=property.list().get(0);
                    int width=(int)Parameters.width.currentValue();
                    int depth=(int)Parameters.depth.currentValue();
                    if(string.contains(":")) {
                        String[] tokens=string.split(":");
                        width=Integer.valueOf(tokens[0]);
                        depth=Integer.valueOf(tokens[1]);
                        if(width!=depth) Logging.mainLogger.config(name+" "+width+"!="+depth+"!");
                        Logging.mainLogger.fine(name+" "+"size: "+string);
                    } else width=depth=Integer.valueOf(string);
                    state.widthFromSgf=width;
                    state.depthFromSgf=depth;
                    int w=width;
                    int d=depth;
                    // check shape and adjust w and d!
                    // 7/15/21 we may not know the shape. we may have regions.
                    Board board=Board.factory.create(w,d,state.topology,state.shape);
                    // get state from controls?
                    Logging.mainLogger.fine(name+" "+"creating board");
                    setBoard(board);
                    // notify(Event.start,"SZ "+property);
                    // maybe we should do the notify above?
                    break;
                case RE:
                    String results=property.list().get(0);
                    Logging.mainLogger.severe(name+" "+"result: "+results);
                    //state.sgfResign(); // very bad
                    // why, seems like the right thing to do?
                    // maybe, but ogs has this up front before any moves
                    // lets keep this off and use ZBC and ZW for resign
                    break;
                case RT: // private property - my root
                    break;
                case ZB: // private property - black resign
                case ZW: // private property - white resign
                    color=null;
                    if(p2.equals(P2.ZB)) color=Stone.black;
                    else if(p2.equals(P2.ZW)) color=Stone.white;
                    else throw new RuntimeException("oops");
                    Logging.mainLogger.info(name+" "+"resigns");
                    state.sgfResign();
                    break;
                default:
                    Logging.mainLogger.config(name+" "+p2+" is not implemented!");
                    break;
            }
        } else Logging.mainLogger.warning(name+" "+"p2 is null!");
    }
    @Override public String toString() {
        String s="";
        if(hasABoard()) { s=board().toString(); }
        s+="current node: "+currentNode().toString();
        s+=" "+currentNode().children;
        return s;
    }
    public boolean goToMNode(MNode target) {
        // looks like some of this old code
        // never worked with variations at top level?
        // so maybe never say the top level mnode that we now have?
        // 8/22/22
        // maybe move this to MNode? yes, but:
        // this uses navigate. since we have a parent instance variable,
        // we should be able to do this without using navigate.
        // 12/19/22 seems to work with command line
        // but command line view seems broken?
        // 1/7/23
        // seems t work, but the test are real slow.
        List<MNode> list=root().lca(currentNode(),target);
        if(list!=null) {
            MNode ancester=list.get(0);
            while(currentNode()!=ancester) {
                if(!Navigate.up.canDo(this)) { System.out.println("can't do up!"); }
                if(Navigate.up.canDo(this)) up();
                if(!Navigate.up.canDo(this)) { return false; }
            }
            // equals here is ==.
            // maybe i need a real equals bases on id?
            // or ?
            if(!currentNode().equals(target)) for(int i=1;i<list.size();i++) {
                ancester=list.get(i);
                int index=currentNode().children.indexOf(ancester);
                if(index>=0) down(index);
                else {
                    Logging.mainLogger.warning(name+" "+"at "+currentNode()+", can not find ancester "+ancester
                            +" in children: "+currentNode().children);
                    return false;
                }
            }
            else Logging.mainLogger.fine("at target, no need to go down");
            if(currentNode().equals(target)) return true;
            else {
                Logging.mainLogger.severe(name+" "+"failed, ended up at "+currentNode());
                return false;
            }
        } else {
            System.out.println("list is null");
            Logging.mainLogger.warning(name+" "+"list is null!");
            return false;
        }
    }
    public static String getExtension(File file) {
        String extension=null;
        String name=file.getName();
        int i=name.lastIndexOf('.');
        if(i>0) if(i==name.length()-1) extension="";
        else extension=name.substring(i+1).toLowerCase();
        return extension;
    }
    public void resetOffset() { xOffset=yOffset=0; }
    public void offset(int x,int y) {
        xOffset+=x;
        yOffset+=y;
        Logging.mainLogger.fine(name+" "+"offset "+xOffset+" "+yOffset);
    }
    public int offsetX(int x) { if(board()==null) return x; return x+xOffset; }
    public int offsetInverseX(int x) { if(board()==null) return x; return x-xOffset; }
    public int offsetY(int y) { if(board()==null) return y; return y+yOffset; }
    public int offsetInverseY(int y) { if(board()==null) return y; return y-yOffset; }
    public int prisoners(Stone color) {
        switch(color) {
            case black:
                return state.blackPrisoners;
            case white:
                return state.whitePrisoners;
            default:
                return 0;
        }
    }
    boolean isduplicateHash() {
        for(Iterator<State> i=stack.iterator();i.hasNext();) {
            State state=i.next();
            if(state!=this.state&&state.hash!=0) if(this.state.hash==state.hash) return true;
        }
        return false;
    }
    public void setRole(Role role) {
        this.role=role;
        setChangedAndNotify(role);
        // this is where we need to set stuff or ?
        // maybe check this when he tries to move.
    }
    public Role role() { return role; }
    public State state() { return state; }
    public boolean inAtari() { return state.inAtari; }
    // waits
    // 1) here: waitForMoveCompleteOnBoard
    //          wait for move # to increase by 1 from old.
    //          uses isWaitingForMoveCompleteOnBoard
    // 2) waitUNtilItIsTimetoMove
    public boolean waitForMoveCompleteOnBoard(int moves) {
        // synchronize this?
        isWaitingForMoveCompleteOnBoard=true;
        boolean once=false;
        int actual=moves();
        modelEt.reset();
        Et etWait=new Et();
        boolean twice=false;
        if(!(moves<=actual&&actual<=moves+1)) throw new RuntimeException("oops "+actual+" "+moves);
        else while(moves()!=moves+1) {
            if(!once) {
                Logging.mainLogger.info(name+" is waiting for move "+(moves+1)+" to complete on board.");
                once=true;
                modelEt.reset();
            }
            if(!twice&&etWait.etms()>100) {
                twice=true;
                //NamedThreadGroup.namedThreadGroup(gameId);
            }
            Thread.yield();
            // how to find thread and interrupt?
            Thread current=Thread.currentThread();
            if(current.isInterrupted()) {
                System.out.println(current);
                if(current instanceof Stopable&&((Stopable)current).isStopping()) //
                    Logging.mainLogger.info("stopping wait for move complete interrupted!");
                else {
                    throw new RuntimeException("not stopping wait for move complete interrupted!");
                }
            }
        }
        isWaitingForMoveCompleteOnBoard=false;
        histogram.add(modelEt.etms());
        Logging.mainLogger.info(name+" "+modelEt+" end wait for move "+moves()+" to "+"omplete on board.");
        return moves()!=moves+1;
    }
    static void strangeGame(Model model) {
        int n=model.movesToGenerate();
        Model.generateAndMakeMoves(model,n);
        List<Move> generatedMoves=model.movesToCurrentState();
        Move pass=model.turn().equals(Stone.black)?Move.blackPass:Move.whitePass;
        model.move(pass);
        pass=model.turn().equals(Stone.black)?Move.blackPass:Move.whitePass;
        model.move(pass);
        // what am i testing here?
    }
    static void randomeMovesUsingParameters(Model model) {
        int width=(int)Parameters.width.currentValue();
        int depth=(int)Parameters.depth.currentValue();
        Topology topology=(Topology)Parameters.topology.currentValue();
        model.setBoard(Board.factory.create(width,depth,topology));
        int n=model.movesToGenerate();
        Logging.mainLogger.config(model.name+" "+model.toString());
        Model.generateRandomMoves(model,n);
    }
    public static Collection<SgfNode> mainLineFromCurrentPosition(Model model) {
        // maybe make this for an mnode?
        if(model.root()==null) {
            Logging.mainLogger.warning("nodel.root() returns null!");
            return Collections.emptySet();
        }
        MNode r=model.root();
        SgfNode root=r.toBinaryTree();
        Collection<SgfNode> path=new ArrayList<>();
        if(root==null) { Logging.mainLogger.warning("nodel.root() returns null!"); return Collections.emptySet(); }
        try {
            while(Navigate.down.canDo(model)) { Navigate.down.do_(model); }
            path=MNode.findPathToNode(model.currentNode(),root);
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("main line from ... caught: "+e);
            parserLogger.severe("caught: "+e);
        } catch(Exception e) {
            System.out.println("main line from ... caught: "+e);
            parserLogger.severe("caught: "+e);
        }
        // we should be able to get path from state stack in model!
        return path;
    }
    private static void lookAtRoot() {
        Model model=new Model();
        model.move(new MoveImpl(Stone.black,new Point()));
        System.out.println("root: "+model.root());
        System.out.println("current node: "+model.currentNode());
        System.out.println("children: "+model.currentNode().children);
        model.save(new OutputStreamWriter(System.out));
        System.out.println();
        //if(true) return;
        System.out.println("|||");
        model.save(new OutputStreamWriter(System.out));
        System.out.println("after save.");
        System.out.flush();
        System.out.println(model);
    }
    public static MNode modelRoundTrip(Reader reader,Writer writer) {
        StringBuffer stringBuffer=new StringBuffer();
        Utilities.fromReader(stringBuffer,reader);
        String expectedSgf=stringBuffer.toString(); // so we can compare
        StringWriter stringWriter=new StringWriter();
        SgfNode games=restoreSgf(new StringReader(expectedSgf));
        if(games==null) return null; // return empty node!
        if(games!=null) if(games.right!=null) System.out.println(" 2 more than one game!");
        // maybe return empty node if sgf is ""?
        //games.saveSgf(stringWriter,noIndent); // writes sgf from binary tree.
        MNode mNodes0=MNode.toGeneralTree(games);
        Model model=new Model();
        model.setRoot(mNodes0);
        MNode mNodes=model.root();
        if(mNodes!=null) {
            if(mNodes.children.size()>1); //System.out.println("more than one child: "+mNodes.children);
            SgfNode sgfRoot=mNodes.toBinaryTree();
            SgfNode actual=sgfRoot.left;
            StringWriter hack=new StringWriter();
            actual.saveSgf(hack,noIndent);
            try {
                writer.write(hack.toString());
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return mNodes;
    }
    public static MNode modelRoundTrip2(String expectedSgf,Writer writer) {
        // move to model package?
        // only used in one test method.
        SgfNode games=restoreSgf(new StringReader(expectedSgf));
        if(games==null) return null;
        if(games!=null) if(games.right!=null) System.out.println(" 2 more than one game!");
        games.saveSgf(new StringWriter(),noIndent);
        MNode mNodes0=MNode.toGeneralTree(games);
        Model model=new Model();
        model.setRoot(mNodes0);
        MNode mNodes=model.root();
        String actualSgf=null;
        if(games!=null) {
            SgfNode sgfRoot=mNodes.toBinaryTree();
            SgfNode actual=sgfRoot.left;
            StringWriter stringWriter=new StringWriter();
            actual.saveSgf(stringWriter,noIndent);
            actualSgf=stringWriter.toString();
        }
        if(actualSgf!=null) try {
            writer.write(actualSgf);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return mNodes;
    }
    public static boolean disconnectFromServer(Model model) {
        if(model.gtp!=null) {
            model.gtp.stop();
            model.gtp=null;
            model.setRole(Role.anything);
            return true;
        } else Logging.mainLogger.severe(model.name+" "+"disconnect when not connected!");
        return false;
    }
    public static boolean connectToServer(Model model) {
        if(model.gtp==null) {
            Socket socket=new Socket();
            boolean ok=connect(IO.host,IO.defaultPort,1000,socket);
            if(ok) {
                End socketEnd=new End(socket);
                model.gtp=new GTPBackEnd(socketEnd,model);
                NamedThread thread=model.gtp.startGTP(0);
                if(thread==null) {
                    Logging.mainLogger.severe("3 startGTP returns null!");
                    throw new RuntimeException("3 can not run backend!");
                }
                // add some more constants?
                // we did under roles.
                return true;
            } else Logging.mainLogger.warning(model.name+" "+"connection failed!");
        } else Logging.mainLogger.warning(model.name+" "+"connection failed!");
        return false;
    }
    public static class State implements Cloneable { // needs an equals or isEqual method.
        private State() {}
        @Override public State clone() throws CloneNotSupportedException { return (State)super.clone(); }
        static void removeCapturedStones(Board board,List<Block> blocks) {
            for(Block block:blocks) {
                Logging.mainLogger.fine(""+" "+"removing "+block);
                for(Point point:block.points()) board.setAt(point,Stone.vacant);
            }
        }
        private void adjustPrisonerCount(Block block,int sign) {
            switch(block.color()) {
                case black:
                    blackPrisoners+=sign*block.points().size();
                    break;
                case white:
                    whitePrisoners+=sign*block.points().size();
                    break;
                default:
                    throw new RuntimeException("oops");
            }
        }
        private void adjustPrisonerCounts(List<Block> captured,int sign) {
            if(captured!=null) for(Block block:captured) adjustPrisonerCount(block,sign);
        }
        private void adjustPrisonerCountsForRestoredCapturedStones(List<Block> captured) {
            adjustPrisonerCounts(captured,-1);
        }
        private void adjustPrisonerCountsForRemovedCapturedStones(List<Block> captured) {
            adjustPrisonerCounts(captured,1);
        }
        private void addStoneToBoard(Stone stone,Point point) { board.setAt(point.x,point.y,stone); }
        private void toggleTurn() { turn=turn.otherColor(); }
        private void sgfResign() { // make sure this is correct
            Move move=turn==Stone.black?Move.blackResign:Move.whiteResign;
            lastMoveGTP=move.toGTPCoordinates(board.width(),board.depth());
            lastColorGTP=move.color();
            moves++;
            toggleTurn();
        }
        // problem with last move - it needs to be in sgf/gtp format?
        private void sgfPass() {
            Move move=turn==Stone.black?Move.blackPass:Move.whitePass;
            lastMoveGTP=move.toGTPCoordinates(board.width(),board.depth());
            lastColorGTP=move.color();
            moves++;
            toggleTurn();
        }
        private void sgfUnpass() { moves--; toggleTurn(); } // ??
        private void sgfMakeMove(Stone stone,Point point) {
            if(board==null) {
                Logging.mainLogger.severe(""+" "+"board is null!");
                throw new RuntimeException("board is null!");
            }
            // check for pass?
            if(stone.equals(Stone.vacant)) throw new RuntimeException("bad move");
            addStoneToBoard(stone,point);
            lastMoveGTP=Coordinates.toGtpCoordinateSystem(point,board.width(),board.depth());
            lastColorGTP=stone;
            // find opponents blocks on adjacent intersections
            // need to find them all so we can check for in atari
            capturedBlocks=Block.findAdjacentCapturedOpponentsBlocks(board,point,stone);
            removeCapturedStones(board,capturedBlocks);
            if(capturedBlocks.size()>0); // Audio.playCaptureSound();
            Block fromThisMove=Block.find(board,point);
            if(fromThisMove.liberties()==0) {
                Logging.mainLogger.fine(""+" "+"self capture");
                Logging.mainLogger.fine(""+" "+"removing "+fromThisMove);
                for(Point point2:fromThisMove.points()) board.setAt(point2.x,point2.y,Stone.vacant);
                selfCaptured=fromThisMove;
            }
            if(capturedBlocks!=null) adjustPrisonerCountsForRemovedCapturedStones(capturedBlocks);
            if(selfCaptured!=null) {
                List<Block> selfCapturedBlocks=new ArrayList<>(1);
                selfCapturedBlocks.add(selfCaptured);
                adjustPrisonerCountsForRemovedCapturedStones(selfCapturedBlocks);
            }
            List<Block> blocksInAtari=Block.findAdjacentOpponentsBlocksInAtari(board,point,stone);
            inAtari=blocksInAtari!=null&&blocksInAtari.size()>0;
            if(inAtari) Logging.mainLogger.config("in atari: "+blocksInAtari);
            hash=hash(board);
            aSingleStoneWasCaptured=capturedBlocks!=null&&capturedBlocks.size()==1
                    &&capturedBlocks.get(0).points().size()==1;
            moves++;
            toggleTurn();
        }
        private void sgfUnmakeMove(Point at) {
            if(board==null) { Logging.mainLogger.warning(""+" "+"board is null in unmake!"); return; }
            if(capturedBlocks!=null) for(Block block:capturedBlocks) {
                Stone who=block.color();
                for(Point point:block.points()) addStoneToBoard(who,point);
            }
            if(selfCaptured!=null) {
                Stone who=selfCaptured.color();
                for(Point point:selfCaptured.points()) addStoneToBoard(who,point);
            }
            board.setAt(at,Stone.vacant);
            adjustPrisonerCountsForRestoredCapturedStones(capturedBlocks);
            if(selfCaptured!=null) {
                List<Block> selfCapturedList=new ArrayList<>(1);
                selfCapturedList.add(selfCaptured);
                adjustPrisonerCountsForRestoredCapturedStones(selfCapturedList);
            }
            moves--;
            toggleTurn();
        }
        long hash(Board board) {
            long hash=0;
            for(int x=0;x<board.width();x++) for(int y=0;y<board.depth();y++) switch(board.at(x,y)) {
                case black:
                    hash^=randomBlack[x][y];
                    break;
                case white:
                    hash^=randomWhite[x][y];
                    break;
                default:
                    break;
            }
            return hash;
        }
        private MNode root,node;
        // 2021
        // the root is a node, so this allows for an entire tree of variations
        // do we ever need variations?
        private Board board;
        private Stone turn=Stone.black;
        private boolean inAtari;
        private int blackPrisoners,whitePrisoners;
        boolean aSingleStoneWasCaptured;
        private volatile int moves;
        private String lastMoveGTP; // this is causing problems in a few places
        private Stone lastColorGTP;
        // shouldn't be doing that anymore.
        // it is null before the first move?
        //private Move ve; // 11/16/21
        // maybe change this to use Move class?
        // now it want's a color :(
        private List<Block> capturedBlocks;
        private Block selfCaptured;
        private long hash;
        // should the stuff below be pushed up?
        // does it make any sense to change these in game?
        private boolean isFromLittleGolem;
        private Topology topology=Topology.normal;
        private Shape shape=Shape.normal;
        // the above looks problematic also.
        private int band=(Integer)Parameters.band.currentValue();
        private Double komi=.5,handicap=0.;
        private Integer gameType=sgfGoGame;
        private String application;
        private String sgfVersion=defaultSgfVersion;
        public Integer widthFromSgf=Board.standard;
        public Integer depthFromSgf=Board.standard;
    }
    public static void main(String[] args) {
        Model model=new Model();
        System.out.println(model.root());
        System.out.println(model.root().parent);
        System.out.println(model.currentNode());
        model.setRoot(5,5);
        strangeGame(model);
        model.setRoot(5,5);
        int n=model.movesToGenerate();
        Model.generateRandomMoves(model,n);
        //randomeMovesUsingParameters(model);
        System.out.println(model);
        Command.doTGOSend("manyFacesTwoMovesAtA1AndR16");
        System.out.println(model);
    }
    public int verbosity;
    // move stuff like type, shape, and band to parameter
    // or at least put the current values in parameter
    // and set these final in state.
    // 7/15/21 type, shape, and band are in parameters.
    // we need type and shape here to construct the board when we see an SZ,
    // or we can get the current values from the parameters. we are doing this now.
    // 11/7/22 maybe getting topologu and shape from parametres is a bad idea.
    private int xOffset,yOffset; // maybe should be in state as it is only used
    // by the view?
    // it does belong to the view, but where else can it go
    public GTPBackEnd gtp; // moved from mediator for command line.
    private State state=new State();
    private Stack<State> stack=new Stack<>();
    public final String name;
    private boolean isWaitingForMoveCompleteOnBoard; // investigate!
    private transient boolean checkingForLegalMove;
    public boolean addNewRoot=false; // probably was a bas idea
    // sometimes, but maybe not always
    public boolean allowMultipleGames=true; // bad name fix later
    transient Role role=Role.anything;
    public static final String desiredExtension="sgf";
    public static final String sgfApplicationName="RTGO";
    public static final String sgfBoardTopology=sgfApplicationName+" topology is ";
    public static final String sgfBoardShape=sgfApplicationName+" shape is ";
    public static final String defaultSgfVersion="1";
    public static final String version="0.1"; // internal i guess
    public static final int sgfGoGame=1;
    public static final int LargestBoardSize=25;
    public static final double epsilon=Math.sqrt(2)*.49;
    public static final String black="black",white="white"; // use black.name()?
    static final long[][] randomBlack=new long[LargestBoardSize][LargestBoardSize];
    static final long[][] randomWhite=new long[LargestBoardSize][LargestBoardSize];
    Random random=new Random();
    static { // https://chessprogramming.wikispaces.com/Transposition+Table#KeyCollisions
        Random random=new Random();
        for(int i=0;i<LargestBoardSize;i++) for(int j=0;j<LargestBoardSize;j++) {
            randomBlack[i][j]=random.nextLong();
            randomWhite[i][j]=random.nextLong();
        }
    }
    // what was: 76.171.113.153 as host?
    Et modelEt=new Et();
    public Histogram histogram=new Histogram(10,0,10);
    public transient long gameId; // temporary
    public final long id=++ids;
    public boolean stopWaiting=false;
    static long ids;
}
