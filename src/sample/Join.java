package sample;
import io.Logging;
public class Join {
    static class theThread extends Thread {
        public final static int THREADPASS=0;
        public final static int THREADFAIL=1;
        public final static int THREADCANCELED=2;
        int _status;
        public int status() { return _status; }
        public theThread() { _status=THREADFAIL; }
        @Override public void run() {
            Logging.mainLogger.info("Thread: Entered\n");
            try {
                while(!done) {
                    Logging.mainLogger.info("Thread: Looping or long running request\n");
                    try {
                        Thread.sleep(1000);
                    } catch(InterruptedException e) {
                        Logging.mainLogger.info("Thread: Join run() sleep interrupted\n");
                    }
                }
            } catch(ThreadDeath d) {
                _status=THREADCANCELED;
            }
        }
        boolean done;
    }
    public static void main(String argv[]) {
        Logging.mainLogger.info(String.valueOf(Thread.activeCount()));
        Logging.mainLogger.info("Entered the testcase\n");
        Logging.mainLogger.info("Create a thread\n");
        theThread t=new theThread();
        Logging.mainLogger.info("Start the thread\n");
        t.start();
        Logging.mainLogger.info("Wait a bit until we 'realize' the thread needs to be canceled\n");
        try {
            Thread.sleep(3000);
        } catch(InterruptedException e) {
            Logging.mainLogger.info("Join main sleep interrupted\n");
        }
        t.done=true;
        ;
        Logging.mainLogger.info("Wait for the thread to complete\n");
        try {
            t.join();
        } catch(InterruptedException e) {
            Logging.mainLogger.info("Join interrupted\n");
        }
        Logging.mainLogger.info("Thread status indicates it was canceled\n");
        if(t.status()!=theThread.THREADCANCELED) { Logging.mainLogger.info("Unexpected thread status\n"); }
        Logging.mainLogger.info(String.valueOf(t.status()));
        Logging.mainLogger.info("Testcase completed\n");
        Logging.mainLogger.info(String.valueOf(Thread.activeCount()));
    }
}
