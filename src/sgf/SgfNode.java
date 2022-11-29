package sgf;
import static io.IO.*;
import static io.Logging.parserLogger;
import static sgf.Parser.*;
import java.io.*;
import java.util.*;
import io.*;
import model.Move;
import utilities.Holder;
public class SgfNode {
    // maybe put a bunch of this stuff into interface Sgf?
    // looks like we can add a children() method.
    // maybe we can make this behave like a normal tree.
    // this would eliminate the normal tree stuff that we are using?
    // probably, take a look later
    // later: maybe not. see tree package.
    // 10/16/22 maybe mnode just have-am sgf node and ad mnode methods?
    // or maybe subclass and mnode methods?
    // 11/1/22 did some work on this in sgf/Tree.java
    public static class SgfOptions {
        // lets collect the stuff that formats sgf in one place.
        public String prepareSgf(String expectedSgf) {
            if(expectedSgf!=null) {
                expectedSgf=SgfNode.options.removeUnwanted(expectedSgf);
                if(roundTripFirst) {
                    //printDifferences(expectedSgf,expectedSgf);
                    expectedSgf=sgfRoundTrip(expectedSgf);
                    //printDifferences(expectedSgf,expectedSgf);
                    //if(!expectedSgf.endsWith("\n")) expectedSgf+="\n";
                } else {
                    //expectedSgf=expectedSgf.replaceAll("\r","");
                    //if(!expectedSgf.endsWith("\n")) expectedSgf+="\n";
                    // 88 if above is commented out. 104 if not.
                }
            }
            return expectedSgf;
        }
        public String removeUnwanted(String string) {
            if(removeCarriageReturn) {
                //how to do leading and trailing spaces
                //System.out.println("0 "+string);
                while(string.contains(" \r")) string=string.replaceAll(" \r","\r");
                while(string.contains("\r ")) string=string.replaceAll("\r ","\r");
                //System.out.println("1 "+string);
                string=string.replaceAll("\r","");
                if(string.contains("\r")) { System.out.println(string); System.exit(1); ; }
                //System.out.println("2 "+string);
            }
            if(removeLineFeed) {
                while(string.contains(" \n")) string=string.replaceAll(" \n","\n");
                while(string.contains("\n ")) string=string.replaceAll("\n ","\n");
                //System.out.println("3 "+string);
                string=string.replaceAll("\n","");
                if(string.contains("\n")) { System.out.println(string); System.exit(1); ; }
                //System.out.println("4 "+string);
            }
            if(removeTrailingLineFeed) if(string.endsWith("\n")) string=string.substring(0,string.length()-1);
            //System.out.println("5 "+string);
            return string;
        }
        public static boolean containsQuotedControlCharacters(Object key,String string) {
            for(int i=0;i<string.length();++i) if(string.charAt(i)=='\\')
                if(i<string.length()-1) if(string.charAt(i+1)=='n'||string.charAt(i+1)=='r') {
                    return true;
                }
            return false;
        }
        public static String removeQuotedControlCharacters(String string) {
            String actual=string.replaceAll("\\\\n","");
            actual=actual.replaceAll("\\\\r","");
            return actual;
        }
        public final boolean removeTrailingLineFeed=true;
        public final boolean roundTripFirst=false; // was true
        public final boolean removeCarriageReturn=true;
        public final boolean removeLineFeed=true;
        public final Indent indent=new Indent("");
    }
    public static SgfOptions options=new SgfOptions();
    public SgfNode() {}
    // make a constructor that takes an sgf tree or a reader?
    public SgfNode(List<SgfProperty> properties,SgfNode right,SgfNode left) {
        // find the inverse - work here!
        this.left=left;
        this.right=right;
        this.properties=new ArrayList<>();
        if(properties!=null) this.properties.addAll(properties);
        setIsAMoveFlags(this);
    }
    public static void setIsAMoveFlags(SgfNode node) {
        // http://www.red-bean.com/sgf/user_guide/index.html#move_vs_place says: Therefore it's illegal to mix setup properties and move properties within the same node.
        for(SgfProperty property:node.properties) {
            if(property.p() instanceof Setup) node.hasASetupType=true;
            if(property.p() instanceof sgf.Move) { node.hasAMoveType=true; }
            if((property.p().equals(P.W)||property.p().equals(P.B))) node.hasAMove=true;
        }
        if(node.hasAMoveType&&node.hasASetupType) {
            parserLogger.severe("node has move and setup type properties!");
            System.out.println("node has move and setup type properties!");
            if(!ignoreMoveAndSetupFlags) { IO.stackTrace(10); System.exit(1); }
        }
    }
    void add(SgfProperty property) {
        if(properties==null) properties=new ArrayList<>();
        properties.add(property);
        setIsAMoveFlags(this);
    }
    private SgfNode lastSibling_(Holder<Integer> h) {
        SgfNode node=null,last=this;
        for(node=right;node!=null;node=node.right) {
            last=node;
            ++h.t;
        }
        return last;
    }
    private SgfNode lastDescendant_(Holder<Integer> h) {
        SgfNode node=null,last=this;
        for(node=left;node!=null;node=node.left) {
            last=node;
            ++h.t;
        }
        return last;
    }
    protected SgfNode lastSibling() { return lastSibling_(new Holder<Integer>(0)); }
    int siblings() { Holder<Integer> siblings=new Holder<Integer>(0); lastSibling_(siblings); return siblings.t; }
    protected SgfNode lastDescendant() { return lastDescendant_(new Holder<Integer>(0)); }
    void addSibling(SgfNode node) {
        // if(right==null) { right=node;
        // System.err.println("added node "+node.id+" as first sibling of node
        // "+this.id);
        // return; }
        SgfNode last=lastSibling();
        last.right=node;
        // System.err.println("added node "+node.id+" as sibling of node
        // "+this.id);
    }
    private SgfNode lastChild() { return left==null?null:left.lastSibling(); }
    private void addDescendant(SgfNode node) {
        SgfNode last=lastDescendant();
        last.left=node;
        // System.err.println("added node "+node.id+" as desendent of node
        // "+last.id);
    }
    private int children() {
        if(left==null) return 0;
        Holder<Integer> siblings=new Holder<>(0);
        left.lastSibling_(siblings);
        return siblings.t+1; // why n+1? may be used as an index elsewhere
    }
    private void addChild(SgfNode node) {
        // if(left==null)
        // System.err.println("added node "+node.id+" as first child of node
        // "+this.id);
        if(left==null) { left=node; return; }
        SgfNode last=left.lastSibling();
        if(last==null) throw new RuntimeException("last is null in addChild");
        last.right=node;
        // System.err.println("added node "+node.id+" as child of node
        // "+this.id);
    }
    @Override public String toString() {
        StringBuffer stringBuffer=new StringBuffer(";");
        if(false) { stringBuffer.append("{id="+sgfId); stringBuffer.append("}"); }
        for(Iterator<SgfProperty> i=properties.iterator();i.hasNext();) stringBuffer.append(i.next().toString());
        return stringBuffer.toString();
    }
    private void saveSgf_(Writer writer,Indent indent) throws IOException {
        indent.in();
        writer.write(this.toString());
        if(left!=null) {
            if(left.right!=null) writer.write(indent.indent()+'(');
            left.saveSgf_(writer,indent);
            if(left.right!=null) writer.write(indent.indent()+')');
        } else writer.write(')');
        if(right!=null) { writer.write('\n'); writer.write(indent.indent()+'('); right.saveSgf_(writer,indent); }
        indent.out();
        writer.flush();
    }
    public void saveSgf(Writer writer,Indent indent) {
        try {
            writer.write(indent.indent()+'(');
            saveSgf_(writer,indent);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    void lastMove_(SgfNode holder) {
        for(Iterator<SgfProperty> i=properties.iterator();i.hasNext();) {
            SgfProperty property=i.next();
            P p=property.p();
            if(p instanceof Move&&(p==P.B||p==P.W)) {
                moves++;
                holder.left=this;
                holder.properties.clear();
                holder.properties.add(property);
                Logging.mainLogger.info("lastNode="+this+", lastMove="+property);
            } else parserLogger.severe(p+" is not a move!");
        }
    }
    public void lastMove(SgfNode holder) {
        // last move of the main line i.e. only the first variation
        // maybe combine this with other code or test against each other?
        lastMove_(holder);
        SgfNode n=left;
        for(;n!=null&&n.right==null;n=n.left) n.lastMove_(holder);
        for(int i=0;n!=null;n=n.right,i++) {
            n.lastMove(holder);
            break; // this is strange?
        }
    }
    boolean findMove_(SgfNode holder,SgfProperty move) {
        Logging.mainLogger.info("examing: "+this);
        for(Iterator<SgfProperty> i=properties.iterator();i.hasNext();) {
            SgfProperty property=i.next();
            P p=property.p();
            if(p instanceof Move&&(p==P.B||p==P.W)) {
                moves++;
                if(property.equals(move)) {
                    holder.left=this;
                    holder.properties.clear();
                    holder.properties.add(property);
                    Logging.mainLogger.info("foundmove: "+this+", lastMove="+property);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean findMove(SgfNode holder,SgfProperty move) {
        // of the main line i.e. only                                         // the first variation
        // maybe just find the n'th move?
        // what about ko? -  move will be the same?
        // only used by combine.
        SgfNode n=left;
        for(;n!=null&&n.right==null;n=n.left) if(n.findMove_(holder,move)) return true;
        for(;n!=null;n=n.right) {
            // maybe i don't have to look here?
            // (at least for iyt games)??
            if(n.findMove_(holder,move)) return true; /* maybe call findMove()? (recurse) */
            else break;
        }
        return false;
    }
    public SgfNode left() { return left; }
    public SgfNode right() { return right; }
    @Override public int hashCode() {
        final int prime=31;
        int result=1;
        result=prime*result+((properties==null)?0:properties.hashCode());
        return result;
    }
    public boolean deepEquals(SgfNode other) {
        if(this==other) return true;
        else if(other==null) return false;
        else if(!equals(other)) return false;
        if(left!=null) {
            boolean isEqual=left.deepEquals(other.left);
            if(!isEqual) return false;
        } else if(other.left!=null) return false;
        if(right!=null) {
            boolean isEqual=right.deepEquals(other.right);
            if(!isEqual) return false;
        } else if(other.right!=null) return false;
        return true;
    }
    @Override public boolean equals(Object obj) {
        if(this==obj) return true;
        else if(obj==null) return false;
        else if(getClass()!=obj.getClass()) return false;
        SgfNode other=(SgfNode)obj;
        if(properties==null) {
            if(other.properties!=null) return false;
        } else if(!properties.equals(other.properties)) return false;
        return true;
    }
    public static String sgfRoundTrip(String expectedSgf) { //restore and save
        if(expectedSgf==null) // hack for now
            return null;
        StringWriter stringWriter=new StringWriter();
        SgfNode games=null;
        String actualSgf=null;
        StringReader stringReader=new StringReader(expectedSgf);
        games=sgfRoundTrip(stringReader,stringWriter);
        actualSgf=stringWriter.toString();
        return actualSgf;
    }
    public static SgfNode sgfRoundTrip(Reader reader,Writer writer) {
        if(reader==null) return null;
        SgfNode games=restoreSgf(reader);
        if(games!=null) games.saveSgf(writer,noIndent);
        return games;
    }
    // lets try to make everyone use this one
    // except for maybe a restore then a save and a restore.
    // maybe add third trip?
    // like round trip o the nodes for above and on the sgf for below?
    public static SgfNode roundTrip(SgfNode expected,StringWriter stringWriter) {
        SgfNode actualSgf=null;
        if(expected!=null) {
            expected.saveSgf(stringWriter,noIndent);
            String sgf=stringWriter.toString();
            actualSgf=restoreSgf(new StringReader(sgf));
        }
        return actualSgf;
    }
    
    public static boolean sgfRoundTripTwice(Reader original) {
        Writer writer=new StringWriter();
        sgfRoundTrip(original,writer);
        String expected=writer.toString(); // cannonical form?
        writer=new StringWriter();
        //roundTrip();
        SgfNode games=restoreSgf(new StringReader(expected));
        if(games!=null) games.saveSgf(writer,noIndent);
        // allow null for now (11/8/22).
        String actual=writer.toString();
        if(!actual.equals(expected)) {
            parserLogger.severe(actual+"!="+original);
            //System.out.println("ex: "+expected);
            //System.out.println("ac: "+actual);
            return false;
        } else return true;
    }
    public static void main(String[] args) throws IOException {
        Set<String> keys=new LinkedHashSet(
                List.of("comments1","twoEmptyWithSemicolon","smartgo4","twosmallgamesflat","smartgo42"));
        for(Object key:Parser.sgfDataKeySet()) {
            System.out.println(key);
            String expected=getSgfData(key);
            System.out.println(expected);
            SgfNode games=restoreSgf(new StringReader(expected));
            StringWriter stringWriter=new StringWriter();
            games.saveSgf(stringWriter,standardIndent);
            String actual4=stringWriter.toString();
            System.out.println(actual4);
            System.out.println("------------");
        }
    }
    public ArrayList<SgfProperty> properties;
    // add an equal method and see what happens
    // maybe these could be immutable?
    public SgfNode left,right;
    boolean hasAMove,hasAMoveType,hasASetupType;
    Holder<Integer> siblings=new Holder<>(0),descendants=new Holder<Integer>(0);
    public static transient int moves;
    final int sgfId=sgfIds++;
    static int sgfIds;
    public static boolean ignoreMoveAndSetupFlags=true; // was false
}
