package sample;
public class Join {
    static class theThread extends Thread {
        public final static int THREADPASS=0;
        public final static int THREADFAIL=1;
        public final static int THREADCANCELED=2;
        int _status;
        public int status() { return _status; }
        public theThread() { _status=THREADFAIL; }
        @Override public void run() {
            System.out.print("Thread: Entered\n");
            try {
                while(!done) {
                    System.out.print("Thread: Looping or long running request\n");
                    try {
                        Thread.sleep(1000);
                    } catch(InterruptedException e) {
                        System.out.print("Thread: Join run() sleep interrupted\n");
                    }
                }
            } catch(ThreadDeath d) {
                _status=THREADCANCELED;
            }
        }
        boolean done;
    }
    public static void main(String argv[]) {
        System.out.println(Thread.activeCount());
        System.out.print("Entered the testcase\n");
        System.out.print("Create a thread\n");
        theThread t=new theThread();
        System.out.print("Start the thread\n");
        t.start();
        System.out.print("Wait a bit until we 'realize' the thread needs to be canceled\n");
        try {
            Thread.sleep(3000);
        } catch(InterruptedException e) {
            System.out.print("Join main sleep interrupted\n");
        }
        t.done=true;
        ;
        System.out.print("Wait for the thread to complete\n");
        try {
            t.join();
        } catch(InterruptedException e) {
            System.out.print("Join interrupted\n");
        }
        System.out.print("Thread status indicates it was canceled\n");
        if(t.status()!=theThread.THREADCANCELED) { System.out.print("Unexpected thread status\n"); }
        System.out.print(t.status());
        System.out.print("Testcase completed\n");
        System.out.println(Thread.activeCount());
    }
}