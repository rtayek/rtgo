package tree;
import static org.junit.Assert.*;
import static tree.Catalan.*;
import static tree.G2.roundTripLong;
import static tree.Node.*;
import static utilities.ParameterArray.modulo;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
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
	@Before public void setUp() throws Exception {}
	@After public void tearDown() throws Exception {}
	@Test public void testArrayValueEqualsCalculatedValue() {
		// System.out.println("equal "+(catalans[nodes]==catalan2(nodes)));
		assertEquals(catalans[nodes],catalan2(nodes));
	}
	@Test public void testBotheCalculationsAgree() {
		long c1=catalan(nodes);
		long c2=catalan2(nodes);
		if(c1<0) System.out.println("catalan("+nodes+") fails!");
		if(c2<0) System.out.println("catalan2("+nodes+") fails!");
		if(c1<0||c1<0); // check for overflow
		else assertEquals(catalan(nodes),Catalan.catalan2(nodes));
	}
	@Test public void testEncodeEncode() {
		ArrayList<Node<Long>> trees=Generator.<Long>one(nodes,iterator,false);
		for(Node<Long> expected:trees) {
			String encodedd=encode(expected,null);
			Node<Long> acatual=decode(encodedd,null);
			assertTrue(structureDeepEquals(expected,acatual));
		}
	}
	@Test public void testCopy() {
		ArrayList<Node<Long>> trees=Generator.one(nodes,iterator,false);
		for(Node<Long> expected:trees) {
			// if(expected==null) continue; // looks like we need this.
			Node<Long> actual=copy(expected);
			assertTrue(structureDeepEquals(expected,actual));
			ArrayList<Long> data2=new ArrayList<>();
			String expectedEncoded=encode(expected,data2);
			String actualEncoded=encode(actual,data2);
			assertEquals(expectedEncoded,actualEncoded);
			assertTrue(deepEquals(expected,actual));
		}
	}
	@Test public void testRelabel() {
		System.out.println(nodes+" nodes.");
		ArrayList<Node<Long>> trees=Generator.one(nodes,iterator,false);
		for(Node<Long> expected:trees) {
			System.out.println("ex: "+G2.pPrint(expected));
			Iterator<Character> j=new Characters();
			Node<Character> actual=reLabelCopy(expected,j);
			System.out.println("ac:"+G2.pPrint(actual));
			Iterator<Long> i=new Longs();
			Node<Long> actual2=reLabelCopy(expected,i);
			Iterator<Character> k=new Characters();
			Node<Character> actual3=reLabelCopy(actual2,k);
			System.out.println("a3:"+G2.pPrint(actual3));
			assertTrue(structureDeepEquals(actual,actual3));
			assertTrue(deepEquals(actual,actual3));
		}
	}
	@Test public void testCheck() {
		ArrayList<Node<Long>> trees=Generator.one(nodes,iterator,false);
		for(Node<Long> node:trees) {
			int n=check(node);
			assertEquals(0,n);
		}
	}
	@Test public void testLongRoundTrip() {
		ArrayList<Node<Long>> trees=Generator.one(nodes,iterator,false);
		for(Node<Long> node:trees) {
			// if(node==null) continue; // looks like we need this.
			// this is a round trip
			String expected=encode(node,null);
			// want to pass data to the deocde in here
			String actual=roundTripLong(expected);
			assertEquals(expected,actual);
		}
	}
	@Test public void testMirrorRoundTrip() { // do we need this?
		// look for duplicate code in node!
		ArrayList<Node<Long>> trees=Generator.one(nodes,iterator,false);
		for(Node<Long> node:trees) {
			if(node==null) continue;
			mirror(node);
			// this is a round trip
			String expected=encode(node,null);
			// want to pass data to the deocde in here
			String actual=roundTripLong(expected);
			assertEquals(expected,actual);
		}
	}
	@Test public void testGenerate() {
		ArrayList<Node<Long>> trees=Generator.one(nodes,iterator,false);
		assertEquals(catalan2(nodes),trees.size());
	}
	int nodes;
	Iterator<Long> iterator=new Longs();
	public static final int max=7; // 11
}
