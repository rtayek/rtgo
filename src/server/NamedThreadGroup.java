package server;
import static io.Logging.mainLogger;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.*;
import controller.GTPBackEnd;
import io.*;
import com.tayek.util.concurrent.Stopable;
import com.tayek.util.core.Et;
import com.tayek.util.log.ColorLogs;
public class NamedThreadGroup { // one set of named threads.
    public static class Check {
        public Check(boolean doCheck) { this.doCheck=doCheck; }
        public Check() { this(false); }
        public void startCheck() {
            if(!doCheck) return;
            if(checkManyThings) { initialThreads=IOs.activeThreads(); ids=removeLeftoverThreads(initialThreads); }
        }
        public void howMany() {
            int black=0,white=0,model=0,game=0,server=0;
            Set<NamedThread> namedThreads=allActiveNamedThreads();
            for(NamedThread namedThread:namedThreads) {
                String name=namedThread.getName();
                if(name.contains("black")) ++black;
                else if(name.contains("white")) ++white;
                else if(name.contains("model")) ++model;
                else if(name.contains("game")) ++game;
                else if(name.contains("server")) ++server;
                else Logging.mainLogger.info("strange name: "+namedThread);
            }
            Logging.mainLogger.info("black: "+black+", white: "+white+", model: "+model+", game: "+game+", server: "+server);
        }
        public void endCheck() {
            if(!doCheck) return;
            if(checkManyThings) {
                Set<Thread> active=IOs.activeThreads();
                IOs.printThreads(active,active.size()+" active.",false);
                if(checkThreads) {
                    Logging.mainLogger.info("<<<");
                    Set<Thread> finalThreads=getFinalThreads(ids,initialThreads);
                    // this remove is done in another place
                    // maybe we don't need this one?
                    if(removeTerminated) NamedThreadGroup.removeAllTerminated();
                    Logging.mainLogger.info(">>>");
                }
                printNamedThreads(allActiveNamedThreads(),"all active named:",false);
                NamedThreadGroup.printThraedsAtEnd();
                //Logging.mainLogger.info("keyset: "+NamedThreadGroup.nameToNamedThreadSet.keySet());
                howMany();
            }
        }
        public boolean doCheck=true;
        public boolean removeTerminated=true;
        public boolean checkThreads=true;
        boolean checkManyThings=true;
        int ids;
        Set<Thread> initialThreads;
    }
    public class NamedThread extends Thread {
        public NamedThread(String name,Runnable runnable) {
            super(runnable);
            this.runnable=runnable;
            this.groupId=NamedThreadGroup.this.groupId;
            setName(name+"-"+this.getId()+" ("+this.groupId+","+this.id+")");
            allNamedThreads.add(this);
        }
        @Override public void start() { Logging.mainLogger.config("started: "+getName()); super.start(); }
        @Override public String toString() {
            if(this!=null) if(this.getName().contains(IOs.timeLimitedThreadName)) {
                mainLogger.severe(this.getName()+" time limited");
            }
            return this==null?null
                    :this.getName()+", "+this.getState()+", isa: "+this.isAlive()+", iisi:  "+this.isInterrupted();
        }
        public final Runnable runnable;
        public final long groupId;
        public final int id=++ids;
    }
    public NamedThreadGroup(long groupId) { this.groupId=groupId; }
    public synchronized void addNamedThread(NamedThread namedThread) { namedThreadsWithRunnables.add(namedThread); }
    public synchronized Set<NamedThread> activeNamedThreads() {
        // this has side effects. fix!!!
        // for this group of named threads
        Set<Thread> threads=IOs.activeThreads();
        Set<NamedThread> active=new LinkedHashSet<>();
        Set<NamedThread> inActive=new LinkedHashSet<>();
        for(NamedThread namedThread:namedThreadsWithRunnables) {
            boolean found=false;
            for(Thread thread:threads) if(namedThread.equals(thread)) { found=true; active.add(namedThread); break; }
            if(!found) inActive.add(namedThread);
        }
        if(inActive.size()>0) {
            //IO.stackTrace(10);
            //Logging.mainLogger.info("removing from group: "+groupId+", inactive: "+inActive.size()
            //+" inactive named threads: "+inActive);
            namedThreadsWithRunnables.removeAll(inActive);
            //Logging.mainLogger.info(
            //        "removing from all: "+groupId+", inactive: "+inActive.size()+" inactive named threads: "+inActive);
            allNamedThreads.removeAll(inActive);
        }
        if(namedThreadsWithRunnables.size()==0); //Logging.mainLogger.info("namedThreadsWithRunnables is empty!");
        return active;
    }
    private synchronized void findNamedThreads() {
        long min=Long.MAX_VALUE,max=Long.MIN_VALUE;
        Set<NamedThread> namedThreads=activeNamedThreads();
        Set<Long> ids=new TreeSet<>();
        for(NamedThread namedThread:namedThreads) {
            ids.add(namedThread.getId());
            min=Math.min(min,namedThread.getId());
            max=Math.max(max,namedThread.getId());
        }
        Logging.mainLogger.info(ids.size()+" ids: "+ids);
    }
    // too much printing here - fix!
    // namedThread:activeNamedThreads())
    public int interruptNamedThreads(String string) {
        Set<Thread> joined=new LinkedHashSet<>();
        for(NamedThread namedThread:activeNamedThreads()) if(!namedThread.getState().equals(Thread.State.TERMINATED)) {
            if(namedThread.runnable instanceof Stopable) ((Stopable)namedThread.runnable).setIsStopping();
            IOs.myClose(null,null,null,namedThread,string,null);
            if(namedThread.getState().equals(Thread.State.TERMINATED)) joined.add(namedThread);
            else; // Logging.mainLogger.info("int: "+namedThread.getName()+" "+IO.toString(namedThread)+" "+IO.toString(namedThread)+"was not terminated!");
        } else Logging.mainLogger.info(" 1 already terminated.");
        if(joined.size()>0) Logging.mainLogger.info("joined: "+joined);
        activeNamedThreads().removeAll(joined);
        int remaining=activeNamedThreads().size();
        //if(remaining>0) printNamedThreads("after remove all, "+remaining+" remaining.");
        return remaining;
    }
    public static synchronized void removeAllTerminated() {
        Set<Thread> terminated=new LinkedHashSet<>();
        for(Thread thread:allNamedThreads) if(thread.getState().equals(State.TERMINATED)) terminated.add(thread);
        if(terminated.size()>0) Logging.mainLogger.info("removing from all: "+terminated.size()+" terminated: "+terminated);
        allNamedThreads.removeAll(terminated);
        Logging.mainLogger.info(allNamedThreads.size()+" remaining threads: "+allNamedThreads);
    }
    public static synchronized Set<NamedThread> allActiveNamedThreads() {
        Set<Thread> threads=IOs.activeThreads();
        Set<NamedThread> set=new LinkedHashSet<>();
        for(Thread thread:threads) if(thread instanceof NamedThread) set.add((NamedThread)thread);
        return set;
    }
    public static synchronized int removeLeftoverThreads(Set<Thread> initialThreads) {
        int ids=NamedThreadGroup.ids;
        Set<NamedThread> leftover=new LinkedHashSet<>(); // from a previous test
        for(Thread thread:initialThreads) if(thread instanceof NamedThread) if(((NamedThread)thread).id<=ids) { // maybe bogus?
            leftover.add((NamedThread)thread);
        } else Logging.mainLogger.info("badness: "+((NamedThread)thread).id+""+ids);
        if(leftover.size()>0) {
            if(removeLeftoverThreads) {
                Logging.mainLogger.info("removing "+leftover.size()+" leftovers: "+leftover);
                initialThreads.removeAll(leftover);
            }
        } else Logging.mainLogger.info("no leftover threads.");
        return ids;
    }
    public static int checkThreads(int ids0,Set<Thread> initialThreads,Set<Thread> finalThreads,boolean quiet) {
        boolean verbose=false; // turn this back on
        int ids=NamedThreadGroup.ids;
        if(initialThreads.equals(finalThreads)) return 0;
        Set<Thread> extraThreads=new LinkedHashSet<>(finalThreads);
        Set<Thread> terminated=new LinkedHashSet<>();
        Set<Thread> notTerminated=new LinkedHashSet<>();
        if(extraThreads.size()>0) {
            for(Thread thread:extraThreads)
                if(thread instanceof NamedThread) if(thread.getState().equals(State.TERMINATED)) terminated.add(thread);
                else notTerminated.add(thread);
            if(notTerminated.size()>0&&!quiet) Logging.mainLogger.info("check threads not terminted: "+notTerminated);
        }
        extraThreads.removeAll(terminated);
        Set<Thread> fewerThreads=new LinkedHashSet<>(initialThreads);
        int extra=finalThreads.size()-initialThreads.size();
        extraThreads.removeAll(initialThreads);
        if(extra>0) {
            if(verbose) {
                Logging.mainLogger.info("extra, current ids:"+NamedThreadGroup.ids+", old ids: "+ids0+"\n");
                Logging.mainLogger.info(extra+" extra: "+extraThreads);
            }
            if(extraThreads.size()==0) Logging.mainLogger.info("where did they go?");
            Set<NamedThread> notInRange=new LinkedHashSet<>();
            for(Thread thread:extraThreads) if(thread instanceof NamedThread) {
                if(thread.getState().equals(State.TERMINATED)) {
                    if(verbose) Logging.mainLogger.info("2 already terminated,ignoring: "+thread);
                    if(notTerminated.contains(thread)) Logging.mainLogger.info("was not terminated but is now: "+thread);
                } else {
                    int id=((NamedThread)thread).id;
                    boolean ok=ids0<id&&id<=NamedThreadGroup.ids;
                    if(!ok) notInRange.add((NamedThread)thread);
                }
            } else {
                if(!thread.getName().contains("AWT-EventQueue"));
                else if(!thread.getName().contains("Image Fetcher"));
                else {
                    Logging.mainLogger.info("not a named thread: "+" "+IOs.toString(thread));
                    IOs.stackTrace(10);
                    printNamedThreads(allActiveNamedThreads(),"not a named thread, active named treads: ",false);
                    System.exit(0);
                }
            }
            if(notInRange.size()>0); //Logging.mainLogger.info("not in range: "+ids0+":"+NamedThreadGroup.ids+" "+notInRange);
        } else if(extra<0) {
            fewerThreads.removeAll(finalThreads);
            if(verbose) { Logging.mainLogger.info("fewer: \n"); Logging.mainLogger.info(String.valueOf(fewerThreads)); }
            int newExtra=finalThreads.size()-initialThreads.size();
            if(newExtra!=0) Logging.mainLogger.info("extra size: "+extraThreads);
            if(finalThreads.equals(initialThreads)) Logging.mainLogger.info("extra is not equal!");
        }
        int newExtra=extraThreads.size()>=0?extraThreads.size():-fewerThreads.size();
        if(newExtra>0) {
            if(!quiet) Logging.mainLogger.info("new extra final threads: "+newExtra+" "+extraThreads);
        } else if(newExtra<0) Logging.mainLogger.info("negative? new extra final threads: "+newExtra);
        return newExtra;
    }
    public static String name(Thread thread) { return thread!=null?thread.getName():"null"; }
    public static Thread findThread(String name) {
        Set<Thread> threads=IOs.activeThreads();
        Thread target=null;
        for(Thread thread:threads) {
            boolean startWatchdog=true; // ???????
            if(startWatchdog&&thread.getName().equals(name)) { target=thread; break; }
        }
        return target;
    }
    public static Set<Thread> getFinalThreads(int ids0,Set<Thread> initialThreads) {
        if(initialThreads==null) {
            Logging.mainLogger.info("initial threads is null!");
            initialThreads=Collections.emptySet();
        }
        Set<Thread> finalThreads=IOs.activeThreads();
        Et et=new Et();
        int max=10;
        int i=0,n=checkThreads(ids0,initialThreads,finalThreads,false);
        if(n>=0) {
            for(i=0;i<max;++i) {
                GTPBackEnd.sleep2(2);
                n=checkThreads(ids0,initialThreads,finalThreads,true);
                if(n==0) {
                    if(i>0) Logging.mainLogger.info("ct final check threads ok after "+i+" iterations and "+et);
                    break;
                }
            }
            if(i==max) Logging.mainLogger.info(n+" nre extra. ct max iterations exceeded");
        } else Logging.mainLogger.info("ct no new extra thread(s) after "+i+" iteration at "+et);
        return finalThreads;
    }
    public static synchronized NamedThread createNamedThread(long gameId,Runnable runnable,String name) {
        if(name==null||name.length()==0) {
            name="null";
            //Exception e=new Exception(); e.printStackTrace();
        }
        NamedThreadGroup namedThreadGroup=groupIdToNamedThreadGroup.get(gameId);
        if(namedThreadGroup==null) {
            namedThreadGroup=new NamedThreadGroup(gameId);
            groupIdToNamedThreadGroup.put(gameId,namedThreadGroup);
        }
        NamedThread namedThread=namedThreadGroup.new NamedThread(name,runnable);
        // maybe constructor can add it in?
        namedThreadGroup.addNamedThread(namedThread);
        if(ColorLogs.containsKey(namedThread.getName())==null) {
            // the above is very broken.
            Logging.mainLogger.info("'"+namedThread+"' can not find color for this named thread!");
            //IO.stackTrace(8);
        }
        return namedThread;
    }
    public static void printNamedThreads(Set<NamedThread> threads,String name,boolean printAll) {
        if(name!=null&&threads.size()>0) Logging.mainLogger.info(name+" "+threads.size()+" threads:");
        int i=0,n=4;
        for(Thread thread:threads) {
            Logging.mainLogger.info(String.valueOf(thread));
            ++i;
            if(!printAll&&i>n) { Logging.mainLogger.info((threads.size()-n)+" more ..."); break; }
        }
    }
    public static synchronized void printNamedThreads(long groupId,String string,boolean printAll) {
        // these have side effects. fix!
        NamedThreadGroup namedThreadGroup=groupIdToNamedThreadGroup.get(groupId);
        if(namedThreadGroup!=null) printNamedThreads(namedThreadGroup.activeNamedThreads(),
                "group: "+groupId+" "+string+" activeNamedThreads()",printAll);
        else Logging.mainLogger.info("no map entry for key: "+groupId);
    }
    public void stopAllStopables_() {
        for(NamedThread namedThread:namedThreadsWithRunnables) {
            if(namedThread.runnable instanceof Stopable) try {
                ((Stopable)namedThread.runnable).stop();
            } catch(IOException|InterruptedException e) {
                e.printStackTrace();
            }
            else throw new RuntimeException("not a stopable!");
        }
        interruptNamedThreads("sas interrupt"); // was not commented out
    }
    private static void stopGroup(NamedThreadGroup namedThreadGroup,long groupId) throws InterruptedException {
        if(namedThreadGroup!=null) {
            if(namedThreadGroup.namedThreadsWithRunnables.size()>0) {
                namedThreadGroup.stopAllStopables_();
            } else Logging.mainLogger.info("empty "+groupId+" "+namedThreadGroup);
        } else Logging.mainLogger.info("no map entry for key: "+groupId);
    }
    public static void stopAllStopables(long groupId) throws InterruptedException {
        stopGroup(groupIdToNamedThreadGroup.get(groupId),groupId);
    }
    public static void stopAllStopables() throws InterruptedException {
        for(Long key:groupIdToNamedThreadGroup.keySet()) stopGroup(groupIdToNamedThreadGroup.get(key),key);
    }
    public static int printNamedThreadGroups(boolean print) {
        // not necessarily active
        boolean once=false;
        int n=0;
        for(Long id:groupIdToNamedThreadGroup.keySet()) {
            Set<NamedThread> threads=groupIdToNamedThreadGroup.get(id).namedThreadsWithRunnables;
            if(threads!=null&&threads.size()>0) {
                if(!once) { once=true; if(print) Logging.mainLogger.info("all threads groups:"); }
                n+=threads.size();
                if(print) {
                    System.out
                            .println("group id: "+id+" has "+threads.size()+" threads, threadsAndRunnables: "+threads);
                }
                printNamedThreads(id,"group id: "+id,print);
            }
        }
        //if(n==0) Logging.mainLogger.info("no named threads");
        return n;
    }
    public static void printThraedsAtEnd() {
        boolean print=true;
        Logging.mainLogger.info("ptae <<<<<<<<<<<<<<<<<<<<<<");
        int n;
        if((n=printNamedThreadGroups(print))>0) { Logging.mainLogger.info("badness2!"); }
        if(n>0) {
            int remaining=0;
            for(Long key:groupIdToNamedThreadGroup.keySet()) {
                remaining+=groupIdToNamedThreadGroup.get(key).interruptNamedThreads(n+" ptae");
            }
            if(remaining>0) Logging.mainLogger.info("after remove all joined, "+remaining+" remaining.");
            // seems like the above gets rid of the badness!
            if(printNamedThreadGroups(print)>0) Logging.mainLogger.info("still badness2!");
            else Logging.mainLogger.info(" badness2 is gone!");
            int i=0;
            Logging.mainLogger.info("iteration: "+i);
            while(printNamedThreadGroups(print)>0) {
                GTPBackEnd.sleep2(2);
                if(printNamedThreadGroups(print)>0);
                else Logging.mainLogger.info("no badness!");
                if(++i>5) { Logging.mainLogger.info("giving up after "+i+" iterations."); break; }
                Logging.mainLogger.info("iteration: "+i);
            }
            n=printNamedThreadGroups(print);
        }
        Logging.mainLogger.info(n+" named threads ptae.");
        Logging.mainLogger.info("ptae >>>>>>>>>>>>>>>>>>>>>>");
        Logging.mainLogger.info(allNamedThreads.size()+"/"+NamedThreadGroup.ids);
        printNamedThreads(allNamedThreads,"all named threads",true);
        IOs.printThreads(IOs.activeThreads(),"active at end",true);
    }
    public final long groupId; // usually a game id.
    public final Set<NamedThread> namedThreadsWithRunnables=new LinkedHashSet<>();
    public static final long groupZero=0;
    public static final long standAlone=-1;
    public static int ids;
    public static boolean removeLeftoverThreads=true;
    public static final Set<NamedThread> allNamedThreads=new LinkedHashSet<>();
    //public static final Map<String,Set<NamedThread>> nameToNamedThreadSet=new TreeMap<>();
    public static final Map<String,Integer> nameToColorIndex=new LinkedHashMap<>();
    public static final Map<Long,NamedThreadGroup> groupIdToNamedThreadGroup=new TreeMap<>();
    // just sets of named threads associated with a name.
    // nothing to do with named thread group.
    // currently there is one set named allKey.
    static {
        nameToColorIndex.put("server",0);
        nameToColorIndex.put("game",1);
        nameToColorIndex.put("recorder",2);
        nameToColorIndex.put("black",3);
        nameToColorIndex.put("white",4);
        nameToColorIndex.put("model",5);
    }
}
