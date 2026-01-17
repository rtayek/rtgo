package sgf.combine;
import io.Logging;
import static io.IOs.standardIndent;
import static sgf.Parser.*;
import java.io.*;
import java.util.ArrayList;
import io.IOs;
import sgf.*;
public class Combine { // the purpose of this class is to combine two sgf files
    static SgfNode combine_(SgfNode annotated,SgfNode current) {
        if(annotated==null) return null;
        Logging.mainLogger.warning("walk old");
        SgfNode holder=new SgfNode();
        holder.sgfProperties=new ArrayList<>(1);
        SgfNode.moves=0;
        annotated.lastMove(holder);
        if(holder.left==null) { Logging.mainLogger.warning("no moves in first variation"); return null; }
        if(holder.sgfProperties.isEmpty()) { Logging.mainLogger.warning("no move property found"); return null; }
        // holder.left has the node from the annotated game
        SgfNode old=holder.left;
        SgfProperty move=holder.sgfProperties.get(0);
        holder.sgfProperties.clear();
        SgfNode.moves=0;
        boolean found=current.findMove(holder,move);
        if(found) Logging.mainLogger.warning("found");
        else {
            Logging.mainLogger.warning("not found");
            return null;
        }
        Logging.mainLogger.warning("node in annotated is: "+old);
        Logging.mainLogger.warning("moved number="+SgfNode.moves);
        Logging.mainLogger.warning("annotated.right is: "+old.right);
        Logging.mainLogger.warning("annotated.left is: "+old.left);
        SgfNode branch=holder.left;
        Logging.mainLogger.warning("node current is: "+branch);
        Logging.mainLogger.warning("moved number="+SgfNode.moves);
        Logging.mainLogger.warning("branch.right is: "+branch.right);
        Logging.mainLogger.warning("branch.left is: "+branch.left);
        // for now just replace?
        SgfNode oldLeft=old.left; // this is the last move, so this link must be
        // to a variation of the last move, so save
        // first variation
        // this will probably fail real badly if another move is made in this
        // game. to do that i would need to know the real last move!
        old.left=branch.left; // add in the new moves
        if(branch.right!=null) throw new RuntimeException("branch.right!=null");
        old.right=branch.right;
        old.right=oldLeft; // preserve first variation
        Logging.mainLogger.warning("combined");
        return annotated;
    }
    public static SgfNode combine(final String name) {
        Logging.mainLogger.warning("process: "+name);
        //Parser parser=new Parser();
        File file=new File(new File(pathToOldGames,"annotated"),name);
        Reader reader=IOs.toReader(file);
        if(reader==null) throw new RuntimeException(file+" has null reader!");
        SgfNode annotated=restoreSgf(reader);
        Logging.mainLogger.warning("annotated: "+name);
        PrintStream out=new PrintStream(System.err,true);
        Writer writer=new PrintWriter(out);
        annotated.saveSgf(writer,standardIndent);
        Logging.mainLogger.warning("");
        SgfNode current=restoreSgf(IOs.toReader(new File(pathToOldGames,name)));
        Logging.mainLogger.warning("current: "+name);
        current.saveSgf(writer,standardIndent);
        Logging.mainLogger.warning("");
        Logging.mainLogger.warning("end of current: "+name);
        if(annotated==null&&current==null) return null;
        if(annotated==null) return current;
        if(current==null) return annotated; /* maybe return null to avoid unecessary io? */
        Logging.mainLogger.warning("got both");
        SgfNode combined=combine_(annotated,current);
        Logging.mainLogger.warning("exit combine");
        return combined;
    }
    static final boolean atHome=true;
    public static final File pathToHere=new File(sgfPath);// new
    // do not put a ./ in front of the "sgf"!
    // File(atHome?"i:/ray/workspace/sgf2":"c:/ray/root/com/tayek/games/go/sgf");
    public static final String pathToOldGames="sgf/old";
    static final String pathToSgf="sgf";
    public static void main(String args[]) {
        try {
            for(Object key:Parser.sgfDataKeySet()) {
                // not clear what this is doing other than parsing and printing.
                // whatever it is may not belong here.
                Logging.mainLogger.warning("key: "+key);
                SgfNode games=restoreSgf(IOs.toReader(getSgfData(key)));
                Logging.mainLogger.warning("game ************");
                if(games!=null) {
                    OutputStreamWriter outputStreamWriter=new OutputStreamWriter(System.err);
                    games.saveSgf(outputStreamWriter,standardIndent);
                }
                Logging.mainLogger.warning("");
            }
        } catch(Exception e) {
            Logging.mainLogger.warning("in main()");
            e.printStackTrace();
        }
        if(true) Logging.mainLogger.warning("************************************");
    }
    public static final String sgfOutputFilename="sgfOutput.txt";
}
