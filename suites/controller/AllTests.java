package controller;
import java.io.File;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import com.tayek.util.core.Texts;
import com.tayek.util.io.FileIO;
import server.NamedThreadGroup;
import io.BS;
import io.Init;
import utilities.SuiteSupport;
import utilities.TestLifecycleHelper;
@RunWith(Suite.class) @SuiteClasses({ //
		AbstractGTPDirectTestCase.GTPDirectTestSuite.class, //
		AbstractBothTestCase.BothTestSuite.class, //
		AbstractGameFixtureTestCase.ParameterizedTestSuite.class, //
		//
		AbstractKnownCommandsTestCase.ParameterizedTestCase.class, //
		GTPDirectNavigationTestCase.class, //
		GTPDirectSendReceiveSgfTestCase.class, //
		TeardownOrderTestCase.class, //
}) public class AllTests extends SuiteSupport {
	@BeforeClass public static void setUpBeforeClass() throws Exception {
		TestLifecycleHelper.testsRun.clear();
	}
	@AfterClass public static void tearDownAfterClass() throws Exception {
		NamedThreadGroup.lastPrint(TestLifecycleHelper.testsRun);
		FileIO.write(Texts.cat(TestLifecycleHelper.testsRun),new File("fromSuite.txt"));
	}
}
