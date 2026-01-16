package sgf;

import org.junit.Test;
import equipment.Point;
import equipment.Stone;
import model.Model;
import utilities.TestKeys;

public class SgfFinderUnitTestCase extends AbstractWatchedTestCase {
    @Test public void testFinderWithSimple() {
        SgfNode games=SgfTestSupport.restoreFromKey(TestKeys.simpleWithVariations);
        SgfTestSupport.assertFinderMatches(games);
    }

    @Test public void testFinderWith3Moves() {
        Model model=new Model();
        model.move(Stone.black,new Point(0,0));
        model.move(Stone.white,new Point(0,1)); // fails if black - check later
        model.move(Stone.black,new Point(0,2));
        SgfTestSupport.assertFinderMatches(model.root().toBinaryTree());
    }
}
