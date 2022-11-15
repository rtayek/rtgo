package utilities;
import static io.Init.first;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import io.*;
import server.NamedThreadGroup;
import server.NamedThreadGroup.Check;
public class MyTestWatcher extends TestWatcher {
    public MyTestWatcher(Class<? extends Object> klass) { this.klass=klass; this.verbosity=defaultVerbosity; }
    public String ets() { return "at: "+first.et+", after: "+et; }
    @Override protected void starting(Description description) {
        ++tests;
        //Init.first.restoreSystmeIO();
        et.reset();
        if(verbosity) System.out.println("starting test: "+klass.getName()+"."+description.getMethodName()+" "+ets());
        if(IO.currentThreadIsTimeLimited()) Logging.mainLogger.severe("time limited thread!");
        // fix this so there is only one test et!
        first.testsRun.add(klass.getName()+"."+description.getMethodName());
        check.startCheck();
    }
    @Override protected void finished(Description description) {
        String finished="finished test "+first.testsRun.size()+" "+ets();
        if(IO.currentThreadIsTimeLimited()) Logging.mainLogger.severe("time limited thread! "+ets());
        String beforeEndCheck=NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids;
        check.endCheck();
        String afterEndCheck=NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids;
        if(verbosity) NamedThreadGroup.printNamedThreadGroups(false);
        if(verbosity) System.out.println(beforeEndCheck+" "+afterEndCheck+" finished(): "+klass.getSimpleName()+"."
                +description.getMethodName()+" "+ets()+" "+tests+" tests.");
        if(tests==lastTest) {
            System.out.println("last test!");
            first.lastPrint();
            if(saveTestsRun) first.saveTestsRun("fromwatcher"+lastTest+".txt");
        }
    }
    @Override protected void failed(Throwable e,Description description) {
        if(IO.currentThreadIsTimeLimited()) Logging.mainLogger.severe("time limited thread!");
        if(verbosity); //System.out.println(reset+description.getMethodName()+" failed!");
        System.out.println(key+" failed. "+klass);
    }
    @Override protected void succeeded(Description description) {
        if(IO.currentThreadIsTimeLimited()) Logging.mainLogger.severe("time limited thread!");
        if(verbosity); //System.out.println(reset+description.getMethodName()+" succeeded.");
    }
    Check check=new Check();
    public Object key;
    public boolean verbosity;
    public boolean resetTestEt=false;
    public boolean saveTestsRun=false;
    private final Et et=new Et();
    public final Class<? extends Object> klass;
    public static int tests;
    public static final int unknowm=-1,controller=311,controllerSuite=261,game=83,server=37;
    public static int lastTest=unknowm;
    public static boolean defaultVerbosity=false;
    public static final String reset=io.ColorLogs.color_RESET;
    static {
        //Init.Main.main(null);
    }
}
