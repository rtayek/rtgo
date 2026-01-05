package io;
import io.Logging;
import java.io.*;
import java.util.function.Function;
import io.IOs.Duplex;
public class Copy implements Runnable {
    public static class CopyBC implements Runnable {
        public CopyBC(BufferedReader in,Writer out) { this.in=in; this.out=out; };
        @Override public void run() {
            boolean once=false;
            try {
                while(!done) {
                    if(!once) { once=true; Logging.mainLogger.info("back end read"); }
                    String string=in.readLine();
                    Logging.mainLogger.info("back end read: "+string);
                    if(string==null) { done=true; break; }
                    if(process!=null) { boolean ok=this.process.apply(string); if(!ok) done=true; }
                }
                out.close();
            } catch(Exception e) {
                Logging.mainLogger.warning(this+" caught: "+e);
            } finally {}
        } //out.close(); don't do this here // why not?
        static void foo() throws IOException {
            // this hangs. figure out why
            Duplex duplex=new Duplex();
            CopyBC copyBC=new CopyBC(duplex.back.in(),duplex.back.out());
            Function<String,Boolean> p=(string)-> {
                try {
                    Logging.mainLogger.info("back end write");
                    copyBC.out.write(string+'\n');
                    copyBC.out.flush();
                    return true;
                } catch(IOException e) {
                    e.printStackTrace();
                }
                return false;
            };
            copyBC.process=p;
            Thread thread=new Thread(copyBC);
            thread.start();
            Logging.mainLogger.info("started");
            //copyBC.run();
            Logging.mainLogger.info("front end write");
            duplex.front.out().write("foo\n");
            duplex.front.out().flush();
            Logging.mainLogger.info("front end read");
            String string=duplex.front.in().readLine();
            Logging.mainLogger.info("front end read:  "+string);
        }
        boolean done;
        Function<String,Boolean> process;
        Thread thread;
        final BufferedReader in;
        final Writer out;
    }
    Copy(BufferedReader in,Writer out) { this.in=in; this.out=out; }
    @Override public void run() { // belongs in io.IO?
        try {
            /*
            while(!done) {
                String string=in.readLine(); // xyzzy
                if(string==null) { done=true; break; }
            
                out.write(string+'\n');
                out.flush();
            }
            out.flush();
             */
            boolean once=false;
            while(!done) {
                if(!once) { once=true; }
                String string=in.readLine(); //xyzzy
                if(string==null) { done=true; break; }
                out.write(string+'\n');
                out.flush();
                //Thread.sleep(0); // omitting makes a test work!
            }
            out.flush();
        } catch(Exception e) {
            Logging.mainLogger.warning(this+" caught: "+e);
        } finally {}
        //out.close(); don't do this here
    }
    void run(boolean useThread,boolean join) {
        if(!useThread) {
            run();
        } else {
            Thread thread=new Thread(this,""+useThread);
            thread.start();
            if(join) try {
                thread.join();
            } catch(InterruptedException e) {
                Logging.mainLogger.severe("caught: "+e);
                ;
            }
        }
    }
    @Override public String toString() { return "Copy [name="+name+", in="+in+", out="+out+"]"; }
    static void sendAndReceive(boolean useThread,BufferedReader r,Writer w) throws IOException {
        Copy rw=new Copy(r,w);
        rw.run(useThread,true);
    }
    public static Writer sendAndReceive(String string,boolean useThread) throws IOException {
        BufferedReader r=IOs.toBufferedReader(string);
        Writer w=new StringWriter();
        sendAndReceive(useThread,r,w);
        return w;
    }
    String name;
    boolean done;
    Thread thread;
    final BufferedReader in;
    final Writer out;
    public static void main(String[] args) throws IOException {
        Logging.mainLogger.info(String.valueOf(Init.first));
        Logging.mainLogger.info("enter main");
        CopyBC.foo();
        Logging.mainLogger.info("exit main");
    }
}
