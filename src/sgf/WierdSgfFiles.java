package sgf;
import static utilities.Utilities.fromFile;
import java.io.*;
public class WierdSgfFiles {
    public static void main(String[] argument) throws Exception {
        File fileList=new File("sgffiles.txt");
        BufferedReader bufferedReader=new BufferedReader(new FileReader(fileList));
        String line=null;
        while((line=bufferedReader.readLine())!=null) {
            File file=new File(line);
            if(file.exists()) {
                long n=file.length();
                if(!line.contains("SGF_DEFECT+WEIRD_FILES"))
                    if(n<100) {
                        System.out.println(n+" "+file+" ");
                        StringBuffer stringBuffer=new StringBuffer();
                        fromFile(stringBuffer,file);
                        System.out.println("'"+stringBuffer+"'");
                    }
            }
        }
        bufferedReader.close();
    }
}
