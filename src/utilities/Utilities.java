package utilities;
import java.io.*;
import java.util.*;
import com.tayek.util.core.Misc;
import com.tayek.util.core.Stacks;
import com.tayek.util.core.Texts;
import com.tayek.util.io.FileIO;
import com.tayek.util.io.PropertiesIO;
public class Utilities {
    public boolean isLineFeedOrCarriageReturn(Character character) {
        return Texts.isLineFeedOrCarriageReturn(character);
    }
    public static void removeCr(final StringBuffer stringBuffer,final String string) {
        Texts.removeCr(stringBuffer,string);
    }
    public static String noEol(String string) {
        return Texts.noEol(string);
    }
    public String quote(String string) {
        return Texts.quote(string);
    }
    public static List<File> addFiles(List<File> files,File dir) {
        return FileIO.addFiles(files,dir);
    }
    public static String method(int n) {
        return Stacks.method(n);
    }
    public static String method() { return Stacks.method(); }
    void mumble() { method(); }
    public static String shortMethod(int n) {
        return Stacks.shortMethod(n);
    }
    public static String shortMethod() { return Stacks.shortMethod(); }
    public static void load(Properties properties,String filename) {
        PropertiesIO.loadPropertiesFile(properties,filename);
    }
    public static Properties load(final InputStream inputStream) {
        return PropertiesIO.load(inputStream);
    }
    public static void store(Properties properties,String filename) {
        PropertiesIO.writePropertiesFile(properties,filename);
    }
    public static void store(final Properties properties,final OutputStream outputStream) {
        PropertiesIO.store(outputStream,properties);
    }
    public static void store(final Properties properties,final File propertiesFile) {
        PropertiesIO.store(propertiesFile,properties);
    }
    public static final Properties defaultProperties=PropertiesIO.defaultProperties;
    public static String getString(String key,ResourceBundle resourceBundle) {
        String string=null;
        try {
            string=resourceBundle.getString(key);
        } catch(MissingResourceException e) {}
        return string;
    }
    public static void fromReader(final StringBuffer stringBuffer,Reader reader) {
        FileIO.fromReader(stringBuffer,reader);
    }
    public static void fromFile(final StringBuffer stringBuffer,final File file) {
        FileIO.fromFile(stringBuffer,file);
    }
    public static String fromFile(final File file) {
        return FileIO.fromFile(file);
    }
    static List<String> toStrings(final BufferedReader r) {
        return FileIO.toStrings(r);
    }
    public static String cat(final String[] data) {
        return Texts.cat(data);
    }
    public static String cat(final List<String> strings) {
        return Texts.cat(strings);
    }
    public static List<String> getFileAsListOfStrings(final File file) {
        return FileIO.getFileAsListOfStrings(file);
    }
    static List<String> getDataThatMayHaveLineFeeds(final String[] data) {
        return FileIO.getDataThatMayHaveLineFeeds(data);
    }
    public static void close(final Reader r) {
        FileIO.close(r);
    }
    public static void close(final Writer w) {
        FileIO.close(w);
    }
    public static int uniform(final int n) { return Misc.uniform(n); }
    static boolean isValidName(String className) {
        return Misc.isValidName(className);
    }
    public static void printDifferences(PrintStream ps,String expected,String actual) {
        Texts.printDifferences(ps,expected,actual);
    }
    public static boolean areEqual(String string1,String string2) {
        return Texts.areEqual(string1,string2);
    }
    public static Byte[] toObjects(byte[] bytes) {
        return Texts.toObjects(bytes);
    }
    public static Character[] toObjects(char[] characters) {
        return Texts.toObjects(characters);
    }
    public static boolean implies(Boolean a,boolean b) { return Misc.implies(a,b); }
}
