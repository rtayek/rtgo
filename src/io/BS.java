package io;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;
import com.tayek.util.core.Et;
import com.tayek.util.log.ConsoleAndLogColors;
import com.tayek.util.log.Sequence;
import server.NamedThreadGroup;
public class BS {
	public synchronized void once() {
		System.out.println("enter once");
		if(!started.get()) {
			System.out.println("starting");
			String forground=System.getProperty("foreground");
			ConsoleAndLogColors.blackOrWhite=forground==null?ConsoleAndLogColors.color_BLACK:ConsoleAndLogColors.color_WHITE;
			Sequence.blackOrWhite=forground!=null?Sequence.black:Sequence.white;
			Sequence.setNameToColorIndex(NamedThreadGroup.nameToColorIndex);
			LogManager.getLogManager().reset();
			// maybe omit this stuff below?
			if(true) {
				Logging.setUpLogging();
				Logging.setLevels(Logging.initialLoggingLevel);
				// do this last or level is null!
				// Logging.parserLogger.setLevel(defaultParserLoggerLevel);
			}
			started.set(true);
		}
		System.out.println("once is executing");
		// twice(); maybe not a good idea.
		System.out.println("exit once");
	}
	public synchronized void twice() {
		System.out.println("enter twice");
		if(!started.get()) once();
		System.out.println("twice is executing");
		et.reset();
		System.out.println("exiy twice");
	}
	private BS() {
		out=System.out;
		err=System.err;
	}
	public static void main(String[] args) {
		BS bs=new BS();
		// bs.once();
		System.out.println("------------");
		bs.twice();
	}
	static {
		System.out.println("static init");
	}
	{
		System.out.println("instance init");
	}
	public final Et et=new Et();
	public final PrintStream out,err;
	private final AtomicBoolean started=new AtomicBoolean();
	public static final ArrayList<String> testsRun=new ArrayList<>();
}
