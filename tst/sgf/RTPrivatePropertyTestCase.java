package sgf;
import static org.junit.Assert.assertEquals;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utilities.*;
@RunWith(Parameterized.class) public class RTPrivatePropertyTestCase extends AbstractSgfFixtureTestCase {
	public RTPrivatePropertyTestCase(Object key) {
		this.key=key;
	}
	@Parameterized.Parameters(name="{0}") public static Collection<Object[]> parameters() {
		return SgfTestParameters.allSgfKeysAndFiles();
	}
	@Test public void testCannonical() {
		String actualSgf=SgfTestIo.restoreAndSave(expectedSgf);
		String actual2=SgfTestIo.restoreAndSave(actualSgf);
		assertEquals(key.toString(),actualSgf,actual2);
	}
	@Test public void testMultipleGames() { // how does it do that?
		String actualSgf=SgfTestIo.restoreAndSave(expectedSgf);
		// assertFalse(expectedSgf.contains(P.RT.toString()));
		// why would we expect this?
	}
	static final Set<String> paths=new LinkedHashSet<>();
}
