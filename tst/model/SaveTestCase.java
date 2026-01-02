package model;
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
		System.out.println("using: "+model.useOldWay);
		model.ensureBoard();
		model.move(Stone.black,"A1",model.board().width());
		//System.out.println(model);
		//model.move(Stone.white,"A2",model.board().width());
		StringWriter stringWriter=new StringWriter();
		boolean ok=model.save(stringWriter);
		assertTrue("save fails",ok);
		// (;FF[4]GM[1]AP[RTGO]C[comment];B[as])
		final String expected=stringWriter.toString();
		System.out.println("expected: "+expected);
		Model m=new Model("",false);
		m.restore(new StringReader(expected));
		Point point=Coordinates.fromGtpCoordinateSystem("A1",19);
		Stone color=m.board().at(point)	;
		//assertEquals(Stone.black,color);
		stringWriter=new StringWriter();
		m.save(stringWriter);
		final String actual=stringWriter.toString();
		System.out.println("actual: "+actual);
		assertEquals(actual,expected);
		// is failing because the new way is not putting in ;RT[Tgo root 
	}
	@Test public void testA1A2TheOldWay() throws IOException {
		Model model=new Model("",true);
		System.out.println("using: "+model.useOldWay);
		model.ensureBoard();
		model.move(Stone.black,"A1",model.board().width());
		//model.move(Stone.white,"A2",model.board().width());
		//System.out.println(model);
		StringWriter stringWriter=new StringWriter();
		boolean ok=model.save(stringWriter);
		assertTrue("save fails",ok);
		// (;FF[4]GM[1]AP[RTGO]C[comment];B[as])
		final String expected=stringWriter.toString();
		System.out.println("expected: "+expected);
		Model m=new Model("",true);
		Point point=Coordinates.fromGtpCoordinateSystem("A1",19);
		Stone color=m.board().at(point)	;
		//assertEquals(Stone.black,color);
		// this test passes but there is no stone there!
		m.restore(new StringReader(expected));
		stringWriter=new StringWriter();
		m.save(stringWriter);
		final String actual=stringWriter.toString();
		System.out.println("actual: "+actual);
		assertEquals(actual,expected);
	}
	// @Test public void testA1A2restored() throws IOException {}
	String foo="(;B[as];W[ar])";
}
