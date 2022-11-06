package io;
import java.io.IOException;
import java.net.ServerSocket;
public class Extra { // unused stuff thay may be useful later
    public static void main(String[] args) {
    }
    static void closeServer(ServerSocket serverSocket,Thread thread) {
        // this was in request listener.
        // maybe we can use some of the code.
        if(serverSocket==null) {
            Logging.mainLogger.warning("attempting to close null server socket with thread: "+thread);
            return;
        }
        if(!serverSocket.isClosed()) { // otherwise join may hang
            Logging.mainLogger.info("closing server socket with thread: "+thread);
            try {
                serverSocket.close();
            } catch(IOException e) {
                Logging.mainLogger.info("caught "+e+" when trying to close server socket with thread: "+thread);
            }
        }
        if(thread==null) { Logging.mainLogger.info("attempting to shut down null thread!"); return; }
        Logging.mainLogger.fine("shutting down server thread: "+thread);
        Logging.mainLogger.fine(thread+" is interrupted: "+thread.isInterrupted());
        if(!thread.isInterrupted()) {
            Logging.mainLogger.fine(thread+" is alive, interrupting.");
            thread.interrupt();
            Logging.mainLogger.fine(thread+"after interrupting");
        }
        try {
            Logging.mainLogger.fine("joining with: "+thread);
            thread.join();
            Logging.mainLogger.fine("server socket is shut down: "+thread);
        } catch(InterruptedException e) {
            Logging.mainLogger.warning(thread+" join interruped");
        } finally {
            // Model.printThreads();
        }
    }
}
