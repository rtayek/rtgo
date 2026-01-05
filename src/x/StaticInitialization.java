package x;
import io.Logging;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import io.IOs;
import sgf.Parser;
class A implements Runnable {
    int id=++ids;
    static Integer ids;
    static {
        ids=0;
    }
    @Override public void run() { Logging.mainLogger.info(String.valueOf(sgfData)); }
    private static Map<String,String> sgfData=new LinkedHashMap<>();
    private static void initializeMap() { sgfData.put("sgfExamleFromRedBean",""); }
    static {
        Logging.mainLogger.info("static init");
        IOs.stackTrace(20);
    }
    static final AtomicBoolean isInitialized=new AtomicBoolean();
    static { // this kind of thing needs to be done once!
        // running multiple instances is causing it to be run more than once.
        //synchronized(isInitialized) {
        synchronized(Parser.class) {
            Logging.mainLogger.info("is initualized: "+isInitialized);
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
        Logging.mainLogger.info(a1.id+" "+A.ids);
        Logging.mainLogger.info(a2.id+" "+A.ids);
        Logging.mainLogger.info(a3.id+" "+A.ids);
    }
}
