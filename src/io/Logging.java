package io;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;
import sgf.ASimpleFormatter;
public class Logging {
    public static StreamHandler flushingStreamHandler(OutputStream outputStream) {
        StreamHandler streamHandler=new StreamHandler(outputStream,new SimpleFormatter()) {
            @Override public synchronized void publish(final LogRecord record) { super.publish(record); flush(); }
        };
        streamHandler.setLevel(Level.ALL); // Default StdErr Setting
        return streamHandler;
    }
    public static class MyFormatter extends Formatter {
        boolean useSequence=true;
        @Override public String format(LogRecord record) {
            String name=Thread.currentThread().getName();
            if(name.length()==0) System.out.println("thread name is empty!");
            if(name.length()>maxThreadNameLength)
                name=name.substring(0,maxThreadNameLength-3)+'~'+name.substring(name.length()-1);
            String line=String.format(format,record.getSequenceNumber(),name,record.getLevel(),record.getMessage(),
                    record.getSourceClassName()+"."+record.getSourceMethodName()+"()"/*,record.getInstant()*/);
            String coloredLine=useSequence?Sequence.color(line,name):ColorLogs.color(line);
            return useColor?coloredLine:line;
        }
        public static final String format="%05d %"+maxThreadNameLength+"s %7s %32s in %s"/*+" at %s*/+"\n";
    }
    private static void log(Logger l) { System.out.println(); logEachLevel(l); }
    public static void setupLogger(Logger logger,Formatter formatter) {
        if(logger!=null) {
            if(!loggerNames.contains(logger.getName())) {
                loggerNames.add(logger.getName());
                //System.out.println("System.err: "+System.err);
                Handler handler=new ConsoleHandler();
                handler.setLevel(Level.ALL);
                logger.setUseParentHandlers(false);
                handler.setFormatter(formatter);
                logger.addHandler(handler);
                Handler handler2;
                boolean what=true;
                if(what) try {
                    File dir=new File("logs");
                    if(!dir.exists()) dir.mkdirs();
                    if(dir.exists()) {
                        handler2=new FileHandler("logs/log",50_000,10,false);
                        handler2.setLevel(Level.ALL);
                        logger.addHandler(handler2);
                    } else throw new RuntimeException(dir+" not found!");
                } catch(SecurityException|IOException e) {
                    e.printStackTrace();
                }
            }
        } else System.out.println("logger is null!");
    }
    public static void deleteLogfiles() {
        File dir=new File("logs");
        if(dir.exists()) {
            String[] filenames=dir.list();
            System.out.println("deleting "+filenames.length+" files");
            for(String filename:filenames) {
                //System.out.println("deleting: "+filename);
                boolean file=new File(filename).delete();
                //System.out.println(file);
            }
        }
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
    public static void setLevels(Level level) { for(String name:loggerNames) Logger.getLogger(name).setLevel(level); }
    public static void setUpLogging() {
        // Logger global=Logger.getGlobal();
        // Logging.setupLogger(global,new ASimpleFormatter());
        //setupLogger(logger,new CustomFormatter());
        //global.info("god constructed");
        // getting called too many times
        // each time adds a log message!
        deleteLogfiles();
        setupLogger(mainLogger,new MyFormatter());
        setupLogger(serverLogger,new MyFormatter());
        setupLogger(gameLogger,new MyFormatter());
        setupLogger(parserLogger,new ASimpleFormatter());
        setLevels(initialLoggingLevel); // do this last or level is null!
    }
    public static void toString(Logger logger) { toString(System.out,logger); }
    public static void toString(PrintStream ps,Logger logger) {
        ps.println("level: "+logger.getLevel());
        Handler[] handlers=logger.getHandlers();
        ps.println("handlers "+Arrays.asList(handlers));
        for(Handler handler:handlers) ps.println(handler.getLevel());
        Logger parent=logger.getParent();
        ps.println("parent: "+parent+" "+parent.getLevel());
        ps.println("use: "+logger.getUseParentHandlers());
        Handler[] parentHandlers=parent.getHandlers();
        ps.println("parent handlers: "+Arrays.asList(parentHandlers));
        for(Handler handler:parentHandlers) ps.println(handler.getLevel());
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
        toString(logger);
        logger.info("hello");
        System.out.println("---");
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        Tee tee=new Tee(byteArrayOutputStream);
        //tee.addOutputStream(System.out);
        System.out.println("out");
        System.err.println("err");
        Handler handler=flushingStreamHandler(tee); // works fine
        handler.setFormatter(new MyFormatter());
        handler.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
        toString(logger);
        logger.severe("log message");
        System.out.println("baos: "+byteArrayOutputStream);
        if(true) return;
        for(int i=0;i<levels.length;++i) { System.out.print(levels[i]+" "+levels[i].intValue()); }
        System.out.println();
        useColor=false;
        System.out.println("mainLogger");
        setupLogger(mainLogger,new MyFormatter());
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
    static final int maxThreadNameLength=12;
    public static final Level initialLoggingLevel=Level.WARNING;
    public static final Set<String> loggerNames=new TreeSet<>();
    //public static final Logger globalLogger=Logger.getGlobal();
    public static final Logger mainLogger=Logger.getLogger(Logging.class.getName());
    public static final Logger serverLogger=Logger.getLogger(server.GoServer.class.getName());
    public static final Logger parserLogger=Logger.getLogger(sgf.Parser.class.getName());
    public static final Logger gameLogger=Logger.getLogger(game.Game.class.getName());
    public static final Level levels[]= {Level.OFF,Level.SEVERE,Level.WARNING,Level.INFO,Level.CONFIG,Level.FINE,
            Level.FINER,Level.FINEST,Level.ALL};
    static {
        // Init.Main.main(null);
    }
}
