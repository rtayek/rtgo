package utilities;
import static io.Init.first;
import com.tayek.util.junit.BasicTestWatcher;
import org.junit.runner.Description;
import io.*;
import io.IOs;
import server.NamedThreadGroup;
import server.NamedThreadGroup.Check;
public class MyTestWatcher extends BasicTestWatcher {
    public MyTestWatcher(Class<? extends Object> klass) { super(klass); this.verbosity=defaultVerbosity; }
    @Override public String ets() { return "at: "+first.et+", after: "+et; }
    @Override protected void starting(Description description) {
        super.starting(description);
        //Init.first.restoreSystmeIO();
        if(verbosity)
            Logging.mainLogger.info("starting test: "+key+" "+klass.getName()+"."+description.getMethodName()+" "+ets());
        if(IOs.currentThreadIsTimeLimited()) Logging.mainLogger.severe("time limited thread!");
        // fix this so there is only one test et!
        first.testsRun.add(klass.getName()+"."+description.getMethodName());
        check.startCheck();
    }
    @Override protected void finished(Description description) {
        super.finished(description);
        String finished="finished test "+first.testsRun.size()+" "+ets();
        if(verbosity) Logging.mainLogger.info(String.valueOf(finished));
        if(IOs.currentThreadIsTimeLimited()) Logging.mainLogger.severe("time limited thread! "+ets());
        String beforeEndCheck=NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids;
        check.endCheck();
        String afterEndCheck=NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids;
        if(verbosity) NamedThreadGroup.printNamedThreadGroups(false);
        if(verbosity) Logging.mainLogger.info(beforeEndCheck+" "+afterEndCheck+" finished(): "+klass.getSimpleName()+"."+description.getMethodName()+" "+ets()+" "+tests+" tests.");
        if(tests==lastTest) {
            Logging.mainLogger.info("last test!");
            first.lastPrint();
            if(saveTestsRun) first.saveTestsRun("fromwatcher"+lastTest+".txt");
        }
    }
    @Override protected void failed(Throwable e,Description description) {
        super.failed(e,description);
        if(IOs.currentThreadIsTimeLimited()) Logging.mainLogger.severe("time limited thread!");
        if(verbosity); //Logging.mainLogger.info(reset+description.getMethodName()+" failed!");
        //Logging.mainLogger.info(key+" failed. "+klass);
    }
    @Override protected void succeeded(Description description) {
        super.succeeded(description);
        if(IOs.currentThreadIsTimeLimited()) Logging.mainLogger.severe("time limited thread!");
        if(verbosity); //Logging.mainLogger.info(reset+description.getMethodName()+" succeeded.");
    }
    Check check=new Check();
    public Object key;
    public boolean verbosity;
    public boolean resetTestEt=false;
    public boolean saveTestsRun=false;
    public static final int unknowm=-1,controller=311,controllerSuite=261,game=83,server=37;
    public static int lastTest=unknowm;
    public static boolean defaultVerbosity=true;
    public static final String reset=com.tayek.util.log.ColorLogs.color_RESET;
    static {
        //Init.Main.main(null);
    }
}
