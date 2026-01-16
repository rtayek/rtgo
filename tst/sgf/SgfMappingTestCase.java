package sgf;

import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;
import core.formats.sgf.SgfNodeMapping;
import model.Model;

public class SgfMappingTestCase {
    @Test public void unknownPropertiesArePreserved() {
        // Build properties with IDs not known to P2 to force them into extras.
        P customXX=new P("XX","1234","unknown","none",""){};
        P customYY=new P("YY","1234","unknown","none",""){};
        SgfProperty keepRoot=SgfTestSupport.property(customXX,"keepme");
        SgfProperty keepChild=SgfTestSupport.property(customYY,"alsokeep");
        SgfProperty rootComment=SgfTestSupport.property(P.C,"hello");

        // Manually build MNode tree to avoid parser dropping unknown IDs
        MNode root=SgfMappingTestSupport.nodeWith(rootComment,keepRoot);
        MNode child=SgfMappingTestSupport.nodeWith(root,SgfTestSupport.property(P.B,"aa"),keepChild);
        root.children().add(child);

        Model model=new Model();

        SgfNodeMapping rootMapping=SgfMappingTestSupport.mapNode(root,model);
        assertEquals(List.of(rootComment,keepRoot),rootMapping.extras());
        rootMapping.applyExtrasTo(root);
        assertEquals(List.of(rootComment,keepRoot),root.extraProperties());

        SgfNodeMapping childMapping=SgfMappingTestSupport.mapNode(child,model);
        assertEquals(List.of(keepChild),childMapping.extras());
        childMapping.applyExtrasTo(child);
        assertEquals(List.of(keepChild),child.extraProperties());
    }

    @Test public void malformedPropertiesBecomeExtrasAndMappingIsPure() {
        SgfProperty badMove=SgfTestSupport.property(P.B,"a");
        SgfProperty emptyMove=new SgfProperty(P.W,List.of());
        SgfProperty badSize=SgfTestSupport.property(P.SZ,"x");

        MNode node=SgfMappingTestSupport.nodeWith(badMove,emptyMove,badSize);
        SgfNodeMapping mapping=SgfMappingTestSupport.mapNode(node);

        assertTrue(mapping.actions().isEmpty());
        assertEquals(List.of(badMove,emptyMove,badSize),mapping.extras());
        assertEquals(List.of(badMove,emptyMove,badSize),node.sgfProperties());
        assertTrue(node.extraProperties().isEmpty());

        mapping.applyExtrasTo(node);
        assertTrue(node.sgfProperties().isEmpty());
        assertEquals(List.of(badMove,emptyMove,badSize),node.extraProperties());
    }
}
