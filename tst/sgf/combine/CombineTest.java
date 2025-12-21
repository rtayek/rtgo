package sgf.combine;
import static io.IOs.standardIndent;
import static sgf.SgfNode.sgfRoundTripTwice;
import java.io.*;
import io.*;
import sgf.SgfNode;
public class CombineTest {
    static boolean testCombine(String name) {
        try {
            SgfNode combined=Combine.combine(name);
            if(combined!=null) {
                System.err.println("combined");
                Writer writer=new StringWriter();
                combined.saveSgf(writer,standardIndent);
                System.err.println(writer.toString());
                System.err.println();
            }
        } catch(Exception e) {
            System.err.println("in testCombine()");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static void main(String args[]) throws Exception {
        Reader reader=null;
        Boolean ok=null;
        Tee tee=Tee.tee(new File(Combine.sgfOutputFilename));
        reader=IOs.toReader(new File(Combine.pathToHere,"ff4_ex.sgf"));
        ok=sgfRoundTripTwice(reader);
        if(!ok) { System.err.println("failure"); throw new Exception("test fails"); }
        reader=IOs.toReader(new File(new File(Combine.pathToOldGames,"annotated"),"test.sgf"));
        ok=sgfRoundTripTwice(reader);
        if(!ok) { System.err.println("failure"); throw new Exception("test fails"); }
        if(!testCombine("test.sgf")) { System.err.println("failure"); throw new Exception("test fails"); }
        System.err.println("done");
        System.out.flush();
        System.err.flush();
    }
}
