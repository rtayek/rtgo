package utilities;
import java.io.*;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.util.*;
import java.util.logging.Logger;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.*;
import org.junit.runner.notification.*;
import org.junit.runners.Suite;
import io.Init;
/**
 * Discovers all JUnit tests and runs them in a suite.
 */
// similar: https://stackoverflow.com/questions/6580670/testsuite-setup-in-junit-4
// how to exckude these when run by eclipse or other method?
// set a flag in god?
// put the suites in a folder that is not a source folder.
// then run the suites from the command line?
// lets try and move this to tst/utilities/
// https://www.baeldung.com/junit-tests-run-programmatically-from-java
// maybe try some repeated tests for fixing timeout problems
@RunWith(MyAllTests.AllTestsRunner.class) public final class MyAllTests extends SuiteSupport {
    //@BeforeClass public static void setUpClass() { System.out.println("set up suite class"); }
    //@AfterClass public static void tearDownClass() { System.out.println("tear down suite class"); godwrapupTests(); }
    private MyAllTests() {}
    // Finds and runs tests.
    public static class AllTestsRunner extends Suite {
        public AllTestsRunner(final Class<?> clazz)
                throws InitializationError,org.junit.runners.model.InitializationError {
            super(clazz,findClasses());
        }
        private static void findClasses(final List<File> classFiles,final File dir) {
            System.out.println("classFiles: "+classFiles+", dir: "+dir);
            File[] files=dir.listFiles();
            if(files!=null) for(File file:files) {
                if(file.isDirectory()) {
                    findClasses(classFiles,file);
                } else if(file.getName().toLowerCase().endsWith(".class")) { classFiles.add(file); }
            }
        }
        private static Class<?>[] findClasses() {
            List<File> classFiles=new ArrayList<File>();
            String packagepath=MyAllTests.class.getPackage().getName().replace(".","/");
            System.out.println("package path: "+packagepath);
            File relativeDir=new File(classesDir.getAbsolutePath()+"\\"+packagepath);
            System.out.println("relative dir: "+relativeDir);
            System.out.println("dir: "+classesDir);
            findClasses(classFiles,relativeDir);
            List<Class<?>> classes=convertToClasses(classFiles,classesDir);
            return classes.toArray(new Class[classes.size()]);
        }
        private static void initializeBeforeTests() {
            System.out.println("initializeBeforeTests: "+Init.first);
        }
        private static List<Class<?>> convertToClasses(final List<File> classFiles,final File classesDir) {
            List<Class<?>> classes=new ArrayList<Class<?>>();
            for(File file:classFiles) {
                if(!file.getName().endsWith("TestCase.class")&&!file.getPath().contains("slow")) { continue; }
                String name=file.getPath().substring(classesDir.getPath().length()+1).replace('/','.').replace('\\',
                        '.');
                name=name.substring(0,name.length()-6);
                Class<?> c;
                try {
                    c=Class.forName(name);
                } catch(ClassNotFoundException e) {
                    throw new AssertionError(e);
                }
                if(!Modifier.isAbstract(c.getModifiers())) { classes.add(c); }
            }
            // sort so we have the same order as Ant
            Collections.sort(classes,new Comparator<Class<?>>() {
                @Override public int compare(final Class<?> c1,final Class<?> c2) {
                    return c1.getName().compareTo(c2.getName());
                }
            });
            return classes;
        }
        @Override public void run(final RunNotifier notifier) {
            initializeBeforeTests();
            notifier.addListener(new RunListener() {
                @Override public void testStarted(final Description description) {
                    //logger.info("Before test "+description.getDisplayName());
                }
                @Override public void testFinished(final Description description) {
                    //logger.info("After test "+description.getDisplayName());
                }
            });
            super.run(notifier);
        }
        private final Logger logger=Logger.getLogger(getClass().getName());
    }
    private static File findClassesDir() {
        try {
            String path=MyAllTests.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            System.out.println("path: "+path);
            return new File(URLDecoder.decode(path,"UTF-8"));
        } catch(UnsupportedEncodingException impossible) {
            // using default encoding, has to exist
            throw new AssertionError(impossible);
        }
    }
    private static final File classesDir=findClassesDir();
}
