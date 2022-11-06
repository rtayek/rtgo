package io;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import io.Logging.MyFormatter;
public class Tee extends FilterOutputStream {
    // make this into a writer or make a version for writers
    // not a good idea unless we use the apache thing
    public Tee(OutputStream foo) {
        super(foo); // this.out is the underlying stream.
        streams.addElement(foo);
        printStream=new PrintStream(this,true) { // only println() is overwritter!
            @Override public void println(String string) { super.println(prefix+string); }
        };
    }
    public synchronized void addOutputStream(OutputStream out) { streams.addElement(out); }
    @Override public synchronized void write(byte[] data,int offset,int length) throws IOException {
        Integer i=0;
        for(Enumeration<OutputStream> e=streams.elements();e.hasMoreElements();) {
            OutputStream out=e.nextElement();
            if(verbose) {
                String index=i.toString();
                out.write(index.charAt(0)); // fails if more than10 streams.
                out.write('>');
            }
            out.write(data,offset,length);
            out.flush();
            ++i;
        }
    }
    public PrintStream setOut() { previousOut=System.out; System.setOut(printStream); return previousOut; }
    public PrintStream restoreOut() {
        PrintStream previous=System.out;
        if(previousOut!=null) System.setOut(previousOut);
        return previous;
    }
    public PrintStream setErr() { previousErr=System.err; System.setErr(printStream); return previousErr; }
    public PrintStream restoreErr() {
        PrintStream previous=System.err;
        if(previousErr!=null) System.setErr(previousErr);
        return previous;
    }
    public void restoreBoth() { if(previousOut!=null) restoreOut(); if(previousErr!=null) restoreErr(); }
    public static Tee tee(File file) {
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file.toString(),true);
            Tee tee=new Tee(fileOutputStream);
            tee.addOutputStream(System.out);
            tee.addOutputStream(System.err);
            tee.setOut();
            tee.setErr();
            Logging.mainLogger.fine("tee'd");
            return tee;
        } catch(IOException e) {
            Logging.mainLogger.warning("tee caught: "+e);
            return null;
        }
    }
    public static void printStuff(PrintStream printStream,PrintStream sysout,PrintStream syserr) {
        sysout.println("1 sysout "); // just comes out on sysout
        syserr.println("2 syserr"); // ditto
        printStream.println("ps from tee");
        Logging.mainLogger.severe("some logging message.");
        Logging.mainLogger.getHandlers()[0].flush();
        sysout.flush();
        syserr.flush();
        printStream.flush();
    }
    public static void foo(Tee tee) {
        PrintStream out=System.out;
        PrintStream err=System.err;
        out.println("111111");
        printStuff(tee.printStream,out,err);
        out.println("222222");
        tee.addOutputStream(new PrintStream(System.err));
        tee.setErr();
        out.println("added syserr and set syserr");
        printStuff(tee.printStream,out,err);
        out.println("3333333");
        tee.restoreErr();
        printStuff(tee.printStream,out,err);
        out.println("4444444");
    }
    private static void oneTee(Logger logger) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        Tee t=new Tee(byteArrayOutputStream);
        PrintStream out=System.out;
        PrintStream err=System.err;
        t.addOutputStream(System.err);
        logger.info("log after add syserr");
        //t.setOut();
        t.setErr();
        System.out.println("out foo");
        t.printStream.println("printstream foo");
        System.err.println("err foo");
        logger.info("log after");
        t.restoreOut();
        t.restoreErr();
        //System.out.println("baos: '"+byteArrayOutputStream+"'");
        out.println("after reset -------------------");
        System.out.println("out foo");
        t.printStream.println("printstream foo");
        System.err.println("err foo");
        logger.info("log after2");
        System.out.println("baos: '"+byteArrayOutputStream+"'");
    }
    public static void main(String[] args) throws IOException,InterruptedException {
        // almost correct. looks like we need two tees to avoid duplicate output.
        LogManager.getLogManager().reset();
        Logger logger=Logger.getLogger("frog");
        Logging.setupLogger(logger,new MyFormatter());
        Handler[] x=logger.getHandlers();
        System.out.println(Arrays.asList(x));
        if(false) oneTee(logger);
        else {
            PrintStream out=System.out;
            PrintStream err=System.err;
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            Tee t=new Tee(byteArrayOutputStream);
            t.setOut();
            Tee t2=new Tee(byteArrayOutputStream);
            t2.setErr();
            t2.prefix="T2 ";
            System.out.println("out foo");
            t.printStream.println("printstream foo");
            System.err.println("err foo");
            logger.info("log after");
            t.flush();
            t2.flush();
            t.restoreOut();
            t2.restoreErr();
            //System.out.println("baos: '"+byteArrayOutputStream+"'");
            //Thread.sleep(100);
            out.println("after reset -------------------");
            System.out.println("out foo");
            t2.printStream.println("printstream foo 2");
            System.err.println("err foo");
            logger.info("log after2");
            System.out.println("baos: '"+byteArrayOutputStream+"'");
        }
    }
    boolean verbose;
    public String prefix="T ";
    PrintStream previousOut,previousErr;
    public final PrintStream printStream;
    Vector<OutputStream> streams=new Vector<OutputStream>();
    public static String lineSeparator=System.getProperty("line.separator");
}
