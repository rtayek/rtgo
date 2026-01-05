package model;
import io.Logging;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import model.OptionsABC.*;
public class Options1And2TestCase {
    @Test public void testProperties() {}
    @Test public void testOptions1() {
        Options1 options=new Options1();
        Object expected=options.options().iterator().next().defaultValue;
        assertEquals(expected,options.options().iterator().next().currentValue());
        Options1 options2=new Options1();
        Integer expected2=1;
        options2.options().iterator().next().setCurrentValue(1);
        assertEquals(expected2,options2.options().iterator().next().currentValue());
        // not a very good test.
        // add more when change mechanism is triggered.
    }
    @Test public void testOptions2() {
        Options2 options=new Options2();
        Logging.mainLogger.info(String.valueOf(options.toString()));
        Logging.mainLogger.info(String.valueOf(options.toLongString()));
        // more work needed here
        // maybe test properties here?
    }
    public static void main(String argv[]) {
        Options0 options0=new Options0();
        Logging.mainLogger.info(String.valueOf(options0.toString()));
        Logging.mainLogger.info(String.valueOf(options0.toLongString()));
        Options1 options=new Options1();
        Logging.mainLogger.info(String.valueOf(options.toString()));
        Logging.mainLogger.info(String.valueOf(options.toLongString()));
        Options2 options2=new Options2();
        Logging.mainLogger.info(String.valueOf(options2.toString()));
        Logging.mainLogger.info(String.valueOf(options2.toLongString()));
    }
}
