package model;
import java.util.ArrayList;
import java.util.List;
import controller.BothEnds;
import controller.Command;
import controller.GTPBackEnd;
import controller.GTPFrontEnd;
import controller.Response;
import equipment.Board;
import equipment.Point;
import equipment.Stone;
import io.Logging;
import io.IOs.End.Holder;
public final class ModelHelper2 {
	private ModelHelper2() {}
	public static void configureModelWithBoardFrom(Model target,Model source) {
		if(target==null||source==null||source.board()==null) return;
		int width=source.board().width();
		int depth=source.board().depth();
		Board.Topology topology=source.board().topology();
		Board.Shape shape=source.board().shape();
		target.setRoot(width,depth,topology,shape);
		target.setBoard(Board.factory.create(width,depth,topology,shape));
	}
	public static Model pushGTPMovesToCurrentStateDirect(Model original,boolean oneAtATime) {
		Model model=ModelHelper.newModelWithBoardFrom(original);
		// should set the board shape and topology also?
		List<String> gtpMoves=original.gtpMovesToCurrentState();
		boolean ok=GTPBackEnd.checkMoveCommandsDirect(model,gtpMoves,oneAtATime);
		if(!ok) Logging.mainLogger.severe("push fails on: "+gtpMoves);
		return model;
	}
	// should we do this before sending commands?
	// int width=model.board().width();
	// int depth=model.board().depth();
	// use width and depth command?
	// String command=Command.boardsize.name()+" "+width+":"+depth;
	// gtpMoves.add(0,command);
	public static void getMovesAndPush(GTPFrontEnd frontEnd,Model model,boolean oneAtATime) {
		List<String> gtpMoves=model.gtpMovesToCurrentState();
		Logging.mainLogger.info("gtp moves: "+gtpMoves);
		if(oneAtATime) {
			for(String gtpMove:gtpMoves) {
				Response response=frontEnd.sendAndReceive(gtpMove);
				if(!response.isOk()) Logging.mainLogger.severe(response+" is not ok!");
			}
		} else frontEnd.sendAndReceive(gtpMoves);
	}
	public static Model pushGTPMovesToCurrentStateBoth(Model original,boolean oneAtATime) {
		Model model=ModelHelper.newModelWithBoardFrom(original,"model");
		BothEnds both=new BothEnds();
		Holder holder=Holder.duplex();
		both.setupBoth(holder,"test",model);
		@SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
		getMovesAndPush(both.frontEnd,original,oneAtATime);
		return model;
	}
    public static List<String> toGTPMoves(List<Move2> moves,int width,int depth) {
        List<String> commands=new ArrayList<>();
        for(Move2 move:moves) { // how about pass and resign?
            String string=Command.play.name()+" "+move.color+" "+Move2.toGTPCoordinates(move,width,depth);
            commands.add(string);
        }
        return commands;
    }
	public static void main(String[] args) {
		Model original=new Model();
		original.setRoot(5,5);
		original.move(Stone.black,new Point());
		original.move(Stone.white,new Point(1,1));
		Model model=pushGTPMovesToCurrentStateDirect(original,false);
		if(!model.board().isEqual(original.board())) Logging.mainLogger.info("fail!");
		Model model2=pushGTPMovesToCurrentStateDirect(original,true);
		if(!model2.board().isEqual(original.board())) Logging.mainLogger.info("fail!");
		Model model3=pushGTPMovesToCurrentStateBoth(original,true);
		if(!model3.board().isEqual(original.board())) Logging.mainLogger.info("fail!");
		Model model4=pushGTPMovesToCurrentStateBoth(original,false);
		if(!model4.board().isEqual(original.board())) Logging.mainLogger.info("fail!");
	}
}
