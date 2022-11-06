package x;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import io.IO;
import sgf.Parser;
class A implements Runnable {
    int id=++ids;
    static Integer ids;
    static {
        ids=0;
    }
    @Override public void run() {
        System.out.println(sgfData);
    }
    private static Map<String,String> sgfData=new LinkedHashMap<>();
    private static void initializeMap() { sgfData.put("sgfExamleFromRedBean",""); }
    static {
        System.out.println("static init");
        IO.stackTrace(20);
    }
    static final AtomicBoolean isInitialized=new AtomicBoolean();
    static { // this kind of thing needs to be done once!
        // running multiple instances is causing it to be run more than once.
        //synchronized(isInitialized) {
        synchronized(Parser.class) {
            System.out.println("is initualized: "+isInitialized);
            boolean ok=isInitialized.compareAndSet(false,true);
            if(ok) initializeMap();
        }
    }
}
public class StaticInitialization {
    public static void main(String[] args) {
        A a1=new A();
        new Thread(a1).start();
        A a2=new A();
        new Thread(a2).start();
        A a3=new A();
        new Thread(a3).start();
        System.out.println(a1.id+" "+A.ids);
        System.out.println(a2.id+" "+A.ids);
        System.out.println(a3.id+" "+A.ids);
    }
}
