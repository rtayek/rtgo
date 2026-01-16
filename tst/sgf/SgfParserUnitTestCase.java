package sgf;

import io.Logging;
import static io.IOs.noIndent;
import static org.junit.Assert.*;
import static sgf.HexAscii.*;
import static sgf.SgfNode.SgfOptions.*;
import java.io.Reader;
import java.util.Arrays;
import org.junit.Test;

public class SgfParserUnitTestCase extends AbstractWatchedTestCase {
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

    @Test public void testRestoreNullReader() {
        assertRestoresNull(SgfTestIo.restore((Reader)null));
    }

    @Test public void testRestoreEmpty() {
        assertRestoresNull(SgfTestIo.restore(""));
    }

    @Test public void testSample() {
        SgfNode root=sample();
        String expected=SgfTestIo.save(root,noIndent);
        Logging.mainLogger.info("sample sgf: "+expected);
        SgfTestSupport.assertSgfRestoreSaveStable(expected);
    }

    @Test public void testNybble() {
        for(byte expected=0;expected<ascii.length;expected++) {
            char c=encode(expected);
            byte actual=decode(c);
            assertEquals(""+expected,expected,actual);
        }
    }

    @Test public void testEncode15() {
        assertEncodedByte((byte)0x0f,"0f");
    }

    @Test public void testEncode16() {
        assertEncodedByte((byte)0x10,"10");
    }

    @Test public void testOneByte() {
        byte b=16;
        String expected=encode(new byte[] {b});
        byte[] bytes=decode(expected);
        String actual=encode(bytes);
        assertEquals(""+b,expected,actual);
    }

    @Test public void testByte() {
        assertRoundTripForAllBytes();
    }

    @Test public void testOneCharacterString() {
        assertRoundTripForAllBytes();
    }

    @Test public void testTwoCharacterString() {
        String s="a1";
        String encoded=encode(s.getBytes());
        byte[] decoded=decode(encoded);
        String actual=new String(decoded);
        assertEquals(s,s,actual);
    }

    @Test public void testString() {
        byte[] expected=testString.getBytes();
        String encodedBytes=encode(expected);
        byte[] actual=decode(encodedBytes);
        boolean ok=Arrays.equals(expected,actual);
        assertTrue(testString,ok);
        String newString=new String(actual);
        assertEquals(testString,testString,newString);
    }

    @Test public void testStringFast() {
        byte[] expected=testString.getBytes();
        String encodedBytes=encodeFast(expected);
        byte[] actual=decode(encodedBytes);
        boolean ok=Arrays.equals(expected,actual);
        assertTrue(testString,ok);
        String newString=new String(actual);
        assertEquals(testString,testString,newString);
    }

    private static void assertRestoresNull(SgfNode games) {
        assertNull(games);
    }

    private SgfNode comment(String string,SgfNode left,SgfNode right) {
        SgfNode node=SgfTestSupport.nodeWithProperty(P.C,string);
        if(left==null&&right==null) return node;
        if(left!=null) left.left=node;
        else if(right!=null) right.right=node;
        else throw new RuntimeException("both left and right are not null!");
        return node;
    }

    private void print(SgfNode node) {
        Logging.mainLogger.info("saved sgf node "+SgfTestIo.save(node,noIndent));
        Logging.mainLogger.info("----------------");
    }

    private SgfNode sample() { // maybe use redbean example?
        SgfNode root=comment("root",null,null);
        Logging.mainLogger.info("node root: "+root+" ");
        print(root);
        SgfNode left1=comment("left1",root,null);
        Logging.mainLogger.info("node left1: "+left1+" ");
        print(root);
        SgfNode right1=comment("right1",null,root);
        Logging.mainLogger.info("node right; "+right1+" ");
        print(root);
        SgfNode left1Left2=comment("left1.left2",left1,null);
        Logging.mainLogger.info("node left1Left2: "+left1Left2+" ");
        print(root);
        SgfNode left1right1=comment("left1.right1",null,left1);
        Logging.mainLogger.info("node left1.right1: "+left1right1+" ");
        print(root);
        Logging.mainLogger.info("node at end");
        return root;
    }

    private void assertRoundTripForAllBytes() {
        for(int bite=0;bite<256;++bite) {
            byte[] expected=new byte[] {(byte)bite};
            String string=encode(expected);
            byte[] actual=decode(string);
            String string2=encode(actual);
            assertEquals(string,string,string2);
        }
    }

    private void assertEncodedByte(byte expected,String expectedHex) {
        String s=encode(new byte[] {expected});
        assertEquals(expectedHex,expectedHex,s);
        byte[] actual=decode(s);
        assertEquals(String.valueOf(expected),expected,actual[0]);
    }

    private final String testString="0123456789abcdefghijklmnopqrstuvwxyz";
}
