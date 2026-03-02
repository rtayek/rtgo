package model;
import utilities.MyTestWatcher;
import io.Logging;
import io.TestIo;
import com.tayek.util.io.FileIO;
import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
public class SaveTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
	private void assertA1RoundTrip() throws IOException {
		Model model=new Model("");
		model.ensureBoard();
		model.move(Stone.black,"A1",model.board().width());
		//Logging.mainLogger.info(model);
		//model.move(Stone.white,"A2",model.board().width());
		// (;FF[4]GM[1]AP[RTGO]C[comment];B[as])
		Model m=new Model("");
		final String expected=TestIo.toString("save expected fails",writer->ModelIo.saveModel(model,writer));
		Logging.mainLogger.info("expected: "+expected);
		ModelIo.restoreModel(m,FileIO.toReader(expected));
		Point point=Coordinates.fromGtpCoordinateSystem("A1",19);
		Stone color=m.board().at(point);
		//assertEquals(Stone.black,color);
		final String actual=TestIo.toString("save actual fails",writer->ModelIo.saveModel(m,writer));
		Logging.mainLogger.info("actual: "+actual);
		assertEquals(actual,expected);
	}
	@Test public void testA1A2() throws IOException {
		assertA1RoundTrip();
		// is failing because the new way is not putting in ;RT[Tgo root 
	}
	// @Test public void testA1A2restored() throws IOException {}
	String foo="(;B[as];W[ar])";

}


