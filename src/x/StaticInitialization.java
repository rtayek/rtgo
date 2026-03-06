package x;
import io.Logging;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import io.IOs;
class A implements Runnable {
    int id=++ids;
    static Integer ids;
    static final AtomicInteger sequence=new AtomicInteger();
    static void trace(String message) {
        System.err.printf("%02d %s%n",sequence.incrementAndGet(),message);
    }
    static {
        trace("A.ids static init");
        ids=0;
    }
    @Override public void run() {
        trace("A.run id="+id);
        Logging.mainLogger.info(String.valueOf(sgfData));
    }
    private static Map<String,String> sgfData=new LinkedHashMap<>();
    private static void initializeMap() { sgfData.put("sgfExamleFromRedBean",""); }
    static {
        trace("A.<clinit> static init");
        Logging.mainLogger.info("static init");
        IOs.stackTrace(20);
    }
    static final AtomicBoolean isInitialized=new AtomicBoolean();
    private static final Object initLock=new Object();
    static { // this kind of thing needs to be done once!
        synchronized(initLock) {
            trace("A.<clinit> guarded init");
            Logging.mainLogger.info("is initualized: "+isInitialized);
            boolean ok=isInitialized.compareAndSet(false,true);
            if(ok) initializeMap();
        }
    }
}
public class StaticInitialization {
    public static void main(String[] args) {
        A.trace("StaticInitialization.main()");
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
