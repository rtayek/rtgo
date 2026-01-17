package tree;
import io.Logging;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
final class TreeSupport {
    private TreeSupport() {}
    static boolean markProcessed(Set<Object> processed,Object data,String label) {
        boolean ok=processed.add(data);
        if(!ok && label!=null) Logging.mainLogger.info(label);
        return ok;
    }
    static void clearProcessed(Set<Object> processed) {
        if(processed!=null) processed.clear();
    }
    static <N,T> void relabel(N root,Iterator<T> iterator,BiConsumer<N,T> setData,
            BiConsumer<N,Consumer<N>> traversal) {
        if(root==null||setData==null||traversal==null) return;
        Consumer<N> relabel=node -> {
            if(node!=null&&iterator!=null) setData.accept(node,iterator.hasNext()?iterator.next():null);
        };
        traversal.accept(root,relabel);
    }
    static <N> void printBinaryTree(N node,String prefix,Function<N,N> left,Function<N,N> right,
            Function<N,?> data) {
        printBinaryTree(node,prefix,true,left,right,data);
    }
    static <N> void printBinaryTree(N node,String prefix,boolean isLeft,Function<N,N> left,
            Function<N,N> right,Function<N,?> data) {
        if(node==null) { Logging.mainLogger.info("0"); return; }
        printBinaryTreeInternal(node,prefix,isLeft,left,right,data);
    }
    static <N> void printMwayTree(N node,String indent,boolean last,Function<N,List<N>> children,
            Function<N,?> data) {
        Logging.mainLogger.info(indent+"+- "+(node!=null?data.apply(node):"0"));
        indent+=last?"   ":"|  ";
        if(node==null) return;
        List<N> childList=children!=null?children.apply(node):null;
        if(childList==null) return;
        for(int i=0;i<childList.size();i++) {
            printMwayTree(childList.get(i),indent,i==childList.size()-1,children,data);
        }
    }
    static <N> void preorderMway(N node,Function<N,List<N>> children,Consumer<N> consumer) {
        if(node==null) return;
        if(consumer!=null) consumer.accept(node);
        List<N> childList=children!=null?children.apply(node):null;
        if(childList==null) return;
        for(N child:childList) if(child!=null) preorderMway(child,children,consumer);
    }
    private static <N> void printBinaryTreeInternal(N node,String prefix,boolean isLeft,Function<N,N> left,
            Function<N,N> right,Function<N,?> data) {
        Logging.mainLogger.info(prefix+(isLeft?"|-- ":"\\-- ")+(node!=null?data.apply(node):"0"));
        if(node==null) return;
        String nextPrefix=prefix+(isLeft?"|   ":"    ");
        N leftNode=left.apply(node);
        if(leftNode!=null) printBinaryTreeInternal(leftNode,nextPrefix,true,left,right,data);
        N rightNode=right.apply(node);
        if(rightNode!=null) printBinaryTreeInternal(rightNode,nextPrefix,false,left,right,data);
    }
    interface ChildrenAccess<N> {
        int size(N node);
        N childAt(N node,int index);
    }
    static <N> boolean deepEquals(N node,N other,boolean checkEqual,
            java.util.function.BiPredicate<N,N> equals,ChildrenAccess<N> children) {
        if(node==other) return true;
        if(node==null||other==null) return false;
        if(checkEqual && equals!=null && !equals.test(node,other)) return false;
        if(children==null) return false;
        int size=children.size(node);
        if(size!=children.size(other)) return false;
        for(int i=0;i<size;++i) {
            N child=children.childAt(node,i);
            N otherChild=children.childAt(other,i);
            if(!deepEquals(child,otherChild,checkEqual,equals,children)) return false;
        }
        return true;
    }
}
