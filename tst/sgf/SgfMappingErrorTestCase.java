package sgf;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import core.formats.sgf.SgfDomainActionMapper;
import core.formats.sgf.SgfMappingContext;
import core.formats.sgf.SgfNodeMapping;
import equipment.Board;
import model.Model;

public class SgfMappingErrorTestCase {
    @Test public void malformedPropertiesBecomeExtrasAndMappingIsPure() {
        SgfProperty badMove=new SgfProperty(P.B,List.of("a"));
        SgfProperty emptyMove=new SgfProperty(P.W,List.of());
        SgfProperty badSize=new SgfProperty(P.SZ,List.of("x"));

        MNode node=new MNode(null);
        node.sgfProperties().add(badMove);
        node.sgfProperties().add(emptyMove);
        node.sgfProperties().add(badSize);

        SgfMappingContext context=new SgfMappingContext(Board.standard,Model.sgfBoardTopology,Model.sgfBoardShape);
        SgfNodeMapping mapping=SgfDomainActionMapper.mapNode(context,node);

        assertTrue(mapping.actions().isEmpty());
        assertEquals(List.of(badMove,emptyMove,badSize),mapping.extras());
        assertEquals(List.of(badMove,emptyMove,badSize),node.sgfProperties());
        assertTrue(node.extraProperties().isEmpty());

        mapping.applyExtrasTo(node);
        assertTrue(node.sgfProperties().isEmpty());
        assertEquals(List.of(badMove,emptyMove,badSize),node.extraProperties());
    }
}
