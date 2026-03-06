package io;
import io.Logging;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.tayek.util.core.Et;
// https://developer.ibm.com/tutorials/j-introducing-junit5-part2-vintage-jupiter-extension-model/
// 3 calls to stack trace, but that seems ok.
public enum Init {
	first;
	private void once() {
		// IO.stackTrace(10);
		if(once) return;
		else once=true;
		Bootstrap.first.once();
		// Logging.mainLogger.info(Parameters.topologies);
		Logging.mainLogger.info("once");
	}
	public void twice() {
		once();
		if(verbose) Logging.mainLogger.info("5 enter twice()");
		et.reset();
		if(verbose) Logging.mainLogger.info("exit twice()");
	}
	public abstract static class Main {
		public static void main(String[] argument) {
			if(verbose) Logging.mainLogger.info("1.5 Init.Main.main(), first: "+first); 
			// needs to be here.
		}
		static {
			if(verbose) Logging.mainLogger.info("1 Init.Main.main static init");
		}
		static {
			if(verbose) Logging.mainLogger.info("static init Init.Main");
		}
	}
	private Init() {
		// IO.stackTrace(10);
		out=System.out;
		err=System.err;
		once();
		twice();
	}
	// put this stuff below into the named thread class.
	// and keep this init code as small as possible.
	public void initiaizeTests() {
		testLifecycle.initiaizeTests(this);
	}
	public void wrapupTests_() {
		testLifecycle.wrapupTests_(this);
	}
	public synchronized void wrapupTests() {
		testLifecycle.wrapupTests(this);
	}
	public void lastPrint() {
		testLifecycle.lastPrint(this);
	}
	public static void init(String[] argument) {
		Logging.mainLogger.info("1.5 Init.Main.main(), first: "+first); // needs
																		// to be
																		// here
	}
	public static void main(String[] args) {
		first.twice();
		System.out.println("-----------");
		Logging.mainLogger.setLevel(Level.ALL);
		System.out.println("enter Init.main()");
		Logging.mainLogger.info("enter Init.main()");
		// Logging.mainLogger.info(first);
		first.initiaizeTests();
		first.initiaizeTests();
		first.wrapupTests();
		first.wrapupTests();
		Logging.mainLogger.info("exit Init.main()");
	}
	public final Et et=new Et();
	public int counter;
	boolean wasWrapupTestsCalled;
	public boolean suiteControls;
	public final PrintStream out,err;
	private SortedMap<String,Object> stuff=new TreeMap<>();
	public final ArrayList<String> testsRun=new ArrayList<>();
	private final TestLifecycle testLifecycle=new TestLifecycle();
	public boolean once;
	static boolean verbose=true;
	static {
		if(verbose) Logging.mainLogger.info("? Init.main static init");
	}
	public static final String notTerminated="notTerminated";
	static int maxCounter;
	/*static*/ Level defaultParserLoggerLevel=Level.OFF;
}
