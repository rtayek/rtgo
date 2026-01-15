package sgf;

import static org.junit.Assert.*;

import java.util.List;

import model.Model;
import org.junit.Test;
import core.formats.sgf.SgfDomainActionMapper;
import core.formats.sgf.SgfMappingContext;
import core.formats.sgf.SgfNodeMapping;
import equipment.Board;

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

        int depth=model.board()!=null?model.board().depth():model.depthFromSgf()>0?model.depthFromSgf():Board.standard;
        SgfMappingContext context=new SgfMappingContext(depth,Model.sgfBoardTopology,Model.sgfBoardShape);

        SgfNodeMapping rootMapping=SgfDomainActionMapper.mapNode(context,root);
        assertEquals(List.of(new SgfProperty(P.C,List.of("hello")),keepRoot),rootMapping.extras());
        rootMapping.applyExtrasTo(root);
        assertEquals(List.of(new SgfProperty(P.C,List.of("hello")),keepRoot),root.extraProperties());

        SgfNodeMapping childMapping=SgfDomainActionMapper.mapNode(context,child);
        assertEquals(List.of(keepChild),childMapping.extras());
        childMapping.applyExtrasTo(child);
        assertEquals(List.of(keepChild),child.extraProperties());
    }
}
