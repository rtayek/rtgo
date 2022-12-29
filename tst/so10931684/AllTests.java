package so10931684;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class) @SuiteClasses({AllTests.InSuiteOnly.SuiteTest1.class,
        AllTests.InSuiteOnly.SuiteTest2.class}) public class AllTests {
    static class InSuiteOnly {
        public static class SuiteTest1 extends Test1 {}
        public static class SuiteTest2 extends Test2 {}
    }
    public class Test0 { @Test public void test0() {} }
}
abstract class Test1 { @Test public void test1() {} }
abstract class Test2 { @Test public void test2() {} }