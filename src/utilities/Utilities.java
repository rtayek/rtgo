package utilities;
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.lang.model.SourceVersion;
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
    void mumble() { method(); }
    public static String method(int n) {
        return Thread.currentThread().getStackTrace()[n].getClassName()+'.'
                +Thread.currentThread().getStackTrace()[n].getMethodName()+"()";
    }
    public static String method() { return method(2); }
    public static String shortMethod(int n) {
        return '.'+Thread.currentThread().getStackTrace()[n].getMethodName()+"()";
    }
    public static String shortMethod() { return shortMethod(2); }
    public static void loadPropertiesFile(Properties properties,String filename) {
        File file=new File(filename);
        try {
            InputStream in=new FileInputStream(file);
            properties.load(in);
            //Logging.mainLogger.config("properties loaded from url: "+file+": "+properties);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void load(Properties properties,String filename,URL url) {
        // this may find a properties file nearby.
        // we are not using this.
        if(url!=null) try {
            InputStream in=url.openStream();
            if(in!=null) {
                properties.load(in);
                Logging.mainLogger.config("properties loaded from url: "+filename+": "+properties);
            } else Logging.mainLogger.warning("properties stream is null!");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        else Logging.mainLogger.warning("url is null for filename: "+filename);
    }
    public static void writePropertiesFile(Properties properties,String filename) {
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
    public static Properties load(final Reader reader) {
        try {
            Properties properties=new Properties();
            properties.load(reader);
            return properties;
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
    public static Properties load(final File propertiesFile) {
        Properties p=null;
        try {
            final InputStream in=new FileInputStream(propertiesFile);
            p=load(in);
        } catch(FileNotFoundException e) {
            System.out.println("load caught: "+e);
        }
        return p;
    }
    public static void store(final File propertiesFile,Properties p) {
        try {
            final OutputStream out=new FileOutputStream(propertiesFile);
            store(out,p);
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
    public static void store(final OutputStream outputStream,final Properties properties) {
        try {
            properties.store(outputStream,null);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void store(final Writer writer,final Properties properties) {
        try {
            properties.store(writer,null);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void storeXml(final OutputStream outputStream,final Properties properties) {
        try {
            properties.storeToXML(outputStream,null);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void storeXml(final File propertiesFile,Properties p) {
        try {
            final OutputStream out=new FileOutputStream(propertiesFile);
            storeXml(out,p);
            out.close();
        } catch(FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static byte[] save(final Object o) {
        try {
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ObjectOutputStream out=new ObjectOutputStream(baos);
            out.writeObject(o);
            out.flush();
            out.close();
            return baos.toByteArray();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object restore(final ObjectInputStream objectInputStream) {
        try {
            final Object o=objectInputStream.readObject();
            objectInputStream.close();
            return o;
        } catch(IOException e) {
            throw new RuntimeException(e);
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object restore(final byte[] b) {
        try {
            ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(b));
            return restore(in);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object restore(final File file) {
        try {
            ObjectInputStream in=new ObjectInputStream(new FileInputStream(file));
            return restore(in);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void toFile(final byte[] b,final File file) {
        try {
            OutputStream out=new FileOutputStream(file);
            out.write(b);
            out.close();
        } catch(FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void toFile(final String s,final File file) {
        try {
            Writer out=new FileWriter(file);
            out.write(s);
            out.close();
        } catch(FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void fromReader(final StringBuffer stringBuffer,Reader reader) {
        if(reader!=null) try {
            int c=0;
            while((c=reader.read())!=-1) stringBuffer.append((char)c);
            reader.close();
        } catch(IOException e) {
            System.out.println("fromReader caught: "+e);
            e.printStackTrace();
        }
    }
    public static String fromReader(final Reader reader) {
        StringBuffer stringBuffer=new StringBuffer();
        fromReader(stringBuffer,reader);
        return stringBuffer.toString();
    }
    public static void fromFile(final StringBuffer stringBuffer,final File file) {
        Reader r=null;
        try {
            r=new FileReader(file);
            fromReader(stringBuffer,r);
        } catch(FileNotFoundException e) {
            System.out.println(file+" fromFile caught: "+e);
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
        final BufferedReader r=new BufferedReader(new StringReader(cat(data))); // mes1
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
                    if(c!=c2) ps.println(" not equal!");
                    ps.println();
                }
            }
            ps.println(">>>>>>>>>>>>>>>");
        }
    }
    public static boolean areEqual(String string1,String string2) {
        if(string1.length()!=string2.length()) System.out.println("strings have different length!");
        int n=Math.min(string1.length(),string2.length());
        for(int i=0;i<n;++i) if(string1.charAt(i)!=string2.charAt(i)) {
            System.err.println("strings differ at character "+i);
            int start=Math.max(0,i-20),end=Math.min(i+20,n-1);
            System.err.print(string1.substring(start,end));
            System.err.print(string2.substring(start,end));
            return false;
        }
        return true;
    }
    public static boolean implies(Boolean a,boolean b) { return !a|b; }
}
