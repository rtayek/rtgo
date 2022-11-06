package p;
interface InitializationOrder {
    abstract static class First {
        static {
            System.out.println(1+" First reference second");
            System.out.println(Second.second);
        }
        public static void main(String[] args) { System.out.println("?"); }
    }
    enum Second {
        second;
        Second() { System.out.println(2+" construct second"); }
        public static void main(String[] args) { System.out.println("?"); }
        static {
            System.out.println("3 Second::main()");
        }
    }
    static class Fifth extends First {
        public static void main(String[] args) { System.out.println(5); }
        static { // force enum second initialization from any main class.
            System.out.println("4");
        }
    }
    public static void main(String[] args) { System.out.println("? InitialzationOrder.main"); }
}
