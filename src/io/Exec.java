package io;
import io.Logging;
import static io.Constants.lineSeparator;
import static java.lang.Math.max;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
// this code was from the rabbit2 tablet project in ray/conradapps.
public class Exec {
    public Exec(String name,String command) { this.name=name; processBuilder=new ProcessBuilder(command); }
    public Exec(String name,String[] strings) {
        this.name=name;
        List<String> command=Arrays.asList(strings);
        processBuilder=new ProcessBuilder(command);
    }
    public static String output(InputStream inputStream) throws IOException {
        StringBuilder sb=new StringBuilder();
        BufferedReader br=null;
        try {
            br=new BufferedReader(new InputStreamReader(inputStream));
            String line=null;
            while((line=br.readLine())!=null) { sb.append(line+lineSeparator); }
        } finally {
            br.close();
        }
        return sb.toString();
    }
    public static void p(String string) { Logging.mainLogger.info(String.valueOf(string)); }
    Consumer<String> out=(line)->Logging.mainLogger.info(String.valueOf(line));
    Consumer<String> err=(line)->Logging.mainLogger.warning(String.valueOf(line));
    static class Printer implements Runnable {
        Printer(String name,InputStream inputStream,Consumer<String> consumer) {
            this.name=name;
            this.inputStream=inputStream;
            this.consumer=consumer;
        }
        @Override public void run() {
            BufferedReader br=null;
            try {
                br=new BufferedReader(new InputStreamReader(inputStream));
                String line=null;
                while((line=br.readLine())!=null) { Logging.mainLogger.info((name!=null?name+": ":"")+line); }
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        final private String name;
        final InputStream inputStream;
        final Consumer<String> consumer;
    }
    public Exec run() {
        Process process=null;
        try {
            Logging.mainLogger.info("start");
            process=processBuilder.start();
            //output=output(process.getInputStream());
            //error=output(process.getErrorStream());
            new Thread(new Printer(name,process.getInputStream(),out)).start();
            new Thread(new Printer(name,process.getErrorStream(),err)).start();
            Logging.mainLogger.info("wait for");
            //rc=process.waitFor();
            boolean okc=process.waitFor(1,TimeUnit.SECONDS);
            Logging.mainLogger.info("get results");
        } catch(IOException e) {
            p("caught: "+e);
            e.printStackTrace();
        } catch(InterruptedException e) {
            p("caught: "+e);
            e.printStackTrace();
        } catch(Exception e) {
            p("caught: "+e);
            e.printStackTrace();
        }
        Logging.mainLogger.info(this+" "+process);
        return this;
    }
    public void print() { print(rc,output,error); }
    public static void print(int rc,String output,String error) {
        p("return code: "+rc);
        p("output: '"+output+"'");
        p("err: '"+error+"'");
    }
    public static int exec(String name,String[] strings) {
        Exec exec=new Exec(name,strings);
        exec.run();
        //exec.print();
        return exec.rc;
    }
    public static int exec(String command) {
        Runtime runtime=Runtime.getRuntime();
        try {
            Logging.mainLogger.info(String.valueOf(command));
            Process proc=runtime.exec(command);
            Logging.mainLogger.info("wait for");
            proc.waitFor();
            return proc.exitValue();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static int ping(String host) { return exec(null,new String[] {"ping",host}); }
    public static boolean isAndroid() {
        return System.getProperty("http.agent")!=null; // fragile!
    }
    public static boolean canWePing(String host,int timeout) {
        String timeoutString="";
        String[] command=null;
        if(isAndroid()) {
            timeoutString+=max(1,timeout/1_000);
            command=new String[] {"ping","-c","1","-W",timeoutString,host};
            Exec exec=new Exec(null,command);
            exec.run();
            if(exec.rc!=0) exec.print();
            return exec.rc==0;
        } else {
            timeoutString+=timeout;
            command=new String[] {"ping","-n","1","-w",""+timeoutString,host};
            Exec exec=new Exec(null,command);
            exec.run();
            //exec.print();
            boolean ok=false;
            //if(exec.output.contains("Reply from "+host+":")) // fragile! yes, very!
            if(!exec.output.contains("TTL expired in transit")&&exec.output.contains("Lost = 0")) // fragile! yes, very!
                // fix this!
                ok=true;
            else;//p("not ok,output: "+exec.output);
            //p("returning: "+ok);
            return ok;
        }
    }
    static void ping() {
        p("------");
        Exec exec=new Exec(null,new String[] {"ping","localhost"});
        exec.run();
        exec.print();
        p("------");
        exec("ping localhost");
        p("------");
        ping("localhost");
        p("------");
        new Exec(null,new String[] {"ping","localhost"}).run().print();
    }
    public static void main(String[] arguments) throws InterruptedException,IOException {
        for(String key:map.keySet()) {
            String[] program=map.get(key);
            Exec exec=new Exec(key,program).run();
        }
    }
    final String name;
    final ProcessBuilder processBuilder;
    int rc;
    String output="",error="";
    static String program="runJava.bat";
    static String[] server=new String[] {program,"server.GoServer"};
    static String[] black=new String[] {program,"gui.Main","-n","black"};
    static String[] white=new String[] {program,"gui.Main","-n","white"};
    static String[] observer=new String[] {program,"gui.Main","-n","observer"};
    static LinkedHashMap<String,String[]> map=new LinkedHashMap<>();
    static {
        map.put("server",server);
        map.put("blackr",black);
        map.put("white",white);
        map.put("observer",observer);
    }
}
