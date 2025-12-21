package io;
import java.util.Set;
import controller.GTPBackEnd;
import utilities.Et;
public class Watchdog extends Thread {
    Watchdog(Thread threadToWatch,int time) {
        if(threadToWatch==null) {
            Logging.mainLogger.severe(getClass().getName()+"watchdo thread is null!");
            throw new RuntimeException("thread to watch is null!");
        }
        System.out.println("watch dog for: "+threadToWatch);
        this.threadToWatch=threadToWatch;
        this.time=time;
        setName("watchdog");
        done=false;
        reason="no reason";
        Logging.mainLogger.severe(this+" w constructed.");
    }
    public String reason() { return reason; }
    @Override public void run() {
        Logging.mainLogger.severe(this+" w enter run().");
        System.out.println(this+" w started "+Init.first.et.etms());
        Et watchdogEt=new Et();
        while(watchdogEt.etms()<=time&&!done&&!isInterrupted()) try {
            Thread.sleep(10);
        } catch(InterruptedException e) {
            reason="InterruptedException";
            Logging.mainLogger.severe(this+" was interrupted.");
        }
        if(!done) {
            Logging.mainLogger.severe(this+" was interrupted while watching: "+threadToWatch);
            reason="!done.";
            System.out.println("interrupting: "+threadToWatch);
            threadToWatch.interrupt();
            try {
                StackTraceElement[] stackTraceElements=threadToWatch.getStackTrace();
                System.out.println(stackTraceElements.length+" elements.");
                //for(StackTraceElement stackTraceElement:stackTraceElements) System.out.println(stackTraceElement);
                threadToWatch.join(100);
                Logging.mainLogger.severe(this+" w join result: "+threadToWatch.isAlive()+" "+threadToWatch.getState());
                System.out.println("w run "+IOs.toString(threadToWatch));
                Logging.mainLogger.severe(this+" w joined with: "+threadToWatch);
            } catch(InterruptedException e) {
                Logging.mainLogger.severe(this+" w join interrupted.");
                System.exit(1);
            } finally {
                Logging.mainLogger.severe("w finally");
                Set<Thread> threads=IOs.activeThreads();
                for(Thread thread:threads) System.out.println("w run 2 "+IOs.toString(thread));
            }
        } else reason="done.";
        Logging.mainLogger.severe(this+" w exiting run().");
    }
    public static Watchdog watchdog(Thread threadToWatch) {
        Watchdog watchdog=new Watchdog(threadToWatch,GTPBackEnd.longTimeoutTime);
        watchdog.start();
        return watchdog;
    }
    public final Thread threadToWatch; // thread to be interrupted.
    private String reason;
    final int time;
    public Boolean done;
    public static void main(String[] args) {}
}
