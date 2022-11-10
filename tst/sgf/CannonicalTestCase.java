package sgf;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import static sgf.SgfNode.sgfRoundTrip;
import java.io.StringReader;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import model.MNodeAcceptor.MakeList;
import model.Model;
import utilities.*;

@RunWith(Parameterized.class) public class CannonicalTestCase {
    public CannonicalTestCase(Object key) { this.key=key; }
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        // 10/22/22 return Parser.sgfTestData(); // breaks this test case
        // may be related to tee test case problems.
        return ParameterArray.parameterize(Parser.sgfDataKeySet());
    }
    @Before public void setUp() throws Exception {
        originalSgf=getSgfData(key);
        // no prepare here!
    }
    @After public void tearDown() throws Exception {}
    @Ignore @Test public void testThatGeneralTreeAlwaysHasRTProperty() {
        // this will depend on whether the add new rot switch is on.
        SgfNode games=restoreSgf(new StringReader(originalSgf));
        MNode root=MNode.toGeneralTree(games);
        // this mnay not be present
        // check the add new root flag in mnode.
        SgfProperty property=root.properties.get(0);
        assertEquals(P.RT,property.p());
    }
    @Ignore @Test public void testSaveMultupleGames() {
        Model model=new Model();
        model.restore(new StringReader(originalSgf));
        boolean hasMultipleGames=model.root().children.size()>1;
        String sgfString=model.save();
        boolean containsRTNode=sgfString.contains("RT[Tgo root]");
        // when games.right!=null ==> multiple games
        // amd we end up with an RT in the sgf!
        //assertEquals(hasMultipleGames,containsRTNode);
        // but we do not want the RT node in the sgf!
        // need to check the add new root switch.
    }
    @Test(timeout=100) public void testLeastCommonAncester() {
        // seems to be working for multiplegames
        MNode root=MNode.restore(new StringReader(originalSgf));
        boolean hasMultipleGames=root.children.size()>1;
        assertNotNull(root);
        List<MNode> list1=MakeList.toList(root);
        List<MNode> list2=MakeList.toList(root);
        //PrintStream x=System.out;
        //System.setOut(new PrintStream(new ByteArrayOutputStream(1_000_000)));
        if(hasMultipleGames) {
            //System.out.println(originalSgf);
            //System.out.println("list1: "+list1);
        }
        for(MNode node1:list1) for(MNode node2:list2) if(!node2.equals(node1)) {
            List<MNode> list=root.lca(node1,node2);
            assertNotNull(list);
        }
        //System.setOut(x);
    }
    @Test public void testCannonical() {
        String expectedSgf=sgfRoundTrip(originalSgf);
        String actualSgf=sgfRoundTrip(expectedSgf);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Ignore @Test public void testCannonicalRoundTripTwice() {
        try {
            Model model=new Model();
            //System.out.println("or:\n"+originalSgf);
            model.restore(new StringReader(originalSgf));
            //System.out.println("restored");
            String expectedSgf=model.save();
            //System.out.println("ex:\n"+expectedSgf);
            model=new Model();
            model.restore(new StringReader(expectedSgf));
            String actualSgf=model.save();
            //System.out.println("ac:\n"+actualSgf);
            assertEquals(key.toString(),expectedSgf,actualSgf);
        } catch(Exception e) {
            fail("'"+key+"' caught: "+e);
        }
    }
    Object key;
    String originalSgf;
    static final Set<String> paths=new LinkedHashSet<>();
}
