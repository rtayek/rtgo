package sgf;
import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;
import io.IO;
/*
       case VK_E:

        Lizzie.frame.toggleGtpConsole();
        break;
      case VK_RIGHT:
        if (e.isShiftDown()) {
          moveBranchDown();
        } else {
          nextBranch();
        }
        break;

      case VK_LEFT:
        if (e.isShiftDown()) {
          moveBranchUp();
        } else if (controlIsPressed(e)) {
          undoToFirstParentWithVariations();
        } else {
          previousBranch();
        }
        break;

      case VK_UP:
        if (controlIsPressed(e) && e.isShiftDown()) {
          goCommentNode(false);
        } else if (e.isShiftDown()) {
          undoToChildOfPreviousWithVariation();
        } else if (controlIsPressed(e)) {
          undo(10);
        } else {
          if (Lizzie.frame.isMouseOver) {
            Lizzie.frame.doBranch(-1);
          } else {
            undo();
          }
        }
        break;

      case VK_PAGE_DOWN:
        if (controlIsPressed(e) && e.isShiftDown()) {
          Lizzie.frame.increaseMaxAlpha(-5);
        } else {
          redo(10);
        }
        break;

      case VK_DOWN:
        if (controlIsPressed(e) && e.isShiftDown()) {
          goCommentNode(true);
        } else if (controlIsPressed(e)) {
          redo(10);
        } else {
          if (Lizzie.frame.isMouseOver) {
            Lizzie.frame.doBranch(1);
          } else {
            redo();
          }
        }
        break;

      case VK_N:
        // stop the ponder
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        Lizzie.frame.startGame();
        break;
      case VK_SPACE:
        if (Lizzie.frame.isPlayingAgainstLeelaz) {
          Lizzie.frame.isPlayingAgainstLeelaz = false;
          Lizzie.leelaz.isThinking = false;
        }
        Lizzie.leelaz.togglePonder();
        refreshType = 2;
        break;

      case VK_P:
        Lizzie.board.pass();
        break;

      case VK_COMMA:
        if (!Lizzie.frame.playCurrentVariation()) Lizzie.frame.playBestMove();
        break;

      case VK_M:
        if (e.isAltDown()) {
          Lizzie.frame.openChangeMoveDialog();
        } else {
          Lizzie.config.toggleShowMoveNumber();
        }
        break;

      case VK_Q:
        Lizzie.frame.openOnlineDialog();
        break;

      case VK_F:
        Lizzie.config.toggleShowNextMoves();
        break;

      case VK_H:
        Lizzie.config.toggleHandicapInsteadOfWinrate();
        break;

      case VK_PAGE_UP:
        if (controlIsPressed(e) && e.isShiftDown()) {
          Lizzie.frame.increaseMaxAlpha(5);
        } else {
          undo(10);
        }
        break;

      case VK_I:
        // stop the ponder
        boolean isPondering = Lizzie.leelaz.isPondering();
        if (isPondering) Lizzie.leelaz.togglePonder();
        Lizzie.frame.editGameInfo();
        if (isPondering) Lizzie.leelaz.togglePonder();
        break;

      case VK_S:
        if (e.isAltDown()) {
          Lizzie.frame.saveImage();
        } else {
          // stop the ponder
          if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
          Lizzie.frame.saveFile();
        }
        break;

      case VK_O:
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        Lizzie.frame.openFile();
        break;

      case VK_V:
        if (controlIsPressed(e)) {
          Lizzie.frame.pasteSgf();
        } else {
          Lizzie.config.toggleShowBranch();
        }
        break;

      case VK_HOME:
        if (controlIsPressed(e)) {
          Lizzie.board.clear();
        } else {
          while (Lizzie.board.previousMove()) ;
        }
        break;

      case VK_END:
        while (Lizzie.board.nextMove()) ;
        break;

      case VK_X:
        if (controlIsPressed(e)) {
          Lizzie.frame.openConfigDialog();
        } else {
          if (!Lizzie.frame.showControls) {
            if (Lizzie.leelaz.isPondering()) {
              wasPonderingWhenControlsShown = true;
              Lizzie.leelaz.togglePonder();
            } else {
              wasPonderingWhenControlsShown = false;
            }
            Lizzie.frame.drawControls();
          }
          Lizzie.frame.showControls = true;
        }
        break;

      case VK_W:
        if (controlIsPressed(e)) {
          Lizzie.config.toggleLargeWinrate();
          refreshType = 2;
        } else if (e.isAltDown()) {
          Lizzie.frame.toggleDesignMode();
        } else {
          Lizzie.config.toggleShowWinrate();
          refreshType = 2;
        }
        break;

      case VK_L:
        Lizzie.config.toggleShowLcbWinrate();
        break;

      case VK_G:
        Lizzie.config.toggleShowVariationGraph();
        refreshType = 2;
        break;

      case VK_T:
        if (controlIsPressed(e)) {
          Lizzie.config.toggleShowCommentNodeColor();
        } else {
          Lizzie.config.toggleShowComment();
          refreshType = 2;
        }
        break;

      case VK_Y:
        Lizzie.config.toggleNodeColorMode();
        break;

      case VK_C:
        if (controlIsPressed(e)) {
          Lizzie.frame.copySgf();
        } else {
          Lizzie.config.toggleCoordinates();
          refreshType = 2;
        }
        break;

      case VK_ENTER:
        if (!Lizzie.leelaz.isThinking) {
          Lizzie.leelaz.sendCommand(
              "time_settings 0 "
                  + Lizzie.config
                      .config
                      .getJSONObject("leelaz")
                      .getInt("max-game-thinking-time-seconds")
                  + " 1");
          Lizzie.frame.playerIsBlack = !Lizzie.board.getData().blackToPlay;
          Lizzie.frame.isPlayingAgainstLeelaz = true;
          Lizzie.leelaz.genmove((Lizzie.board.getData().blackToPlay ? "B" : "W"));
        }
        break;

      case VK_DELETE:
      case VK_BACK_SPACE:
        if (e.isShiftDown()) {
          deleteBranch();
        } else {
          deleteMove();
        }
        break;

      case VK_Z:
        if (e.isShiftDown()) {
          toggleHints();
        } else if (e.isAltDown()) {
          Lizzie.config.toggleShowSubBoard();
        } else {
          startTemporaryBoard();
        }
        break;

      case VK_A:
        if (controlIsPressed(e)) {
          Lizzie.board.clearAnalysis();
        } else if (e.isAltDown()) {
          Lizzie.frame.openAvoidMoveDialog();
        } else {
          shouldDisableAnalysis = false;
          Lizzie.board.toggleAnalysis();
        }
        break;

      case VK_B:
        Lizzie.config.toggleShowPolicy();
        break;

      case VK_PERIOD:
        if (Lizzie.leelaz.isKataGo) {
          if (e.isAltDown()) {
            Lizzie.frame.toggleEstimateByZen();
          } else {
            if (e.isControlDown()) {
              // ctrl-. cycles modes, but only if estimates being displayed
              if (Lizzie.config.showKataGoEstimate) Lizzie.config.cycleKataGoEstimateMode();
            } else Lizzie.config.toggleKataGoEstimate();
            Lizzie.leelaz.ponder();
            if (!Lizzie.config.showKataGoEstimate) {
              Lizzie.frame.removeEstimateRect();
            }
          }
        } else Lizzie.frame.toggleEstimateByZen();
        // if (!Lizzie.board.getHistory().getNext().isPresent()) {
        // Lizzie.board.setScoreMode(!Lizzie.board.inScoreMode());}
        break;

      case VK_D:
        if (Lizzie.leelaz.isKataGo) {
          if (Lizzie.config.showKataGoScoreMean && Lizzie.config.kataGoNotShowWinrate) {
            Lizzie.config.showKataGoScoreMean = false;
            Lizzie.config.kataGoNotShowWinrate = false;
            break;
          }
          if (Lizzie.config.showKataGoScoreMean && !Lizzie.config.kataGoNotShowWinrate) {
            Lizzie.config.kataGoNotShowWinrate = true;
            break;
          }
          if (Lizzie.config.showKataGoScoreMean) {
            Lizzie.config.showKataGoScoreMean = false;
            break;
          }
          if (!Lizzie.config.showKataGoScoreMean) {
            Lizzie.config.showKataGoScoreMean = true;
            Lizzie.config.kataGoNotShowWinrate = false;
          }
        } else {
          toggleShowDynamicKomi();
        }
        break;

      case VK_R:
        Lizzie.frame.replayBranch(e.isAltDown());
        break;

      case VK_OPEN_BRACKET:
        if (Lizzie.frame.boardPositionProportion > 0) {
          Lizzie.frame.boardPositionProportion--;
          refreshType = 2;
        }
        break;

      case VK_CLOSE_BRACKET:
        if (Lizzie.frame.boardPositionProportion < 8) {
          Lizzie.frame.boardPositionProportion++;
          refreshType = 2;
        }
        break;

      case VK_K:
        Lizzie.config.toggleEvaluationColoring();
        break;

        // Use Ctrl+Num to switching multiple engine
      case VK_0:
      case VK_1:
      case VK_2:
      case VK_3:
      case VK_4:
      case VK_5:
      case VK_6:
      case VK_7:
      case VK_8:
      case VK_9:
        if (controlIsPressed(e)) {
          Lizzie.engineManager.switchEngine(e.getKeyCode() - VK_0);
          refreshType = 0;
        }
        break;
      default:
        shouldDisableAnalysis = false;
    }

    if (shouldDisableAnalysis && Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();

    Lizzie.frame.refresh(refreshType);
  }

  private boolean wasPonderingWhenControlsShown = false;

  @Override
  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
      case VK_X:
        if (wasPonderingWhenControlsShown) Lizzie.leelaz.togglePonder();
        Lizzie.frame.showControls = false;
        Lizzie.frame.refresh(1);
        break;

      case VK_Z:
        stopTemporaryBoard();
        Lizzie.frame.refresh(1);
        break;

      default:
    }
  }

  private long wheelWhen;

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (Lizzie.frame.processCommentMouseWheelMoved(e)) {
      return;
    }
    if (e.getWhen() - wheelWhen > 0) {
      wheelWhen = e.getWhen();
      if (Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();
      if (e.getWheelRotation() > 0) {
        if (Lizzie.frame.isMouseOver) {
          Lizzie.frame.doBranch(1);
        } else {
          redo();
        }
      } else if (e.getWheelRotation() < 0) {
        if (Lizzie.frame.isMouseOver) {
          Lizzie.frame.doBranch(-1);
        } else {
          undo();
        }
      }
      Lizzie.frame.refresh();
    }
  }
}
public class LizzieSgf {
    static class MyAcceptor implements SgfAcceptor {
        @Override public void accept(SgfNode node) {
            boolean hasLizzie=false;
            int index=0;
            for(Property property:node.properties) {
                if(property.p().id.equals(P2.LZ.name())) hasLizzie=true;
                ++index;
            }
            if(hasLizzie) nodes.put(node.id,node);
        }
        SortedMap<Integer,SgfNode> nodes=new TreeMap<>();
    }
    public static void print(SgfNode node) {
        for(int i=0;i<node.properties.size();++i) {
            Property property=node.properties.get(i);
            System.out.println(i+"="+property);
        }
    }
    static Property getLizzieProperty(SgfNode node) {
        for(Property property:node.properties)
            if(property.p().id.equals(P2.LZ.name())) return property;
        return null;
    }
    public static void parseLizzieProperty(String lizzie) {
        System.out.println('`'+lizzie+"'");
        String[] words=lizzie.split(" ");
        List<String> list=Arrays.asList(words);
        System.out.println("as a list:"+list);
        
    }
*/
// B[qd]LZ[0.7.2 42.4 14
class MyAcceptor extends SgfAcceptorImpl {
    @Override public void accept(SgfNode node) {
        System.out.print(node+" "+node.properties.size()+" properties. ");
        boolean nodeHasLizzieProperty=false;
        for(SgfProperty property:node.properties) {
            if(property.p().id.equals(P2.LZ.name())) nodeHasLizzieProperty=true;
            System.out.print(property.p().getClass().getName()+" ");
        }
        if(nodeHasLizzieProperty) idToNode.put(node.sgfId,node);
    }
    SortedMap<Integer,SgfNode> idToNode=new TreeMap<>(); // maybe just need id of node.
    // but still have to scan properties for lizzie stuff.
}
class LizzieSgf {
    public static void main(String[] args) {
        MyAcceptor myAcceptor=new MyAcceptor();
        Traverser traverser=new Traverser(myAcceptor);
        File file=new File("lizzie1.sgf");
        SgfNode games=new Parser().parse(IO.toReader(file));
        traverser.visit(games);
        //System.out.println("lizzies nodes: "+myAcceptor.idToNode);
        for(Integer key:myAcceptor.idToNode.keySet()) {
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            SgfNode node=myAcceptor.idToNode.get(key);
            System.out.println(node);
            //Property lizzieProperty=getLizzieProperty(node);
            //List<String> list=lizzieProperty.list();
            //parseLizzieProperty(lizzieProperty.list.get(0));
            //if(list.size()>1) System.err.println("LZ property.list has "+list.size()+" elements!");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
    }
}
