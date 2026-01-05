package p;
import io.Logging;
public class Fifth extends InitializationOrder.First {
    public static void main(String[] args) { Logging.mainLogger.info(String.valueOf(5)); }
    static { // force enum second initialization from any main class.
        Logging.mainLogger.info("4");
    }
    {
        Logging.mainLogger.info("? fifth");
    }
}
