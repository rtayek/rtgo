package model;
import org.junit.*;
import sgf.Parser;
public class OneModelRoundtripTestCase extends ModelRoundtripTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Override @Before public void setUp() throws Exception {}
    @Override @After public void tearDown() throws Exception {}
    {
        expected=Parser.empty;
        expected=Parser.emptyWithSemicolon;
        expected=Parser.twoEmpty;
        expected=Parser.twoEmptyWithSemicolon;
    }
}
