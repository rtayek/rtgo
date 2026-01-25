package gui;
import static org.junit.Assert.*;
import org.junit.*;
import utilities.TestSupport;
public class JitterTestCase extends TestSupport {
    @Test public void testZero() { Jitter jitter=Jitter.get(0,0); assertEquals(Jitter.zero,jitter); }
    @Test public void test19by19() {
        Jitter jitter=Jitter.get(19,19);
        assertNotNull(jitter);
        Jitter jitter2=Jitter.get(19,19);
        assertTrue(jitter==jitter2);
    }
    @Test public void test15by17() {
        Jitter jitter=Jitter.get(15,17);
        assertNotNull(jitter);
        Jitter jitter2=Jitter.get(15,17);
        assertTrue(jitter==jitter2);
    }
}
