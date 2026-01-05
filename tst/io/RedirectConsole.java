package io;
// this is not used, but it may be useful.
// so take a look at it sometime?
import java.io.*;
import java.util.logging.*;
/**
 * Console redirection utility.
 *
 * Replaces console streams with
 * printstreams that log all writes when flushed.
 */
public class RedirectConsole {
    /** Logger to handle writes to console err. */
    private static final Logger CONSOLE_ERR_LOGGER=Logger.getLogger("console.err");
    /** Logger to handle writes to console out. */
    private static final Logger CONSOLE_OUT_LOGGER=Logger.getLogger("console.out");
    /** The previous console err stream. */
    private static PrintStream previousErr;
    /** The previous console out stream. */
    private static PrintStream previousOut;
    /**
     * Redirect console streams to java.util.logging.Logger objects.
     *
     * Console out is redirected to logger "console.out". Console err is redirected
     * to logger "console.err".
     *
     * Use the cancel method to undo this redirection.
     */
    public static void redirect(ConsoleStreams console) {
        if(previousErr!=null||previousOut!=null) { cancel(console); }
        previousOut=console.out;
        console.out=new LoggerPrintStream("STDOUT",CONSOLE_OUT_LOGGER);
        previousErr=console.err;
        console.err=new LoggerPrintStream("STDERR",CONSOLE_ERR_LOGGER);
    }
    /**
     * Undo a redirection previously setup using redirect().
     *
     * Restores the console streams to their state before redirect was
     * called.
     */
    public static void cancel(ConsoleStreams console) {
        if(previousOut!=null) {
            // flush any pending output
            console.out.flush();
            // restore previous output stream
            console.out=previousOut;
            previousOut=null;
        }
        if(previousErr!=null) {
            // flush any pending output
            console.err.flush();
            // restore previous error stream
            console.err=previousErr;
            previousErr=null;
        }
    }
    /**
     * A PrintStream that writes messages to a Logger object.
     */
    private static class LoggerPrintStream extends PrintStream {
        /** Name for output, prepended to LogRecord. */
        private String name;
        /** Logger used when flush is called. */
        private Logger logger;
        public LoggerPrintStream(final String name,final Logger logger) {
            // true is for autoflush
            super(new ByteArrayOutputStream(),true);
            this.name=name;
            this.logger=logger;
        }
        /**
         * Override to force synchronization.
         */
        @Override public synchronized void write(final byte[] b) throws IOException { super.write(b); }
        /**
         * Override to force synchronization.
         */
        @Override public synchronized void write(final int b) { super.write(b); }
        /**
         * Override to force synchronization.
         */
        @Override public synchronized void write(final byte[] buf,final int off,final int len) {
            super.write(buf,off,len);
        }
        /**
         * Flush forces message to be written to log file.
         */
        @Override public synchronized void flush() {
            ByteArrayOutputStream baos=(ByteArrayOutputStream)out;
            try {
                logger.log(new LogRecord(Level.INFO,this.name+" "+baos.toString().trim()));
            } finally {
                // tried to write at least, clear buffer...
                out=new ByteArrayOutputStream();
            }
        }
    }
}
