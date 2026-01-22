package controller;
import static io.Init.first;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import io.Init;
import utilities.SuiteSupport;
@RunWith(Suite.class) @SuiteClasses({ //
    AbstractGTPDirectTestCase.GTPDirectTestSuite.class, //
    AbstractBothTestCase.BothTestSuite.class, //
    AbstractGameFixtureTestCase.ATestSuite.class, //
    AbstractGameFixtureTestCase.ParameterizedTestSuite.class, //
    //
    AbstractKnownCommandsTestCase.ParameterizedTestCase.class, //
    GTPDirectNavigationTestCase.class, //
    GTPDirectSendReceiveSgfTestCase.class, //
    TeardownOrderTestCase.class, //
}) public class AllTests extends SuiteSupport {
    @BeforeClass public static void setUpBeforeClass() throws Exception { Init.first.testsRun.clear(); }
    @AfterClass public static void tearDownAfterClass() throws Exception {
        first.lastPrint();
        first.saveTestsRun("fromSuite.txt");
        
    }
}
