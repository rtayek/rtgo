package io;
import io.Logging;
import static io.Logging.*;
import static server.NamedThreadGroup.name;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;
import com.tayek.util.concurrent.Stopable;
import com.tayek.util.io.Indent;
import controller.GTPBackEnd;
import server.NamedThreadGroup.NamedThread;
// move more io methods here
// maybe move pipe and duplex classes here
// these are or will be moved to util project.
public class IOs {
    private static class Idea {
        volatile transient boolean newDone=false;
        void blockingReadLoopIdea(Consumer<String> consumer) {
            try {
                loop:while(!newDone) {
                    while(!in.ready()) {
                        if(thread.isInterrupted()) break loop;
                        if(newDone) break loop;
                        GTPBackEnd.sleep2(1);
                    }
                    String string=in.readLine();
                    if(string==null) break loop;
                    consumer.accept(string);
                }
            } catch(Exception e) {}
        }
        void callBlockingReadLoop() {
            Consumer<String> consumer=(x)->Logging.mainLogger.info(String.valueOf(x));
            blockingReadLoopIdea(consumer);
        }
        BufferedReader in;
        Thread thread;
    }
    public static String toString(Thread thread) {
        if(thread!=null) if(thread.getName().contains(timeLimitedThreadName)) {
            Logging.mainLogger.info(String.valueOf(thread.getName()));
            mainLogger.severe(thread.getName()+" time limited");
        }
        return thread==null?null
                :"thread: name: "+thread.getName()+", state: "+thread.getState()+", is alive: "+thread.isAlive()
                +", is interrupted:  "+thread.isInterrupted();
    }
    public static ServerSocket getServerSocket(int port) {
        ServerSocket serverSocket=null;
        try {
            serverSocket=new ServerSocket(port);
            serverLogger.info("server socket: "+serverSocket);
        } catch(IOException e) {
            serverLogger.severe("creat server faled for port "+port);
        }
        return serverSocket;
    }
    private static void stopThread_(Thread thread) {
        if(thread.isAlive()) {
            if(!thread.isInterrupted()) {
                mainLogger.info("interrupting: "+toString(thread));
                thread.interrupt();
                GTPBackEnd.sleep2(GTPBackEnd.yield);
            } else Logging.mainLogger.info(thread.getName()+" was already interrupted");
            GTPBackEnd.sleep2(GTPBackEnd.yield);
            if(thread.isInterrupted()) {
                if(currentThreadIsTimeLimited())
                    Logging.mainLogger.info("time limited thread not joining with: "+thread);
                else {
                    GTPBackEnd.sleep2(GTPBackEnd.yield);
                    try {
                        Logging.mainLogger.info("joining with: "+thread);
                        thread.join(10);
                        Logging.mainLogger.info("after join: "+toString(thread));
                        switch(thread.getState()) {
                            case TERMINATED:
                                Logging.mainLogger.info("joined with: "+thread);
                                break;
                            default:
                                Logging.mainLogger.severe("join failed with: "+thread+" "+toString(thread));
                        }
                    } catch(InterruptedException e) {
                        Logging.mainLogger.severe("join interrupted! "+thread);
                        Logging.mainLogger.severe(toString(thread));
                    }
                }
            } else Logging.mainLogger.severe(thread.getName()+" 1 was not interrupted: "+toString(thread));
        } else Logging.mainLogger.info("thread is not alive: "+toString(thread));
    }
    public static void stopThread(Thread thread,Stopable stopable) {
        if(thread!=null) if(!thread.equals(Thread.currentThread())) {
            Thread.State state=thread.getState();
            if(state.equals(Thread.State.TERMINATED)) return;
            switch(state) {
                case TERMINATED:
                    Logging.mainLogger.severe("thread is already terminated: "+thread.getName());
                    break;
                case BLOCKED: // break;
                case NEW: // break;
                case RUNNABLE: // break;
                case TIMED_WAITING:// break;
                case WAITING: // break;
                    stopThread_(thread);
            }
            Thread.State endState=thread.getState();
            if(!endState.equals(Thread.State.TERMINATED))
                Logging.mainLogger.info("thread not terminated! "+thread.getName());
        } else {
            Logging.mainLogger.severe("trying to interrupting current thread!: "+thread.getName());
        }
    }
    public static /* synchronized */ void myClose(Reader in,Writer out,Socket socket,final NamedThread thread,
            String name,Stopable stopable) { // super.close()?
        // https://stackoverflow.com/questions/19170744/interrupting-a-thread-from-itself/49077093#49077093
        final Thread currentThread=Thread.currentThread();
        if(currentThread.equals(thread)) throw new RuntimeException("calling my close with current thread.");
        boolean alwaysReturn=false;
        if(alwaysReturn) return;
        Logging.mainLogger.info("enter my close(), name: "+name+", thread: "+thread);
        Logging.mainLogger.info("current thread: "+toString(currentThread));
        if(thread!=null) {
            if(!currentThread.equals(thread)) {
                // try to set is stopping before interrupt.
                if(thread instanceof Stopable) ((Stopable)thread).setIsStopping();
                Logging.mainLogger.info("interrupting "+thread+" before closing anything.");
                thread.interrupt();
                GTPBackEnd.sleep2(GTPBackEnd.yield);
                stopThread(thread,stopable);
            } else {
                if(currentThreadIsTimeLimited()) Logging.mainLogger.severe("time limited thread not stopping: "+thread);
                Logging.mainLogger.info("1 not stopping current thread: "+thread);
            }
        }
        if(socket!=null) {
            if(!socket.isClosed()) try { // seems to work better here before in and out.
                // try last instead of first
                // gtp test suite works when we do this after in and out
                Logging.mainLogger.info("closing socket input: "+name+", thread: "+thread);
                if(!socket.isInputShutdown()) socket.shutdownInput();
            } catch(IOException e) {
                if(!stopable.isStopping()) Logging.mainLogger.severe("caught: "+e);
                else Logging.mainLogger.severe("stopping caught: "+e);
            }
            // } else { // try something
            if(out!=null) try { // seems to work better if done first.
                Logging.mainLogger.info("closing out: "+name+", thread: "+thread);
                out.close();
                Logging.mainLogger.info("out is closed: "+name+", thread: "+thread);
            } catch(IOException e) {
                if(!stopable.isStopping()) Logging.mainLogger.severe("caught: "+e);
                else Logging.mainLogger.severe("caught: "+e);
            }
            if(in!=null) try {
                Logging.mainLogger.info("interrupt before closing: "+thread);
                if(thread!=null&&!currentThread.equals(thread)) if(!thread.isInterrupted()) thread.interrupt();
                if(true) {
                    Logging.mainLogger.info("closing in: "+name+", thread: "+thread);
                    in.close();
                    Logging.mainLogger.info("in is closed: "+name+", thread: "+thread);
                } else Logging.mainLogger.info("skipping in.close() for: "+name+", thread: "+thread);
            } catch(IOException e) {
                if(!stopable.isStopping()) Logging.mainLogger.severe("caught: "+e);
                else Logging.mainLogger.severe("caught: "+e);
            }
        }
        Logging.mainLogger.info("after out and in, name: "+name+", thread: "+thread);
        if(socket!=null) if(!socket.isClosed()) {
            Logging.mainLogger.info("socket was not closed! closing socket: "+name+", thread: "+thread);
            closeSocket(socket);
            if(!socket.isClosed()) Logging.mainLogger.severe("name: "+name+" close socket fails! "+name);
        }
        if(thread!=null) {
            if(!thread.equals(currentThread)) {
                thread.interrupt();
                stopThread(thread,stopable);
            } else {
                Logging.mainLogger.info("2 not stopping current thread: "+thread);
            }
        }
        if(thread!=null&&thread.isAlive()) {
            Logging.mainLogger.warning(thread+" is still alive! "+toString(thread));
            if(!thread.equals(currentThread)) stopThread(thread,stopable);
            else Logging.mainLogger.info("3 not stopping current thread: "+thread);
        }
        if(socket!=null&&!socket.isClosed()) Logging.mainLogger.warning(name+" socket is not closed!");
        Logging.mainLogger.info("name: "+name+" exit my close(): "+name(thread));
    }
    private static String connect_(int timeout,Socket socket,InetSocketAddress inetSocketAddress) {
        String string="";
        try {
            Logging.mainLogger.fine(""+" "+"connecting to: "+inetSocketAddress+" with timeout: "+timeout);
            socket.connect(inetSocketAddress,timeout);
            Logging.mainLogger.fine(""+" "+"connected to: "+socket);
        } catch(java.net.BindException e) {
            string+=e;
            Logging.mainLogger.warning(""+" "+"connect caught: "+e+" on: "+inetSocketAddress);
        } catch(java.net.SocketException e) {
            string+=e;
            Logging.mainLogger.warning(""+" "+"caught: "+e);
        } catch(UnknownHostException e) {
            string+=e;
            Logging.mainLogger.warning(""+" "+"caught: "+e);
        } catch(IOException e) {
            string+=e;
            Logging.mainLogger.warning(""+" "+"caught: "+e);
        } catch(Exception e) {
            string+=e;
            Logging.mainLogger.warning(""+" "+"caught: "+e);
        } finally {}
        return string;
    }
    public static boolean connect(String host,int port,int timeout,Socket socket) {
        if(socket==null) throw new RuntimeException("socket is null!");
        InetSocketAddress inetSocketAddress=new InetSocketAddress(host,port);
        for(int i=0;i<10;++i) {
            String string=connect_(timeout,socket,inetSocketAddress);
            if(string.equals("")) return true;
            Logging.mainLogger.info("string: "+string);
            Logging.mainLogger.info("connect "+host+":"+port+" fails on try: "+i);
            //IO.stackTrace(10);
            GTPBackEnd.sleep2(1);
        }
        return false;
    }
    public static BufferedReader in_(Socket socket) {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Writer out_(Socket socket) {
        try {
            return new OutputStreamWriter(socket.getOutputStream());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void closeSocket(Socket socket) {
        if(socket!=null) try {
            if(!socket.isClosed()) {
                if(!socket.isInputShutdown()) socket.shutdownInput();
                if(!socket.isOutputShutdown()) socket.shutdownOutput();
                if(!socket.isClosed()) socket.close();
            }
        } catch(IOException e) {
            Logging.mainLogger.info("close caught: "+e);
        }
    }
    // consolidate these close and interrupt methods.
    private static void interrrupt(Thread thread) { // move this higher up.
        if(thread!=null) {
            if(thread.isAlive()&&!thread.isInterrupted()) {
                // interruptedByMe=true;
                Logging.mainLogger.info("interrupt");
                thread.interrupt();
                Logging.mainLogger.info("interrupted");
            } else Logging.mainLogger.info("attempt to interrupt thread that is not alive or already interrupted!");
        } else Logging.mainLogger.info("attempt to interrupt null thread!");
    }
    @SuppressWarnings("unused") public static synchronized Set<Thread> activeThreads() {
        int n=Thread.activeCount();
        Thread[] threads=new Thread[2*n];
        int actual=Thread.enumerate(threads);
        if(actual>n) Logging.mainLogger.severe("missed some threads!");
        Set<Thread> set=new LinkedHashSet<>();
        for(Thread thread:threads) if(thread!=null) set.add(thread);
        return set;
    }
    public static void printThreads(Set<Thread> threads,String name,boolean all) {
        // maybe remove main and reader thread?
        if(name!=null&&threads.size()>0) Logging.mainLogger.info(name+" threads:");
        int i=0,n=4;
        for(Thread thread:threads) {
            Logging.mainLogger.info(String.valueOf(thread));
            ++i;
            if(!all&&i>n) { Logging.mainLogger.info((threads.size()-n)+" more ..."); break; }
        }
    }
    public static void stackTrace(int n) {
        String name=Thread.currentThread().getName();
        StackTraceElement[] ste=Thread.currentThread().getStackTrace();
        for(int i=2;i<Math.min(n,ste.length);++i) Logging.mainLogger.info(name+" "+ste[i]);
    }
    public static boolean currentThreadIsTimeLimited() {
        if(false&&Thread.currentThread().getName()==IOs.timeLimitedThreadName) {
            Logging.mainLogger.info("on "+Thread.currentThread().getName());
            Logging.mainLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            System.exit(1);
        }
        return Thread.currentThread().getName()==IOs.timeLimitedThreadName;
    }
    public static final String timeLimitedThreadName="Time-limited test";
    public static final Integer anyPort=0,noPort=-1;
    public static final String host="localhost";
    public static final Integer defaultPort=3000;
    public static int testPort=6000;
    public static final Indent noIndent=new Indent("");
    public static final Indent oneSpaceIndent=new Indent(" ");
    public static final Indent standardIndent=new Indent("  ");
    public static final Set<Integer> ports=Set.of(IOs.noPort,IOs.anyPort,IOs.defaultPort);
    static {
        //Logging.mainLogger.info("ports: "+ports);
    }
}
