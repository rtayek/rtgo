package sgf.combine;
import static io.IO.standardIndent;
import static sgf.Parser.*;
import java.io.*;
import java.util.ArrayList;
import io.IO;
import sgf.*;
public class Combine { // the purpose of this class is to combine two sgf files
    static SgfNode combine_(SgfNode annotated,SgfNode current) {
        if(annotated==null) return null;
        System.err.println("walk old");
        SgfNode holder=new SgfNode();
        holder.properties=new ArrayList<>(1);
        SgfNode.moves=0;
        annotated.lastMove(holder);
        if(holder.left==null) { System.err.println("no moves in first variation"); return null; }
        if(holder.properties.isEmpty()) { System.err.println("no move property found"); return null; }
        // holder.left has the node from the annotated game
        SgfNode old=holder.left;
        SgfProperty move=holder.properties.get(0);
        holder.properties.clear();
        SgfNode.moves=0;
        boolean found=current.findMove(holder,move);
        if(found) System.err.println("found");
        else {
            System.err.println("not found");
            return null;
        }
        System.err.println("node in annotated is: "+old);
        System.err.println("moved number="+SgfNode.moves);
        System.err.println("annotated.right is: "+old.right);
        System.err.println("annotated.left is: "+old.left);
        SgfNode branch=holder.left;
        System.err.println("node current is: "+branch);
        System.err.println("moved number="+SgfNode.moves);
        System.err.println("branch.right is: "+branch.right);
        System.err.println("branch.left is: "+branch.left);
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
        System.err.println("combined");
        return annotated;
    }
    static SgfNode combine(final String name) {
        System.err.println("process: "+name);
        //Parser parser=new Parser();
        File file=new File(new File(pathToOldGames,"annotated"),name);
        Reader reader=IO.toReader(file);
        if(reader==null) throw new RuntimeException(file+" has null reader!");
        SgfNode annotated=restoreSgf(reader);
        System.err.println("annotated: "+name);
        PrintStream out=new PrintStream(System.err,true);
        Writer writer=new PrintWriter(out);
        annotated.save(writer,standardIndent);
        System.err.println();
        SgfNode current=restoreSgf(IO.toReader(new File(pathToOldGames,name)));
        System.err.println("current: "+name);
        current.save(writer,standardIndent);
        System.err.println();
        System.err.println("end of current: "+name);
        if(annotated==null&&current==null) return null;
        if(annotated==null) return current;
        if(current==null) return annotated; /* maybe return null to avoid unecessary io? */
        System.err.println("got both");
        SgfNode combined=combine_(annotated,current);
        System.err.println("exit combine");
        return combined;
    }
    static final String eoln=System.getProperty("line.separator");
    static final boolean atHome=true;
    public static final File pathToHere=new File("sgf");// new
    // do not put a ./ in front of the "sgf"!
    // File(atHome?"i:/ray/workspace/sgf2":"c:/ray/root/com/tayek/games/go/sgf");
    static final String pathToOldGames="sgf/old";
    static final String pathToSgf="sgf";
    public static void main(String args[]) {
        try {
            for(Object key:Parser.sgfDataKeySet()) {
                // not clear what this is doing other than parsing and printing.
                // whatever it is may not belong here.
                System.err.println("key: "+key);
                SgfNode games=restoreSgf(getSgfData(key));
                System.err.println("game ************");
                if(games!=null) {
                    OutputStreamWriter outputStreamWriter=new OutputStreamWriter(System.err);
                    games.save(outputStreamWriter,standardIndent);
                }
                System.err.println();
            }
        } catch(Exception e) {
            System.err.println("in main()");
            e.printStackTrace();
        }
        if(true) System.err.println("************************************");
    }
    public static final String sgfOutputFilename="sgfOutput.txt";
}