package sgf;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import java.io.StringReader;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import model.MNodeAcceptor.MakeList;
import model.Model;
import utilities.*;
@RunWith(Parameterized.class) public class CannonicalTestCase {
	public CannonicalTestCase(Object key) {
		this.key=key;
		watcher.key=key;
	}
	@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
	@Parameterized.Parameters(name="{0}") public static Collection<Object[]> parameters() {
		Set<Object> objects=new LinkedHashSet<>();
		objects.addAll(sgfDataKeySet());
		objects.addAll(sgfFiles());
		return ParameterArray.parameterize(objects);
	}
	@Before public void setUp() throws Exception {
		expectedSgf=getSgfData(key);
		// no prepare here!
	}
	@After public void tearDown() throws Exception {}
	@Test public void testThatGeneralTreeAlwaysHasRTProperty() {
		// this will depend on whether the add new rot switch is on.
		SgfNode games=restoreSgf(new StringReader(expectedSgf));
		MNode root=MNode.toGeneralTree(games);
		// this mnay not be present
		// check the add new root flag in mnode.
		if(root!=null) {
			SgfProperty property=root.sgfProperties().get(0);
			assertEquals(P.RT,property.p());
		}
	}
	@Test public void testSaveMultupleGames() {
		Model model=new Model();
		model.restore(new StringReader(expectedSgf));
		boolean hasMultipleGames=model.root().children().size()>1;
		String sgfString=model.save();
		boolean containsRTNode=sgfString.contains("RT[Tgo root]");
		// when games.right!=null ==> multiple games
		// amd we end up with an RT in the sgf!
		// assertEquals(hasMultipleGames,containsRTNode);
		// but we do not want the RT node in the sgf!
		// need to check the add new root switch.
		// 11/28/22 seems like we are doing this somewhere else.
	}
	@Ignore @Test() public void testLeastCommonAncester() { // slow, so ignore
															// for now.
		// ignoring for now as it is slow
		// seems to be working for multiple games
		MNode root=MNode.restore(new StringReader(expectedSgf));
		boolean hasMultipleGames=root!=null&&root.children().size()>1;
		assertNotNull(root);
		List<MNode> list1=MakeList.toList(root);
		List<MNode> list2=MakeList.toList(root);
		// PrintStream x=System.out;
		// System.setOut(new PrintStream(new ByteArrayOutputStream(1_000_000)));
		if(hasMultipleGames) {
			// System.out.println(originalSgf);
			// System.out.println("list1: "+list1);
		}
		for(MNode node1:list1) //
			for(MNode node2:list2) //
				if(!node1.equals(node2)) { //
					List<MNode> list=root.lca(node1,node2);
					// System.out.println(node1+" "+node2+" "+list);
					assertNotNull(key.toString(),list);
				}
		// System.setOut(x);
	}
	Object key;
	String expectedSgf;
	static final Set<String> paths=new LinkedHashSet<>();
}
