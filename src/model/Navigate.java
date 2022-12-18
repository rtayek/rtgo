package model;
import java.util.List;
import io.Logging;
import sgf.*;
public enum Navigate { // move more code here?
    top,bottom,up,down,left,right,delete; // just added delete
    // do i really want to add delete?
    //
    public boolean canDo(Model model) { return canDo(this,model); }
    private static boolean canDo(Navigate navigate,Model model) {
        if(model.currentNode()!=null) switch(navigate) {
            case top:
            case up:
            case delete: // recently added
                boolean hasAParent=model.currentNode().parent!=null;
                boolean parentIsRT=hasAParent&&model.currentNode().parent.properties.get(0).p().equals(P.RT);
                return hasAParent; //&&!parentIsRT;
                // breaks gui, no up!
            case bottom:
            case down:
                return model.currentNode().children.size()>0;
            case right: // move to right sibling
                MNode parent=model.currentNode().parent;
                if(parent!=null) {
                    List<MNode> children=parent.children;
                    int me=children.indexOf(model.currentNode());
                    int siblings=children.size();
                    return me<siblings-1;
                }
                break;
            case left: // move to left sibling
                parent=model.currentNode().parent;
                if(parent!=null) {
                    List<MNode> children=parent.children;
                    int me=children.indexOf(model.currentNode());
                    return me>0;
                }
                break;
            default:
                Logging.mainLogger.config("unhandled case: "+navigate);
        }
        else Logging.mainLogger.config("no current node!");
        return false;
    }
    public synchronized boolean do_(Model model) {
        boolean canDo=canDo(model);
        if(canDo) do_(this,model);
        else {
            Logging.mainLogger.warning("looks like a toroidal game!");
            // why would i think this?
            // good question!
        }
        return canDo;
    }
    private static boolean do_(Navigate navigate,Model model) {
        if(navigate.canDo(model)) switch(navigate) {
            case top:
                model.top();
                return true;
            case bottom:
                model.bottom();
                return true;
            case up:
                model.up();
                return true;
            case down:
                model.down(0); // ???? or last child?
                return true;
            case right:
                model.right();
                return true;
            case left:
                model.left();
                return true;
            default:
                Logging.mainLogger.config(""+" "+"unhandled case: "+navigate);
                return false;
        }
        else return false;
    }
    public static void main(String[] args) {}
}
