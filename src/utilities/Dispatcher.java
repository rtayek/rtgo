package utilities;
import io.Logging;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import com.tayek.util.core.Pair;
class Main {
    public static void main(String[] arguments) {
        Logging.mainLogger.info("you just ran the default main class for the dispatcher.");
        Logging.mainLogger.info("all it does is print this message.");
    }
}
public class Dispatcher {
    public Dispatcher(final String[] arguments) { this.arguments=arguments; add(Main.class); }
    public void add(Class<?> clazz) {
        entryPoints.put(Integer.valueOf(entryPoints.size()+1),new Pair<String,Class<?>>(clazz.getName(),clazz));
    }
    public void remove(int i) { entryPoints.remove(i); }
    void menu() {
        Logging.mainLogger.info("menu:");
        for(int x:entryPoints.keySet())
            Logging.mainLogger.info(x+" "+entryPoints.get(x).second.getSimpleName()+" ("+entryPoints.get(x).first+")");
    }
    public void runDispatcher() throws IllegalAccessException,IllegalArgumentException,InvocationTargetException,
            NoSuchMethodException,SecurityException,IOException {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        loop:while(true) {
            menu();
            Integer number=null;
            String string=null;
            boolean ok=false;
            String[] parts=null;
            while(!ok) {
                Logging.mainLogger.info("enter a number and any argumemts.");
                string=in.readLine();
                Logging.mainLogger.info("string is: '"+string+"'");
                if(string==null) break loop;
                if(!string.isEmpty()) {
                    parts=string.split(" ");
                    number=Integer.valueOf(parts[0]);
                    if(number!=null) ok=true;
                } else Logging.mainLogger.info(parts[0]+" is not a valid choice");
                Logging.mainLogger.info("at bottom of !ok loop.");
            }
            /*final Pair<String,Class<?>> pair=*/runMain(number,string,parts);
        }
        Logging.mainLogger.info("exit main loop.");
        System.out.flush();
    }
    private Pair<String,Class<?>> runMain(Integer number,String string,String[] parts) {
        final Pair<String,Class<?>> pair=entryPoints.get(number);
        if(pair.equals(null)) {
            Logging.mainLogger.info(string+" is not a valid choice");
        } else {
            final Class<?> entryPoint=pair.second;
            if(entryPoint!=null) {
                final String[] theRest=parts.length>1?Arrays.copyOfRange(parts,1,parts.length):new String[0];
                Logging.mainLogger.info("the rest: "+Arrays.asList(theRest));
                new Thread(new Runnable() {
                    @Override public void run() {
                        Logging.mainLogger.info("running: "+entryPoint+" with: "+Arrays.asList(theRest));
                        try {
                            entryPoint.getMethod("main",String[].class).invoke(null,(Object)theRest);
                        } catch(IllegalAccessException|IllegalArgumentException|InvocationTargetException
                                |NoSuchMethodException|SecurityException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        //printThreads();
                    }
                },pair.second.toString()).start();
            } else throw new RuntimeException("entry point does not exist!");
        }
        return pair;
    }
    public static void main(final String[] arguments) throws Exception {
        new Dispatcher(arguments) {
            {
                while(entryPoints.size()>0) // how can this happen?
                    remove(1);
                add(gui.Main.class);
                add(server.GoServer.class);
                add(controller.CommandLine.class);
            }
        }.runDispatcher();
    }
    public final String[] arguments; // given to main
    public final Map<Integer,Pair<String,Class<?>>> entryPoints=new TreeMap<>();
}
