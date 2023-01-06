package server;
import static io.Logging.serverLogger;
import java.io.*;
import java.net.*;
import java.util.*;
import controller.*;
import io.*;
import io.IO.*;
import io.IO.End.Holder;
import model.Model;
import server.NamedThreadGroup.NamedThread;
public class GoServer implements Runnable,Stopable {
    private GoServer(ServerSocket serverSocket) {
        //deleteServerSGFFiles(); // call from tests instead
        // or put test games into a different folder.
        this.serverSocket=serverSocket;
        serverLogger.info("server is on: "+serverSocket);
    }
    int connections() { return connections.size(); }
    @Override public boolean isStopping() { return isStopping; }
    @Override public boolean setIsStopping() { boolean rc=isStopping; isStopping=true; return rc; }
    GameFixture setupRemoteGame(Model recorder) { // recorder needs to force all of these models to be the same.
        // yes. that will be done by some kind of shove (tgo send sgf).
        // between the next two socket connections - hack
        End blackFrontEnd,whiteFrontEnd;
        synchronized(connections) {
            blackFrontEnd=connections.remove();
            whiteFrontEnd=connections.remove();
        }
        Holder blackHolder=Holder.frontEnd(blackFrontEnd);
        Holder whiteHolder=Holder.frontEnd(whiteFrontEnd);
        GameFixture game=new GameFixture(recorder);
        synchronized(games) {
            games.add(game);
        }
        game.setupServerSide(blackHolder.front,whiteHolder.front);
        System.out.println("after setup server side.");
        // we may always want everything except the board size?
        return game;
    }
    // now send a what command
    // and:
    // 1. add to observers
    // 2. add to black players
    // 3. add to white players
    // 4. start new thread for teaching game
    //
    // september 2021
    // looks like it will play a game if you start the server
    // and start two guis and connect them.
    // looks like we need a lot of work
    //
    // https://www.gnu.org/software/gnugo/gnugo_19.html says:
    // GTP is an asymmetric protocol involving two parties which we call controller and engine.
    // The controller sends all commands and the engine only responds to these commands.
    // so looks like the we may need a separate connection to drive things
    // maybe mark responses? but no one is always reading
    // maybe we can add a command that says: is there anything you want me to do peripdically?
    // maybe send this every 5 seconds? erk!
    // this sounds like a hack
    // so maybe 2 acceptors:
    //      1. model connects and sends commands like list games,
    //      2. maybe we can just pass the socket to a process that?q
    public void addConnection(End end) {
        Logging.mainLogger.info("added connection: "+end);
        synchronized(connections) {
            connections.add(end);
        }
    }
    @Override public void run() {
        final int port=serverSocket!=null?serverSocket.getLocalPort():IO.noPort;
        serverLogger.info("go server is running on port: "+port);
        boolean once=false;
        while(!isStopping&&!namedThread.isInterrupted()) {
            if(port==IO.noPort) {
                if(!once) {
                    //System.out.println("wait for connection)");
                    once=true;
                }
                synchronized(connections) {
                    if(connections.size()>=2) {
                        //System.out.println(connections()+" connections.");
                        Model recorder=new Model("recorder");
                        GameFixture game=setupRemoteGame(recorder);
                        game.startGameThread(); // last chance.
                    }
                }
            } else {
                try {
                    if(!once) {
                        //System.out.println("block on accept)");
                        once=true;
                    }
                    Socket socket=serverSocket.accept();
                    Logging.mainLogger.info(socket+" "+serverSocket);
                    synchronized(connections) {
                        serverLogger.info("connection from: "+socket);
                        addConnection(new End(socket));
                        if(connections.size()>=2) {
                            Model recorder=new Model("recorder");
                            GameFixture game=setupRemoteGame(recorder);
                            game.startGameThread();
                        }
                    }
                } catch(IOException e) {
                    if(isStopping()) serverLogger.info(this+" stopping caught: "+e);
                    else serverLogger.severe(this+" not stopping caught: "+e);
                    break;
                }
            }
            GTPBackEnd.sleep2(2);
        }
        serverLogger.info("server is exiting");
    }
    public static GoServer startServer(int port) throws IOException {
        // fix this later. there will be fewer choices.
        GoServer goServer=null;
        if(port==IO.noPort) {
            goServer=new GoServer(null);
            (goServer.namedThread=NamedThreadGroup.createNamedThread(goServer.id,goServer,"server")).start();
        } else if(port==IO.anyPort) {
            ServerSocket serverSocket=IO.getServerSocket(0);
            // tricklike. make sure that we are connecting to this server.
            if(serverSocket!=null) {
                goServer=new GoServer(serverSocket);
                (goServer.namedThread=NamedThreadGroup.createNamedThread(goServer.id,goServer,"server")).start();
            }
        } else goServer=startServer(true,port);
        return goServer;
    }
    private static GoServer startServer(boolean useSocketForServer,int port) throws IOException {
        GoServer goServer=null;
        for(int i=0;i<maxTries;i++) {
            ServerSocket serverSocket=IO.getServerSocket(port+i);
            if(serverSocket!=null) {
                goServer=new GoServer(serverSocket);
                // bad id!!!
                // we ant the named thread groups to be different
                // using server may conflict with game id!
                (goServer.namedThread=NamedThreadGroup.createNamedThread(goServer.id,goServer,"server")).start();
                break;
            }
        }
        return goServer;
    }
    private void stopServer() throws IOException,InterruptedException {
        isStopping=true;
        if(serverSocket!=null) { if(!serverSocket.isClosed()) serverSocket.close(); }
        IO.myClose(null,null,null,namedThread,"server",this);
    }
    public void stopEverything() throws IOException,InterruptedException {
        serverLogger.info("stopping go server.");
        for(GameFixture game:games) game.stop();
        stopServer();
    }
    @Override public void stop() throws IOException,InterruptedException { stopEverything(); }
    public static void stop(GoServer goServer,GameFixture game) throws IOException,InterruptedException {
        if(game!=null) game.stop(); // should be called by go server - maybe not if it's null?
        if(goServer!=null) goServer.stopEverything(); // try moving this around later
        // too many stops, consolidate?
        try {
            if(game!=null&&false) NamedThreadGroup.stopAllStopables(game.id);
        } catch(InterruptedException e) {
            Logging.mainLogger.info("stop all stopables was interrupted.");
            e.printStackTrace();
        }
    }
    boolean anyInterruptions() { return Thread.currentThread().isInterrupted()||namedThread.isInterrupted(); }
    GameFixture waitForAGame() {
        //System.out.println("waiting for a game at: "+first.et);
        GameFixture game=null;
        while(games.size()<1&&!anyInterruptions()) Thread.yield();
        if(anyInterruptions()) {
            Logging.mainLogger.severe("wait for game ... thread is interrupted: "+Thread.currentThread()+" in "+this);
            return null;
        }
        synchronized(games) {
            game=games.iterator().next(); // remove from server
            // not really removed , this game is still in the list.
        }
        //System.out.println("got a game at: "+first.et);
        return game;
    }
    GameFixture connectAndSetupGame(int port) {
        Holder blackHolder=Holder.create(port);
        Holder whiteHolder=Holder.create(port);
        if(port==IO.noPort) addConnection(blackHolder.front); // dfuplex
        if(port==IO.noPort) addConnection(whiteHolder.front); // dfuplex
        if(connections.size()<2) System.out.println("1 waiting for a game");
        // let server eat the connections and create a game.
        GameFixture game=waitForAGame();
        game.printStatus();
        System.out.println("1 end of waiting for a game");
        game.blackFixture.setupBackEnd(blackHolder.back,game.blackName(),game.id);
        game.whiteFixture.setupBackEnd(whiteHolder.back,game.whiteName(),game.id);
        game.startPlayerBackends(); // server does not know about these.
        /*
        if(game.namedThread==null) {
            System.out.println("game was not started!");
            game.startGameThread();
        } else System.out.println("game was already started!");
        throw new RuntimeException("game was already started!");
         */
        // why is the above not throwing?
        return game;
    }
    public static void serverDtrt(int port) throws Exception {
        GoServer goServer=GoServer.startServer(port);
        // this will always connect to the server socket's local port
        final int serverPort=goServer.serverSocket!=null?goServer.serverSocket.getLocalPort():IO.noPort;
        GameFixture game=goServer.connectAndSetupGame(serverPort);
        Model blackModel=game.blackFixture.backEnd.model;
        // use recorder fixture instead of black's?
        int width=blackModel.board().width(),depth=blackModel.board().depth();
        int m=width*depth-1;
        m=1;
        GameFixture.playSillyGame(game,m); // simulate remote clients
        Logging.mainLogger.info("wrapup game.");
        game.stop();
        goServer.stopEverything();
    }
    private static boolean serverIsRunning(int port) {
        // write a test for this
        boolean serverIsRunning=false;
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            serverSocket.close();
        } catch(IOException e) {
            serverIsRunning=true;
        }
        return serverIsRunning;
    }
    public static void main(String[] arguments) throws Exception,InterruptedException {
        System.out.println(Init.first);
        System.out.println("Level: "+Logging.mainLogger.getLevel());
        int n=arguments==null?0:arguments.length;
        if(false) {
            try {
                File dir=new File(GameFixture.directory);
                if(!dir.exists()) {
                    dir.mkdir();
                    if(!dir.exists()) throw new RuntimeException("can not create folder: "+dir);
                }
                GoServer goServer=GoServer.startServer(IO.defaultPort);
            } catch(IOException e) {
                e.printStackTrace();
            }
            //gui.Main.main(new String[] {});
            return;
        }
        int m=1;
        if(arguments!=null&&arguments.length>0) m=1000000;
        for(int i=0;i<m;++i) {
            Logging.mainLogger.warning("sample game: "+i);
            // make this into a test!
            for(int port:IO.ports) serverDtrt(port);
            //serverDtrt(IO.noPort);
            //serverDtrt(IO.anyPort);
            //serverDtrt(IO.defaultPort);
        }
        NamedThreadGroup.printThraedsAtEnd();
        Logging.mainLogger.info("exit main");
    }
    public final ServerSocket serverSocket;
    transient NamedThread namedThread;
    long id;
    transient boolean isStopping;
    private Queue<End> connections=new LinkedList<>();
    // idea, maybe have a queue of holders?
    public Set<GameFixture> games=new LinkedHashSet<>(); // how to remove these when they
    // end?
    static final int maxTries=10;
}
