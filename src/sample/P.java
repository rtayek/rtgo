package sample;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
public class P {
    boolean load_(InputStream in) throws IOException {
        boolean rc=false;
        if(in!=null) { properties.load(in); in.close(); rc=true; }
        System.out.println(method(3)+" returns: "+rc);
        return rc;
    }
    boolean load(String name) throws IOException {
        InputStream in=getClass().getResourceAsStream(name);
        return load_(in);
    }
    boolean loadcl(String name) throws IOException {
        InputStream in=getClass().getClassLoader().getResourceAsStream(name);
        return load_(in);
    }
    boolean loadurl(String name) throws IOException {
        URL url=getClass().getResource(name);
        if(url!=null) {
            InputStream in=url.openStream();
            return load_(in);
        } else return load_(null);
    }
    boolean loadurlcl(String name) throws IOException {
        URL url=getClass().getClassLoader().getResource(name);
        if(url!=null) {
            InputStream in=url.openStream();
            return load_(in);
        } else return load_(null);
    }
    void run() throws IOException {
        System.out.println("with file: sample/p.properties");
        for(String filename:filenames) {
            System.out.println("filename: "+filename);
            new P().load(filename);
            new P().loadcl(filename);
            new P().loadurl(filename);
            new P().loadurlcl(filename);
        }
    }
    static String method(int n) { return Thread.currentThread().getStackTrace()[n].getMethodName()+"()"; }
    public static void main(String[] args) throws IOException { new P().run(); }
    Properties properties=new Properties();
    boolean result;
    String string;
    static String propertyFilename="p.properties";
    static final String[] filenames=new String[] {propertyFilename,"sample"+propertyFilename,"/"+propertyFilename,
            "/sample/"+propertyFilename};
}
