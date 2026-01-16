package sgf;
import java.io.File;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.ParameterArray;
import utilities.TestKeys;
@RunWith(Parameterized.class) public class SgfSmokeTestCase extends AbstractSgfRoundTripTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        return ParameterArray.parameterize(
                TestKeys.emptyWithSemicolon,
                TestKeys.oneMoveAtA1,
                TestKeys.simpleWithVariations,
                TestKeys.manyFacesTwoMovesAtA1AndR16OnA9by9Board,
                new File(Parser.sgfPath,"variation.sgf")
        );
    }
    @Test public void testModelRoundTripTwice() {
        SgfModelRoundTripHarness.assertModelRoundTripTwice(expectedSgf);
    }
}
