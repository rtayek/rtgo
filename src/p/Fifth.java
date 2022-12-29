package p;
public class Fifth extends InitializationOrder.First {
    public static void main(String[] args) { System.out.println(5); }
    static { // force enum second initialization from any main class.
        System.out.println("4");
    }
    {
        System.out.println("? fifth");
    }
}
