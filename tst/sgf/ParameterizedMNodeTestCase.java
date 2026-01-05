package sgf;
import static sgf.Parser.*;
import java.util.*;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedMNodeTestCase extends AbstractMNodeTestCase {
	@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
	@Parameterized.Parameters(name="{0}") public static Collection<Object[]> parameters() {
		Set<Object> objects=new LinkedHashSet<>();
		objects.addAll(sgfDataKeySet());
		objects.addAll(sgfFiles());
		return ParameterArray.parameterize(objects);
	}
	public ParameterizedMNodeTestCase(Object key) {
		this.key=key;
	}
}
