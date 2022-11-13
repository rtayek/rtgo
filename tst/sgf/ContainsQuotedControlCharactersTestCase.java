package sgf;
import static org.junit.Assert.*;
import static sgf.SgfNode.SgfOptions.*;
import org.junit.Test;
public class ContainsQuotedControlCharactersTestCase {
    @Test public void testContainsQuotedControlCharacters() {
        String key="foo\\nba\r",string=key;
        boolean bad=containsQuotedControlCharacters(key,string);
        if(bad) {
            String actual=removeQuotedControlCharacters(string);
            boolean bad2=containsQuotedControlCharacters(key,actual);
            assertFalse(bad2);
        }
        assertTrue(bad);
    }
}
