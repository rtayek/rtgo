package core.cli;
import java.io.*;
public class RunCliFromReader {
    public static void main(String[] args) throws Exception {
        String commands="";
        StringReader stringReader=new StringReader(commands);
        PrintWriter printWriter=new PrintWriter(System.out,true);
        new CliMain(new BufferedReader(stringReader),printWriter);
    }
}
