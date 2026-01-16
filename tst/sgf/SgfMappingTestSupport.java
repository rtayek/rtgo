package sgf;

import core.formats.sgf.SgfDomainActionMapper;
import core.formats.sgf.SgfMappingContext;
import core.formats.sgf.SgfNodeMapping;
import equipment.Board;
import model.Model;

final class SgfMappingTestSupport {
    private SgfMappingTestSupport() {}

    static SgfMappingContext contextFor(Model model) {
        int depth=Board.standard;
        if(model!=null) {
            if(model.board()!=null) depth=model.board().depth();
            else if(model.depthFromSgf()>0) depth=model.depthFromSgf();
        }
        return new SgfMappingContext(depth,Model.sgfBoardTopology,Model.sgfBoardShape);
    }

    static SgfNodeMapping mapNode(MNode node,Model model) {
        return SgfDomainActionMapper.mapNode(contextFor(model),node);
    }

    static SgfNodeMapping mapNode(MNode node) {
        return mapNode(node,null);
    }

    static MNode nodeWith(SgfProperty... properties) {
        return nodeWith(null,properties);
    }

    static MNode nodeWith(MNode parent,SgfProperty... properties) {
        MNode node=new MNode(parent);
        if(properties!=null) {
            for(SgfProperty property:properties) {
                node.sgfProperties().add(property);
            }
        }
        return node;
    }
}
