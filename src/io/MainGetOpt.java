package io;
import java.io.File;
import java.util.*;
import gnu.getopt.*;
public class MainGetOpt {
    static void help() {
        // we need role
        //
        System.out.println("usage -n name -r role -d directory");
        System.out.println("options");
        System.out.println("-h help (this text)");
        System.out.println("-n name");
        System.out.println("-r name");
        System.out.println("-d directory (i.e. -d d:/usr/lec)");
        System.out.println("-v  - verbose mode");
        System.exit(1);
    }
    public static Map<String,Object> processArguments(String[] argv) {
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        int c;
        String arg;
        LongOpt[] longopts=new LongOpt[3];
        //
        StringBuffer sb=new StringBuffer();
        longopts[0]=new LongOpt("help",LongOpt.NO_ARGUMENT,null,'h');
        longopts[1]=new LongOpt("outputdir",LongOpt.REQUIRED_ARGUMENT,sb,'o');
        longopts[2]=new LongOpt("maximum",LongOpt.OPTIONAL_ARGUMENT,null,2);
        //
        final String options="-:n:r:dhvw;";
        Getopt getopt=new Getopt("testprog",argv,options,longopts);
        getopt.setOpterr(false); // We'll do our own error handling
        //
        if(false) { System.out.println(options); for(int i=0;i<longopts.length;i++) System.out.println(longopts[i]); }
        while((c=getopt.getopt())!=-1) switch(c) {
            case 0:
                arg=getopt.getOptarg();
                System.out.println("Got long option with value '"+(char)(new Integer(sb.toString())).intValue()
                        +"' with argument "+((arg!=null)?arg:"null"));
                help();
                break;
            case 1:
                System.out.println("I see you have return in order set and that "
                        +"a non-option argv element was just found "+"with the value '"+getopt.getOptarg()+"'");
                help();
                break;
            case 2:
                arg=getopt.getOptarg();
                System.out.println("I know this, but pretend I didn't");
                System.out.println("We picked option "+longopts[getopt.getLongind()].getName()+" with value "
                        +((arg!=null)?arg:"null"));
                help();
                break;
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
            case 'W':
                System.out.println("Hmmm. You tried a -W with an incorrect long "+"option name");
                break;
            case ':':
                System.out.println("You need an argument for option "+(char)getopt.getOptopt());
                break;
            case '?':
                System.out.println("The option '"+(char)getopt.getOptopt()+"' is not valid");
                break;
            default:
                System.out.println("getopt() returned "+c);
                break;
        }
        for(int i=getopt.getOptind();i<argv.length;i++) // maybe these are gameid's???
            System.out.println("Non option argv element: "+argv[i]+"\n");
        return map;
    }
    public static void main(String[] arguments) { processArguments(arguments); }
    static Boolean verbose=false;
    static File directory;
}
