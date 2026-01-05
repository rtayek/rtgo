package sgf.combine;
import static io.IOs.standardIndent;
import java.io.*;
import io.*;
import sgf.SgfRoundTrip;
import sgf.SgfNode;
import sgf.SgfTestIo;
public class CombineTest {
    static boolean testCombine(String name) {
        try {
            SgfNode combined=Combine.combine(name);
            if(combined!=null) {
                System.err.println("combined");
                System.err.println(SgfTestIo.save(combined,standardIndent));
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
        ok=SgfRoundTrip.roundTripTwice(reader);
        if(!ok) { System.err.println("failure"); throw new Exception("test fails"); }
        reader=IOs.toReader(new File(new File(Combine.pathToOldGames,"annotated"),"test.sgf"));
        ok=SgfRoundTrip.roundTripTwice(reader);
        if(!ok) { System.err.println("failure"); throw new Exception("test fails"); }
        if(!testCombine("test.sgf")) { System.err.println("failure"); throw new Exception("test fails"); }
        System.err.println("done");
        System.out.flush();
        System.err.flush();
    }
}
