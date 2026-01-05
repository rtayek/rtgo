package io;
import io.Logging;
import java.io.File;
import gnu.getopt.*;
public class GetOpt {
    static void help() {
        // we need role
        //
        Logging.mainLogger.info("usage -r role -d directory");
        Logging.mainLogger.info("options");
        Logging.mainLogger.info("-h help (this text)");
        Logging.mainLogger.info("-d directory (i.e. -d d:/usr/lec)");
        Logging.mainLogger.info("-v  - verbose mode");
        System.exit(1);
    }
    static void processArguments(String[] argv) {
        int c;
        String arg;
        StringBuffer sb=new StringBuffer();
        LongOpt[] longopts=GetOptSupport.defaultLongOpts(sb);
        //
        final String options="-:r:dhvw;";
        Getopt getopt=new Getopt("testprog",argv,options,longopts);
        getopt.setOpterr(false); // We'll do our own error handling
        //
        if(false) { GetOptSupport.logLongOpts(options,longopts); }
        while((c=getopt.getopt())!=-1) {
            if(GetOptSupport.handleCommonOption(c,getopt,longopts,sb,GetOpt::help)) continue;
            switch(c) {
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
    }
    public static void main(String[] arguments) {
        // TODO Auto-generated method stub
    }
    static Boolean verbose=false;
    static File directory;
}
