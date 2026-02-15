package io;
import io.Logging;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import controller.GTPBackEnd;
import server.NamedThreadGroup;
import com.tayek.util.core.Et;
import com.tayek.util.core.Texts;
import com.tayek.util.io.FileIO;
import com.tayek.util.log.ColorLogs;
import com.tayek.util.log.Sequence;
// https://developer.ibm.com/tutorials/j-introducing-junit5-part2-vintage-jupiter-extension-model/
// 3 calls to stack trace, but that seems ok.
public enum Init {
    first;
    private void once() {
        //IO.stackTrace(10);
        if(once) return;
        else once=true;
        if(verbose) Logging.mainLogger.info("4 once initialize");
        String forground=System.getProperty("foreground");
        if(verbose) Logging.mainLogger.info("fotreground: "+forground);
        ColorLogs.blackOrWhite=forground==null?ColorLogs.color_BLACK:ColorLogs.color_WHITE;
        Sequence.blackOrWhite=forground!=null?Sequence.black:Sequence.white;
        Sequence.setNameToColorIndex(NamedThreadGroup.nameToColorIndex);
        if(verbose) Logging.mainLogger.info("blackOrWhite: "+Sequence.blackOrWhite);
        LogManager.getLogManager().reset();
        // maybe omit this stuff below?
        if(true) {
            Logging.setUpLogging();
            Logging.setLevels(Logging.initialLoggingLevel); // do this last or level is null!
            Logging.parserLogger.setLevel(defaultParserLoggerLevel);
        }
        //Logging.mainLogger.info(Parameters.topologies);
        Logging.mainLogger.info("once");
    }
    private void restoreSystmeIO() { System.setOut(out); System.setErr(err); }
    public void twice() {
        once();
        if(verbose) Logging.mainLogger.info("5 enter twice()");
        et.reset();
        if(verbose) Logging.mainLogger.info("exit twice()");
    }
    public abstract static class Main {
        public static void main(String[] argument) {
            if(verbose) Logging.mainLogger.info("1.5 Init.Main.main(), first: "+first); // needs to be here
        }
        static {
            if(verbose) Logging.mainLogger.info("1 Init.Main.main static init");
        }
        static {
            if(verbose) Logging.mainLogger.info("static init Init.Main");
        }
    }
    private Init() {
        //IO.stackTrace(10);
        out=System.out;
        err=System.err;
        once();
        twice();
    }
    public boolean add(NamedThreadGroup namedThreadGroup) {
        if(!NamedThreadGroup.groupIdToNamedThreadGroup.containsKey(namedThreadGroup.groupId)) {
            NamedThreadGroup.groupIdToNamedThreadGroup.put(namedThreadGroup.groupId,namedThreadGroup);
            return true;
        } else {
            Logging.mainLogger.severe("duplicate game id: "+namedThreadGroup.groupId);
            return false;
        }
    }
    // put this stuff below into the named thread class.
    // and keep this init code as small as possible.
    public void initiaizeTests() {
        Logging.mainLogger.info("suit contols: "+first.suiteControls);
        Logging.mainLogger.info("wrapup counter: "+counter);
        if(suiteControls) return;
        // should i get initial ids here?
        ++counter;
        maxCounter=Math.max(counter,maxCounter);
    }
    public void wrapupTests_() {
        wasWrapupTestsCalled=true;
        Logging.mainLogger.info("wrapup tests");
        Logging.mainLogger.info("highest named threads id: "+NamedThreadGroup.ids);
        Logging.mainLogger.info(NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids);
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.allNamedThreads,"all",false);
        IOs.printThreads(IOs.activeThreads(),"active at end",false);
        GTPBackEnd.sleep2(2); // was 10
        NamedThreadGroup.removeAllTerminated();
        Logging.mainLogger.info("highest named threads id: "+NamedThreadGroup.ids);
        Logging.mainLogger.info(NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids);
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.allNamedThreads,"all",false);
        IOs.printThreads(IOs.activeThreads(),"active at end",false);
    }
    public synchronized void wrapupTests() {
        boolean printMore=false;
        if(printMore) { IOs.stackTrace(3); Logging.mainLogger.info("wrapup counter: "+counter+", max counter: "+maxCounter); }
        if(wasWrapupTestsCalled) Logging.mainLogger.severe("duplicate call to wrapup");
        if(suiteControls) return;
        else {
            if(--counter>0) { Logging.mainLogger.info("counter: "+counter+" not wrapping up"); return; }
            Logging.mainLogger.info("not returning");
        }
        Logging.mainLogger.info("counter: "+counter);
        wrapupTests_();
    }
    public void saveTestsRun(String fileName) {
        FileIO.write(Texts.cat(testsRun),new File(fileName));
    }
    public void lastPrint() {
        NamedThreadGroup.printThraedsAtEnd();
        int n=NamedThreadGroup.printNamedThreadGroups(true);
        Logging.mainLogger.info(String.valueOf(n));
        IOs.printThreads(IOs.activeThreads(),"last",true);
        Logging.mainLogger.info("tests run: "+first.testsRun);
    }
    public static void init(String[] argument) {
        Logging.mainLogger.info("1.5 Init.Main.main(), first: "+first); // needs to be here
    }
    public static void main(String[] args) {
        Logging.mainLogger.info("enter Init.main()");
        //Logging.mainLogger.info(first);
        first.initiaizeTests();
        first.initiaizeTests();
        first.wrapupTests();
        first.wrapupTests();
        Logging.mainLogger.info("exit Init.main()");
    }
    public final Et et=new Et();
    public int counter;
    boolean wasWrapupTestsCalled;
    public boolean suiteControls;
    {
        NamedThreadGroup namedThreadGroup=new NamedThreadGroup(NamedThreadGroup.groupZero);
        NamedThreadGroup.groupIdToNamedThreadGroup.put(NamedThreadGroup.groupZero,namedThreadGroup);
        namedThreadGroup=new NamedThreadGroup(NamedThreadGroup.standAlone);
        NamedThreadGroup.groupIdToNamedThreadGroup.put(NamedThreadGroup.standAlone,namedThreadGroup);
    }
    public final PrintStream out,err;
    private SortedMap<String,Object> stuff=new TreeMap<>();
    public final ArrayList<String> testsRun=new ArrayList<>();
    public boolean once;
    static boolean verbose=false;
    static {
        if(verbose) Logging.mainLogger.info("? Init.main static init");
    }
    public static final String notTerminated="notTerminated";
    static int maxCounter;
    /*static*/ Level defaultParserLoggerLevel=Level.OFF;
}
