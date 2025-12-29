package sgf;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

import model.DomainAction;
import model.Model;
import model.ModelHelper;
import org.junit.Test;

public class ExtraPropertiesTestCase {
    @Test public void unknownPropertiesArePreserved() {
        // Build properties with IDs not known to P2 to force them into extras.
        P customXX=new P("XX","1234","unknown","none",""){};
        P customYY=new P("YY","1234","unknown","none",""){};
        SgfProperty keepRoot=new SgfProperty(customXX,List.of("keepme"));
        SgfProperty keepChild=new SgfProperty(customYY,List.of("alsokeep"));

        // Manually build MNode tree to avoid parser dropping unknown IDs
        MNode root=new MNode(null);
        root.sgfProperties().add(new SgfProperty(P.C,List.of("hello")));
        root.sgfProperties().add(keepRoot);

        MNode child=new MNode(root);
        child.sgfProperties().add(new SgfProperty(P.B,List.of("aa")));
        child.sgfProperties().add(keepChild);
        root.children().add(child);

        Model model=new Model();

        DomainAction.mapNodeToDomainActions(model,root); // mutates extraProperties
        assertEquals(List.of(keepRoot),root.extraProperties());

        DomainAction.mapNodeToDomainActions(model,child);
        assertEquals(List.of(keepChild),child.extraProperties());
    }
}
