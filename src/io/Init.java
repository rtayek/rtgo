package io;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import controller.GTPBackEnd;
import server.NamedThreadGroup;
import utilities.Et;
// https://developer.ibm.com/tutorials/j-introducing-junit5-part2-vintage-jupiter-extension-model/
// 3 calls to stack trace, but that seems ok.
public enum Init {
    first;
    private void once() {
        //IO.stackTrace(10);
        if(once) return;
        else once=true;
        if(verbose) System.out.println("4 once initialize");
        String forground=System.getProperty("foreground");
        if(verbose) System.out.println("fotreground: "+forground);
        ColorLogs.blackOrWhite=forground==null?ColorLogs.color_BLACK:ColorLogs.color_WHITE;
        Sequence.blackOrWhite=forground!=null?Sequence.black:Sequence.white;
        if(verbose) System.out.println("blackOrWhite: "+Sequence.blackOrWhite);
        LogManager.getLogManager().reset();
        // maybe omit this stuff below?
        if(false) {
            Logging.setUpLogging();
            Logging.setLevels(Logging.initialLoggingLevel); // do this last or level is null!
            Logging.parserLogger.setLevel(defaultParserLoggerLevel);
        }
        //System.out.println(Parameters.topologies);
        System.out.println("once");
    }
    private void restoreSystmeIO() { System.setOut(out); System.setErr(err); }
    public void twice() {
        once();
        if(verbose) System.out.println("5 enter twice()");
        et.reset();
        if(verbose) System.out.println("exit twice()");
    }
    public abstract static class Main {
        public static void main(String[] argument) {
            if(verbose) System.out.println("1.5 Init.Main.main(), first: "+first); // needs to be here
        }
        static {
            if(verbose) System.out.println("1 Init.Main.main static init");
        }
        static {
            if(verbose) System.out.println("static init Init.Main");
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
        System.out.println("suit contols: "+first.suiteControls);
        System.out.println("wrapup counter: "+counter);
        if(suiteControls) return;
        // should i get initial ids here?
        ++counter;
        maxCounter=Math.max(counter,maxCounter);
    }
    public void wrapupTests_() {
        wasWrapupTestsCalled=true;
        System.out.println("wrapup tests");
        System.out.println("highest named threads id: "+NamedThreadGroup.ids);
        System.out.println(NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids);
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.allNamedThreads,"all",false);
        IO.printThreads(IO.activeThreads(),"active at end",false);
        GTPBackEnd.sleep2(2); // was 10
        NamedThreadGroup.removeAllTerminated();
        System.out.println("highest named threads id: "+NamedThreadGroup.ids);
        System.out.println(NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids);
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.allNamedThreads,"all",false);
        IO.printThreads(IO.activeThreads(),"active at end",false);
    }
    public synchronized void wrapupTests() {
        boolean printMore=false;
        if(printMore) { IO.stackTrace(3); System.out.println("wrapup counter: "+counter+", max counter: "+maxCounter); }
        if(wasWrapupTestsCalled) Logging.mainLogger.severe("duplicate call to wrapup");
        if(suiteControls) return;
        else {
            if(--counter>0) { System.out.println("counter: "+counter+" not wrapping up"); return; }
            System.out.println("not returning");
        }
        System.out.println("counter: "+counter);
        wrapupTests_();
    }
    public void saveTestsRun(String fileName) {
        File file=new File(fileName);
        Writer writer;
        try {
            writer=new FileWriter(file);
            for(String testName:testsRun) writer.write(testName+'\n');
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void lastPrint() {
        NamedThreadGroup.printThraedsAtEnd();
        int n=NamedThreadGroup.printNamedThreadGroups(true);
        System.out.println(n);
        IO.printThreads(IO.activeThreads(),"last",true);
        System.out.println("tests run: "+first.testsRun);
    }
    public static void init(String[] argument) {
        System.out.println("1.5 Init.Main.main(), first: "+first); // needs to be here
    }
    public static void main(String[] args) {
        System.out.println("enter Init.main()");
        //System.out.println(first);
        first.initiaizeTests();
        first.initiaizeTests();
        first.wrapupTests();
        first.wrapupTests();
        System.out.println("exit Init.main()");
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
        if(verbose) System.out.println("? Init.main static init");
    }
    public static final String notTerminated="notTerminated";
    static int maxCounter;
    /*static*/ Level defaultParserLoggerLevel=Level.OFF;
}
