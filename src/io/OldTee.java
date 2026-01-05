package io;
import io.Logging;
import java.io.*;
import java.util.*;
public class OldTee extends FilterOutputStream /* make this into a writer or make a version for writers */ { // tee utility
    public OldTee(final OutputStream out) { super(out); streams.addElement(out); }
    public void teeTo() {
        //Tee tee=new Tee(fileOutputStream);
        //tee.addOutputStream(System.out); /* make into a constructor and put into Tee? */
    }
    public synchronized void addOutputStream(final OutputStream out) { streams.addElement(out); }
    @Override public synchronized void write(final int b) throws IOException {
        for(Enumeration<OutputStream> e=streams.elements();e.hasMoreElements();) {
            final OutputStream out=e.nextElement();
            out.write(b);
            out.flush();
        }
    }
    @Override public synchronized void write(final byte[] data,final int offset,final int length) throws IOException {
        for(Enumeration<OutputStream> e=streams.elements();e.hasMoreElements();) {
            final OutputStream out=e.nextElement();
            out.write(data,offset,length);
            out.flush();
        }
    }
    public static void tee(final File file) { tee(file,true); }
    public static void tee(final File file,final boolean delete) {
        if(delete&&file.exists()) file.delete();
        final OldTee tee=new OldTee(System.out);
        final PrintStream printStream=new PrintStream(tee,true);
        System.setOut(printStream);
        System.setErr(printStream);
        try {
            tee.addOutputStream(new FileOutputStream(file.toString(),true));
        } catch(IOException e) {
            Logging.mainLogger.info(String.valueOf(e));
        }
        Logging.mainLogger.info("tee'd");
    }
    Vector<OutputStream> streams=new Vector<OutputStream>();
}
