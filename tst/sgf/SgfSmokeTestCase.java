package sgf;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.ParameterArray;
import utilities.TestKeys;
@RunWith(Parameterized.class) public class SgfSmokeTestCase extends AbstractSgfRoundTripTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        List<Object> keys=Arrays.asList(
                TestKeys.emptyWithSemicolon,
                TestKeys.oneMoveAtA1,
                TestKeys.simpleWithVariations,
                TestKeys.manyFacesTwoMovesAtA1AndR16OnA9by9Board,
                new File(Parser.sgfPath,"variation.sgf")
        );
        return ParameterArray.parameterize(keys);
    }
    @Test public void testModelRoundTripTwice() {
        SgfTestSupport.assertModelRoundTripTwice(expectedSgf);
    }
}
