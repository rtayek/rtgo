package sgf;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import core.formats.sgf.SgfNodeMapping;

public class SgfMappingErrorTestCase {
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
