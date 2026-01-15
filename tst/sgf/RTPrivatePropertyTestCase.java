package sgf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utilities.*;
@RunWith(Parameterized.class) public class RTPrivatePropertyTestCase extends AbstractAllSgfFixtureTestCase {
	@Test public void testMultipleGames() { // how does it do that?
		String actualSgf=SgfTestSupport.restoreAndSave(expectedSgf);
		// assertFalse(expectedSgf.contains(P.RT.toString()));
		// why would we expect this?
	}
}
