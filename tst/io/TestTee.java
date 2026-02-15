package io;
import java.io.OutputStream;
import java.io.PrintStream;
import com.tayek.util.misc.Tee;
class TestTee extends Tee {
    TestTee(OutputStream out,ConsoleStreams console) {
        super(out);
        this.console=console;
    }
    @Override public PrintStream setOut() {
        previousOut=console.out;
        console.out=printStream;
        return previousOut;
    }
    @Override public PrintStream restoreOut() {
        PrintStream previous=console.out;
        if(previousOut!=null) console.out=previousOut;
        return previous;
    }
    @Override public PrintStream setErr() {
        previousErr=console.err;
        console.err=printStream;
        return previousErr;
    }
    @Override public PrintStream restoreErr() {
        PrintStream previous=console.err;
        if(previousErr!=null) console.err=previousErr;
        return previous;
    }
    private final ConsoleStreams console;
}
