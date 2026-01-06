package model;
import static org.junit.Assert.*;
//import static org.junit.Assert.assertTrue;
import java.io.*;
import java.util.logging.Level;
import org.junit.*;
import io.Logging;
import utilities.MyTestWatcher;
// (;FF[4]GM[1]AP[RTGO]C[comment];B[as])
public class SmallerSaveTestCase {
	@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
	private String dtrt(Model m) {
		String actual=ModelTestIo.restoreAndSave(m,sgf,restored->{
			Logging.mainLogger.info("restored, root: "+restored.root().toString());
			boolean hasRT=Model.hasRT(restored.root());
			assertTrue(hasRT);
			// it does not have a root because the new way did no execute it.
			// hasRT does no see it because it only looks at sgf roperties and does not look at a extra properties.
		});
		Logging.mainLogger.info("saved: "+actual);
		return actual;
	}
	// @Test public void atestNothing() throws IOException {}
	@Test public void testNoMoves() throws IOException {
		Logging.setLevels(Level.INFO);
		Model m=new Model("new way",false);
		final String actual=dtrt(m);
		assertEquals(sgf,actual);
	}
	@Test public void testNOneovesTheOldWay() throws IOException {
		Logging.setLevels(Level.INFO);
		Model m=new Model("old way",true);
		final String actual=dtrt(m);
		assertEquals(sgf,actual);
	}
	// @Test public void testA1A2restored() throws IOException {}
	final String sgf="(;FF[4]GM[1]AP[RTGO]C[comment];B[as])";
	String foo="(;B[as];W[ar])";
}
