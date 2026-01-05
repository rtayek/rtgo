package io;
import io.Logging;
import java.io.File;
import java.util.*;
import gnu.getopt.*;
public class MainGetOpt {
    static void help() {
        // we need role
        //
        Logging.mainLogger.info("usage -n name -r role -d directory");
        Logging.mainLogger.info("options");
        Logging.mainLogger.info("-h help (this text)");
        Logging.mainLogger.info("-n name");
        Logging.mainLogger.info("-r name");
        Logging.mainLogger.info("-d directory (i.e. -d d:/usr/lec)");
        Logging.mainLogger.info("-v  - verbose mode");
        System.exit(1);
    }
    public static Map<String,Object> processArguments(String[] argv) {
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        int c;
        String arg;
        StringBuffer sb=new StringBuffer();
        LongOpt[] longopts=GetOptSupport.defaultLongOpts(sb);
        //
        final String options="-:n:r:dhvw;";
        Getopt getopt=new Getopt("testprog",argv,options,longopts);
        getopt.setOpterr(false); // We'll do our own error handling
        //
        if(false) { GetOptSupport.logLongOpts(options,longopts); }
        while((c=getopt.getopt())!=-1) {
            if(GetOptSupport.handleCommonOption(c,getopt,longopts,sb,MainGetOpt::help)) continue;
            switch(c) {
                case 'n':
                    arg=getopt.getOptarg();
                    if(arg==null) { help(); System.exit(1); }
                    map.put("name",arg);
                    break;
                case 'd':
                    arg=getopt.getOptarg();
                    if(arg==null) { help(); System.exit(1); }
                    directory=new File(arg);
                    break;
                case 'v':
                    verbose=true;
                    break;
                case 'h':
                    help();
                    break;
                default:
                    Logging.mainLogger.info("getopt() returned "+c);
                    break;
            }
        }
        GetOptSupport.logRemainingArgs(getopt,argv);
        return map;
    }
    public static void main(String[] arguments) { processArguments(arguments); }
    static Boolean verbose=false;
    static File directory;
}
