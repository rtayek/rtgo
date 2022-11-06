package x;
import java.io.*;
import java.util.*;
public class TeeExperiment extends FilterOutputStream {
    public TeeExperiment(OutputStream out) {
        super(out);
        streams.addElement(out);
        printStream=new PrintStream(this,true) {
            @Override public void println(String string) { super.println("T "+string); }
        };
    }
    @Override public synchronized void write(int b) throws IOException {
        for(Enumeration<OutputStream> e=streams.elements();e.hasMoreElements();) {
            OutputStream out=e.nextElement();
            out.write(b);
            out.flush();
        }
    }
    @Override public synchronized void write(byte[] data,int offset,int length) throws IOException {
        for(Enumeration<OutputStream> e=streams.elements();e.hasMoreElements();) {
            OutputStream out=e.nextElement();
            out.write(data,offset,length);
            out.flush();
        }
    }
    public synchronized void addOutputStream(OutputStream out) { streams.addElement(out); }
    
    public static void main(String[] args) throws IOException {
        // make a bunch of print streams
        // and put a prefix out in front so we can tell where they are coming from
        //
        // maybe i need a tee for each printstream
        // ie tee for out with sink
        // and a tee for err with the sane sink (for test view)
        // yes,make twp tees.
        System.out.println("hello");
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        TeeExperiment tee=new TeeExperiment(byteArrayOutputStream);
        //tee.write('j');
        tee.printStream.println("j");
        System.out.println("'"+byteArrayOutputStream+"'");
        System.out.println(byteArrayOutputStream.size());
        //tee.printStream.println("foo");
        tee.printStream.println("foo");
        System.out.println("'"+byteArrayOutputStream+"'");
        System.out.println(byteArrayOutputStream.size());
        System.out.println("-----");
        tee.addOutputStream(System.err);
        byteArrayOutputStream.reset();
        tee.write('j');
        System.out.println("'"+byteArrayOutputStream+"'");
        System.out.println(byteArrayOutputStream.size());
        
        tee.printStream.println("foo");
        System.out.println("'"+byteArrayOutputStream+"'");
        System.out.println(byteArrayOutputStream.size());
        System.out.println("goodnye");
    }
    public final PrintStream printStream;
    Vector<OutputStream> streams=new Vector<OutputStream>();
}
