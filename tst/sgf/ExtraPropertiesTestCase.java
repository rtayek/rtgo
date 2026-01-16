package sgf;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import core.formats.sgf.SgfNodeMapping;
import model.Model;

public class ExtraPropertiesTestCase {
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
}
