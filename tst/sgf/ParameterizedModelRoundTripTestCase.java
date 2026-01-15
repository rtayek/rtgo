package sgf;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class) public class ParameterizedModelRoundTripTestCase extends AbstractModelRoundtripTestCase {
    @Parameterized.Parameters(name = "{0}")
 public static Collection<Object[]> parameters() {
        return allSgfParameters();
    }
    public ParameterizedModelRoundTripTestCase(Object key) { this.key=key; }
}
