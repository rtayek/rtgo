package io;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.LogManager;
import com.tayek.util.log.ColorLogs;
import com.tayek.util.log.Sequence;
import server.NamedThreadGroup;
/** Early process bootstrap. Prefer calling {@code Bootstrap.first.twice()} as
 * the first line in main(). */
public final class Bootstrap {
	public enum LogMode {
		QUIET(Level.WARNING),VERBOSE(Level.INFO);
		LogMode(Level level) {
			this.level=level;
		}
		public Level level() {
			return level;
		}
		private final Level level;
	}
	public synchronized void once() {
		once(modeFromProperty());
	}
	public synchronized void once(LogMode mode) {
		if(started.get()) {
			Logging.setLevels(mode.level());
			currentMode=mode;
			return;
		}
		applyColorConfiguration();
		LogManager.getLogManager().reset();
		Logging.setUpLogging();
		Logging.setLevels(mode.level());
		currentMode=mode;
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
	public synchronized void twice(LogMode mode) {
		once(mode);
		if(verbose) Logging.mainLogger.info("5 enter twice()");
		if(verbose) Logging.mainLogger.info("exit twice()");
	}
	// Compatibility wrappers for existing callers.
	public static void start() {
		first.once();
	}
	public static void start(LogMode mode) {
		first.once(mode);
	}
	public static LogMode modeFromProperty() {
		String value=System.getProperty(LOG_MODE_PROPERTY,DEFAULT_MODE.name());
		String normalized=value==null?"":value.trim().toLowerCase(Locale.ROOT);
		if("quiet".equals(normalized)) return LogMode.QUIET;
		if("verbose".equals(normalized)) return LogMode.VERBOSE;
		return DEFAULT_MODE;
	}
	public static LogMode mode() {
		return first.currentMode;
	}
	public static boolean isStarted() {
		return first.started.get();
	}
	private void applyColorConfiguration() {
		foregroundProperty=System.getProperty("foreground");
		ColorLogs.blackOrWhite=foregroundProperty==null?ColorLogs.color_BLACK:ColorLogs.color_WHITE;
		Sequence.blackOrWhite=foregroundProperty!=null?Sequence.black:Sequence.white;
		Sequence.setNameToColorIndex(NamedThreadGroup.nameToColorIndex);
	}
	private Bootstrap() {}
	public static void main(String[] argument) {
		new Bootstrap().twice();
	}
	public static final Bootstrap first=new Bootstrap();
	public static final String LOG_MODE_PROPERTY="rtgo.log";
	public static final LogMode DEFAULT_MODE=LogMode.VERBOSE;
	public static boolean verbose=true;
	private final AtomicBoolean started=new AtomicBoolean();
	private volatile LogMode currentMode=DEFAULT_MODE;
	private String foregroundProperty;
}
