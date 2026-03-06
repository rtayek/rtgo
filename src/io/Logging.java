package io;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;
import com.tayek.util.log.JulLogging;
import com.tayek.util.log.MyFormatter;
import com.tayek.util.misc.Tee;
import sgf.ASimpleFormatter;
public class Logging {
	private static void addFileHandler(Logger logger,Formatter formatter) {
		try {
			File dir=new File("logs");
			if(!dir.exists()) dir.mkdirs();
			if(!dir.exists()) throw new RuntimeException(dir+" not found!");
			JulLogging.addFileHandler(logger,"logs/log",50_000,10,false,formatter,Level.ALL);
		} catch(SecurityException|IOException e) {
			throw new RuntimeException("failed to configure logger '"+logger.getName()+'\'',e);
		}
	}
	public static void setupLogger(Logger logger,Formatter formatter) {
		if(logger==null) {
			System.out.println("logger is null!");
			return;
		}
		loggerNames.add(logger.getName());
		JulLogging.clearHandlers(logger);
		logger.setUseParentHandlers(false);
		JulLogging.addConsoleHandler(logger,formatter,Level.ALL);
		addFileHandler(logger,formatter);
	}
	public static void deleteLogfiles() {
		File dir=new File("logs");
		if(dir.exists()) {
			File[] files=dir.listFiles();
			if(files==null) return;
			System.out.println("deleting "+files.length+" files");
			for(File file:files) {
				if(file.isFile()) {
					// System.out.println("deleting: "+file);
					boolean ignored=file.delete();
					// System.out.println(ignored);
				}
			}
		}
	}
	public static void setLevels(Level level) {
		for(String name:loggerNames)
			Logger.getLogger(name).setLevel(level);
	}
	public static void setUpLogging() {
		// Logger global=Logger.getGlobal();
		// Logging.setupLogger(global,new ASimpleFormatter());
		// setupLogger(logger,new CustomFormatter());
		// global.info("god constructed");
		// getting called too many times
		// each time adds a log message!
		deleteLogfiles();
		//setupLogger(mainLogger,new MyFormatter(useColor));
		setupLogger(mainLogger,new ASimpleFormatter()); // test
		setupLogger(serverLogger,new MyFormatter(useColor));
		setupLogger(gameLogger,new MyFormatter(useColor));
		setupLogger(parserLogger,new ASimpleFormatter());
		setLevels(initialLoggingLevel); // do this last or level is null!
	}
	// ---------- diagnostics ----------
	private static void log(Logger l) {
		System.out.println();
		logEachLevel(l);
	}
	static void logEachLevel(Logger logger) {
		for(int i=0;i<levels.length;i++) {
			System.out.println("logger level is set to: "+levels[i]);
			logger.setLevel(levels[i]);
			for(int j=0;j<levels.length;j++) {
				System.out.print(" [ "+levels[j]+" ");
				logger.log(levels[j],levels[i].toString());
				System.out.println(" "+levels[j]+" ]");
			}
		}
	}
	public static void toString(PrintStream ps,Logger logger) {
		ps.println("level: "+logger.getLevel());
		Handler[] handlers=logger.getHandlers();
		ps.println("handlers "+Arrays.asList(handlers));
		for(Handler handler:handlers)
			ps.println(handler.getLevel());
		Logger parent=logger.getParent();
		ps.println("parent: "+parent+" "+parent.getLevel());
		ps.println("use: "+logger.getUseParentHandlers());
		Handler[] parentHandlers=parent.getHandlers();
		ps.println("parent handlers: "+Arrays.asList(parentHandlers));
		for(Handler handler:parentHandlers)
			ps.println(handler.getLevel());
	}
	public static void main(String[] args) {
		// see jdk/conf/loggging.properties
		// works fine, but uses flushingStreamHandler(tee).
		// how to make this work when the loggers are already setup?
		System.out.println("enter main");
		LogManager.getLogManager().reset();
		Logger logger=null;
		// both ways work because they get the same system.err
		if(false) {
			logger=Logger.getLogger("frog"); // does not have a console handler
			// one line of log comes out.
		} else {
			Logging.setUpLogging();
			logger=Logging.mainLogger; // has a console handler
			// two lines of log comes out.
		}
		toString(System.out,logger);
		logger.info("hello");
		System.out.println("---");
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		Tee tee=new Tee(byteArrayOutputStream);
		// tee.addOutputStream(System.out);
		System.out.println("out");
		System.err.println("err");
		Handler handler=JulLogging.flushingStreamHandler(tee); // works fine
		handler.setFormatter(new MyFormatter(useColor));
		handler.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
		toString(System.out,logger);
		logger.severe("log message");
		System.out.println("baos: "+byteArrayOutputStream);
		if(true) return;
		for(int i=0;i<levels.length;++i) {
			System.out.print(levels[i]+" "+levels[i].intValue());
		}
		System.out.println();
		useColor=false;
		System.out.println("mainLogger");
		setupLogger(mainLogger,new MyFormatter(useColor));
		System.out.println("logging main() <<<<<<<<<<<<<<<<");
		log(mainLogger);
		System.out.println("logging main() >>>>>>>>>>>>>>>>");
		/*
		System.out.println("serverLogger");
		log(serverLogger);
		System.out.println("gameLogger");
		log(gameLogger);
		System.out.println("parserLogger");
		log(parserLogger);
		 */
	}
	public static boolean useColor=true;
	public static final Level initialLoggingLevel=Level.INFO;
	public static final Set<String> loggerNames=new TreeSet<>();
	// public static final Logger globalLogger=Logger.getGlobal();
	public static final Logger mainLogger=Logger.getLogger(Logging.class.getName());
	public static final Logger serverLogger=Logger.getLogger(server.GoServer.class.getName());
	public static final Logger parserLogger=Logger.getLogger(sgf.Parser.class.getName());
	public static final Logger gameLogger=Logger.getLogger(game.Game.class.getName());
	public static final Level levels[]= {Level.OFF,Level.SEVERE,Level.WARNING,Level.INFO,Level.CONFIG,Level.FINE,Level.FINER,Level.FINEST,Level.ALL};
	static {
		// Init.Main.main(null);
	}
}
