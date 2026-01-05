package model;
import io.Logging;
import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
import utilities.MyTestWatcher;
public class SaveTestCase {
	@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
	@Test public void testA1A2() throws IOException {
		Model model=new Model("",false);
		Logging.mainLogger.info("using: "+model.useOldWay);
		model.ensureBoard();
		model.move(Stone.black,"A1",model.board().width());
		//Logging.mainLogger.info(model);
		//model.move(Stone.white,"A2",model.board().width());
		// (;FF[4]GM[1]AP[RTGO]C[comment];B[as])
		final String expected=ModelTestIo.save(model);
		Logging.mainLogger.info("expected: "+expected);
		Model m=new Model("",false);
		ModelTestIo.restore(m,expected);
		Point point=Coordinates.fromGtpCoordinateSystem("A1",19);
		Stone color=m.board().at(point)	;
		//assertEquals(Stone.black,color);
		final String actual=ModelTestIo.save(m);
		Logging.mainLogger.info("actual: "+actual);
		assertEquals(actual,expected);
		// is failing because the new way is not putting in ;RT[Tgo root 
	}
	@Test public void testA1A2TheOldWay() throws IOException {
		Model model=new Model("",true);
		Logging.mainLogger.info("using: "+model.useOldWay);
		model.ensureBoard();
		model.move(Stone.black,"A1",model.board().width());
		//model.move(Stone.white,"A2",model.board().width());
		//Logging.mainLogger.info(model);
		// (;FF[4]GM[1]AP[RTGO]C[comment];B[as])
		final String expected=ModelTestIo.save(model);
		Logging.mainLogger.info("expected: "+expected);
		Model m=new Model("",true);
		Point point=Coordinates.fromGtpCoordinateSystem("A1",19);
		Stone color=m.board().at(point)	;
		//assertEquals(Stone.black,color);
		// this test passes but there is no stone there!
		ModelTestIo.restore(m,expected);
		final String actual=ModelTestIo.save(m);
		Logging.mainLogger.info("actual: "+actual);
		assertEquals(actual,expected);
	}
	// @Test public void testA1A2restored() throws IOException {}
	String foo="(;B[as];W[ar])";
}
