package model;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedModelTestCase extends AbstractModelTestCase {
	@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
	@Parameterized.Parameters(name="{0}") public static Collection<Object[]> parameters() {
		return SgfTestParameters.allSgfKeysAndFiles();
	}
	public ParameterizedModelTestCase(Object key) {
		this.key=key;
	}
}
