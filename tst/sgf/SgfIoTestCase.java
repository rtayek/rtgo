package sgf;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import model.Model;

public class SgfIoTestCase {
    @Test public void testModelRoundTrip() {
        Model original=new Model();
        Model restored=new Model();
        TestIoSupport.RoundTrip roundTrip=TestIoSupport.roundTrip(original,restored);
        assertEquals(roundTrip.expected(),roundTrip.actual());
    }
}
