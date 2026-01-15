package sgf.combine;
import static io.IOs.standardIndent;
import java.io.File;
import io.*;
import sgf.SgfNode;
import sgf.SgfTestIo;
import sgf.SgfTestSupport;
public class CombineTest {
    static boolean testCombine(String name) {
        Logging.mainLogger.info("test combine: "+name);
        try {
            SgfNode combined=Combine.combine(name);
            if(combined==null) {
                Logging.mainLogger.warning("combine returns null!");
                return false;
            }
            Logging.mainLogger.warning("combined");
            Logging.mainLogger.warning(String.valueOf(SgfTestIo.save(combined,standardIndent)));
            Logging.mainLogger.warning("");
        } catch(Exception e) {
            Logging.mainLogger.warning("in testCombine()");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static void main(String args[]) throws Exception {
        Boolean ok=null;
        Tee tee=Tee.tee(new File(Combine.sgfOutputFilename));
        ok=SgfTestSupport.roundTripTwice(new File(Combine.pathToHere,"ff4_ex.sgf"));
        if(!ok) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
        ok=SgfTestSupport.roundTripTwice(new File(new File(Combine.pathToOldGames,"annotated"),"test.sgf"));
        if(!ok) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
        if(!testCombine("test.sgf")) { Logging.mainLogger.warning("failure"); throw new Exception("test fails"); }
        Logging.mainLogger.warning("done");
    }
}
