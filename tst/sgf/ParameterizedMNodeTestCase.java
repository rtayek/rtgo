package sgf;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedMNodeTestCase extends AbstractMNodeTestCase {
	@Parameterized.Parameters(name="{0}") public static Collection<Object[]> parameters() {
		return SgfTestParameters.allSgfKeysAndFiles();
	}
	public ParameterizedMNodeTestCase(Object key) {
		this.key=key;
	}
}
