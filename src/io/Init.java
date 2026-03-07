package io;
import io.Logging;
import java.io.*;
import java.util.logging.*;
import server.NamedThreadGroup;
import com.tayek.util.core.Et;
import com.tayek.util.log.ConsoleAndLogColors;
import com.tayek.util.log.Sequence;
// https://developer.ibm.com/tutorials/j-introducing-junit5-part2-vintage-jupiter-extension-model/
// 3 calls to stack trace, but that seems ok.
public enum Init {
	first;
	{ System.out.println("after first"); }
	private void once() {
		IOs.stackTrace(10);
		System.out.println("enter once");
		if(once) return;
		else once=true;
		System.out.println("4 once initialize");
		String forground=System.getProperty("foreground");
		System.out.println("fotreground: "+forground);
		ConsoleAndLogColors.blackOrWhite=forground==null?ConsoleAndLogColors.color_BLACK:ConsoleAndLogColors.color_WHITE;
		Sequence.blackOrWhite=forground!=null?Sequence.black:Sequence.white;
		Sequence.setNameToColorIndex(NamedThreadGroup.nameToColorIndex);
		System.out.println("blackOrWhite: "+Sequence.blackOrWhite);
		LogManager.getLogManager().reset();
		// maybe omit this stuff below?
		if(true) {
			Logging.setUpLogging();
			Logging.setLevels(Logging.initialLoggingLevel);
			// do this last or level is null!
			// Logging.parserLogger.setLevel(defaultParserLoggerLevel);
		}
		// Logging.mainLogger.info(Parameters.topologies);
		System.out.println("exit once");
	}
	public void twice() {
		System.out.println("enter twice");
		once();
		et.reset();
		System.out.println("exit twice()");
	}
	public abstract static class Main {
		public static void main(String[] argument) {
			//System.out.println("1.5 Init.Main.main(), first: "+first);
		}
	}
	private Init() {
		System.out.println("enter constructor");
		IOs.stackTrace(10);
		out=System.out;
		err=System.err;
		// once();
		// twice();
	}
	public static void main(String[] args) {
		System.out.println("enter Init.main()");
		first.twice();
		System.out.println("exit Init.main()");
	}
	public final Et et=new Et();
	public final PrintStream out,err;
	public boolean once;
	static boolean verbose=true;
	{
		System.out.println("instance init");
		IOs.stackTrace(10);
	}
	static {
		System.out.println("static init");
		IOs.stackTrace(10);
	}
	public static final String notTerminated="notTerminated";
	/*static*/ Level defaultParserLoggerLevel=Level.OFF;
}
