package sgf;
import io.Logging;
import java.io.File;
import java.util.List;
import com.tayek.util.io.FileIO;
public class WierdSgfFiles {
    public static void main(String[] argument) throws Exception {
        File fileList=new File("sgffiles.txt");
        List<String> lines=FileIO.toStrings(fileList);
        for(String line:lines) {
            File file=new File(line);
            if(file.exists()) {
                long n=file.length();
                if(!line.contains("SGF_DEFECT+WEIRD_FILES")) if(n<100) {
                    Logging.mainLogger.info(n+" "+file+" ");
                    String text=FileIO.fromFile(file);
                    Logging.mainLogger.info("'"+text+"'");
                }
            }
        }
    }
}
