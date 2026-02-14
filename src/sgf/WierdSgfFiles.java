package sgf;
import io.Logging;
import java.io.*;
import com.tayek.util.io.FileIO;
public class WierdSgfFiles {
    public static void main(String[] argument) throws Exception {
        File fileList=new File("sgffiles.txt");
        BufferedReader bufferedReader=new BufferedReader(new FileReader(fileList));
        String line=null;
        while((line=bufferedReader.readLine())!=null) {
            File file=new File(line);
            if(file.exists()) {
                long n=file.length();
                if(!line.contains("SGF_DEFECT+WEIRD_FILES")) if(n<100) {
                    Logging.mainLogger.info(n+" "+file+" ");
                    StringBuffer stringBuffer=new StringBuffer();
                    FileIO.fromFile(stringBuffer,file);
                    Logging.mainLogger.info("'"+stringBuffer+"'");
                }
            }
        }
        bufferedReader.close();
    }
}
