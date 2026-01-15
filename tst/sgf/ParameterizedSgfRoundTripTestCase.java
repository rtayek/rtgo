package sgf;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedSgfRoundTripTestCase extends AbstractSgfRoundTripTestCase {
	@Parameterized.Parameters(name="{0}") public static Collection<Object[]> parameters() {
		return SgfTestParameters.allSgfKeysAndFiles();
	}
	public ParameterizedSgfRoundTripTestCase(Object key) {
		this.key=key;
	}
}
