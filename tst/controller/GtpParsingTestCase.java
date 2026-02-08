package controller;
import org.junit.Rule;
import utilities.MyTestWatcher;
import static org.junit.Assert.*;
import org.junit.Test;
public class GtpParsingTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Test public void testNormalizeArgumentsForSimpleCommand() {
        String input="name";
        String stripped=GtpParsing.strip(input);
        GtpParsing.ParsedTokens parsed=GtpParsing.parseTokens(stripped);
        String[] arguments=GtpParsing.normalizeArguments(stripped,parsed.tokens);
        assertEquals(-1,parsed.id);
        assertArrayEquals(parsed.tokens,arguments);
        assertEquals(1,arguments.length);
        assertEquals("name",arguments[0]);
    }

    @Test public void testNormalizeArgumentsForReceiveSgf() {
        String sgf="(;FF[4]GM[1]C[hello world])";
        String input="123 "+Command.tgo_receive_sgf.name()+" "+sgf;
        String stripped=GtpParsing.strip(input);
        GtpParsing.ParsedTokens parsed=GtpParsing.parseTokens(stripped);
        String[] arguments=GtpParsing.normalizeArguments(stripped,parsed.tokens);
        assertEquals(123,parsed.id);
        assertEquals(2,arguments.length);
        assertEquals(Command.tgo_receive_sgf.name(),arguments[0]);
        assertEquals(sgf,arguments[1]);
    }
}

