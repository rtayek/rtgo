package sgf;
import io.Logging;
import static org.junit.Assert.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import model.MNodeAcceptor.MakeList;
import model.Model;
import utilities.*;
@RunWith(Parameterized.class) public class CannonicalTestCase extends AbstractAllSgfFixtureTestCase {
	@Test public void testThatGeneralTreeAlwaysHasRTProperty() {
		// this will depend on whether the add new rot switch is on.
        SgfNode games=restoreExpectedSgf();
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
        ModelTestIo.restore(model,expectedSgf);
        boolean hasMultipleGames=model.root().children().size()>1;
        String sgfString=model.save();
		boolean containsRTNode=sgfString.contains("RT[Tgo root]");
		// when games.right!=null ==> multiple games
		// and we end up with an RT in the sgf!
		// assertEquals(hasMultipleGames,containsRTNode);
		// but we do not want the RT node in the sgf!
		// need to check the add new root switch.
		// 11/28/22 seems like we are doing this somewhere else.
	}
	@Ignore @Test() public void testLeastCommonAncester() { // slow, so ignore
															// for now.
		// ignoring for now as it is slow
		// seems to be working for multiple games
        MNode root=SgfTestIo.restoreMNode(expectedSgf);
		boolean hasMultipleGames=root!=null&&root.children().size()>1;
		assertNotNull(root);
		List<MNode> list1=MakeList.toList(root);
		List<MNode> list2=MakeList.toList(root);
		// System.setOut(new PrintStream(new ByteArrayOutputStream(1_000_000)));
		if(hasMultipleGames) {
			// Logging.mainLogger.info(originalSgf);
			// Logging.mainLogger.info("list1: "+list1);
		}
		for(MNode node1:list1) //
			for(MNode node2:list2) //
				if(!node1.equals(node2)) { //
					List<MNode> list=root.lca(node1,node2);
					// Logging.mainLogger.info(node1+" "+node2+" "+list);
					assertNotNull(key.toString(),list);
				}
		// System.setOut(x);
	}
	static final Set<String> paths=new LinkedHashSet<>();
}
