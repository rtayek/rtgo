package iox;
import io.Logging;
// https://stackoverflow.com/questions/6606720/does-java-have-the-static-order-initialisation-fiasco
// https://stackoverflow.com/questions/48471245/when-should-i-use-lazy-singletons-over-normal-singletons
// https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
class Singleton {
    private Singleton() { Logging.mainLogger.info("3 ctor"); }
    private static final class LazyHolder {
        static {
            Logging.mainLogger.info("2 lazy holder");
        }
        private static final Singleton instance=new Singleton();
        static {
            Logging.mainLogger.info("4 lazy holder");
        }
    }
    public static final Singleton instance() { return LazyHolder.instance; }
}
public class Init2 { // this looks incorrect now
    public static class Elvis {
        private static final Elvis instance=new Elvis();
        //private Elvis() { Logging.mainLogger.info("ctor"); }
        public boolean leftTheBuilding() { return true; }
    }
    public static Elvis elvis() { return Elvis.instance; }
    public static void main(String[] args) { Elvis elvis=Init2.elvis(); Logging.mainLogger.info(String.valueOf(elvis.leftTheBuilding())); }
    static {
        Logging.mainLogger.info("static init Init2");
    }
}
class Other { void other() { Logging.mainLogger.info("?"); } }
class Main {
    public static void main(String[] args) { //
        Logging.mainLogger.info("1 main");
        Singleton singleton=Singleton.instance();
        Logging.mainLogger.info("in main(): "+singleton);
        new Other().other();
    }
}
