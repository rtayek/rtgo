package io;
import java.io.PrintStream;
class ConsoleStreams {
    ConsoleStreams(PrintStream out,PrintStream err) { this.out=out; this.err=err; }
    PrintStream out;
    PrintStream err;
}
