package sgf;
import static org.junit.Assert.assertEquals;
import static sgf.Parser.*;
import static sgf.SgfNode.sgfRestoreAndSave;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class RTPrivatePropertyTestCase {
	public RTPrivatePropertyTestCase(Object key) {
		this.key=key;
	}
	@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
	@Parameterized.Parameters(name="{0}") public static Collection<Object[]> parameters() {
		Set<Object> objects=new LinkedHashSet<>();
		objects.addAll(sgfDataKeySet());
		objects.addAll(sgfFiles());
		return ParameterArray.parameterize(objects);
	}
	@Before public void setUp() throws Exception {
		originalSgf=getSgfData(key);
		// no prepare here!
	}
	@After public void tearDown() throws Exception {}
	@Test public void testCannonical() {
		String expectedSgf=sgfRestoreAndSave(originalSgf);
		String actualSgf=sgfRestoreAndSave(expectedSgf);
		assertEquals(key.toString(),expectedSgf,actualSgf);
	}
	@Test public void testMultipleGames() { // how does it do that?
		String expectedSgf=sgfRestoreAndSave(originalSgf);
		// assertFalse(expectedSgf.contains(P.RT.toString()));
		// why would we expect this?
	}
	Object key;
	String originalSgf;
	static final Set<String> paths=new LinkedHashSet<>();
}
