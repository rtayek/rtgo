package core.cli;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import com.tayek.util.io.FileIO;
public class RunCliFromReader {
    public static void main(String[] args) throws Exception {
        String commands="";
        Reader stringReader=FileIO.toReader(commands);
        PrintWriter printWriter=new PrintWriter(System.out,true);
        new CliMain(new BufferedReader(stringReader),printWriter);
    }
}
