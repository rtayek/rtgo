package sgf;
import static io.IO.*;
import static io.Logging.parserLogger;
import static sgf.Parser.*;
import java.io.*;
//http://en.wikipedia.org/wiki/Binary_tree#Encoding_general_trees_as_binary_trees
//http://blogs.msdn.com/b/ericlippert/archive/2010/04/19/every-binary-tree-there-is.aspx
import java.util.*;
import io.*;
import model.MNodeAcceptor.MNodeFinder;
//  s      b       m       o
// txt -> sgf -> mnode -> model
// txt <- sgf <- mnode <- model
public class MNode {
    public MNode(MNode parent) { this.parent=parent; hasAMove=hasAMoveType=false; }
    public SgfNode toBinaryTree() {
        SgfNode left=null,tail=null;
        for(int i=0;i<children.size();++i) {
            if(i==0) {
                left=tail=children.get(i).toBinaryTree();
                // is this throwing if there is a variation on the first move in the game?
                if(left.right!=null) throw new RuntimeException("wierdness!");
            } else {
                SgfNode newRight=children.get(i).toBinaryTree();
                tail.right=newRight;
                tail=newRight;
            }
        }
        SgfNode binaryNode=new SgfNode(properties,null,left); // first child
        return binaryNode;
    }
    public static void setIsAMoveFlags(MNode node) {
        // copy of code in sgf node.
        // http://www.red-bean.com/sgf/user_guide/index.html#move_vs_place says: Therefore it's illegal to mix setup properties and move properties within the same node.
        for(SgfProperty property:node.properties) {
            if(property.p() instanceof Setup) node.hasASetupType=true;
            if(property.p() instanceof sgf.Move) { node.hasAMoveType=true; }
            if((property.p().equals(P.W)||property.p().equals(P.B))) node.hasAMove=true;
        }
        if(node.hasAMoveType&&node.hasASetupType) parserLogger.severe("node has move and setup type properties!");
    }
    private static MNode toGeneralTree(SgfNode binaryNode,MNode grandParent) {
        if(binaryNode==null) return null;
        MNode parent=new MNode(grandParent);
        if(grandParent!=null) grandParent.children.add(parent);
        else throw new RuntimeException("gradparent is null!");
        parent.properties.addAll(binaryNode.properties);
        setIsAMoveFlags(parent);
        if(binaryNode.left!=null) { @SuppressWarnings("unused") MNode child=toGeneralTree(binaryNode.left,parent); }
        if(binaryNode.right!=null) {
            @SuppressWarnings("unused") MNode child=toGeneralTree(binaryNode.right,grandParent);
        } else; //Logging.mainLogger.severe("binaryNode.right is null!");
        return parent;
    }
    public static MNode toGeneralTree(SgfNode binaryNode) {
        // this looks broken. see red bean test case.
        if(binaryNode==null) return null; // maybe return and empty root! (my MNode root)
        if(binaryNode.right!=null) { Logging.mainLogger.info("binaryNode.right is non null!"); }
        MNode mRoot=new MNode(null);
        try {
            SgfProperty property=new SgfProperty(P.RT,Arrays.asList(new String[] {"Tgo root"}));
            mRoot.properties.add(property);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        mRoot.data=mRoot.id; // just so it's not null
        //System.out.println("new root and children: "+mRoot+" "+mRoot.children);
        @SuppressWarnings("unused") MNode mNode=toGeneralTree(binaryNode,mRoot);
        return mRoot;
    }
    public List<MNode> lca(MNode current,MNode target) {
        // another find in sgf!
        MNodeFinder finder=MNodeFinder.find(current,this);
        MNodeFinder targetFinder=MNodeFinder.find(target,this);
        int n=Math.min(targetFinder.ancestors.size(),finder.ancestors.size());
        MNode lca=null;
        for(int i=0;i<n;i++)
            if(finder.ancestors.get(i).equals(targetFinder.ancestors.get(i))) lca=targetFinder.ancestors.get(i);
        if(lca!=null) {
            int index=targetFinder.ancestors.indexOf(lca);
            return targetFinder.ancestors.subList(index,targetFinder.ancestors.size());
        } else return null;
    }
    public static MNode restore(Reader reader) {
        SgfNode games=restoreSgf(reader);
        if(games!=null&&games.right()!=null) System.out.println("root has variations!");
        MNode node=toGeneralTree(games);
        return node;
    }
    public static boolean save(Writer writer,MNode root,Indent indent) {
        // this fails to save if there are variations on the *first* move!
        // seems like it fails if the are any variations!
        // was failing because we did not add the extra root node.
        if(indent==null) indent=new Indent(IO.standardIndent);
        SgfNode games=null;
        if(root!=null) {
            int children=root.children.size();
            games=root.toBinaryTree(); // ths is where root gets tossed.
            // that's why none of the sgf in the root is not coming out.
            //System.out.println("discarding: "+root);
            if(games.left!=null) games.left.saveSgf(writer,indent);
            Logging.mainLogger.info("games.right: "+games.right);
        }
        return true; // for now
    }
    public static MNode mNodeRoundTrip(StringReader stringReader,StringWriter stringWriter) {
        MNode root=MNode.restore(stringReader);
        boolean ok=MNode.save(stringWriter,root,noIndent);
        if(!ok) System.out.println("not ok!");
        return root;
    }

    public static MNode quietLoad(Reader reader) {
        PrintStream old=System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream(1_000_000)));
        MNode root=MNode.restore(reader);
        System.out.close();
        System.setOut(old);
        return root;
    }
    private static MNode quietLoad(File file) {
        Logging.mainLogger.info("loading: "+file);
        Reader reader=IO.toReader(file);
        MNode root=quietLoad(reader);
        return root;
    }
    @Override public String toString() {
        StringBuffer stringBuffer=new StringBuffer(";");
        if(data!=null) stringBuffer.append("("+data+")");
        for(Iterator<SgfProperty> i=properties.iterator();i.hasNext();) stringBuffer.append(i.next().toString());
        return stringBuffer.toString();
    }
    public static Collection<SgfNode> findPathToNode(MNode target,SgfNode root) {
        SgfNodeFinder finder=SgfNodeFinder.finder(target.toBinaryTree(),root);
        Collection<SgfNode> path=finder.pathToTarget;
        return path;
    }
    
    public static void main(String[] args) {
        System.out.println(Init.first);
        //lookAtRoot();
        String oneGame="(;GM[1];B[as];B[at])";
        System.out.println(oneGame);
        MNode root=MNode.restore(new StringReader(oneGame));
        boolean ok=MNode.save(new PrintWriter(System.out),root,standardIndent);
        String expected=getSgfData("smartgo43");
        System.out.println(expected);
        root=MNode.restore(new StringReader(expected));
        ok=MNode.save(new PrintWriter(System.out),root,standardIndent);
    }
    public Integer data;
    public Integer id=++ids;
    public final MNode parent;
    public ArrayList<MNode> children=new ArrayList<>();
    //public /*final*/ boolean hasAMove,hasAMoveType;
    boolean hasAMove,hasAMoveType,hasASetupType;
    public final List<SgfProperty> properties=new ArrayList<>();
    // maybe these could be/should immutable?
    public static int ids;
}
