package io;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;
import com.tayek.util.log.ConsoleAndLogColors;
import com.tayek.util.log.Sequence;
import server.NamedThreadGroup;
/** Early process bootstrap. Prefer calling {@code Bootstrap.first.twice()} as
 * the first line in main(). */
public final class Bootstrap {
	public synchronized void once() {
		if(started.get()) return;
		applyColorConfiguration();
		LogManager.getLogManager().reset();
		Logging.setUpLogging();
		Logging.setLevels(Logging.initialLoggingLevel);
		started.set(true);
		if(verbose) {
			Logging.mainLogger.info("4 once initialize");
			Logging.mainLogger.info("fotreground: "+foregroundProperty);
			Logging.mainLogger.info("blackOrWhite: "+Sequence.blackOrWhite);
		}
	}
	public synchronized void twice() {
		once();
		if(verbose) Logging.mainLogger.info("5 enter twice()");
		if(verbose) Logging.mainLogger.info("exit twice()");
	}
	// Compatibility wrappers for existing callers.
	public static void start() {
		first.once();
	}
	public static boolean isStarted() {
		return first.started.get();
	}
	private void applyColorConfiguration() {
		foregroundProperty=System.getProperty("foreground");
		ConsoleAndLogColors.blackOrWhite=foregroundProperty==null?ConsoleAndLogColors.color_BLACK:ConsoleAndLogColors.color_WHITE;
		Sequence.blackOrWhite=foregroundProperty!=null?Sequence.black:Sequence.white;
		Sequence.setNameToColorIndex(NamedThreadGroup.nameToColorIndex);
	}
	private Bootstrap() {}
	public static void main(String[] argument) {
		new Bootstrap().twice();
	}
	public static final Bootstrap first=new Bootstrap();
	public static boolean verbose=true;
	private final AtomicBoolean started=new AtomicBoolean();
	private String foregroundProperty;
}
