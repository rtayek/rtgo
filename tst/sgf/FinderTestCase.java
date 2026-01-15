package sgf;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import equipment.*;
import model.Model;
import utilities.*;
class Verifier extends SgfAcceptorImpl {
	Verifier(SgfNode root) {
		this.root=root;
	}
	@Override public void accept(SgfNode target) {
		SgfNodeFinder finder=SgfNodeFinder.finder(target,root);
		finder.checkMove();
		assertTrue(finder.found!=null);
		assertEquals(target,finder.found);
	}
	final SgfNode root;
}
@RunWith(Parameterized.class) public class FinderTestCase extends AbstractAllSgfFixtureTestCase {
	public static void verify(SgfNode games) {
		Verifier verifier=new Verifier(games);
		SgfTestSupport.traverse(verifier,games);
    }
    @Test public void testFinderWithSimple() {
        games=SgfTestSupport.restoreFromKey(TestKeys.simpleWithVariations);
        verify(games);
    }
	// move these out of this parameterized test case!
	@Test public void testFinderWith3Moves() {
		Model model=new Model();
		model.move(Stone.black,new Point(0,0));
		model.move(Stone.white,new Point(0,1)); // fails if black - check later
		model.move(Stone.black,new Point(0,2));
		MNode root=model.root();
		games=root.toBinaryTree();
		verify(games);
	}
	// add tests for all of the sgf files we have
	@Test public void testFinder() {
		// File file=new File(Parser.map.get(key));
		games=restoreAndTraverse(Verifier::new);
	}
	SgfNode games;
	boolean old=false;
}
