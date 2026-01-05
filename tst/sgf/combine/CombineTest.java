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
                Logging.mainLogger.warning("combined");
                Logging.mainLogger.warning(String.valueOf(SgfTestIo.save(combined,standardIndent)));
                Logging.mainLogger.warning("");
            }
        } catch(Exception e) {
            Logging.mainLogger.warning("in testCombine()");
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
        if(!ok) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
        reader=IOs.toReader(new File(new File(Combine.pathToOldGames,"annotated"),"test.sgf"));
        ok=SgfRoundTrip.roundTripTwice(reader);
        if(!ok) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
        if(!testCombine("test.sgf")) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
        Logging.mainLogger.warning("done");
    }
}
