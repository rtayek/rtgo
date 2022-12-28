package controller;
import static controller.GTPBackEnd.*;
import java.io.*;
import java.net.*;
import java.util.List;
import io.*;
import io.IO.*;
import server.NamedThreadGroup.NamedThread;
public class GTPFrontEnd implements Runnable,Stopable { // front end for GTP
    // try to consolidate the i/o.
    @SuppressWarnings("serial") public static class PipeException extends RuntimeException {
        PipeException(String cause) { super(cause); }
        PipeException(String cause,Throwable throwable) { super(cause,throwable); }
    }
    @SuppressWarnings("serial") public static class PipeEofException extends PipeException {
        PipeEofException(String cause) { super(cause); }
    }
    public GTPFrontEnd(End end) { this(end,defaultOk,defaultBad,defaultQuit); }
    private GTPFrontEnd(End end,String ok,String bad,String quit) {
        this.socket=end.socket();
        this.in=end.in();
        this.out=end.out();
        this.ok=ok;
        this.bad=bad;
        this.quit=quit;
    }
    public void sendString(String string) {
        try {
            out.write(string+'\n');
            out.flush();
        } catch(IOException e) {
            if(!isStopping) Logging.mainLogger.severe(this+" "+isStopping()+" caught: "+" "+this);
        }
    }
    public Response receive() { // do NOT synchronize this method!
        String s="";
        try {
            String string;
            // looks like it reads until double line feed?
            // gtp/ passive base reads one line and executes.
            Logging.mainLogger.info("FE '"+name+"' waiting to receive.");
            for(string=in.readLine();string!=null;string=in.readLine()) { // xyzzy
                Logging.mainLogger.info("FE '"+name+"' received: '"+string+"'.");
                s+=string+'\n';
                boolean breakOnLinefeed=true; // looks like we need this!
                if(breakOnLinefeed&&string.equals("")) {
                    Logging.mainLogger.info("FE '"+name+"' read empty string. breaking out of read loop.");
                    break;
                }
                Logging.mainLogger.info("FE '"+name+"' waiting to receive.");
            }
            if(string==null) { // we are now getting this (new behavior).
                if(s.equals("")) Logging.mainLogger.warning("new behaviour 3: "+name+" received: "+s);
                else Logging.mainLogger.warning(name+"receive() received: '"+s+"' and end of file!");
                in.close();
                out.close();
                return null;
            }
        } catch(SocketException e) {
            if(isStopping) {
                Logging.mainLogger.info(name+" stopping 00 caught: "+e);
            } else {
                System.out.println(isStopping());
                System.out.println(e.getMessage());
                Logging.mainLogger.warning(name+" 10 caught: "+e);
            }
            //throw new RuntimeException(e);
            return null;
        } catch(InterruptedIOException e) {
            if(isStopping) {
                Logging.mainLogger.info(name+" stopping 15 caught: "+e);
            } else {
                System.out.println(isStopping());
                System.out.println(e.getMessage());
                Logging.mainLogger.warning(name+" 20 caught: "+e);
            }
            //throw new RuntimeException(e);
            return null;
        } catch(IOException e) {
            Logging.mainLogger.warning(name+" 30 caught: "+e);
            //throw new RuntimeException(e);
            return null;
        } catch(Exception e) {
            Logging.mainLogger.warning(name+" 40 caught: "+e);
            //throw new RuntimeException(e);
            return null;
        }
        char c;
        if(s.length()>0) for(c=s.charAt(0);s.length()>1&&(c==' '||c=='\n'||c=='\r');c=s.charAt(0)) {
            Logging.mainLogger.info(name+" discarding character: "+c);
            s=s.substring(1);
            //if(true) throw new RuntimeException(getClass().getSimpleName()+" oops");
            // what was the above for?
        }
        if(!s.startsWith(okString)) {
            //GTPBackEnd.sleep2(10);
            //System.out.println("received: '"+s+"'");
            Logging.mainLogger.severe(name+" receive problem! '"+s+"'");
        }
        Response response=Response.response(s);
        return response;
    }
    public Response sendAndReceive(String string) {
        // consolidate this with the gtp dorect test case send and receive!
        Logging.mainLogger.fine(name+" sending: "+string);
        sendString(string);
        Response response=receive();
        Logging.mainLogger.fine(name+" received: "+response);
        // new behavior: getting a null here
        if(response!=null) { if(response.isBad()) Logging.mainLogger.info("1 command: "+string+" failed: "+response); }
        return response;
    }
    public boolean sendAndReceive(List<String> strings) {
        for(String string:strings) {
            Response response=sendAndReceive(string);
            if(response!=null) {
                if(false) if(response==null) {
                    Logging.mainLogger.warning("sar new behavior");
                    return false; // new behavior
                }
                if(!response.isOk()) return false;
            } else return true;
        }
        return true;
    }
    @Override public boolean isStopping() { return isStopping; }
    @Override public boolean setIsStopping() { boolean rc=isStopping; isStopping=true; return rc; }
    @Override public void stop() {
        Logging.mainLogger.fine("enter front end stop "+name+" "+thread);
        isStopping=true;
        IO.myClose(in,out,socket,thread,name,this);
        Logging.mainLogger.fine("exit front end stop "+name+" "+thread);
    }
    @Override public void run() { // usually just used for testing,
        // try a write read loop
        // just send a name command.
        String string;
        try {
            for(string=in.readLine();string!=null;string=in.readLine()) {
                out.write(string+'\n');
                out.flush();
            }
            in.close();
            out.close(); // causes some tests to fail
        } catch(IOException e) {
            Logging.mainLogger.severe("run() caught: "+e+" "+this);
        }
    }
    @Override public String toString() {
        return "GTPFrontEnd [name="+name+", in="+in+", out="+out+", thread="+thread+"]";
    }
    public boolean isStopping;
    public String getName() { return name; }
    public void setName(String name) { this.name=name; }
    public BufferedReader getIn() { return in; }
    public Writer getOut() { return out; }
    private final String ok,bad,quit;
    public transient String name="null";
    public final BufferedReader in;
    public final Writer out;
    public Socket socket;
    public NamedThread thread;
    public static final String defaultOk=""+okCharacter;
    public static final String defaultBad=""+badCharacter;
    public static final String defaultQuit=Command.quit.name();
}
