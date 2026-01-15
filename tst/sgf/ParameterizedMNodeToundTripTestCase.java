package sgf;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class) public class ParameterizedMNodeToundTripTestCase extends AbstractMNodeRoundTripTestCase {
	@Parameterized.Parameters(name="{0}") public static Collection<Object[]> parameters() {
		return allSgfParameters();
	}
	public ParameterizedMNodeToundTripTestCase(Object key) {
		this.key=key;
	}
}
