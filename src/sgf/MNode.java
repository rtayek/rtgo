package sgf;
import static io.IOs.*;
import static io.Logging.parserLogger;
import static sgf.Parser.*;
import static com.tayek.util.io.FileIO.toReader;
import java.io.*;
//http://en.wikipedia.org/wiki/Binary_tree#Encoding_general_trees_as_binary_trees
//http://blogs.msdn.com/b/ericlippert/archive/2010/04/19/every-binary-tree-there-is.aspx
import java.util.*;
import com.tayek.util.io.Indent;
import io.*;
import model.ModelIo;
import model.MNodeAcceptor.MNodeFinder;
//  s      b       m       o
// txt -> sgf -> mnode -> model
// txt <- sgf <- mnode <- model
public class MNode {
	public MNode(MNode parent) {
		this.parent=parent;
		hasAMove=hasAMoveType=false;
	}
	public MNode(MNode parent,List<SgfProperty> sgfProperties) { // just for
																	// testing
		this(parent);
		this.sgfProperties.addAll(sgfProperties);
	}
	public void setFlags() {
		PropertyFlags.Flags flags=PropertyFlags.analyze(sgfProperties);
		hasAMove=flags.hasAMove;
		hasAMoveType=flags.hasAMoveType;
		hasASetupType=flags.hasASetupType;
	}
	@Override public int hashCode() {
		return Objects.hash(sgfProperties,extraProperties);
		// remember to fix this when we move to generics
	}
	@Override public boolean equals(Object obj) {
		if(this==obj) return true;
		if(obj==null) return false;
		if(getClass()!=obj.getClass()) return false;
		MNode other=(MNode)obj;
		return Objects.equals(sgfProperties,other.sgfProperties)&&Objects.equals(extraProperties,other.extraProperties);
	}
	boolean checkFlags() {
		boolean ok=true;
		if(PropertyFlags.hasMixedMoveAndSetup(hasAMoveType,hasASetupType)) {
			parserLogger.severe("node has move and setup type properties!");
			Logging.mainLogger.info("node has move and setup type properties!");
			if(!ignoreMoveAndSetupFlags) {
				IOs.stackTrace(10);
				System.exit(1);
			}
			ok=false;
		}
		return ok;
	}
	public SgfNode toBinaryTree() {
	// standard convention
	// left pointer → first child
	// right pointer → next sibling
	// what have we been doing?
	// https://chatgpt.com/share/6958f58b-fb8c-8008-a2a5-3ef7cc6ce710
		SgfNode left=null,tail=null;
		for(int i=0;i<children.size();++i) {
			MNode child=children.get(i);
			if(i==0) {
				if(child!=null) {
					left=tail=child.toBinaryTree();
					if(left.right!=null) throw new RuntimeException("weirdness!");
				} else Logging.mainLogger.info("first child is null!");
			} else {
				SgfNode newRight=child.toBinaryTree();
				tail.right=newRight;
				tail=newRight;
			}
		}
		List<SgfProperty> allProps=new ArrayList<>(sgfProperties.size()+extraProperties.size());
		allProps.addAll(sgfProperties);
		allProps.addAll(extraProperties);
		SgfNode node=new SgfNode(allProps,null,left); // first child
		return node;
	}
	private static MNode toGeneralTree(SgfNode node,MNode grandParent) {
		if(node==null) {
			//if(true) throw new RuntimeException("node is null in toGeneralTree()");
			if(grandParent!=null) {
				System.out.println("adding null child to grandparent.");
				grandParent.children.add(null);
			}
			return null;
		}
		MNode parent=new MNode(grandParent);
		if(grandParent!=null) grandParent.children.add(parent);
		else throw new RuntimeException("gradparent is null!");
		parent.sgfProperties.addAll(node.sgfProperties);
		if(node!=null) {
			node.setFlags();
			boolean ok=node.checkFlags();
			if(!ok) Logging.mainLogger.info("node has move and setup type properties!");
		}
		if(node.left!=null) {
			@SuppressWarnings("unused") MNode child=toGeneralTree(node.left,parent);
		}
		if(node.right!=null) {
			@SuppressWarnings("unused") MNode child=toGeneralTree(node.right,grandParent);
		} else; // Logging.mainLogger.severe("binaryNode.right is null!");
		return parent;
	}
	public static MNode toGeneralTree(SgfNode node) {
		// this looks broken. see red bean test case.
		// if(node==null) return null; // maybe return and empty root! (my MNode
		// root)
		if(node!=null&&node.right!=null) {
			Logging.mainLogger.info("binaryNode.right is non null!");
		}
		SgfNode extra=new SgfNode();
		extra.left=node; // might be null
		MNode sentinel=new MNode(null);
		// 1/21/23
		// maybe we don't need and extra node if we already have one?
		// maybe this can not happen?
		// apparently there is a way and we are not doing it now.
        try {
            // RT is a sentinel extra-root marker; it is a no-op in the engine and must round-trip unchanged.
            SgfProperty property=new SgfProperty(P.RT,Arrays.asList(new String[] {"Tgo root"}));
			sentinel.sgfProperties.add(property);
			Logging.mainLogger.info("toGeneralTree() added RT property to extra root node");
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		sentinel.data=sentinel.id; // just so it's not null
		// Logging.mainLogger.info("new root and children: "+mRoot+"
		// "+mRoot.children);
		@SuppressWarnings("unused") MNode mNode=toGeneralTree(extra.left,sentinel);
		return sentinel;
	}
	public List<MNode> lca(MNode current,MNode target) {
		// another find in sgf!
		MNodeFinder finder=MNodeFinder.find(current,this);
		MNodeFinder targetFinder=MNodeFinder.find(target,this);
		int n=Math.min(targetFinder.ancestors.size(),finder.ancestors.size());
		MNode lca=null;
		for(int i=0;i<n;i++)
			if(finder.ancestors.get(i).equals(targetFinder.ancestors.get(i))) lca=targetFinder.ancestors.get(i);
		// see if the above is really just using ==.
		// it was, now it uses the properties in equals.
		if(lca!=null) {
			int index=targetFinder.ancestors.indexOf(lca);
			return targetFinder.ancestors.subList(index,targetFinder.ancestors.size());
		} else return null;
	}
	public static MNode restoreMNodes(Reader reader) {
		SgfNode games=new Parser().parse(reader);
		if(games!=null&&games.right()!=null) Logging.mainLogger.info("root has variations!");
		MNode node=toGeneralTree(games);
		return node;
	}
	public static boolean saveMNodes(Writer writer,MNode root,Indent indent) {
		// this fails to save if there are variations on the *first* move!
		// seems like it fails if the are any variations!
		// was failing because we did not add the extra root node.
		if(indent==null) indent=new Indent(IOs.standardIndent);
		SgfNode games=null;
		if(root!=null) {
			int children=root.children.size();
			games=root.toBinaryTree(); // this is where root gets tossed.
			// that's why none of the sgf in the root is not coming out.
			// Logging.mainLogger.info("discarding: "+root);
			if(games.left!=null) games.left.saveSgf(writer,indent);
			Logging.mainLogger.info("games.right: "+games.right);
		}
		return true; // for now
	}
	public static MNode restoreMNodesQuietly(Reader reader) {
		PrintStream old=System.out;
		System.setOut(new PrintStream(new ByteArrayOutputStream(1_000_000)));
		MNode root=MNode.restoreMNodes(reader);
		System.out.close();
		System.setOut(old);
		return root;
	}
	@Override public String toString() {
		StringBuffer stringBuffer=new StringBuffer(";");
		if(data!=null) stringBuffer.append("("+data+")");
		for(Iterator<SgfProperty> i=sgfProperties.iterator();i.hasNext();)
			stringBuffer.append(i.next().toString());
		// these extra properties are new (january 2026).
		for(Iterator<SgfProperty> i=extraProperties.iterator();i.hasNext();)
			stringBuffer.append(i.next().toString());
		return stringBuffer.toString();
	}
	public static Collection<SgfNode> findPathToNode(MNode target,SgfNode root) {
		SgfNodeFinder finder=SgfNodeFinder.finder(target.toBinaryTree(),root);
		Collection<SgfNode> path=finder.pathToTarget;
		return path;
	}
	private static void saveMNode(Writer writer,MNode node) throws IOException {
		// intentionally emits only SGF properties, not extra properties.
		writer.write(';');
		for(Iterator<SgfProperty> i=node.sgfProperties.iterator();i.hasNext();)
			writer.write(i.next().toString());
	}
	private static void saveMNodesDirectly_(Writer writer,MNode root,Indent indent) throws IOException {
		if(root!=null) {
			saveMNode(writer,root);
			ArrayList<MNode> children=root.children;
			int n=children.size();
			for(int i=0;i<children.size();++i) {
				if(n>1) writer.write(indent.indent()+'(');
				saveMNodesDirectly_(writer,children.get(i),indent);
				if(n>1) writer.write(indent.indent()+')');
			}
		}
		return;
	}
	public static void saveMNodesDirectly(Writer writer,MNode root,Indent indent) throws IOException {
		if(root==null) return;
		if(indent==null) indent=new Indent(IOs.standardIndent);
		writer.write(indent.indent()+'(');
		saveMNodesDirectly_(writer,root,indent);
		writer.write(indent.indent()+')');
	}
	public static void label(MNode node,final Iterator<Long> i) { // traverse
																	// and set
																	// labels
		node.label=i.next();
		for(MNode n:node.children)
			label(n,i);
	}
	public static void main(String[] args) throws IOException {
		Logging.mainLogger.info(String.valueOf(Init.first));
		if(true) {
			String expectedSgf=Parser.sgfExamleFromRedBean;
			MNode mNode=restoreMNodes(toReader(expectedSgf));
			String saved=ModelIo.saveMNodesDirectlyToString(mNode);
			Logging.mainLogger.info("saved directly: "+saved);
			return;
		}
		// lookAtRoot();
		String oneGame="(;GM[1];B[as];B[at])";
		Logging.mainLogger.info(String.valueOf(oneGame));
		MNode root=MNode.restoreMNodes(toReader(oneGame));
		boolean ok=MNode.saveMNodes(new PrintWriter(System.out),root,standardIndent);
		String expected=getSgfData("smartgo43");
		Logging.mainLogger.info(String.valueOf(expected));
		root=MNode.restoreMNodes(toReader(expected));
		ok=MNode.saveMNodes(new PrintWriter(System.out),root,standardIndent);
	}
	public boolean hasAMove() {
		return hasAMove;
	}
	public ArrayList<MNode> children() {
		return children;
	}
	public boolean hasAMoveType() {
		return hasAMoveType;
	}
	public Long label() {
		return label;
	}
	public void setLabel(Long label) {
		this.label=label;
	}
	public List<SgfProperty> sgfProperties() {
		return sgfProperties;
	}
	public List<SgfProperty> extraProperties() {
		return extraProperties;
	}
	public void addExtraProperty(SgfProperty property) {
		extraProperties.add(property);
	}
	public MNode parent() {
		return parent;
	}
	private Long label;
	private Integer data;
	private final Integer id=++ids;
	private final MNode parent;
	private final ArrayList<MNode> children=new ArrayList<>();
	// public /*final*/ boolean hasAMove,hasAMoveType;
	private boolean hasAMove,hasAMoveType,hasASetupType;
	private final List<SgfProperty> sgfProperties=new ArrayList<>();
	private final List<SgfProperty> extraProperties=new ArrayList<>();
	// maybe these could be/should immutable?
	private static boolean ignoreMoveAndSetupFlags=true; // was false
	private static int ids;
}

