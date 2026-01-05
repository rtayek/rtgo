package utilities;
import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import javax.lang.model.SourceVersion;
import io.IOs;
import io.Logging;
public class Utilities {
    public boolean isLineFeedOrCarriageReturn(Character character) {
        return character.equals('\n')||character.equals('\r');
    }
    public static void removeCr(final StringBuffer stringBuffer,final String string) { // adds everything but a carriage return.
        for(int i=0;i<string.length();i++) if(string.charAt(i)!='\r') stringBuffer.append(string.charAt(i));
    }
    public static String noEol(String string) {
        // look for duplicate code!for this and others.
        String s=string;
        if(s.charAt(s.length()-1)=='\n') s=s.substring(0,s.length()-1);
        if(s.charAt(s.length()-1)=='\r') s=s.substring(0,s.length()-1);
        return s;
    }
    public String quote(String string) {
        StringBuffer sb=new StringBuffer(string.length());
        for(int i=0;i<string.length();i++) {
            char c=string.charAt(i);
            if(isLineFeedOrCarriageReturn(c)) sb.append("\\\\");
            sb.append(c);
        }
        return sb.toString();
    }
    public static List<File> addFiles(List<File> files,File dir) {
        if(files==null) files=new LinkedList<File>();
        if(!dir.isDirectory()) { files.add(dir); return files; }
        for(File file:dir.listFiles()) addFiles(files,file);
        return files;
    }
    public static String method(int n) {
        return Thread.currentThread().getStackTrace()[n].getClassName()+'.'
                +Thread.currentThread().getStackTrace()[n].getMethodName()+"()";
    }
    public static String method() { return method(2); }
    void mumble() { method(); }
    public static String shortMethod(int n) {
        return '.'+Thread.currentThread().getStackTrace()[n].getMethodName()+"()";
    }
    public static String shortMethod() { return shortMethod(2); }
    public static void load(Properties properties,String filename) {
        File file=new File(filename);
        try {
            InputStream in=new FileInputStream(file);
            properties.load(in);
            //Logging.mainLogger.config("properties loaded from url: "+file+": "+properties);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Properties load(final InputStream inputStream) {
        final Properties p=new Properties(defaultProperties); // add in any
        // new defaults?
        try {
            p.load(inputStream);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }
    public static void store(Properties properties,String filename) {
        try {
            // File file=new File(new File("resources/model"),filename);
            File file=new File(filename);
            Logging.mainLogger.config("writing new properties to: "+filename+": "+properties);
            properties.store(new FileOutputStream(file),"initial");
        } catch(FileNotFoundException e) {
            Logging.mainLogger.warning("properties"+" "+"caught: "+e+" property file was not written!");
        } catch(IOException e) {
            Logging.mainLogger.warning("properties"+" "+"caught: "+e+" property file was not written!");
        }
    }
    public static void store(final Properties properties,final OutputStream outputStream) {
        try {
            properties.store(outputStream,null);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void store(final Properties properties,final File propertiesFile) {
        try {
            final OutputStream out=new FileOutputStream(propertiesFile);
            store(properties,out);
            out.close();
        } catch(FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static final Properties defaultProperties=new Properties();
    static { /* add some properties */}
    public static String getString(String key,ResourceBundle resourceBundle) {
        String string=null;
        try {
            string=resourceBundle.getString(key);
        } catch(MissingResourceException e) {}
        return string;
    }
    public static void fromReader(final StringBuffer stringBuffer,Reader reader) {
        if(reader!=null) try {
            int c=0;
            while((c=reader.read())!=-1) stringBuffer.append((char)c);
            reader.close();
        } catch(IOException e) {
            Logging.mainLogger.info("fromReader caught: "+e);
            e.printStackTrace();
        }
    }
    public static void fromFile(final StringBuffer stringBuffer,final File file) {
        Reader r=null;
        try {
            r=new FileReader(file);
            fromReader(stringBuffer,r);
        } catch(FileNotFoundException e) {
            Logging.mainLogger.info(file+" fromFile caught: "+e);
        }
    }
    public static String fromFile(final File file) {
        StringBuffer stringBuffer=new StringBuffer();
        fromFile(stringBuffer,file);
        return stringBuffer.toString();
    }
    static List<String> toStrings(final BufferedReader r) {
        final List<String> l=new LinkedList<String>();
        String line=null;
        try {
            for(line=r.readLine();(line=r.readLine())!=null;) l.add(line);
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return l;
    }
    public static String cat(final String[] data) {
        final StringBuffer sb=new StringBuffer();
        for(int i=0;i<data.length;i++) sb.append(data[i]).append('\n');
        return sb.toString();
    }
    public static String cat(final List<String> strings) {
        final StringBuffer sb=new StringBuffer();
        for(String string:strings) sb.append(string).append('\n');
        return sb.toString();
    }
    public static List<String> getFileAsListOfStrings(final File file) {
        BufferedReader r=null;
        try {
            r=new BufferedReader(new FileReader(file));
            final List<String> l=toStrings(r);
            r.close();
            return l;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    static List<String> getDataThatMayHaveLineFeeds(final String[] data) {
        final BufferedReader r=IOs.toBufferedReader(cat(data)); // mes1
        // ha/ line feeds!
        return toStrings(r);
    }
    public static void close(final Reader r) {
        try {
            r.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void close(final Writer w) {
        try {
            w.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static int uniform(final int n) { return (int)Math.floor(Math.random()*n); }
    static boolean isValidName(String className) {
        return SourceVersion.isIdentifier(className)&&!SourceVersion.isKeyword(className);
    }
    public static void printDifferences(PrintStream ps,String expected,String actual) {
        if(!expected.equals(actual)) {
            ps.println("<<<<<<<<<<<<<<<");
            ps.println("ex: "+expected.length()+" '"+expected+"'");
            ps.println("ac: "+actual.length()+" '"+actual+"'");
            ps.println("ex: "+expected.endsWith("\n")+", ac: "+actual.endsWith("\n"));
            ps.println("ex: "+expected.endsWith("\r\n")+", ac: "+actual.endsWith("\r\n"));
            ps.println(expected.equals(actual));
            byte[] bytes=actual.getBytes();
            byte[] bytes2=actual.getBytes();
            if(expected.length()!=actual.length()) { ps.println("ex: "+expected.length()+", ac: "+actual.length()); }
            for(int i=0;i<Math.min(actual.length(),expected.length());++i) {
                char c=actual.charAt(i);
                char c2=expected.charAt(i);
                if(c!=c2) {
                    boolean ok=!Character.isISOControl(c);
                    ps.print(i+" "+(ok?c:" ")+" "+(!ok?bytes[i]:""));
                    boolean ok2=!Character.isISOControl(c2);
                    ps.print(", "+(ok2?c2:" ")+" "+(!ok2?bytes2[i]:""));
                    if(c!=c2) ps.println(" not equal in print differences!");
                    ps.println();
                }
            }
            ps.println(">>>>>>>>>>>>>>>");
        }
    }
    public static boolean areEqual(String string1,String string2) {
        if(string1.length()!=string2.length()) Logging.mainLogger.info("strings have different length!");
        int n=Math.min(string1.length(),string2.length());
        for(int i=0;i<n;++i) if(string1.charAt(i)!=string2.charAt(i)) {
            Logging.mainLogger.warning("strings differ at character "+i);
            int start=Math.max(0,i-20),end=Math.min(i+20,n-1);
            Logging.mainLogger.warning(String.valueOf(string1.substring(start,end)));
            Logging.mainLogger.warning(String.valueOf(string2.substring(start,end)));
            return false;
        }
        return true;
    }
    public static Byte[] toObjects(byte[] bytes) {
        return IntStream.range(0,bytes.length).mapToObj(i->bytes[i]).toArray(Byte[]::new);
    }
    public static Character[] toObjects(char[] characters) {
        return IntStream.range(0,characters.length).mapToObj(i->characters[i]).toArray(Character[]::new);
    }
    public static boolean implies(Boolean a,boolean b) { return !a|b; }
}
