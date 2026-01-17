package tree;
import io.Logging;
import static org.junit.Assert.*;
import static tree.Catalan.*;
import static tree.G2.roundTripLong;
import static tree.Node.*;
import static utilities.ParameterArray.modulo;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import tree.G2.Generator;
import utilities.Iterators.*;
import utilities.MyTestWatcher;
@RunWith(Parameterized.class) public class CatalanTestCase {
	@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
	public CatalanTestCase(int nodes) {
		this.nodes=nodes;
	}
	// @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
	@Parameterized.Parameters(name="{0}") public static Collection<Object[]> parameters() {
		return modulo(max+1);
	}
	@Test public void testArrayValueEqualsCalculatedValue() {
		// Logging.mainLogger.info("equal "+(catalans[nodes]==catalan2(nodes)));
		assertEquals(catalans[nodes],catalan2(nodes));
	}
	@Test public void testBotheCalculationsAgree() {
		long c1=catalan(nodes);
		long c2=catalan2(nodes);
		if(c1<0) Logging.mainLogger.info("catalan("+nodes+") fails!");
		if(c2<0) Logging.mainLogger.info("catalan2("+nodes+") fails!");
		if(c1<0||c1<0); // check for overflow
		else assertEquals(catalan(nodes),Catalan.catalan2(nodes));
	}
	@Test public void testEncodeEncode() {
		forEachTree(new TreeVisitor() {
			@Override public void visit(Node<Long> expected) {
				String encodedd=encode(expected,null);
				Node<Long> acatual=decode(encodedd,null);
				assertTrue(structureDeepEquals(expected,acatual));
			}
		});
	}
	@Test public void testCopy() {
		forEachTree(new TreeVisitor() {
			@Override public void visit(Node<Long> expected) {
				// if(expected==null) continue; // looks like we need this.
				Node<Long> actual=copy(expected);
				assertTrue(structureDeepEquals(expected,actual));
				ArrayList<Long> data2=new ArrayList<>();
				String expectedEncoded=encode(expected,data2);
				String actualEncoded=encode(actual,data2);
				assertEquals(expectedEncoded,actualEncoded);
				assertTrue(deepEquals(expected,actual));
			}
		});
	}
	@Test public void testRelabel() {
		Logging.mainLogger.info(nodes+" nodes.");
		forEachTree(new TreeVisitor() {
			@Override public void visit(Node<Long> expected) {
				Logging.mainLogger.info("ex: "+G2.pPrint(expected));
				Iterator<Character> j=new Characters();
				Node<Character> actual=reLabelCopy(expected,j);
				Logging.mainLogger.info("ac:"+G2.pPrint(actual));
				Iterator<Long> i=new Longs();
				Node<Long> actual2=reLabelCopy(expected,i);
				Iterator<Character> k=new Characters();
				Node<Character> actual3=reLabelCopy(actual2,k);
				Logging.mainLogger.info("a3:"+G2.pPrint(actual3));
				assertTrue(structureDeepEquals(actual,actual3));
				assertTrue(deepEquals(actual,actual3));
			}
		});
	}
	@Test public void testCheck() {
		forEachTree(new TreeVisitor() {
			@Override public void visit(Node<Long> node) {
				int n=check(node);
				assertEquals(0,n);
			}
		});
	}
	@Test public void testLongRoundTrip() {
		forEachTree(new TreeVisitor() {
			@Override public void visit(Node<Long> node) {
				// if(node==null) continue; // looks like we need this.
				// this is a round trip
				String expected=encode(node,null);
				// want to pass data to the deocde in here
				String actual=roundTripLong(expected);
				assertEquals(expected,actual);
			}
		});
	}
	@Test public void testMirrorRoundTrip() { // do we need this?
		// look for duplicate code in node!
		forEachTree(true,new TreeVisitor() {
			@Override public void visit(Node<Long> node) {
				mirror(node);
				// this is a round trip
				String expected=encode(node,null);
				// want to pass data to the deocde in here
				String actual=roundTripLong(expected);
				assertEquals(expected,actual);
			}
		});
	}
	@Test public void testGenerate() {
		ArrayList<Node<Long>> trees=buildTrees();
		assertEquals(catalan2(nodes),trees.size());
	}
	private ArrayList<Node<Long>> buildTrees() {
		return Generator.one(nodes,iterator,false);
	}
	private void forEachTree(TreeVisitor visitor) {
		forEachTree(false,visitor);
	}
	private void forEachTree(boolean skipNull,TreeVisitor visitor) {
		ArrayList<Node<Long>> trees=buildTrees();
		for(Node<Long> node:trees) {
			if(skipNull&&node==null) continue;
			visitor.visit(node);
		}
	}
	private interface TreeVisitor {
		void visit(Node<Long> node);
	}
	int nodes;
	Iterator<Long> iterator=new Longs();
	public static final int max=7; // 11
}
