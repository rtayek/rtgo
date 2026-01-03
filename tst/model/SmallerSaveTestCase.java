package model;
import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
import utilities.MyTestWatcher;
// (;FF[4]GM[1]AP[RTGO]C[comment];B[as])
public class SmallerSaveTestCase {
	@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
	
	@Test public void testNoMoves() throws IOException {
		Model m=new Model("",false);
		m.restore(new StringReader(sgf));
		System.out.println(m);
		StringWriter stringWriter=new StringWriter();
		m.save(stringWriter);
		final String actual=stringWriter.toString();
		assertEquals(actual,sgf);
	}
	@Test public void testNoMovesTheOldWay() throws IOException {
		Model m=new Model("",true);
		m.restore(new StringReader(sgf));
		StringWriter stringWriter=new StringWriter();
		m.save(stringWriter);
		final String actual=stringWriter.toString();
		assertEquals(actual,sgf);
	}
	@Test public void testA1A2() throws IOException {
		Model m=new Model("",false);
		m.restore(new StringReader(sgf));
		System.out.println(m);
		Point point=Coordinates.fromGtpCoordinateSystem("A1",19);
		Stone color=m.board().at(point)	;
		assertEquals(Stone.black,color);
		
		//StringWriter stringWriter=new StringWriter();
		//m.save(stringWriter);
		//final String actual=stringWriter.toString();
		//assertEquals(actual,sgf);
	}
	@Test public void testA1A2TheOldWay() throws IOException {
		Model m=new Model("",true);
		m.restore(new StringReader(sgf));
		System.out.println(m);
		Point point=Coordinates.fromGtpCoordinateSystem("A1",19);
		Stone color=m.board().at(point)	;
		assertEquals(Stone.black,color);
		
		//StringWriter stringWriter=new StringWriter();
		//m.save(stringWriter);
		//final String actual=stringWriter.toString();
		//assertEquals(actual,sgf);
	}
	// @Test public void testA1A2restored() throws IOException {}
	final String sgf="(;FF[4]GM[1]AP[RTGO]C[comment];B[as])";


	String foo="(;B[as];W[ar])";
}
