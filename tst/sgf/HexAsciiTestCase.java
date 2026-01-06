package sgf;
import static org.junit.Assert.*;
import static sgf.HexAscii.*;
import java.util.*;
import org.junit.*;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
/*@RunWith(Parameterized.class)*/ public class HexAsciiTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> data() {
        return SgfTestParameters.allSgfKeysAndFiles();
    }
    public HexAsciiTestCase() {}
    //public HexAsciiTestCase(String key) { this.key=key; }
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testNybble() {
        for(byte expected=0;expected<ascii.length;expected++) {
            char c=encode(expected);
            byte actual=decode(c);
            assertEquals(""+expected,expected,actual);
        }
    }
    @Test public void testEncode15() {
        byte[] expected=new byte[] {0x0f};
        String s=encode(expected);
        assertEquals("0f","0f",s);
        byte[] actual=decode(s);
        assertEquals(""+expected[0],expected[0],actual[0]);
    }
    @Test public void testEncode16() {
        byte[] expected=new byte[] {0x10};
        String s=encode(expected);
        assertEquals("10","10",s);
        byte[] actual=decode(s);
        assertEquals(""+expected[0],expected[0],actual[0]);
    }
    @Test public void testOneByte() {
        byte b=16;
        String expected=encode(new byte[] {b});
        byte[] bytes=decode(expected);
        String actual=encode(bytes);
        assertEquals(""+b,expected,actual);
    }
    @Test public void testByte() {
        for(int bite=0;bite<256;++bite) {
            byte[] expected=new byte[] {(byte)bite};
            String string=encode(expected);
            byte[] actual=decode(string);
            String string2=encode(actual);
            assertEquals(string,string,string2);
        }
    }
    @Test public void testOneCharacterString() {
        for(int bite=0;bite<256;++bite) {
            byte[] expected=new byte[] {(byte)bite};
            String string=encode(expected);
            byte[] actual=decode(string);
            String string2=encode(actual);
            assertEquals(string,string,string2);
        }
    }
    @Test public void testTwoCharacterString() {
        String s="a1";
        String encoded=encode(s.getBytes());
        byte[] decoded=decode(encoded);
        String actual=new String(decoded);
        //assertEquals(s,encoded,decoded);
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
    String key,testString="0123456789abcdefghijklmnopqrstuvwxyz";
}
