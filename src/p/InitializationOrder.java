package p;
import io.Logging;
interface InitializationOrder {
    abstract static class First {
        static {
            Logging.mainLogger.info(1+" First reference second");
            Logging.mainLogger.info(String.valueOf(Second.second));
        }
        public static void main(String[] args) { Logging.mainLogger.info("?"); }
    }
    enum Second {
        second;
        Second() { Logging.mainLogger.info(2+" construct second"); }
        public static void main(String[] args) { Logging.mainLogger.info("?"); }
        static {
            Logging.mainLogger.info("3 Second::main()");
        }
    }
    static class Fifth extends First {
        public static void main(String[] args) { Logging.mainLogger.info(String.valueOf(5)); }
        static { // force enum second initialization from any main class.
            Logging.mainLogger.info("4");
        }
    }
    public static void main(String[] args) { Logging.mainLogger.info("? InitialzationOrder.main"); }
}
