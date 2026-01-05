package sgf;
import static io.IOs.*;
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
		// http://www.red-bean.com/sgf/user_guide/index.html#move_vs_place says:
		// Therefore it's illegal to mix setup properties and move properties
		// within the same node.
		for(SgfProperty sgfProperty:sgfProperties) {
			if(sgfProperty.p() instanceof Setup) hasASetupType=true;
			if(sgfProperty.p() instanceof sgf.Move) {
				hasAMoveType=true;
			}
			if((sgfProperty.p().equals(P.W)||sgfProperty.p().equals(P.B))) hasAMove=true;
		}
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
		if(hasAMoveType&&hasASetupType) {
			parserLogger.severe("node has move and setup type properties!");
			System.out.println("node has move and setup type properties!");
			if(!ignoreMoveAndSetupFlags) {
				IOs.stackTrace(10);
				System.exit(1);
			}
			ok=false;
		}
		return ok;
	}
	// standard convention
	// left pointer → first child
	// right pointer → next sibling
	// what have we been doing?
	// https://chatgpt.com/share/6958f58b-fb8c-8008-a2a5-3ef7cc6ce710
	public SgfNode toBinaryTree() {
		SgfNode left=null,tail=null;
		for(int i=0;i<children.size();++i) {
			MNode child=children.get(i);
			if(i==0) {
				if(child!=null) {
					left=tail=child.toBinaryTree();
					if(left.right!=null) throw new RuntimeException("wierdness!");
				} else System.out.println("first chile is null!");
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
			if(grandParent!=null) grandParent.children.add(null);
			return null;
		}
		MNode parent=new MNode(grandParent);
		if(grandParent!=null) grandParent.children.add(parent);
		else throw new RuntimeException("gradparent is null!");
		parent.sgfProperties.addAll(node.sgfProperties);
		if(node!=null) {
			node.setFlags();
			boolean ok=node.checkFlags();
			if(!ok) System.out.println("node has move and setup type properties!");
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
		MNode sentenel=new MNode(null);
		// 1/21/23
		// maybe we don't need and extra node if we already have one?
		// maybe this can not happen?
		// apparently there is a way and we ar not doing it now.
		try {
			SgfProperty property=new SgfProperty(P.RT,Arrays.asList(new String[] {"Tgo root"}));
			sentenel.sgfProperties.add(property);
			Logging.mainLogger.info("toGeneralTree() added RT property to extra root node");
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		sentenel.data=sentenel.id; // just so it's not null
		// System.out.println("new root and children: "+mRoot+"
		// "+mRoot.children);
		@SuppressWarnings("unused") MNode mNode=toGeneralTree(extra.left,sentenel);
		return sentenel;
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
		if(indent==null) indent=new Indent(IOs.standardIndent);
		SgfNode games=null;
		if(root!=null) {
			int children=root.children.size();
			games=root.toBinaryTree(); // ths is where root gets tossed.
			// that's why none of the sgf in the root is not coming out.
			// System.out.println("discarding: "+root);
			if(games.left!=null) games.left.saveSgf(writer,indent);
			Logging.mainLogger.info("games.right: "+games.right);
		}
		return true; // for now
	}
	public static MNode mNodeRoundTrip(StringReader stringReader,StringWriter stringWriter) {
		return SgfRoundTrip.mNodeRoundTrip(stringReader,stringWriter,SgfRoundTrip.MNodeSaveMode.standard);
	}
	public static MNode mNodeDirectRoundTrip(StringReader stringReader,StringWriter stringWriter) {
		return SgfRoundTrip.mNodeRoundTrip(stringReader,stringWriter,SgfRoundTrip.MNodeSaveMode.direct);
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
		Reader reader=IOs.toReader(file);
		MNode root=quietLoad(reader);
		return root;
	}
	@Override public String toString() {
		StringBuffer stringBuffer=new StringBuffer(";");
		if(data!=null) stringBuffer.append("("+data+")");
		for(Iterator<SgfProperty> i=sgfProperties.iterator();i.hasNext();)
			stringBuffer.append(i.next().toString());
		for(Iterator<SgfProperty> i=extraProperties.iterator();i.hasNext();)
			stringBuffer.append(i.next().toString());
		return stringBuffer.toString();
	}
	public static Collection<SgfNode> findPathToNode(MNode target,SgfNode root) {
		SgfNodeFinder finder=SgfNodeFinder.finder(target.toBinaryTree(),root);
		Collection<SgfNode> path=finder.pathToTarget;
		return path;
	}
	public static String toString(MNode mNode) {
		StringBuffer stringBuffer=new StringBuffer(";");
		// if(false) { stringBuffer.append("{id="+sgfId);
		// stringBuffer.append("}"); }
		for(Iterator<SgfProperty> i=mNode.sgfProperties.iterator();i.hasNext();)
			stringBuffer.append(i.next().toString());
		return stringBuffer.toString();
	}
	private static void saveDirectly_(Writer writer,MNode root,Indent indent) throws IOException {
		if(root!=null) {
			writer.write(toString(root));
			ArrayList<MNode> children=root.children;
			int n=children.size();
			for(int i=0;i<children.size();++i) {
				if(n>1) writer.write(indent.indent()+'(');
				saveDirectly_(writer,children.get(i),indent);
				if(n>1) writer.write(indent.indent()+')');
			}
		}
		return;
	}
	public static void saveDirectly(Writer writer,MNode root,Indent indent) throws IOException {
		if(root==null) return;
		if(indent==null) indent=new Indent(IOs.standardIndent);
		writer.write(indent.indent()+'(');
		saveDirectly_(writer,root,indent);
		writer.write(indent.indent()+')');
	}
	public static MNode restoreRedBean() {
		String expectedSgf=Parser.sgfExamleFromRedBean;
		MNode mNode=restore(new StringReader(expectedSgf));
		return mNode;
	}
	public static String saveDirectly(MNode mNode) {
		StringWriter stringWriter=new StringWriter();
		for(MNode child:mNode.children) // save all of the games
			try {
				saveDirectly(stringWriter,child,noIndent);
			} catch(IOException e) {
				System.out.println("caught: "+e);
			}
		return stringWriter.toString();
	}
	public static void label(MNode node,final Iterator<Long> i) { // traverse
																	// and set
																	// labels
		node.label=i.next();
		for(MNode n:node.children)
			label(n,i);
	}
	public static void main(String[] args) throws IOException {
		System.out.println(Init.first);
		if(true) {
			MNode mNode=restoreRedBean();
			String saved=saveDirectly(mNode);
			System.out.println("saved directly: "+saved);
			return;
		}
		// lookAtRoot();
		String oneGame="(;GM[1];B[as];B[at])";
		System.out.println(oneGame);
		MNode root=MNode.restore(new StringReader(oneGame));
		boolean ok=MNode.save(new PrintWriter(System.out),root,standardIndent);
		String expected=getSgfData("smartgo43");
		System.out.println(expected);
		root=MNode.restore(new StringReader(expected));
		ok=MNode.save(new PrintWriter(System.out),root,standardIndent);
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
