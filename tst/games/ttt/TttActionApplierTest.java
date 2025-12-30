package games.ttt;

import static org.junit.Assert.*;

import core.api.ApplyResult;
import core.engine.Action;
import equipment.Point;
import java.util.List;
import org.junit.Test;

public class TttActionApplierTest {
    private final TttPlugin plugin=new TttPlugin();
    private final TttActionApplier applier=new TttActionApplier();

    @Test public void playViaAction() {
        TttSpec spec=plugin.defaultSpec();
        TttState state=plugin.initialState(spec);
        Action play00=new Action.Play(0,0,null);
        ApplyResult<TttState> result=applier.apply(state,play00);
        assertTrue(result.accepted);
        assertEquals(TttMark.x,result.state.at(0,0));
    }

    @Test public void mapperRoundTrip() {
        TttMove move=new TttMove.Place(new Point(1,2));
        TttSpec spec=plugin.defaultSpec();
        Action action=TttActionMapper.toAction(move,spec);
        assertTrue(action instanceof Action.Play);
        ApplyResult<TttState> result=applier.apply(plugin.initialState(spec),action);
        assertTrue(result.accepted);
        assertEquals(TttMark.x,result.state.at(1,2));
    }

    @Test public void unsupportedActionRejected() {
        TttState state=plugin.initialState(plugin.defaultSpec());
        ApplyResult<TttState> result=applier.apply(state,new Action.Pass(null));
        assertFalse(result.accepted);
    }

    @Test public void metadataActionRejected() {
        TttState state=plugin.initialState(plugin.defaultSpec());
        Action.Metadata metadata=new Action.Metadata("note", List.of("hello"));
        ApplyResult<TttState> result=applier.apply(state,metadata);
        assertFalse(result.accepted);
    }
}
