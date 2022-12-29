package iox;
// https://stackoverflow.com/questions/6606720/does-java-have-the-static-order-initialisation-fiasco
// https://stackoverflow.com/questions/48471245/when-should-i-use-lazy-singletons-over-normal-singletons
// https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
class Singleton {
    private Singleton() { System.out.println("3 ctor"); }
    private static final class LazyHolder {
        static {
            System.out.println("2 lazy holder");
        }
        private static final Singleton instance=new Singleton();
        static {
            System.out.println("4 lazy holder");
        }
    }
    public static final Singleton instance() { return LazyHolder.instance; }
}
public class Init2 { // this looks incorrect now
    public static class Elvis {
        private static final Elvis instance=new Elvis();
        //private Elvis() { System.out.println("ctor"); }
        public boolean leftTheBuilding() { return true; }
    }
    public static Elvis elvis() { return Elvis.instance; }
    public static void main(String[] args) { Elvis elvis=Init2.elvis(); System.out.println(elvis.leftTheBuilding()); }
    static {
        System.out.println("static init Init2");
    }
}
class Other { void other() { System.out.println("?"); } }
class Main {
    public static void main(String[] args) { //
        System.out.println("1 main");
        Singleton singleton=Singleton.instance();
        System.out.println("in main(): "+singleton);
        new Other().other();
    }
}
