package gui;
import utilities.MyTestWatcher;
import static org.junit.Assert.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import equipment.Point;
public class GamePanelTestCase {
	@Rule public final MyTestWatcher watcher=new MyTestWatcher(getClass());
	@Before public void setUp() throws Exception {}
	@After public void tearDown() throws Exception {}
	@Test public void test() {
		for(float f=1.0f;f<2;f+=.1f) {
			Point x=GamePanel.closest(new Point2D.Float(f,0));
			System.out.println(f+" "+x.x);
		}
		Jitter jitter=new Jitter(19,19);
		List<Integer> x=Arrays.stream(jitter.xJitters).boxed().collect(Collectors.toList());
		List<Integer> y=Arrays.stream(jitter.yJitters).boxed().collect(Collectors.toList());
		System.out.println(Arrays.asList(x));
		System.out.println(Arrays.asList(y));
	}
}
