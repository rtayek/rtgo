package model;
import org.junit.*;
import model.Options.OptionsA;
import model.OptionsSubclass.OptionsC;
enum Foo { bar }
class Options {
    public class Option<T extends Enum<T>> { Option(T t) { this.t=t; } final T t; }
    public static class OptionsA extends Options {
        {
            new Option<Foo>(Foo.bar);
        }
    }
}
class OptionsB extends Options {
    {
        new Option<Foo>(Foo.bar);
    }
}
class OptionsSubclass extends Options {
    public class OptionSubclass<T extends Enum<T>>extends Option<T> { public OptionSubclass(T t) { super(t); } }
    public static class OptionsC extends OptionsSubclass {
        {
            new OptionSubclass<Foo>(Foo.bar);
        }
    }
}
class OptionsD extends OptionsSubclass {
    {
        new OptionSubclass<Foo>(Foo.bar);
    }
}
public class SublassOptionsTestCase {
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test() { new OptionsA(); new OptionsB(); new OptionsC(); new OptionsD(); }
}
