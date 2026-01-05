package core.cli;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
public class RunCliFromReader {
    private static Reader toReader(String string) {
        return new StringReader(string);
    }
    public static void main(String[] args) throws Exception {
        String commands="";
        Reader stringReader=toReader(commands);
        PrintWriter printWriter=new PrintWriter(System.out,true);
        new CliMain(new BufferedReader(stringReader),printWriter);
    }
}
